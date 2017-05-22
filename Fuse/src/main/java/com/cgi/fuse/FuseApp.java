package com.cgi.fuse;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.util.ExchangeHelper;

public class FuseApp {
	
	public static void main(String[] args) throws Exception {
		
		// Create the camel context for the REST API routing in Fuse.
		CamelContext contextFuseAPI = new DefaultCamelContext();
		
		// connect to embedded ActiveMQ JMS broker.
        ConnectionFactory connectionFactory = 
            new ActiveMQConnectionFactory("vm://localhost");
        contextFuseAPI.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
		// Start the route created in FuseAPIRoute.java.
		contextFuseAPI.addRoutes(new FuseAPIRoute());
		
		// Start context lifecycle.
		contextFuseAPI.start();		
		Thread.sleep(5000);
		
		
		
		// Create a producer template.
		// It acts as a consumer to request body of the message.
		ProducerTemplate template = contextFuseAPI.createProducerTemplate();
		
		String quoteAPIEndpoint = "direct:quoteAPI";	// Get Rest from external service.
		String mapsAPIEndpoint = "direct:mapsAPI";		// Get Rest from another external service.
		String xmlToJsonAPIEndpoint = "direct:geoXMLtoJsonAPI"; // Get Rest from xml API
		
		Object externalAPI1 = template.requestBody(quoteAPIEndpoint, null, String.class);
		Object externalAPI2 = template.requestBody(mapsAPIEndpoint, null, String.class);
		template.requestBody(xmlToJsonAPIEndpoint, null, String.class);
		
		// Create an exchange object to manipulate the messages.
		Exchange exchange = new DefaultExchange(contextFuseAPI);
		
		// Convert the files with API to string files
		String str_externalAPI1 = ExchangeHelper.convertToType(exchange, String.class, externalAPI1);
		String str_externalAPI2 = ExchangeHelper.convertToType(exchange, String.class, externalAPI2);
		
		String mergeAPIEndpoint = "direct:mergeAPI";	// Access the merge route.
		
		// Send the converted APIs to the 'mergeAPI' route to be merged into a file.
		template.sendBodyAndHeader(mergeAPIEndpoint, str_externalAPI1, "ID", 1);
		template.sendBodyAndHeader(mergeAPIEndpoint, str_externalAPI2, "ID", 1);

		contextFuseAPI.stop();
		
		
	}

}
