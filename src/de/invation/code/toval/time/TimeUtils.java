package de.invation.code.toval.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import de.invation.code.toval.validate.Validate;

public class TimeUtils {
	
	public static final boolean DEFAULT_CLEAR_TIME_OF_DAY = false;
	
	public static int actualYear(){
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public static Date today(){
		return new Date();
	}
	
	public static Date today(boolean clearTimeOfDay){
		if(clearTimeOfDay)
			return clearTimeOfDay(today());
		return today();
	}
	
	public static Date tomorrow(){
		return tomorrow(DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	public static Date tomorrow(boolean clearTimeOfDay){
		return diffFromDate(today(), Calendar.DATE, 1, clearTimeOfDay);
	}
	
	public static Date yesterday(){
		return yesterday(DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	public static Date yesterday(boolean clearTimeOfDay){
		return diffFromDate(today(), Calendar.DATE, -1, clearTimeOfDay);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	public static Date pastFromNow(TimeValue value){
		return pastFromNow(value, DEFAULT_CLEAR_TIME_OF_DAY);
	}

	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	public static Date pastFromNow(TimeValue value, boolean clearTimeOfDay){
		return diffFromNow(-value.getValueInMilliseconds(), clearTimeOfDay);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	public static Date pastFromDate(Date date, TimeValue value, boolean clearTimeOfDay){
		return diffFromDate(date, -value.getValueInMilliseconds(), clearTimeOfDay);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	public static Date futureFromNow(TimeValue value){
		return futureFromNow(value, DEFAULT_CLEAR_TIME_OF_DAY);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	public static Date futureFromNow(TimeValue value, boolean clearTimeOfDay){
		return diffFromNow(value.getValueInMilliseconds(), clearTimeOfDay);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	public static Date futureFromDate(Date date, TimeValue value, boolean clearTimeOfDay){
		return diffFromDate(date, value.getValueInMilliseconds(), clearTimeOfDay);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	private static Date diffFromNow(long diff, boolean clearTimeOfDay){
		return diffFromDate(new Date(), diff, clearTimeOfDay);
	}
	
	/**
	 * Caution: Difference calculation is based on adding/subtracting milliseconds and omits time zones.<br>
	 * In some cases the method might return possibly unexpected results due to time zones.<br>
	 * When for example to the last day of CEST without time of day information (in 2016: 30.10.2016 00:00:00 000) milliseconds for one day are added,
	 * the resulting date is NOT 30.10.2016 23:00:00 000 (as expected due to one hour subtraction because of summer time) but 31.10.2016 00:00:00 000. 
	 */
	private static Date diffFromDate(Date date, long diff, boolean clearTimeOfDay){
		Date result = convertToUtilDate(convertToLocalDateTime(date).plusNanos(diff*1000000));
		if(!clearTimeOfDay)
			return result;
		return clearTimeOfDay(result);
	}
	
	
	public static Date diffFromDate(Date date, int calendarField, int count, boolean clearTimeOfDay){
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarField, count);
        if(clearTimeOfDay)
        	return clearTimeOfDay(cal.getTime());
        return cal.getTime();
	}
	
	public static Date clearTimeOfDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dat = cal.get(Calendar.DATE);
		cal.clear();
		cal.set(year, month, dat);
		return cal.getTime();
	}
	
	public static final Date firstDayOfActualYear(){
		return firstDayOfYear(actualYear());
	}
	
	public static final Date firstDayOfYear(int year){
		Validate.positive(year);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, 0, 1);
		return cal.getTime();
	}
	
	public static final Date lastDayOfActualYear(){
		return lastDayOfYear(actualYear());
	}
	
	public static final Date lastDayOfYear(int year){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, 11, 31);
		return cal.getTime();
	}
	
	public static final Date actualYearQuarterStart(int quarter){
		return quarterStart(Calendar.getInstance().get(Calendar.YEAR), quarter);
	}
	
	public static final Date quarterStart(int year, int quarter){
		Validate.positive(year);
		Validate.inclusiveBetween(1, 4, quarter);
		return firstDayOfMonth(year, (quarter-1)*3);
		
	}
	
	public static final Date actualYearQuarterEnd(int quarter){
		return quarterEnd(Calendar.getInstance().get(Calendar.YEAR), quarter);
	}
	
	public static final Date quarterEnd(int year, int quarter){
		Validate.positive(year);
		Validate.inclusiveBetween(1, 4, quarter);
		return lastDayOfMonth(year, 2 + (quarter-1)*3);
	}
	
	public static Date firstDayOfActualMonth(){
		Calendar cal = Calendar.getInstance();
		return firstDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
	}
	
	public static Date firstDayOfMonth(int year, int month){
		return convertToUtilDate(LocalDate.of(year, month + 1, 1));
	}
	
	public static Date lastDayOfActualMonth(){
		Calendar cal = Calendar.getInstance();
		return lastDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
	}
	
	public static Date lastDayOfMonth(int year, int month){
		return convertToUtilDate(LocalDate.of(year, month + 1, 1).with(TemporalAdjusters.lastDayOfMonth()));
	}
	
	private static final void setTime(Date date, int calendarField, int value){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(calendarField, value);
		date.setTime(cal.getTimeInMillis());
	}
	
	public static final void setYear(Date date, int year){
		setTime(date, Calendar.YEAR, year);
	}
	
	public static final void setMonth(Date date, int month){
		setTime(date, Calendar.MONTH, month);
	}
	
	public static final boolean inActualYear(Date date){
		return firstDayOfActualYear().getTime() <= date.getTime() && lastDayOfActualYear().getTime() >= date.getTime();
	}
	
	public static TimeValue difference(Date date1, Date date2){
		return difference(date1, date2, null);
	}
	
	public static TimeValue difference(Date date1, Date date2, TimeScale scale){
		TimeValue difference = new TimeValue(Math.abs(date1.getTime() - date2.getTime()), TimeScale.MILLISECONDS);
		if(scale != null)
			difference.setScale(scale, true);
		return difference;
	}
	
	public static double differenceVal(Date date1, Date date2, TimeScale scale){
		return difference(date1, date2, scale).getValue();
	}
	
	public static long differenceValRounded(Date date1, Date date2, TimeScale scale){
		return Math.round(differenceVal(date1, date2, scale));
	}
	
	public static MinMaxDate calcMinMax(Collection<Date> dates){
		Date min = null;
		Date max = null;
		for(Date date: dates){
			if(date == null)
				continue;
			if(min == null && max == null){
				min = date;
				max = date;
				continue;
			} else {
				if(date.before(min))
					min = date;
				if(date.after(max))
					max = date;
			}
		}
		return new MinMaxDate(min, max);
	}
	
	public static class MinMaxDate {
		public Date min;
		public Date max;
		
		public MinMaxDate(Date min, Date max) {
			super();
			this.min = min;
			this.max = max;
		}
		
	}
	
	public static LocalDateTime convertToLocalDateTime(Date date){
		return convertToLocalDateTime(date, ZoneId.systemDefault());
	}
	
	public static LocalDateTime convertToLocalDateTime(Date date, ZoneId zone){
		Validate.notNull(date);
        Validate.notNull(zone);
		return date.toInstant().atZone(zone).toLocalDateTime();
	}
	
	public static LocalDate convertToLocalDate(Date date){
		return convertToLocalDate(date, ZoneId.systemDefault());
	}
	
	public static LocalDate convertToLocalDate(Date date, ZoneId zone){
		Validate.notNull(date);
        Validate.notNull(zone);
		return date.toInstant().atZone(zone).toLocalDate();
	}
	
	public static ZonedDateTime convertToZonedDateTime(Date date){
		return convertToZonedDateTime(date, ZoneId.systemDefault());
	}
	
	public static ZonedDateTime convertToZonedDateTime(Date date, ZoneId zone){
		Validate.notNull(date);
        Validate.notNull(zone);
		return Instant.ofEpochMilli(date.getTime()).atZone(zone);
	}
	
	public static Date convertToUtilDate(LocalDateTime date) {
		return convertToUtilDate(date, ZoneId.systemDefault());
	}
	
	public static Date convertToUtilDate(LocalDateTime date, ZoneId zone) {
        Validate.notNull(date);
        Validate.notNull(zone);
        return java.util.Date.from(((LocalDateTime) date).atZone(zone).toInstant());
	}
	
	public static Date convertToUtilDate(LocalDate date) {
		return convertToUtilDate(date, ZoneId.systemDefault());
	}
	
	public static Date convertToUtilDate(LocalDate date, ZoneId zone) {
		Validate.notNull(date);
        Validate.notNull(zone);
        return java.util.Date.from(((LocalDate) date).atStartOfDay(zone).toInstant());
    }
	
	public static Date convertToUtilDate(ZonedDateTime date) {
		Validate.notNull(date);
        return java.util.Date.from(((ZonedDateTime) date).toInstant());
    }
	
	public static void main(String[] args) throws ParseException {
//		System.out.println(tomorrow());
//		System.out.println(yesterday());
//		System.out.println(tomorrow(true));
//		System.out.println(yesterday(true));
//		System.out.println(today());
//		System.out.println(today(true));
//		System.out.println(actualYearStart());
//		System.out.println(actualYearEnd());
//		System.out.println(actualYearQuarterStart(1));
//		System.out.println(actualYearQuarterEnd(1));
//		System.out.println(actualYearQuarterStart(2));
//		System.out.println(actualYearQuarterEnd(2));
//		System.out.println(actualYearQuarterStart(3));
//		System.out.println(actualYearQuarterEnd(3));
//		System.out.println(actualYearQuarterStart(4));
//		System.out.println(actualYearQuarterEnd(4));

//LocalDateTime dt1 = LocalDateTime.now();
//System.out.println(dt1);
//LocalDateTime dt2 = LocalDateTime.of(2016, Month.OCTOBER, 30, 0, 0);
//System.out.println(dt2);
//LocalDateTime dt3 = dt2.plusDays(1);
//System.out.println(dt3);
//		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//		Date date1 = sdf.parse("30.10.2016");
//		System.out.println(date1);
//		System.out.println(diffFromDate(date1, new TimeValue(1, TimeScale.DAYS).getValueInMilliseconds(), false));
		
//		System.out.println(quarterStart(2017, 1));
//		System.out.println(quarterStart(2017, 2));
//		System.out.println(quarterStart(2017, 3));
//		System.out.println(quarterStart(2017, 4));
//		
//		System.out.println(quarterEnd(2017, 1));
//		System.out.println(quarterEnd(2017, 2));
//		System.out.println(quarterEnd(2017, 3));
//		System.out.println(quarterEnd(2017, 4));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		System.out.println(firstDayOfActualMonth());
		
	}

}
