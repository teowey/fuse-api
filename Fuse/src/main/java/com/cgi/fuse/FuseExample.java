package com.cgi.fuse;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FuseExample {

	public static void main(String[] args) throws Exception {
		CamelContext contextFuseAPI = new DefaultCamelContext();
		
		contextFuseAPI.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				from("timer://file://C:/Users/orlowweyt/Desktop/?repeatCount=1")
				.to("restlet:http://www.annonsera.se/api/ads/get.xml?category=fordon&count=1?restletMethods")
				.to("file://C:/Users/orlowweyt/Desktop/?fileName=test_new_xml.xml")
				.log("XML was copied to the local folder in a .txt file");
				
			}
		});
		contextFuseAPI.start();
		Thread.sleep(10000);
		contextFuseAPI.stop();
	}
}
