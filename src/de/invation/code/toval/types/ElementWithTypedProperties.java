package de.invation.code.toval.types;

public interface ElementWithTypedProperties<E extends Enum<E>> {
	
	public void setProperty(E propertyType, Object property) throws Exception;
	
	public Object getProperty(E propertyType);
	
	public <T extends ElementWithTypedProperties<E>> PropertyComparator<T,E> getComparator(E propertyType);

}
