package de.invation.code.toval.properties;

/**
 * This interface defines required methods for handling properties in a typed way 
 * (i.e. defined keys are used for property names, not only strings).<br>
 * Typed property handling is possible by deriving {@link AbstractTypedProperties}.
 * For this, the property type has to be defined explicitly in form of an enum which implements this interface.
 * 
 * @author StockerT
 *
 */
public interface Property {
	
	public PropertyCharacteristics getPropertyCharacteristics();
	
	public void validate(Object value) throws PropertyException;
	
}
