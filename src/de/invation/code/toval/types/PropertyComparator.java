package de.invation.code.toval.types;

import java.util.Comparator;

import de.invation.code.toval.types.ElementWithTypedProperties;

public abstract class PropertyComparator<P extends ElementWithTypedProperties<E>, E extends Enum<E>> implements Comparator<P> {

	private E property;
	
	public PropertyComparator(E property){
		this.property = property;
	}
	
	@Override
	public int compare(P o1, P o2) {
		if(o1.getProperty(property) == null && o2.getProperty(property) == null)
			return 0;
		if(o1.getProperty(property) == null)
			return -1;
		if(o2.getProperty(property) == null)
			return 1;
		return compareInternal(o1, o2);
	}
	
	protected abstract int compareInternal(P o1, P o2);

}
