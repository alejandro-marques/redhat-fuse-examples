package com.redhat.training.jb421;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangePrinter implements Processor {
	
	private static Logger log = LoggerFactory.getLogger(ExchangePrinter.class);
	
	public void process(Exchange exchange){
		
		String body = exchange.getIn().getBody(String.class);
		// set the value of body here
		
		log.info("Body: "+ body);
		
		log.info("Headers:");
		Map<String,Object> headers = exchange.getIn().getHeaders();
		// set the value of headers here
				
		for(String key: headers.keySet()){
			log.info("Key: " + key + " | Value: "+ headers.get(key));
		}
	}

}
