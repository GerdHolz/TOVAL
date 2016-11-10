package de.invation.code.toval.time;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class TimeValue implements Comparable<TimeValue>, Serializable{
	
	private static final long serialVersionUID = -7827974152969997391L;

	private final String toStringFormat = "%s %s";
	private final String toStringFormatShort = "%s%s";
	
	protected static final long FACTOR_SECONDS = 1000;
	protected static final long FACTOR_MINUTES = FACTOR_SECONDS*60;
	protected static final long FACTOR_HOURS = FACTOR_MINUTES*60;
	protected static final long FACTOR_DAYS = FACTOR_HOURS*24;
	protected static final long FACTOR_WEEKS = FACTOR_DAYS*7;
	protected static final long FACTOR_MONTHS = FACTOR_DAYS*30;
	protected static final long FACTOR_YEARS = FACTOR_DAYS*360;
	
	protected Double value = 0.0;
	protected TimeScale scale = TimeScale.MILLISECONDS;
	
	public TimeValue(){}
	
	public TimeValue(TimeValue timeValue) throws ParameterException{
		this(timeValue.getValue(), timeValue.getScale());
	}
	
	public TimeValue(Double value, TimeScale scale) throws ParameterException{
		Validate.notNull(value);
		Validate.notNull(scale);
		this.value = value;
		this.scale = scale;
	}
	
	public TimeValue(Integer value, TimeScale scale) throws ParameterException{
		this(value.doubleValue(), scale);
	}
	
	public TimeValue(Long value, TimeScale scale) throws ParameterException{
		this(value.doubleValue(), scale);
	}
	
	public void activateShortDescriptorMode(){
		this.scale.activateShortDescriptorMode();
	}
	
	public void activateLongDescriptorMode(){
		this.scale.activateLongDescriptorMode();
	}

	public Double getValue() {
		return value;
	}

	public TimeScale getScale() {
		return scale;
	}
	
	public void adjustScale(){
		setScale(recommendedScale(), true);
	}
	
	public TimeScale recommendedScale(){
		TimeValue copy = this.clone();
		if(copy.getValue() > 1){
			while(copy.increaseScale()){
				if(copy.getValue() < 1){
					copy.decreaseScale();
					break;
				}
			}
		} else if(copy.getValue() < 1){
			while(copy.decreaseScale()){
				if(copy.getValue() > 1){
					copy.increaseScale();
					break;
				}
			}
		}
		return copy.getScale();
	}
	
	public boolean increaseScale(){
		TimeScale newScale = null;
		switch(scale){
			case MILLISECONDS: 	newScale = TimeScale.SECONDS;
			break;
			case SECONDS: 		newScale = TimeScale.MINUTES;
			break;
			case MINUTES: 		newScale = TimeScale.HOURS;
			break;
			case HOURS: 		newScale = TimeScale.DAYS;
			break;
			case DAYS: 			newScale = TimeScale.WEEKS;
			break;
			case WEEKS: 		newScale = TimeScale.MONTHS;
			break;
			case MONTHS:		newScale = TimeScale.YEARS;
			break;
			default:			newScale = null;
			break;
		}
		if(newScale != null){
			setScale(newScale, true);
			return true;
		}
		return false;
	}
	
	public boolean decreaseScale(){
		TimeScale newScale = null;
		switch(scale){
			case SECONDS: 		newScale = TimeScale.MILLISECONDS;
			break;
			case MINUTES: 		newScale = TimeScale.SECONDS;
			break;
			case HOURS: 		newScale = TimeScale.MINUTES;
			break;
			case DAYS: 			newScale = TimeScale.HOURS;
			break;
			case WEEKS: 		newScale = TimeScale.DAYS;
			break;
			case MONTHS:		newScale = TimeScale.WEEKS;
			break;
			case YEARS:			newScale = TimeScale.MONTHS;
			break;
			default:			newScale = null;
			break;
		}
		if(newScale != null){
			setScale(newScale, true);
			return true;
		}
		return false;
	}
	
	public void setScale(TimeScale scale, boolean adjustValue){
		if(scale == this.scale)
			return;
		if(adjustValue){
			switch(scale){
				case MILLISECONDS: 	value = getValueInMilliseconds().doubleValue();
									break;
				case SECONDS: 		value = getValueInMilliseconds().doubleValue()/FACTOR_SECONDS;
									break;
				case MINUTES: 		value = getValueInMilliseconds().doubleValue()/FACTOR_MINUTES;
									break;
				case HOURS:			value = getValueInMilliseconds().doubleValue()/FACTOR_HOURS;
									break;
				case DAYS: 			value = getValueInMilliseconds().doubleValue()/FACTOR_DAYS;
									break;
				case WEEKS: 		value = getValueInMilliseconds().doubleValue()/FACTOR_WEEKS;
									break;
				case MONTHS:		value = getValueInMilliseconds().doubleValue()/FACTOR_MONTHS;
									break;
				case YEARS:			value = getValueInMilliseconds().doubleValue()/FACTOR_YEARS;
			}
		}
		this.scale = scale;
	}
	
	public TimeValue clone(){
		try {
			return new TimeValue(new Double(value), scale);
		} catch (ParameterException e) {
			return null;
		}
	}
	
	public boolean isSmallerThan(TimeValue o){
		return this.compareTo(o) < 0;
	}
	
	public boolean isSmallerEqualThan(TimeValue o){
		return this.compareTo(o) <= 0;
	}
	
	public boolean isBiggerThan(TimeValue o){
		return this.compareTo(o) > 0;
	}
	
	public boolean isBiggerEqualThan(TimeValue o){
		return this.compareTo(o) >= 0;
	}
	
	@Override
	public int compareTo(TimeValue o) {
		return (int) Math.signum(getValueInMilliseconds() - o.getValueInMilliseconds());
	}
	
	public Long getValueInMilliseconds() {
		switch(scale){
			case MILLISECONDS: 	return value.longValue();
			case SECONDS: 		return Long.valueOf(Math.round(value*FACTOR_SECONDS));
			case MINUTES: 		return Long.valueOf(Math.round(value*FACTOR_MINUTES));
			case HOURS: 		return Long.valueOf(Math.round(value*FACTOR_HOURS));
			case DAYS: 			return Long.valueOf(Math.round(value*FACTOR_DAYS));
			case WEEKS: 		return Long.valueOf(Math.round(value*FACTOR_WEEKS));
			case MONTHS:		return Long.valueOf(Math.round(value*FACTOR_MONTHS));
			case YEARS:			return Long.valueOf(Math.round(value*FACTOR_YEARS));
			default: 			return value.longValue();
		}
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeValue other = (TimeValue) obj;
		if (scale != other.scale)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		NumberFormat nf = new DecimalFormat("0.##");
		if(scale.isShortDesctiptorMode())
			return String.format(toStringFormatShort, nf.format(value), scale);
		return String.format(toStringFormat, nf.format(value), scale);
	}

}
