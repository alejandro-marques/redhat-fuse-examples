package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;

public class FileRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file://origin")
		.filter(xpath("/order/orderItems/orderItem[not(contains(orderItemPublisherName,'ABC Company'))]"))
		.to("file://destination?fileExist=Append&fileName=output.xml");
	}


}
