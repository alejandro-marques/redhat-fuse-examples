package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;

public class FTPRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("ftp://localhost?username=user&password=password&delete=true&include=order.*xml&passiveMode=true")
		.log("New file ${headers.CamelFileName} picked up from ${headers.CamelFileHost}")
		.process(new ExchangePrinter())
		.to("file:orders?fileName=${header.CamelFileHost}&fileExist=Append");
	}
}
