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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

import com.kixeye.scout.ServiceInstanceDescriptor;
import com.kixeye.scout.ServiceStatus;

/**
 * A Eureka Service descriptor.
 * 
 * @author ebahtijaragic
 */
public class EurekaServiceInstanceDescriptor implements ServiceInstanceDescriptor {
	private final EurekaApplication parent;
	
	private final String app;
	private final String ipAddress;
	private final boolean isPortEnabled;
	private final int port;
	private final boolean isSecurePortEnabled;
	private final int securePort;
	private final ServiceStatus status;
	private final ServiceStatus overridenStatus;
	private final Map<String, String> metadata = new HashMap<>();
	
	/**
	 * Creates a descriptor from a parent and a raw element.
	 * 
	 * @param name
	 * @param instanceElement
	 */
	protected EurekaServiceInstanceDescriptor(EurekaApplication parent, Element instanceElement) {
		this.parent = parent;
		
		this.app = instanceElement.getChildText("app");
		this.ipAddress = instanceElement.getChildText("ipAddr");
		
		Element port = instanceElement.getChild("port");
		
		if (port != null) {
			this.isPortEnabled = Boolean.valueOf(port.getAttributeValue("enabled", "true"));
			this.port = Integer.valueOf(port.getTextTrim());
		} else {
			this.isPortEnabled = false;
			this.port = -1;
		}
		
		Element securePort = instanceElement.getChild("securePort");
		
		if (securePort != null) {
			this.isSecurePortEnabled = Boolean.valueOf(securePort.getAttributeValue("enabled", "true"));
			this.securePort = Integer.valueOf(securePort.getTextTrim());
		} else {
			this.isSecurePortEnabled = false;
			this.securePort = -1;
		}
		
		Element statusElement = instanceElement.getChild("status");
		ServiceStatus status = null;

		if (statusElement != null) {
			switch (statusElement.getTextTrim()) {
				case "UP":
					status = ServiceStatus.UP;
					break;
				case "DOWN":
					status = ServiceStatus.DOWN;
					break;
				default:
					status = ServiceStatus.UNKNOWN;
			}
		}
		
		this.status = status;

		Element overridenStatusElement = instanceElement.getChild("overriddenstatus");
		ServiceStatus overridenStatus = null;
		
		if (overridenStatusElement != null) {
			switch (overridenStatusElement.getTextTrim()) {
				case "UP":
					overridenStatus = ServiceStatus.UP;
					break;
				case "DOWN":
					overridenStatus = ServiceStatus.DOWN;
					break;
				default:
					overridenStatus = ServiceStatus.UNKNOWN;
			}
		}
		
		this.overridenStatus = overridenStatus;
		
		Element metadata = instanceElement.getChild("metadata");
		
		if (metadata != null) {
			for (Element element : metadata.getChildren()) {
				this.metadata.put(element.getName(), element.getText());
			}
		}
	}

	/**
	 * @see com.kixeye.scout.ServiceInstanceDescriptor#getName()
	 */
	public String getName() {
		return app;
	}

	/**
	 * @see com.kixeye.scout.ServiceInstanceDescriptor#getStatus()
	 */
	public ServiceStatus getStatus() {
		return status;
	}

	/**
	 * @return the parent
	 */
	public EurekaApplication getParent() {
		return parent;
	}

	/**
	 * @return the app
	 */
	public String getApp() {
		return app;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return the isPortEnabled
	 */
	public boolean isPortEnabled() {
		return isPortEnabled;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the isSecurePortEnabled
	 */
	public boolean isSecurePortEnabled() {
		return isSecurePortEnabled;
	}

	/**
	 * @return the securePort
	 */
	public int getSecurePort() {
		return securePort;
	}

	/**
	 * @return the overridenStatus
	 */
	public ServiceStatus getOverridenStatus() {
		return overridenStatus;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return Collections.unmodifiableMap(metadata);
	}
}
