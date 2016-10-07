package de.invation.code.toval.properties;

import java.awt.Color;

public enum PrimitivePropertyValueType {
	
	STRING(String.class),
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	BOOLEAN(Boolean.class),
	COLOR(Color.class);
	
	private Class<?> valueClass;
	
	private PrimitivePropertyValueType(){}
	
	private PrimitivePropertyValueType(Class<?> valueClass){
		this.valueClass = valueClass;
	}
	
	public boolean containsValueClass(){
		return valueClass != null;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

}
