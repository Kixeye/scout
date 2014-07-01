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

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

/**
 * Describes an instance in EC2.
 * 
 * @author ebahtijaragic
 */
public class EurekaServiceAmazonDataCenterInfo implements EuerkaServiceDataCenterInfo {
	public static final String DATA_CENTER_INFO_CLASS = "com.netflix.appinfo.AmazonInfo";

	private final EurekaServiceInstanceDescriptor parent;

	private final String name;
	private final Map<String, String> metadata = new HashMap<>();

	/**
	 * Creates a descriptor from a parent and a raw element.
	 * 
	 * @param parent
	 * @param instanceElement
	 */
	protected EurekaServiceAmazonDataCenterInfo(EurekaServiceInstanceDescriptor parent, Element instanceElement) {
		this.parent = parent;
		this.name = instanceElement.getChildText("name");
		Element metadata = instanceElement.getChild("metadata");

		if (metadata != null) {
			for (Element element : metadata.getChildren()) {
				this.metadata.put(element.getName(), element.getText());
			}
		}
	}

	/**
	 * @return the parent
	 */
	public EurekaServiceInstanceDescriptor getParent() {
		return parent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}
}
