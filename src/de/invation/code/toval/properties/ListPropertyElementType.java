package de.invation.code.toval.properties;

public enum ListPropertyElementType {
	
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	BOOLEAN(Boolean.class),
	STRING(String.class),
	ENUM();
	
	private Class<?> valueClass;
	
	private ListPropertyElementType(){}
	
	private ListPropertyElementType(Class<?> valueClass){
		this.valueClass = valueClass;
	}
	
	public boolean containsValueClass(){
		return valueClass != null;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

}
