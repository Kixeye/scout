package com.kixeye.scout.eureka;

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
