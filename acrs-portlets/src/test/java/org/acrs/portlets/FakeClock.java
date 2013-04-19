package org.acrs.portlets;

import java.util.Date;

import org.acrs.data.model.Clock;

public class FakeClock implements Clock {

	Date date;

	
	public FakeClock(Date date) {
		this.date = date;
	}
	
	@Override
	public Date getCurrentDate() {
		return date;
	}
	
}
