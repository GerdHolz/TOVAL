package de.invation.code.toval.properties;

/**
 * This interface defines required methods for handling properties in a typed way 
 * (i.e. defined keys are used for property names, not only strings).<br>
 * Typed property handling is possible by deriving {@link AbstractTypedProperties}.
 * For this, the property type has to be defined explicitly in form of an <b>enum</b> which implements this interface.<br>
 * <br>
 * A property in this setting is defined by property characteristics ({@link PropertyCharacteristics}), 
 * which define name, description, default value, etc. and allows to validate concrete property values.
 * 
 * @author Thomas Stocker
 */
public interface Property {
	
	/**
	 * Returns the property characteristics such as name, description, default value, etc.
	 * @return concrete property characterization
	 */
	public PropertyCharacteristics getPropertyCharacteristics();
	
	/**
	 * Validates the given property value.
	 * @param value The property value to validate.
	 * @throws PropertyException In case the given value is not accepted.
	 */
	public void validate(Object value) throws PropertyException;
	
}
