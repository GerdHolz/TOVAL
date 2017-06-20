package de.invation.code.toval.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.invation.code.toval.graphic.dialog.PropertySettingDialog;
import de.invation.code.toval.validate.ParameterException;

/**
 * This class provides functionality to handle properties in a typed way 
 * (i.e. defined keys are used for property names, not only strings).<br>
 * The class requires the class which defines the property type in form of an enum which implements the interface {@link Property}.<br>
 * <br>
 * Properties can be requested and set by providing the property type explicitly.<br>
 * This allows applications to handle properties in a straightforward way by just defining an adequate subclass of {@link AbstractTypedProperties}
 * with the opportunity of an easy integration of graphical dialogs (see {@link PropertySettingDialog}). 
 * 
 * @author Thomas Stocker
 *
 * @param <P> Property type
 */
public abstract class AbstractTypedProperties<P extends Enum<P> & Property> extends AbstractProperties {
	
	private static final String FORMAT_EXCEPTION_PROPERTY_VALUE_FROM_LIST_ELEMENT_STRING = "Cannot extract property value from list element \"%s\". Expected type: %s";
	
	protected PropertyListenerSupport<P> listenerSupport = new PropertyListenerSupport<>();
	
	protected Class<P> propertyClass;
	
	protected AbstractTypedProperties(Class<P> propertyClass) throws Exception {
		super(false);
		setPropertyClass(propertyClass);
		loadDefaultProperties();
	}
	
	protected AbstractTypedProperties(Class<P> propertyClass, String fileName) throws Exception {
		super(false);
		setPropertyClass(propertyClass);
		this.fileName = fileName;
		try {
			load(fileName);
		} catch (IOException e) {
			loadDefaultProperties();
		}
	}
	
	private void setPropertyClass(Class<P> propertyClass) throws Exception {
		for (P property : propertyClass.getEnumConstants()) {
			try {
				property.getPropertyCharacteristics().validate();
			} catch (Exception e) {
				throw new Exception("Invalid property specification", e);
			}
		}
		this.propertyClass = propertyClass;
	}
	
	//------- Property setting -------------------------------------------------------------
	
	public void setProperty(P property, Object newValue) throws PropertyException{
		setProperty(property, newValue, false);
	}
	
	public void setProperty(P property, Object newValue, boolean skipComparisonToOldValue) throws PropertyException{
//		System.out.println("Setting property value: " + property.getPropertyCharacteristics().getName() + " " + newValue);
		
		Object oldValue = null;
		if (!skipComparisonToOldValue) {
			oldValue = getProperty(property);
			if (newValue == null && oldValue == null)
				return;
			if (newValue != null && oldValue != null && newValue.equals(oldValue))
				return;
		}
			
		String newValueAsString = getPropertyValueAsString(property, newValue);
//		System.out.println("Property value as String: " + newValueAsString);
		props.setProperty(property.getPropertyCharacteristics().getName(), newValueAsString);
//		System.out.println("props set: " + props.getProperty(property.getPropertyCharacteristics().getName()));
		if(!skipComparisonToOldValue)
			listenerSupport.notifyPropertyChange(property, oldValue, newValue);
//		System.out.println("listeners notified");
	}
	
	public String getPropertyValueAsString(P property, Object value) throws PropertyException {
		if(value == null)
			return property.getPropertyCharacteristics().getNullReplacement();
		
		try {
			switch(property.getPropertyCharacteristics().getValueType()){
			case STRING:
				return getStringFromObject(value, PrimitivePropertyValueType.STRING);
			case BOOLEAN:
				return getStringFromObject(value, PrimitivePropertyValueType.BOOLEAN);
			case DOUBLE:
				return getStringFromObject(value, PrimitivePropertyValueType.DOUBLE);
			case INTEGER:
				return getStringFromObject(value, PrimitivePropertyValueType.INTEGER);
			case COLOR:
				return getStringFromObject(value, PrimitivePropertyValueType.COLOR);
			case ENUM:
				return value.toString();
			case LIST:
				return super.getCollectionAsString((Collection<?>) value);
			default:
				return value.toString();
			}
		} catch (Exception e) {
			throw new PropertyException(property, value, "Cannot determine string value", e);
		}
	}

	/**
	 * Extracts the value of the given property.<br>
	 * 
	 * @param property The property whose value is requested.
	 * @return The property value or <code>null</code> in case there is no entry for the property.
	 * @note Note, that for mandatory properties an exception will be thrown in case no corresponding entry can be found.
	 * @throws PropertyException in case no entry for a mandatory property can be found.
	 * @see #getPropertyValueFromString(Enum, String)
	 */
	public Object getProperty(P property) throws PropertyException{
//		System.out.println("Getting property value " + property.getPropertyCharacteristics().getName());
		String propertyValueAsString = props.getProperty(property.getPropertyCharacteristics().getName());
//		System.out.println("Property value as string:" + propertyValueAsString);
		if(propertyValueAsString == null){
			if(property.getPropertyCharacteristics().isMandatory())
				throw new PropertyException(property, propertyValueAsString, "No entry for mandatory property");
			return null;
		}
		return getPropertyValueFromString(property, propertyValueAsString);
	}

	/**
	 * Converts the value of a property from string representation to the property specific type.<br>
	 * Target type of the extraction procedure is the property value type specified in {@link PropertyCharacteristics#getValueType()}.<br>
	 * This method makes use of string conversion methods in class {@link AbstractProperties}.<br>
	 * After the conversion the value is validated by applying the validation method which is provided by the property itself. 
	 * (This will detect illegal modifications of properties outside the designated methods).
	 * 
	 * @param property The property whose value is desired.
	 * @param propertyValueAsString The property value in string representation.
	 * @return The property value converted to the expected type.
	 * @throws PropertyException in case the string conversion fails.
	 * @see Property#getPropertyCharacteristics()
	 * @see AbstractProperties#getObjectFromString(String, PrimitivePropertyValueType)
	 * @see AbstractProperties#getEnumFromString(String, Class)
	 * @see Property#validate(Object)
	 */
	@SuppressWarnings("unchecked")
	public Object getPropertyValueFromString(P property, String propertyValueAsString) throws PropertyException {
//		System.out.println("getPropertyValueFromString(" + property.getPropertyCharacteristics().getName() + ", \"" + propertyValueAsString + "\")");
		Object result = null;
//		System.out.println("Value type: " + property.getPropertyCharacteristics().getValueType());
		try {
			switch (property.getPropertyCharacteristics().getValueType()) {
			case STRING:
				result = getObjectFromString(propertyValueAsString, PrimitivePropertyValueType.STRING);
				break;
			case BOOLEAN:
				result = getObjectFromString(propertyValueAsString, PrimitivePropertyValueType.BOOLEAN);
				break;
			case COLOR:
				result = getObjectFromString(propertyValueAsString, PrimitivePropertyValueType.COLOR);
				break;
			case DOUBLE:
				result = getObjectFromString(propertyValueAsString, PrimitivePropertyValueType.DOUBLE);
				break;
			case INTEGER:
				result = getObjectFromString(propertyValueAsString, PrimitivePropertyValueType.INTEGER);
				break;
			case ENUM:
				result = getEnumFromString(propertyValueAsString, property.getPropertyCharacteristics().getEnumClass());
				break;
			case LIST:
				List<String> elementsAsString = extractValues(propertyValueAsString);
				result = new ArrayList<>();
				try {
					for (String elementAsString : elementsAsString) {
						switch (property.getPropertyCharacteristics().getListElementType()) {
						case BOOLEAN:
							((List<Object>) result).add(getObjectFromString(elementAsString, PrimitivePropertyValueType.BOOLEAN));
							break;
						case DOUBLE:
							((List<Object>) result).add(getObjectFromString(elementAsString, PrimitivePropertyValueType.DOUBLE));
							break;
						case INTEGER:
							((List<Object>) result).add(getObjectFromString(elementAsString, PrimitivePropertyValueType.INTEGER));
							break;
						case STRING:
							((List<Object>) result).add(getObjectFromString(elementAsString, PrimitivePropertyValueType.STRING));
							break;
						case ENUM:
							((List<Object>) result).add(getEnumFromString(elementAsString, (Class<? extends Enum>) property.getPropertyCharacteristics().getListElementClass()));
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
					throw new PropertyException(property, propertyValueAsString,
							String.format(FORMAT_EXCEPTION_PROPERTY_VALUE_FROM_LIST_ELEMENT_STRING,
									propertyValueAsString, property.getPropertyCharacteristics().getListElementType()),
							e);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw new PropertyException(property, propertyValueAsString, "Cannot extract property value from string", e);
		}

		if (result != null) {
			try {
				property.validate(result);
			} catch (ParameterException e) {
				throw new PropertyException(property, propertyValueAsString, "Invalid property value", e);
			}
		}
		return result;
	}
	
	public Class<?> getPropertyClass(){
		return propertyClass;
	}
		
	// ------- Default Properties -----------------------------------------------------------
	
	@Override
	protected void loadDefaultProperties() {
		for (P property : propertyClass.getEnumConstants()) {
			try {
				setProperty(property, property.getPropertyCharacteristics().getDefaultValue(), true);
			} catch (PropertyException e) {
				e.printStackTrace();
			}    
		}
	}
	
	public void reset(){
		loadDefaultProperties();
	}
	
	// ------- Listeners --------------------------------------------------------------------
	
	public boolean addListener(PropertyListener<P> listener){
		return listenerSupport.addListener(listener);
	}
	
	public boolean removeListener(PropertyListener<P> listener){
		return listenerSupport.removeListener(listener);
	}

}
