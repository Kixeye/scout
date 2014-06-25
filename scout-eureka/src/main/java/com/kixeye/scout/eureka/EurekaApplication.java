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

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * Describes a Eureka application.
 * 
 * @author ebahtijaragic
 */
public class EurekaApplication {
	private final EurekaApplications parent;

	private final String name;
	private final List<EurekaServiceInstanceDescriptor> instances = new ArrayList<>();

	/**
	 * Creates a new application with a parent and raw element.
	 * 
	 * @param parent
	 * @param element
	 */
	protected EurekaApplication(EurekaApplications parent, Element element) {
		this.parent = parent;
		this.name = element.getChildText("name");

		for (Element instance : element.getChildren("instance")) {
			instances.add(new EurekaServiceInstanceDescriptor(this, instance));
		}
	}

	/**
	 * @return the parent
	 */
	public EurekaApplications getParent() {
		return parent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the instances
	 */
	public List<EurekaServiceInstanceDescriptor> getInstances() {
		return instances;
	}
}
