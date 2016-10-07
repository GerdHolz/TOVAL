package de.invation.code.toval.properties;

import java.awt.Color;
import java.util.List;

public enum PropertyValueType {
	
	STRING(String.class),
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	BOOLEAN(Boolean.class),
	ENUM(Enum.class),
	COLOR(Color.class),
	LIST(List.class);
	
	private Class<?> valueClass;
	
	private PropertyValueType(){}
	
	private PropertyValueType(Class<?> valueClass){
		this.valueClass = valueClass;
	}
	
	public boolean containsValueClass(){
		return valueClass != null;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

}
