package com.redhat.training.jb421;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.spi.BrowsableEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/camel-context.xml"})
@UseAdviceWith
public class HeaderRouteTest {

	@Autowired
	private CamelContext context;
	
	private final static String JOURNALS_FOLDER = "./tmp/orders";

	@EndpointInject(uri="file:" + JOURNALS_FOLDER)
	private BrowsableEndpoint journal;
	
	// There are six order files in the source folder, and all them are to be processed
	private final static int ORDER_QTY = 6;
	// Maximum time to wait for the routes to do their work
	private final static int TIMEOUT = 6;
	
	@Test
	public void testVendorJournalsAndQueues() throws Exception {

        NotifyBuilder builder = new NotifyBuilder(context).whenDone(ORDER_QTY).create();
		context.start();
		builder.matches(TIMEOUT, TimeUnit.SECONDS);

		List<Exchange> journals = journal.getExchanges();
		assertTrue("There should be two journal files", journals.size() == 2);
		assertTrue("First journal should be for Dec, 10 2016", "journal-20161210.txt".equals(journals.get(0).getIn().getHeader("CamelFileName", String.class)));
		assertTrue("Second journal should be for Dec, 11 2016", "journal-20161211.txt".equals(journals.get(1).getIn().getHeader("CamelFileName", String.class)));
	}

	private final static String DATA_FOLDER = "../data/orders2";
	private final static String SRC_FOLDER = JOURNALS_FOLDER + "/incoming";

	@Before
	public void setupFolders() throws IOException {
		CamelSpringTestSupport.deleteDirectory(JOURNALS_FOLDER);
		CamelSpringTestSupport.createDirectory(SRC_FOLDER);
		copyAllFiles(DATA_FOLDER, SRC_FOLDER);
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

