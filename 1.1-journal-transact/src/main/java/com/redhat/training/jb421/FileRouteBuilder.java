package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;

public class FileRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file:orders/incoming?include=order.*xml")
		.to("file:orders?fileName=journal.txt&fileExist=Append");
	}
}
