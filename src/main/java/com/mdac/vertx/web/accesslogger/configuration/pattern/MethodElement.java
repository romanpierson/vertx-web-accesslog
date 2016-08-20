package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;

public class MethodElement extends GenericAccessLogElement{

	public MethodElement(){
		super(Arrays.asList("cs-method", "%m"), "method");
	}
	
}
