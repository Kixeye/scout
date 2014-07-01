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

import org.jdom2.Element;

/**
 * Describes an instance in EC2.
 * 
 * @author ebahtijaragic
 */
public class EurekaServiceAmazonDataCenterInfo implements EuerkaServiceDataCenterInfo {
	public static final String DATA_CENTER_INFO_CLASS = "com.netflix.appinfo.AmazonInfo";

	private final EurekaServiceInstanceDescriptor parent;

	private final String availabilityZone;
	private final String publicIpv4;
	private final String instanceId;
	private final String publicHostname;
	private final String localIpv4;
	private final String amiId;
	private final String instanceType;

	/**
	 * Creates a descriptor from a parent and a raw element.
	 * 
	 * @param parent
	 * @param instanceElement
	 */
	protected EurekaServiceAmazonDataCenterInfo(
			EurekaServiceInstanceDescriptor parent, Element instanceElement) {
		this.parent = parent;

		this.availabilityZone = instanceElement.getChildText("availability-zone");
		this.publicIpv4 = instanceElement.getChildText("public-ipv4");
		this.instanceId = instanceElement.getChildText("instance-id");
		this.publicHostname = instanceElement.getChildText("public-hostname");
		this.localIpv4 = instanceElement.getChildText("local-ipv4");
		this.amiId = instanceElement.getChildText("ami-id");
		this.instanceType = instanceElement.getChildText("instance-type");
	}

	/**
	 * @return the parent
	 */
	public EurekaServiceInstanceDescriptor getParent() {
		return parent;
	}

	/**
	 * @return the availabilityZone
	 */
	public String getAvailabilityZone() {
		return availabilityZone;
	}

	/**
	 * @return the publicIpv4
	 */
	public String getPublicIpv4() {
		return publicIpv4;
	}

	/**
	 * @return the instanceId
	 */
	public String getInstanceId() {
		return instanceId;
	}

	/**
	 * @return the publicHostname
	 */
	public String getPublicHostname() {
		return publicHostname;
	}

	/**
	 * @return the localIpv4
	 */
	public String getLocalIpv4() {
		return localIpv4;
	}

	/**
	 * @return the amiId
	 */
	public String getAmiId() {
		return amiId;
	}

	/**
	 * @return the instanceType
	 */
	public String getInstanceType() {
		return instanceType;
	}
}
