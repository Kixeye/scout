package com.kixeye.scout.eureka;

/*
 * #%L
 * Scout
 * %%
 * Copyright (C) 2014 KIXEYE, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.client.config.RequestConfig;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kixeye.relax.HttpPromise;
import com.kixeye.relax.HttpPromise.HttpPromiseListener;
import com.kixeye.relax.HttpResponse;
import com.kixeye.relax.RestClient;
import com.kixeye.relax.RestClientSerDe;
import com.kixeye.relax.RestClients;
import com.kixeye.scout.ServiceDiscoveryClient;

/**
 * Discovers services listed on a Eureka service.
 * 
 * @author ebahtijaragic
 */
public class EurekaServiceDiscoveryClient implements ServiceDiscoveryClient<EurekaServiceInstanceDescriptor>, Closeable {
	private static final Logger logger = LoggerFactory.getLogger(EurekaServiceDiscoveryClient.class);
	
	private static final String ACCEPT_TYPE = "application/xml";
	
	private final ScheduledExecutorService scheduledExecutor;
	
	private final RestClient restClient;
	
	private final Timer refreshTimer = new Timer("discoveryClientRefreshTimer", true);
	
	private final AtomicReference<EurekaApplications> applicationsRef = new AtomicReference<>(null);
	
	private volatile long lastRefreshTime = -1;
	
	/**
	 * Creates a discovery client with default scheduler.
	 * 
	 * @param eurekaServiceUrl the url of the eureka instance, i.e. http://host:port/eureka/v2
	 * @param refreshRate
	 * @param refreshRateTimeUnit
	 */
	public EurekaServiceDiscoveryClient(String eurekaServiceUrl, long refreshRate, TimeUnit refreshRateTimeUnit) {
		this(eurekaServiceUrl, refreshRate, refreshRateTimeUnit, Executors.newScheduledThreadPool(1));
	}
	
	/**
	 * Creates a discovery client.
	 * 
	 * @param eurekaServiceUrl the url of the eureka instance, i.e. http://host:port/eureka/v2
	 * @param refreshRate
	 * @param refreshRateTimeUnit
	 * @param scheduledExecutor
	 */
	public EurekaServiceDiscoveryClient(String eurekaServiceUrl, long refreshRate, TimeUnit refreshRateTimeUnit, ScheduledExecutorService scheduledExecutor) {
		while (eurekaServiceUrl.endsWith("/")) {
			eurekaServiceUrl = eurekaServiceUrl.substring(0, eurekaServiceUrl.length() - 1);
		}
		
		this.restClient = RestClients.create(eurekaServiceUrl, serDe)
					.withUserAgentName(EurekaServiceDiscoveryClient.class.getSimpleName())
					.withRequestConfig(RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build())
				.build();
		
		this.scheduledExecutor = scheduledExecutor;
		this.scheduledExecutor.scheduleAtFixedRate(refreshTask, 0, refreshRate, refreshRateTimeUnit);
	}
	
	/**
	 * Describes all instances.
	 */
	public List<EurekaServiceInstanceDescriptor> describeAll() {
		List<EurekaServiceInstanceDescriptor> descriptors = new ArrayList<>();
		
		EurekaApplications applications = applicationsRef.get();
		
		if (applications != null) {
			for (EurekaApplication application : applications.getApplications()) {
				for (EurekaServiceInstanceDescriptor instanceDescriptor : application.getInstances()) {
					descriptors.add(instanceDescriptor);
				}
			}
		}
		
		return descriptors;
	}

	/**
	 * Describes a particular instance.
	 */
	public List<EurekaServiceInstanceDescriptor> describe(String serviceName) {
		EurekaApplications applications = applicationsRef.get();
		
		if (applications != null) {
			for (EurekaApplication application : applications.getApplications()) {
				if (application.getName().equals(serviceName)) {
					return application.getInstances();
				}
			}
		}
		
		return new ArrayList<>();
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		try {
			this.refreshTimer.cancel();
		} catch (Exception e) {
			logger.error("Unable to cancel timer", e);
		} finally {
			this.restClient.close();
		}
	}
	
	/**
	 * Returns true if we received a response.
	 * 
	 * @return
	 */
	public long getLastRefreshTime() {
		return lastRefreshTime;
	}
	
	/**
	 * Pings the discovery service.
	 */
	private final Runnable refreshTask = new Runnable() {
		public void run() {
			try {
				restClient.get("/apps", ACCEPT_TYPE, Element.class).addListener(serviceResponseListener);
			} catch (Exception e) {
				logger.error("Unable to retrieve apps from EurekaService", e);
			}
		}
	};

	/**
	 * Handles the discovery service response.
	 */
	private final HttpPromiseListener<HttpResponse<Element>> serviceResponseListener = new HttpPromiseListener<HttpResponse<Element>>() {
		public void handle(HttpPromise<HttpResponse<Element>> promise) {
			try {
				applicationsRef.set(new EurekaApplications(promise.get().getBody().deserialize()));
				lastRefreshTime = System.currentTimeMillis();
			} catch (Exception e) {
				logger.error("Unable to parse the EurekaService response", e);
			}
		}
	};
	
	/**
	 * A XML Rest SerDe.
	 */
	private static final RestClientSerDe serDe = new RestClientSerDe() {
		/**
		 * @see com.kixeye.relax.RestClientSerDe#serialize(java.lang.String, java.lang.Object)
		 */
		public byte[] serialize(String mimeType, Object obj) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new XMLOutputter().output((Element)obj, baos);
			return baos.toByteArray();
		}
		
		/**
		 * @see com.kixeye.relax.RestClientSerDe#deserialize(java.lang.String, byte[], int, int, java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public <T> T deserialize(String mimeType, byte[] data, int offset, int length, Class<T> clazz) throws IOException {
			ByteArrayInputStream bais = new ByteArrayInputStream(data, offset, length);
			
			try {
				return (T) new SAXBuilder().build(bais).getRootElement();
			} catch (JDOMException e) {
				throw new IOException(e);
			}
		}
	};
}
