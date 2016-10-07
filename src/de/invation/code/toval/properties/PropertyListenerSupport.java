package de.invation.code.toval.properties;

import de.invation.code.toval.event.AbstractListenerSupport;

public class PropertyListenerSupport<P extends Enum<P>> extends AbstractListenerSupport<PropertyListener<P>> {

	private static final long serialVersionUID = 3461562345279510223L;
	
	public void notifyPropertyChange(P property, Object oldValue, Object newValue){
		for(PropertyListener<P> listener: listeners){
			listener.propertyChange(property, oldValue, newValue);
		}
	}

}
