package com.cgi.fuse;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.util.ExchangeHelper;

public class FuseApp {
	
	public static void main(String[] args) throws Exception {
		
		// Create the camel context for the REST API routing in Fuse
		CamelContext contextFuseAPI = new DefaultCamelContext();
		
		// Start the route inside the context to listen to the ActiveMQ
		contextFuseAPI.addRoutes(new FuseAPIRoute());
		
		// Start context lifecycle
		contextFuseAPI.start();
		
		Thread.sleep(3000);
		
		// Create a producer template to request body of the message
		ProducerTemplate template = contextFuseAPI.createProducerTemplate();
		
		// Request the body and converts it to a string and outputs it in the console
		//Object result = template.requestBody("direct:getRestFromExternalService", null, String.class);
		//template.sendBody("direct:output", result);
		
		Object test = template.requestBody("direct:getRestFromExternalService", null, String.class);
        //System.out.println("Response : " + test);		
		Object test2 = template.requestBody("direct:getAnotherRestFromExternalService", null, String.class);

		contextFuseAPI.stop();
		
		
	}

}
