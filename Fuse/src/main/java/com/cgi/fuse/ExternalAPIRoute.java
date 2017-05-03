package com.cgi.fuse;

import java.io.File;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.ExchangeHelper;

public class ExternalAPIRoute {

	public static void main(String[] args) throws Exception {

		// Create the camel context for the REST API routing in Fuse
		CamelContext contextFuseAPI = new DefaultCamelContext();

		// Start the route inside the context to listen to the ActiveMQ
		contextFuseAPI.addRoutes(new RouteBuilder() {

			@Override
			public void configure() {
				from("direct:getRestFromExternalService")
					.setHeader(Exchange.HTTP_METHOD, simple("GET"))
					.to("http://data.stockholm.se/set/Befolkning/Befforandr/?apikey=F0C2K0J9J000F19E1CIE4CJ7Z804QFB0");
			}
		});

		contextFuseAPI.start();
		
		Thread.sleep(3000);
		
		
		ProducerTemplate template = contextFuseAPI.createProducerTemplate();
		
		Object result = template.requestBody("direct:getRestFromExternalService", null, String.class);
		Exchange exchange = new DefaultExchange(contextFuseAPI);
		String response = ExchangeHelper.convertToType(exchange, String.class, result); 
        System.out.println("Response : "+ response);		
		
		contextFuseAPI.stop();
	}
}
