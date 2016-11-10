package de.invation.code.toval.types;

import java.util.ArrayList;
import java.util.Collection;

public interface ElementWithTypedProperties<E extends Enum<E>> {
	
	public static final int toStringDistance = 20;
	public static final String toStringBaseFormat =	  "%-" + toStringDistance + "s: %%s\n";
	
	public Class<?> getPropertyClass();
	
	public void setProperty(E propertyType, Object property) throws Exception;
	
	public Object getProperty(E propertyType) throws Exception;
	
	default <T extends ElementWithTypedProperties<E>> PropertyComparator<T,E> getComparator(E propertyType){
		return new PropertyComparator<T,E>(propertyType);
	}
	
	default <T extends ElementWithTypedProperties<E>> PropertyComparator<T,E> getComparator(E propertyType, boolean descending){
		return new PropertyComparator<T,E>(propertyType, descending);
	}
	
	default Collection<E> getIgnoredPropertiesforPrinting(){
		return new ArrayList<>();
	}
	
	default String toPropertyBasedString(boolean includeNullValues) throws Exception{
		StringBuilder builder = new StringBuilder();
		Object[] propertyValues = new Object[getPropertyClass().getEnumConstants().length];
		int insertedPropertyValue = 0;
		Collection<E> ignoredPropertiesforPrinting = getIgnoredPropertiesforPrinting();
		for(Object enumConstant: getPropertyClass().getEnumConstants()){
			E enumConstantOfCorrectType = (E) enumConstant;
			if(ignoredPropertiesforPrinting.contains(enumConstantOfCorrectType))
				continue;
			Object propertyValue = getProperty(enumConstantOfCorrectType);
			if(!includeNullValues && propertyValue == null)
				continue;
			builder.append(String.format(toStringBaseFormat, enumConstant));
			propertyValues[insertedPropertyValue++] = propertyValue;
		}
		String toStringFormat = builder.toString();
		return String.format(toStringFormat, propertyValues);
	}

}
