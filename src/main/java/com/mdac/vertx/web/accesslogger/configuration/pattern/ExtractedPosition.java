package com.mdac.vertx.web.accesslogger.configuration.pattern;


public class ExtractedPosition {

	final private int start;
	final private int offset;
	final private AccessLogElement element;
	
	public ExtractedPosition(final int start, final int offset) {
		super();
		this.start = start;
		this.offset = offset;
		this.element = null;
	}
	
	public ExtractedPosition(final int start, final int offset, final AccessLogElement element) {
		super();
		this.start = start;
		this.offset = offset;
		this.element = element;
	}

	public int getStart() {
		return start;
	}

	public int getOffset() {
		return offset;
	}

	public AccessLogElement getElement() {
		return element;
	}
	
	
	
}
