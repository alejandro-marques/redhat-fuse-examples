package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;

public class OrderRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		String path1 = "/order/orderItems/orderItem/orderItemPrice/text()";
		String path2 = "number(/order/orderItems/orderItem/orderItemPrice/text()) > 10.00";
		
		from("file:orders/incoming")
		.routeId("OrderRoute1")
		.setHeader("amount", xpath(path1))
		.log("Incoming amount is ${header.amount}")
		.filter(xpath(path2))
		.log("Accepted order from file: ${header.CamelFileNameOnly}.")
		.to("file:orders?fileName=journal.xml&fileExist=Append");
		
	}

}
