package com.cgi.fuse;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class FuseAPIRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {		
		
		from("direct:getRestFromExternalService")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("jetty:http://gturnquist-quoters.cfapps.io/api/random")
		.to("file:src/data?noop=true&fileName=quote.json");
		
		from("direct:getAnotherRestFromExternalService")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("jetty:http://maps.googleapis.com/maps/api/geocode/json?address=stockholm,sweden")
		.to("file:src/data?noop=true&fileName=geoapi.json");		
		
	}
}
