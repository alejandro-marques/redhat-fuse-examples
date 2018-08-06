package com.redhat.training.jb421;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/camel-context.xml"})
@UseAdviceWith
@MockEndpoints("activemq:*")
public class CBRRouteTest {

	@Autowired
	private CamelContext context;
	
	@EndpointInject(uri="mock:activemq:queue:abc")
	private MockEndpoint mock1;
	@EndpointInject(uri="mock:activemq:queue:orly")
	private MockEndpoint mock2;
	@EndpointInject(uri="mock:activemq:queue:namming")
	private MockEndpoint mock3;

	
	// There are six order files in the source folder, and all them are to be processed
	private static int ORDER_QTY = 6;
	// Maximum time to wait for the route to do its work
	private static int TIMEOUT = 6;
	
	@Test
	public void testVendorQueues() throws Exception {

		mock1.expectedMessageCount(3);
		mock2.expectedMessageCount(2);
		mock3.expectedMessageCount(1);
				
        NotifyBuilder builder = new NotifyBuilder(context).whenDone(ORDER_QTY * 2).create();
		context.start();
		builder.matches(TIMEOUT, TimeUnit.SECONDS);

		MockEndpoint.assertIsSatisfied(TIMEOUT, TimeUnit.SECONDS, mock1, mock2, mock3);
	}

	private static String DATA_FOLDER = "../data/orders";
	private static String INCOMING_FOLDER = "orders/incoming";

	@Before
	public void setupFolders() throws IOException {
		CamelSpringTestSupport.deleteDirectory(INCOMING_FOLDER);
		CamelSpringTestSupport.createDirectory(INCOMING_FOLDER);
		copyAllFiles(DATA_FOLDER, INCOMING_FOLDER);
	}
	
	private void copyAllFiles(String srcFolderName, String dstFolderName) throws IOException {
		File srcFolder = new File(srcFolderName);
		Path dstFolder = Paths.get(dstFolderName);
		for (File file: srcFolder.listFiles()) {
			Path src = file.toPath();
	        Files.copy(src, dstFolder.resolve(src.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	
}

