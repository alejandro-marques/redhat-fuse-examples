package com.redhat.training.jb421;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.training.jb421.utils.CamelTestLogAppender;

public class OrderRouteTest extends CamelSpringTestSupport {

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/bundle-context.xml");
	}

	@Before
	public void setupFolders() {
		deleteDirectory("orders");
		createDirectory("orders/incoming");
	}

	@Test
	public void testRoute() {

		// For testing log DSL in route
		CamelTestLogAppender appender = registerAppender("OrderRoute1");
		try {
			NotifyBuilder notify = new NotifyBuilder(context).whenDone(2).create();
			//Send file contents to route
			template.sendBodyAndHeader("file://orders/incoming", ACCEPTED_ORDER, Exchange.FILE_NAME,
					"order-1.xml");
			template.sendBodyAndHeader("file://orders/incoming", REJECTED_ORDER, Exchange.FILE_NAME,
					"order-2.xml");
			notify.matches(4, TimeUnit.SECONDS); // wait 4 seconds for 2 messages to be processed

			//Verify output results
			File journal = new File("orders/journal.xml");
			assertTrue("Journal not created", journal.exists());
			String journalContents = context.getTypeConverter().convertTo(String.class, journal);
			assertTrue("Order not found, order value is greater than $10", journalContents.contains("<orderId>1"));
			assertFalse("Invalid order found, order value is not greater than $10",
					journalContents.contains("<orderId>2"));
			
			//Verify logging
			assertTrue("Logging for first file processed not found", appender.contains("Incoming amount is 10.59"));
			assertTrue("Logging for first file accepted not found",
					appender.contains("Accepted order from file: order-1.xml"));
			assertTrue("Logging for second file processed not found", appender.contains("Incoming amount is 9.99"));
			assertFalse("Logging for second file accepted found and should not be there",
					appender.contains("Accepted order from file: order-2.xml"));
		} finally {
			unregisterAppender(appender, "OrderRoute1");
		}
	}

	private CamelTestLogAppender registerAppender(String logger) {
		CamelTestLogAppender appender = new CamelTestLogAppender();

		Logger.getLogger(logger).addAppender(appender);

		return appender;
	}

	private void unregisterAppender(CamelTestLogAppender appender, String logger) {
		Logger.getLogger(logger).removeAppender(appender);
	}

	private final static String ACCEPTED_ORDER = "<order>" + "<orderId>1</orderId>" + "<orderItems>" + "<orderItem>"
			+ "<orderItemId>1</orderItemId>" + "<orderItemQty>1</orderItemQty>"
			+ "<orderItemPublisherName>ORly</orderItemPublisherName>" + "<orderItemPrice>10.59</orderItemPrice>"
			+ "</orderItem>" + "</orderItems>" + "</order>";

	private final static String REJECTED_ORDER = "<order>" + "<orderId>2</orderId>" + "<orderItems>" + "<orderItem>"
			+ "<orderItemId>1</orderItemId>" + "<orderItemQty>1</orderItemQty>"
			+ "<orderItemPublisherName>ORly</orderItemPublisherName>" + "<orderItemPrice>9.99</orderItemPrice>"
			+ "</orderItem>" + "</orderItems>" + "</order>";
}
