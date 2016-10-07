package de.invation.code.toval.properties;

import java.util.Arrays;
import java.util.List;

import de.invation.code.toval.graphic.component.PropertySettingPanel;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public class PropertyCharacteristics {
	
	public static final String DEFAULT_NULL_REPLACEMENT = "";
	
	private static final String FORMAT_EXCEPTION_INVALID_PROPERTY_CHARACTERISTIC = "Invalid property characteristic [%s]: %s";
	private static final String FORMAT_EXCEPTION_INVALID_DEFAULT_VALUE_TYPE = "Invalid default value type of property \"%s\". Expected \"%s\" but got \"%s\"";
	
	private static final String EXCEPTION_NULL_OR_EMPTY_NAME = "Property name is null or empty";
	private static final String EXCEPTION_NULL_OR_EMPTY_DESCRIPTION = "Property description is null or empty";
	private static final String EXCEPTION_NULL_VALUE_TYPE = "Property value type not specified";
	private static final String EXCEPTION_NULL_DEFAULT_VALUE = "Default property value not specified";
	private static final String EXCEPTION_NULL_ENUM_CLASS = "Enum class not specified";
	private static final String EXCEPTION_NULL_LIST_ELEMENT_TYPE = "List element type not specified";
	private static final String EXCEPTION_NULL_LIST_ELEMENT_CLASS = "List element class not specified";
	private static final String EXCEPTION_NULL_NULL_REPLACEMENT = "Null replacement not specified";
	
	
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
	private Boolean isMandatory;
	/**
	 * Defines the fallback value which is used for deriving the string representation of property values when they are <code>null</code>.<br>
	 * This is the case when property values are displayed in graphical components for editing.
	 * @see PropertySettingPanel
	 */
	private String nullReplacement = DEFAULT_NULL_REPLACEMENT;
	
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
	
	public PropertyCharacteristics(String name, String displayName, PropertyValueType valueType, Object defaultValue) {
		this.name = name;
		this.displayName = displayName;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
	}

	public PropertyCharacteristics(String name, String displayName, PropertyValueType valueType, Object defaultValue, boolean isMandatory) {
		this(name, displayName, valueType, defaultValue);
		this.isMandatory = isMandatory;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		if(displayName == null)
			return getName();
		return displayName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public PropertyValueType getValueType() {
		return valueType;
	}
	
	public Class<? extends Enum<?>> getEnumClass() {
		return enumClass;
	}
	
	public void setEnumClass(Class<? extends Enum<?>> enumClass) {
		this.enumClass = enumClass;
	}
	
	public ListPropertyElementType getListElementType() {
		return listElementType;
	}

	public Class<?> getListElementClass() {
		if(listElementType != null && listElementType.containsValueClass())
			return listElementType.getValueClass();
		return listElementClass;
	}

	public void setListElementClass(Class<?> listElementClass) {
		this.listElementClass = listElementClass;
		if(listElementClass.isEnum()){
			setPermittedValues(Arrays.asList(listElementClass.getEnumConstants()));
		}
	}

	public void setListElementType(ListPropertyElementType listElementType) {
		this.listElementType = listElementType;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public Boolean isMandatory() {
		return isMandatory;
	}
	
	public void setNullReplacement(String nullReplacement){
		this.nullReplacement = nullReplacement;
	}
	
	public String getNullReplacement() {
		return nullReplacement;
	}

	public void setPermittedValues(List<Object> permittedValues) {
		this.permittedValues = permittedValues;
	}

	public List<Object> getPermittedValues() {
		return permittedValues;
	}
	
	public boolean isValid(){
		try{
			validate();
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
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
