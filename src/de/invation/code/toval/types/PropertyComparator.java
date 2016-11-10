package de.invation.code.toval.types;

import java.util.Comparator;

import de.invation.code.toval.types.ElementWithTypedProperties;

public class PropertyComparator<P extends ElementWithTypedProperties<E>, E extends Enum<E>> implements Comparator<P> {
	
	private static final String FORMAT_INCOMPATIBLE_TYPES = "Unable to compare values of types '%s' and '%s'";

	private E propertyType;
	private boolean descending = true;
	
	public PropertyComparator(E propertyType){
		this.propertyType = propertyType;
	}
	
	public PropertyComparator(E propertyType, boolean descending){
		this.propertyType = propertyType;
		this.descending = descending;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compare(P o1, P o2) {
		Comparable comparable1 = null;
		Comparable comparable2 = null;
		try {
			comparable1 = (Comparable) o1.getProperty(propertyType);
			comparable2 = (Comparable) o2.getProperty(propertyType);
		} catch (Exception e) {}
		
		if(comparable1 == null && comparable2 == null)
			return 0;
		if(comparable1 == null)
			return descending ? 1 : -1;
		if(comparable2 == null)
			return descending ? -1 : 1;
		if(comparable1.getClass() != comparable2.getClass())
			throw new ClassCastException(String.format(FORMAT_INCOMPATIBLE_TYPES, comparable1.getClass().getName(), comparable2.getClass().getName()));
		int compResult = comparable1.compareTo(comparable2);
		return descending ? - compResult : compResult;
	}

}
