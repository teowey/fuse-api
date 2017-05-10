package com.cgi.fuse;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class FuseAPIRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {		
		
		/*
		 *  - Camel Routes to retrieve APIs  	 
		 * 
		 * 	An endpoint is created for each Route using the direct: component 
		 *  It gets the information from the external http using Jetty
		 *  Finally, it stores the API in memory as a JSON file.  
		 * 
		 */
		
		// 1st API route		
		from("direct:getRestFromExternalService")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("jetty:http://gturnquist-quoters.cfapps.io/api/random")
		.to("file:src/data?noop=true&fileName=quoteapi.json");
		
		// 2nd API route
		from("direct:getAnotherRestFromExternalService")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("jetty:http://maps.googleapis.com/maps/api/geocode/json?address=stockholm,sweden")
		.to("file:src/data?noop=true&fileName=geoapi.json");		
			
		/*
		 *  - Filter test to organize different APIs 
		 * 
		 *  Based on the Camel in Action example OrderRouter
		 *  Just creates a route from the memory folder to
		 *  a filter that prints the API if it is in JSON.
		 * 
		 */
		
		// load file orders from src/data into the JMS queue
		from("file:src/data?noop=true").to("jms:incomingOrders");
		
		// content-based router that filters the APIs
		from("jms:incomingOrders")
		.choice()
		.when(header("CamelFileName").endsWith(".json"))
				.to("jms:jsonOrders")
		.when(header("CamelFileName").endsWith(".xml"))
                 .to("jms:xmlOrders");
        
		// Print out in the console the names of the API files that are in JSON format
		from("jms:jsonOrders").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				System.out.println("Received JSON order: "
						+ exchange.getIn().getHeader("CamelFileName"));
			}
		});
	}
}
