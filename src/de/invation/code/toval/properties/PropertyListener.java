package de.invation.code.toval.properties;

public interface PropertyListener<P extends Enum<P>> {
	
	public void propertyChange(P property, Object oldValue, Object newValue);

}
