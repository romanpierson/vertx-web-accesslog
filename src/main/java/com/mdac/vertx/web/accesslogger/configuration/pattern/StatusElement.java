package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;

public class StatusElement extends GenericAccessLogElement{

	public StatusElement(){
		super(Arrays.asList("sc-status", "%s"), "status");
	}
	
}
