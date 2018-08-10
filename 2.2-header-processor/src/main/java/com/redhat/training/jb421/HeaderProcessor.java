package com.redhat.training.jb421;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.xml.XPathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderProcessor implements Processor {
	
	final private static Logger log = LoggerFactory.getLogger(HeaderProcessor.class);

	final private static String XPATH_DATE = "/order/orderDate/text()";
	final private static DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	public void process(Exchange exchange) {
		log.info("Generating the journalFileName header...");
		String orderXml = exchange.getIn().getBody(String.class);
		String orderDateTime = XPathBuilder.xpath(XPATH_DATE).evaluate(exchange.getContext(), orderXml);
		log.info("orderDateTime: " + orderDateTime);
		Calendar orderCal = DatatypeConverter.parseDateTime(orderDateTime);
		String orderDate = df.format (orderCal.getTime());
		exchange.getIn().setHeader("journalFileName", "journal-" + orderDate + ".txt");
	}

}

