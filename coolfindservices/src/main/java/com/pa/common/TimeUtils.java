package com.pa.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	public static long getMilis(long timestamp){
		return timestamp*1000l;
	}
	public static long getMilis(String timestamp){
		try{
		return Long.parseLong(timestamp)*1000l;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String strTimeDiff(Date start, Date end) {
		long diff = end.getTime() - start.getTime();

		int Days = (int) (diff / (1000 * 60 * 60 * 24));
		long timeLeft = diff - Days * (1000 * 60 * 60 * 24);

		int Hours = (int) (timeLeft / (1000 * 60 * 60));
		timeLeft = timeLeft - Hours * (1000 * 60 * 60);
		int Mins = (int) (timeLeft / (1000 * 60));

		// return (String)
		// DateUtils.getRelativeTimeSpanString(end.getTime()-start.getTime());
		if(Days>0)
		return Days + "d " + Hours + "h " + Mins + "m ago";
		else
		if(Hours>0)
		{
			return  Hours + "h " + Mins + "m ago";
			
		}else{
			return  Mins + "m ago";
			
		}
	}

	public static String dateToStr(Date date) {
		SimpleDateFormat fmtOut = new SimpleDateFormat("EEEE, dd MMM yyyy");
		return fmtOut.format(date);
	}

	public static String getTime(Date date) {
		SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm");
		return fmtOut.format(date);
	}

	public static Date strToDate(String dateString) {
		// String dateString = "03/26/2012 11:49:00 AM";
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			convertedDate = null;
		}

		return convertedDate;
	}
	public static Date strToDate(String dateString,String format) {
		// String dateString = "03/26/2012 11:49:00 AM";
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				format);
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			convertedDate = null;
		}

		return convertedDate;
	}

	public static String timeStampToStr(String timeStamp) {
		String dateString = "";

		try {
			//Tracer.d(timeStamp);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Date convertedDate = new Date(1000 * Long.parseLong(timeStamp));
			dateString = dateFormat.format(convertedDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	public static String timeStampToDDMMYY(String timeStamp) {
		String dateString = "";

		try {
			//Tracer.d(timeStamp);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd MMM yyyy hh:mm:ss");
			Date convertedDate = new Date(Long.parseLong(timeStamp));
			dateString = dateFormat.format(convertedDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	
	public static long dateToTimeStamp(int year,int monthOfYear,int dayOfMonth){
		Calendar cal=Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		
		return cal.getTimeInMillis();
	}
	
	public static String dateToDDMMMYY(int year,int monthOfYear,int dayOfMonth){
		Calendar cal=Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		
		Date date=new Date(cal.getTimeInMillis());
		
		SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy");
		return fmtOut.format(date);

	}
	
	public static String dateToDDMMYY(Date date){
		SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy");
		return fmtOut.format(date);

	}
	public static String dateToYYMMDD(int year,int monthOfYear,int dayOfMonth){
		Calendar cal=Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		
		Date date=new Date(cal.getTimeInMillis());
		
		SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
		return fmtOut.format(date);

	}

}
