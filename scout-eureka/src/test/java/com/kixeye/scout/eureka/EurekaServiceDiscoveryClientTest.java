package com.kixeye.scout.eureka;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kixeye.scout.ServiceStatus;

/**
 * Tests the {@link EurekaServiceDiscoveryClient}
 * 
 * @author ebahtijaragic
 */
public class EurekaServiceDiscoveryClientTest {
private static final Logger logger = LoggerFactory.getLogger(EurekaServiceDiscoveryClientTest.class);
	
	private Connection connection = null;
	private int port = -1;
	private Container testContainer = new Container() {
		@Override
		public void handle(Request request, Response response) {
			if ("/eureka/v2/apps".equals(request.getTarget())) {
				try {
					try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("sampleEurekaResponse.xml")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
						
						String line = null;
						
						while ((line = reader.readLine()) != null) {
							response.getByteChannel().write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));
						}
					}
					
					response.setContentType("application/xml");
					response.setStatus(Status.OK);
				} catch (Exception e) {
					logger.error("Unexpected exception", e);
				}
			} else {
				response.setStatus(Status.NOT_FOUND);
			}
			
			try {
				response.close();
			} catch (Exception e) {
				logger.error("Unexpected exception", e);
			}
		}
	};
	
	@Before
	public void setUp() throws Exception {
		Server server = new ContainerServer(testContainer);
		connection = new SocketConnection(server);
		
		ServerSocket socketServer = new ServerSocket(0);
		port = socketServer.getLocalPort();
		socketServer.close();
		
		connection.connect(new InetSocketAddress(port));
	}
	
	@After
	public void tearDown() throws Exception {
		connection.close();
	}
	
	@Test
	public void testGetAllApps() throws Exception {
		try (EurekaServiceDiscoveryClient client = new EurekaServiceDiscoveryClient("http://localhost:" + port + "/eureka/v2", 5000)) {
			int count = 0;
			while (!client.hasApplications() && count < 20) {
				Thread.sleep(100);
				count++;
			}
			
			List<EurekaServiceInstanceDescriptor> descriptors = client.describeAll();
			
			Assert.assertEquals(2, descriptors.size());
			
			EurekaServiceInstanceDescriptor eurekaDescriptor = null;
			EurekaServiceInstanceDescriptor demoDescriptor = null;
			
			for (EurekaServiceInstanceDescriptor descritor : descriptors) {
				if ("EUREKA".equals(descritor.getName())) {
					eurekaDescriptor = descritor;
				} else if ("DEMO".equals(descritor.getName())) {
					demoDescriptor = descritor;
				}
			}
			
			Assert.assertNotNull(eurekaDescriptor);
			Assert.assertEquals("EUREKA", eurekaDescriptor.getApp());
			Assert.assertEquals("127.0.1.1", eurekaDescriptor.getIpAddress());
			Assert.assertEquals(ServiceStatus.UNKNOWN, eurekaDescriptor.getOverridenStatus());
			Assert.assertEquals(ServiceStatus.UP, eurekaDescriptor.getStatus());
			Assert.assertEquals(80, eurekaDescriptor.getPort());
			Assert.assertEquals(443, eurekaDescriptor.getSecurePort());
			Assert.assertEquals(true, eurekaDescriptor.isPortEnabled());
			Assert.assertEquals(false, eurekaDescriptor.isSecurePortEnabled());
			
			Assert.assertNotNull(demoDescriptor);
			Assert.assertEquals("DEMO", demoDescriptor.getApp());
			Assert.assertEquals("127.0.1.2", demoDescriptor.getIpAddress());
			Assert.assertEquals(ServiceStatus.UNKNOWN, demoDescriptor.getOverridenStatus());
			Assert.assertEquals(ServiceStatus.UP, demoDescriptor.getStatus());
			Assert.assertEquals(80, demoDescriptor.getPort());
			Assert.assertEquals(443, demoDescriptor.getSecurePort());
			Assert.assertEquals(true, demoDescriptor.isPortEnabled());
			Assert.assertEquals(false, demoDescriptor.isSecurePortEnabled());
			Assert.assertEquals("462507", demoDescriptor.getMetadata().get("sessions"));
			Assert.assertEquals("472887", demoDescriptor.getMetadata().get("connections"));
		}
	}
	
	@Test
	public void testGetSingleApp() throws Exception {
		try (EurekaServiceDiscoveryClient client = new EurekaServiceDiscoveryClient("http://localhost:" + port + "/eureka/v2", 5000)) {
			int count = 0;
			while (!client.hasApplications() && count < 20) {
				Thread.sleep(100);
				count++;
			}
			
			List<EurekaServiceInstanceDescriptor> descriptors = client.describe("DEMO");
			
			Assert.assertEquals(1, descriptors.size());
			
			EurekaServiceInstanceDescriptor demoDescriptor = descriptors.get(0);
			
			Assert.assertNotNull(demoDescriptor);
			Assert.assertEquals("DEMO", demoDescriptor.getApp());
			Assert.assertEquals("127.0.1.2", demoDescriptor.getIpAddress());
			Assert.assertEquals(ServiceStatus.UNKNOWN, demoDescriptor.getOverridenStatus());
			Assert.assertEquals(ServiceStatus.UP, demoDescriptor.getStatus());
			Assert.assertEquals(80, demoDescriptor.getPort());
			Assert.assertEquals(443, demoDescriptor.getSecurePort());
			Assert.assertEquals(true, demoDescriptor.isPortEnabled());
			Assert.assertEquals(false, demoDescriptor.isSecurePortEnabled());
			Assert.assertEquals("462507", demoDescriptor.getMetadata().get("sessions"));
			Assert.assertEquals("472887", demoDescriptor.getMetadata().get("connections"));
		}
	}
}
