package com.cgi.fuse;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.util.ExchangeHelper;

public class FuseApp {
	
	public static void main(String[] args) throws Exception {
		CamelContext contextFuseAPI = new DefaultCamelContext();
		
		contextFuseAPI.addRoutes(new FuseAPIRoute());
		
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
