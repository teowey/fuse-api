package com.cgi.fuse;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FuseAPIRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Create the camel context for the REST API routing in Fuse
				

				// Start the route inside the context to listen to the ActiveMQ
				

					
						from("direct:getRestFromExternalService")
							.setHeader(Exchange.HTTP_METHOD, simple("GET"))
							.to("http://gturnquist-quoters.cfapps.io/api/random");
					
	
	}
}
