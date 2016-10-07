package de.invation.code.toval.time;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	
	public static final boolean DEFAULT_CLEAR_TIME_OF_DAY = false;
	
	public static Date today(){
		return new Date();
	}
	
	public static Date today(boolean clearTimeOfDay){
		return diffDayFromNow(0, clearTimeOfDay);
	}
	
	public static Date tomorrow(){
		return tomorrow(DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	public static Date tomorrow(boolean clearTimeOfDay){
		return futureFromNow(new TimeValue(1, TimeScale.DAYS), clearTimeOfDay);
	}
	
	public static Date yesterday(){
		return yesterday(DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	public static Date yesterday(boolean clearTimeOfDay){
		return pastFromNow(new TimeValue(1, TimeScale.DAYS), clearTimeOfDay);
	}
	
	public static Date pastFromNow(TimeValue value){
		return pastFromNow(value, DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	public static Date pastFromNow(TimeValue value, boolean clearTimeOfDay){
		return diffDayFromNow(-value.getValueInMilliseconds(), clearTimeOfDay);
	}
	
	public static Date futureFromNow(TimeValue value){
		return futureFromNow(value, DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	public static Date futureFromNow(TimeValue value, boolean clearTimeOfDay){
		return diffDayFromNow(value.getValueInMilliseconds(), clearTimeOfDay);
	}
	
	private static Date diffDayFromNow(long diff, boolean clearTimeOfDay){
		Date time = new Date();
		time.setTime(time.getTime() + diff);
		if(!clearTimeOfDay)
			return time;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		cal.clear();
		cal.set(year, month, date);
		return cal.getTime();
	}
	
	public static final Date actualYearStart(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.clear();
		cal.set(year, 0, 1);
		return cal.getTime();
	}
	
	public static final Date actualYearEnd(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.clear();
		cal.set(year, 11, 31);
		return cal.getTime();
	}
	
	public static final boolean inActualYear(Date date){
		return actualYearStart().getTime() <= date.getTime() && actualYearEnd().getTime() >= date.getTime();
	}
	
	public static void main(String[] args) {
		System.out.println(tomorrow());
		System.out.println(yesterday());
		System.out.println(tomorrow(true));
		System.out.println(yesterday(true));
		System.out.println(today());
		System.out.println(today(true));
		System.out.println(actualYearStart());
		System.out.println(actualYearEnd());
	}

}
