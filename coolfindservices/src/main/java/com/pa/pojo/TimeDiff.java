package com.pa.pojo;

public class TimeDiff {
	public long timeDiffInMilis, days, hours, mins, seconds;

	public TimeDiff(long timeDiff,long days,long hours, long mins,long seconds) {
		timeDiffInMilis=timeDiff;
		this.days=days;
		this.hours=hours;
		this.mins=mins;
		this.seconds=seconds;
	}
}
