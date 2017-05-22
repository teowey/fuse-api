package com.cgi.fuse;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/*
 * 
 *  - Camel Routes to process APIs  
 *  	 
 *  This file is a collection of Camel routes.
 *  The routes included in this file have different purposes 
 *  in the processing of the APIs. 
 *   
 */

public class FuseAPIRoute extends RouteBuilder {

		
	@Override
	public void configure() throws Exception {		
	
		/*
		 * 	An endpoint is created for each Route using the direct: component 
		 *  It gets the information from the external http using Jetty
		 *  Finally, it stores the API in memory as a JSON file.  
		 * 
		 */		
		
		// 1st API route		
		from("direct:quoteAPI")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("jetty:http://gturnquist-quoters.cfapps.io/api/random")
		.to("file:src/data?noop=true&fileName=quoteapi.json");
		
		// 2nd API route
		from("direct:mapsAPI")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("jetty:http://maps.googleapis.com/maps/api/geocode/json?address=stockholm,sweden")
		.to("file:src/data?noop=true&fileName=mapsapi.json");
	
		
		
		// XML to JSON route.
		/*
		 * Route to get XML API 
		 * Write the resulting JSON file in the data folder with the 
		 * other JSON API files.
		 */
		from("direct:geoXMLtoJsonAPI")
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD, simple("GET"))
		.to("http://api.geonames.org/weatherIcao?ICAO=LSZH&username=rheh&style=full")
		.to("direct:marshalAPI");
		
		// XML to JSON conversion using transformation pattern 'marshal' component.
		from("direct:marshalAPI")
		.marshal().xmljson()
		.to("file:src/data?noop=true&fileName=testxmlapi.json");
		
		
		/*
		 *  - Filter to organize different APIs 
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
		
		/*
		 *	- Use a route to merge APIs 
		 * 
		 *  This is a route specific to concatenating 
		 *  2 different APIs into 1 single file.
		 *  The FuseApp.java will call this route by
		 *  the name 'mergeAPI' and process it using methods
		 *  from the 'template'. 
		 * 
		 * 
		 */
		
		// create a route to concatenate 2 different APIs
		from("direct:mergeAPI")
		.aggregate(header("ID"), new StringAggregationStrategy()).completionSize(2)
		.to("file:src/data?noop=true&fileName=apimerge.json");
	}
}
