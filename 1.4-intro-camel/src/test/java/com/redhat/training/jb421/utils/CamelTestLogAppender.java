package com.redhat.training.jb421.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class CamelTestLogAppender extends AppenderSkeleton {
	
	private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

	@Override
	public void close() {
		//noop
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		log.add(event);
	}
	
	public boolean contains(String contents) {
		for(LoggingEvent event : this.log) {
			if (event.getMessage().toString().contains(contents))
				return true;
		}
		return false;
	}

}
