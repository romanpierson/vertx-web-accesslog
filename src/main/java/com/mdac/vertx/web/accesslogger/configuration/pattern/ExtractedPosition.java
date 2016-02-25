package com.mdac.vertx.web.accesslogger.configuration.pattern;


public class ExtractedPosition {

	final int start;
	final int offset;
	
	public ExtractedPosition(final int start, final int offset) {
		super();
		this.start = start;
		this.offset = offset;
	}

	public int getStart() {
		return start;
	}

	public int getOffset() {
		return offset;
	}
	
	
	
}
