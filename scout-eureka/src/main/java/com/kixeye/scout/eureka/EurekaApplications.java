package com.kixeye.scout.eureka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;

/**
 * Describes lists of Eureka applications.
 * 
 * @author ebahtijaragic
 */
public class EurekaApplications {
	private final List<EurekaApplication> applications = new ArrayList<>();

	/**
	 * Creates a top level application object.
	 * 
	 * @param element
	 */
	protected EurekaApplications(Element element) {
		for (Element application : element.getChildren("application")) {
			applications.add(new EurekaApplication(this, application));
		}
	}

	/**
	 * @return the applications
	 */
	public List<EurekaApplication> getApplications() {
		return  Collections.unmodifiableList(applications);
	}
}
