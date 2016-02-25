package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;
import java.util.Collection;

public class PatternResolver {

	final Collection<AccessLogElement> availableElements = Arrays.asList(new RequestElement());
	
	char elementIdentifier = '%';
	char [] allowedElementIdentifiers = new char[]{'h','l','u','t','r','s','b','i'};
	
	
	protected boolean isElementIdentifier(final char identifier){
		
		for(final char c : allowedElementIdentifiers){
			if(identifier == c){
				return true;
			}
		}
		
		return false;
	}
	
	public ResolvedPatternResult resolvePattern(final String pattern){
		
		return null;
		
	}
	
	public ResolvedPatternResult resolvePatternOLD(final String pattern){
		
		// %h %l %u %t "%r" %s %b "%{Referer}i" "%{User-Agent}i"
		
		// First split by potential "
		pattern.split("\"");
		
		boolean inElement = false;      // If we are in a element (defined by %
		boolean inElementSpecificator = false; // If we are in a element specificator identified by {ﬁ
		StringBuilder sbSpecificator = null;
		String currentElement = null;
		
		for(final byte b : pattern.getBytes()){
			
			final char ch = (char) b;
				
			if(inElement){
				
				if(isElementIdentifier(ch)){
					currentElement = "" + ch;
				} else if (ch == '{'){
					inElementSpecificator = true;
					sbSpecificator = new StringBuilder();
				} else {
					// This should basically mean that the element has finished
					
				}
				
				
			} else if (elementIdentifier == ch) {
				
				if(inElement){
					
					if(currentElement == null){
						// We previously were reading an element but we could not get its identifier - invalid
						throw new RuntimeException("Invalid pattern");
					} else {
						
					}
					
					// Previous element needs to be terminated
					
					System.out.println(currentElement);
					
					
					currentElement = null;
					
				}
				
				inElement = true;
			}
			
			System.out.println((char) b);
		}
		
		
		return null;
		
	}

}
