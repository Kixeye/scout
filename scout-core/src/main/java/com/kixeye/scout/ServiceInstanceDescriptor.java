package com.kixeye.scout;

/**
 * Describes a service.
 * 
 * @author ebahtijaragic
 */
public interface ServiceInstanceDescriptor {
	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @return the status
	 */
	public ServiceStatus getStatus();
}
