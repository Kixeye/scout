package com.kixeye.scout;

import java.util.List;

/**
 * An interface for a Discovery Client.
 * 
 * @author ebahtijaragic
 */
public interface ServiceDiscoveryClient<T extends ServiceInstanceDescriptor> {
	/**
	 * Describes all the services this client knows about.
	 * 
	 * @return
	 */
	public List<T> describeAll();
	
	/**
	 * Describes all services with the given name.
	 * 
	 * @param serviceName
	 * @return
	 */
	public List<T> describe(String serviceName);
}
