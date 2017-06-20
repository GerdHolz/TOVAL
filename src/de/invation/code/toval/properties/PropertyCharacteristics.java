package de.invation.code.toval.properties;

import java.util.Arrays;
import java.util.List;

import de.invation.code.toval.graphic.component.PropertySettingPanel;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

/**
 * This class provides a characterization of properties.<br>
 * A property is defined by the following information:
 * <ul>
 * <li>A unique name</li>
 * <li>A property name for display</li>
 * <li>A detailed description of the property</li>
 * <li>The property value type</li>
 * <li>A default property value</li>
 * <li>A flag indicating whether the property is mandatory or optional</li>
 * <li>A <code>null</code> replacement string for display</li>
 * <li>A list of permitted property values</li>
 * </ul>
 * 
 * @author Thomas Stocker
 */
public class PropertyCharacteristics {
	
	public 	static final boolean 	DEFAULT_ERROR_HANDLING_PROPERTY						= false;
	public 	static final boolean 	DEFAULT_MANDATORY_STATE 							= false;
	public 	static final String 	DEFAULT_NULL_REPLACEMENT 							= "";
	
	private static final String 	FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC 	= "Invalid property characteristic [%s]: %s";
	private static final String 	FORMAT_EXCEPTION_INVALID_DEFAULT_VALUE_TYPE		 	= "Invalid default value type of property \"%s\". Expected \"%s\" but got \"%s\"";
	
	private static final String 	EXCEPTION_NULL_OR_EMPTY_NAME 						= "Property name is null or empty";
	private static final String 	EXCEPTION_NULL_VALUE_TYPE 							= "Property value type not specified";
	private static final String 	EXCEPTION_NULL_DEFAULT_VALUE 						= "Default property value not specified";
	private static final String 	EXCEPTION_NULL_ENUM_CLASS 							= "Enum class not specified";
	private static final String 	EXCEPTION_NULL_LIST_ELEMENT_TYPE 					= "List element type not specified";
	private static final String 	EXCEPTION_NULL_LIST_ELEMENT_CLASS 					= "List element class not specified";
	private static final String 	EXCEPTION_NULL_NULL_REPLACEMENT 					= "Null replacement not specified";
	
	
	
	/**
	 * Property name (i.e. the enum constant).
	 */
	private String name;
	/**
	 * Property name in readable form.
	 */
	private String displayName;
	/**
	 * Property description.
	 */
	private String description;
	/**
	 * Type of property value.
	 * @see PropertyValueType
	 */
	private PropertyValueType valueType;
	/**
	 * Default property value.
	 */
	private Object defaultValue;
	/**
	 * Flag for marking property as mandatory (i.e. is required to exist when property files are read and is always stored in property files).<br>
	 * For non existing mandatory properties in properties files, exceptions are thrown.
	 */
	private Boolean isMandatory = DEFAULT_MANDATORY_STATE;
	/**
	 * Defines the fallback value which is used for deriving the string representation of property values when they are <code>null</code>.<br>
	 * This is the case when property values are displayed in graphical components for editing.
	 * @see PropertySettingPanel
	 */
	private String nullReplacement = DEFAULT_NULL_REPLACEMENT;
	/**
	 * Flag for marking property as error handling property (i.e. refers to error handling).
	 */
	private Boolean isErrorHandlingProperty = DEFAULT_ERROR_HANDLING_PROPERTY;
	/**
	 * Defines the enum class of a property with value type {@link PropertyValueType#ENUM}.<br>
	 * @note Only required when property is of type {@link PropertyValueType#ENUM}.
	 */
	private Class<? extends Enum<?>> enumClass;
	/**
	 * Defines the type of list elements of properties with value type {@link PropertyValueType#LIST}.<br>
	 * This type is used by graphical components to decide on the adequacy of string conversion methods. 
	 * @note Only required when property is of type {@link PropertyValueType#LIST}.
	 * @see ListPropertyElementType
	 */
	private ListPropertyElementType listElementType;
	/**
	 * Defines the class of list elements of properties with value type {@link PropertyValueType#LIST}.<br>
	 * This type is used by graphical components to decide on the adequacy of string conversion methods.<br>
	 * With this information, properties which contain a list of enum constants can be handled also
	 * (in this case the concrete enum class is required and {@link #listElementType} is not sufficient for conversion). 
	 * @note Only required when property is of type {@link PropertyValueType#LIST}.
	 */
	private Class<?> listElementClass;
	/**
	 * Defines permitted values of a property with value type {@link PropertyValueType#LIST}.<br>
	 * This information is used by graphical components ({@link PropertySettingPanel}) to restrict input to permitted values.<b>
	 * A <code>null</code> value is interpreted as no restriction.
	 * @note Only required when property is of type {@link PropertyValueType#LIST}.
	 * @see PropertySettingPanel
	 */
	private List<Object> permittedValues;
	
	/**
	 * Creates new property characteristics.
	 * @param name The (unique) property name.
	 * @param displayName A string to use when the property is displayed (name replacement).
	 * @param valueType The type of property values.
	 * @param defaultValue The default property value.
	 */
	public PropertyCharacteristics(String name, String displayName, PropertyValueType valueType, Object defaultValue) {
		this.name = name;
		this.displayName = displayName;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
	}

	/**
	 * Creates new property characteristics.
	 * @param name The (unique) property name.
	 * @param displayName A string to use when the property is displayed (name replacement).
	 * @param valueType The type of property values.
	 * @param defaultValue The default property value.
	 * @param isMandatory flag indicating whether this property is mandatory.
	 */
	public PropertyCharacteristics(String name, String displayName, PropertyValueType valueType, Object defaultValue, boolean isMandatory) {
		this(name, displayName, valueType, defaultValue);
		this.isMandatory = isMandatory;
	}
	
	/**
	 * Returns the (unique) property name.
	 * @return The property name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the display name, i.e. the name which is displayed instead of the properties' name.<br>
	 * @return The display name in case it is not <code>null</code>;
	 * the property name otherwise.
	 */
	public String getDisplayName() {
		if(displayName == null)
			return getName();
		return displayName;
	}

	/**
	 * Sets the property description, i.e. a string describing the property in more detail.
	 * @param description The property description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns a string which describes the property in more detail.
	 * @return The property description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the type of property values.
	 * @see PropertyValueType
	 * @return The type of property values.
	 */
	public PropertyValueType getValueType() {
		return valueType;
	}
	
	/**
	 * Returns the class of enum property values.
	 * @return The enum class of property value.
	 */
	public Class<? extends Enum<?>> getEnumClass() {
		return enumClass;
	}
	
	/**
	 * Sets the enum class when the property value type is set to {@link PropertyValueType#ENUM}.
	 * @param enumClass The enum class to set for property values.
	 */
	public void setEnumClass(Class<? extends Enum<?>> enumClass) {
		this.enumClass = enumClass;
	}

	/**
	 * Sets the class of list elements when the property value is actually a list of elements.<br>
	 * This method only returns the list element class which has been set by {@link #setListElementClass(Class)} 
	 * when the list element type does not contain a value class. This is only the case with enum list element types.
	 * @return The type (class) of list elements.
	 */
	public Class<?> getListElementClass() {
		if(listElementType != null && listElementType.containsValueClass())
			return listElementType.getValueClass();
		return listElementClass;
	}

	/**
	 * Sets the class of list elements when the property value is actually a list of elements.<br>
	 * The only usage of this method which makes sense is, when property value is actually a list of enum constants. 
	 * In this case the element type (enum class name) has to be set explicitly.
	 * In all other cases the class name of list elements is implicitly defined by the list element type ({@link ListPropertyElementType}).<br>
	 * For enum classes, permitted values are automatically set to the enums' values.
	 * @param listElementClass Type (class) of list elements
	 */
	public void setListElementClass(Class<?> listElementClass) {
		this.listElementClass = listElementClass;
		if(listElementClass.isEnum()){
			setPermittedValues(Arrays.asList(listElementClass.getEnumConstants()));
		}
	}

	/**
	 * Returns the type of list elements when the property value is actually a list of elements.
	 * @return The type of list elements.
	 * @see ListPropertyElementType
	 */
	public ListPropertyElementType getListElementType() {
		return listElementType;
	}
	
	/**
	 * Sets the type of list elements when the property value is actually a list of elements.
	 * @param listElementType The type of list elements.
	 * @see ListPropertyElementType
	 */
	public void setListElementType(ListPropertyElementType listElementType) {
		this.listElementType = listElementType;
	}

	/**
	 * Returns the properties' default value.
	 * @return The default value when the property value is not set explicitly.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Indicates whether the property is mandatory or optional.
	 * @return <code>true</code> when the property is mandatory;<br>
	 * <code>false</code> otherwise.
	 */
	public Boolean isErrorHandlingProperty() {
		return isErrorHandlingProperty;
	}
	
	/**
	 * Indicates whether the property refers to error handling.
	 * @return <code>true</code> when the property is an error handling property;<br>
	 * <code>false</code> otherwise.
	 */
	public Boolean isMandatory() {
		return isMandatory;
	}
	
	/**
	 * Sets the string which should be used instead of <code>null</code> when property values are displayed.
	 * @param nullReplacement The <code>null</code> replacement string.
	 */
	public void setNullReplacement(String nullReplacement){
		this.nullReplacement = nullReplacement;
	}
	
	/**
	 * Returns the string which should be used instead of <code>null</code> when property values are displayed.
	 * @return The <code>null</code> replacement string.
	 */
	public String getNullReplacement() {
		return nullReplacement;
	}

	/**
	 * Sets permitted property values.
	 * @param permittedValues Permitted property values.
	 */
	public void setPermittedValues(List<Object> permittedValues) {
		this.permittedValues = permittedValues;
	}

	/**
	 * Returns a list of permitted property values.<br>
	 * In case this method returns <code>null</code>, there are no restrictions defined.
	 */
	public List<Object> getPermittedValues() {
		return permittedValues;
	}
	
	/**
	 * Checks the validity of the property characteristics.
	 * @see #validate()
	 * @return <code>true</code> when this property characteristics are valid;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean isValid(){
		try{
			validate();
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Checks the validity of the property characteristics.<br>
	 * This is the case when...
	 * <ul>
	 * <li>...no name is set.</li>
	 * <li>...no type is set.</li>
	 * <li>...the <code>null</code> replacement string is explicitly set to <code>null</code>.</li>
	 * <li>...no default value has been set.</li>
	 * <li>...the default value is of wrong type.</li>
	 * <li>...for enum property value types no enum class is set.</li>
	 * <li>...for list property value types no list element class is set.</li>
	 * <li>...for list property value types with enum element type no enum class is set.</li>
	 * </ul>
	 * @throws Exception
	 */
	public void validate() throws Exception{
		try {
			Validate.notNull(getName());
			Validate.notEmpty(getName());
		} catch(ParameterException e){
			throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_OR_EMPTY_NAME), e);
		}
		try {
			Validate.notNull(getValueType());
		} catch(ParameterException e){
			throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_VALUE_TYPE), e);
		}
		try {
			Validate.notNull(getNullReplacement());
		} catch(ParameterException e){
			throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_NULL_REPLACEMENT), e);
		}
		try {
			Validate.notNull(getDefaultValue());
		} catch(ParameterException e){
			throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_DEFAULT_VALUE), e);
		}
		if(!getValueType().getValueClass().isAssignableFrom(getDefaultValue().getClass()))
			throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_DEFAULT_VALUE_TYPE, getName(), valueType.getValueClass().getSimpleName(), defaultValue.getClass().getSimpleName()));
		if(getValueType() == PropertyValueType.ENUM){
			try {
				Validate.notNull(getEnumClass());
			} catch(ParameterException e){
				throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_ENUM_CLASS), e);
			}
		} else if(getValueType() == PropertyValueType.LIST){
			try {
				Validate.notNull(getListElementType());
			} catch(ParameterException e){
				throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_LIST_ELEMENT_TYPE), e);
			}
			if(getListElementType() == ListPropertyElementType.ENUM){
				try {
					Validate.notNull(getListElementClass());
				} catch(ParameterException e){
					throw new Exception(String.format(FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC, getName(), EXCEPTION_NULL_LIST_ELEMENT_CLASS), e);
				}
			}
		}
	}

}
