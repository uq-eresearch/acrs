package org.acrs.data.model;

import java.util.Date;

public class SystemClock implements Clock {

	public Date getCurrentDate() {
		return new Date();
	}
}
