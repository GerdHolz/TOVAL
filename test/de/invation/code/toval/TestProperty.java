package de.invation.code.toval;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import de.invation.code.toval.properties.ListPropertyElementType;
import de.invation.code.toval.properties.Property;
import de.invation.code.toval.properties.PropertyCharacteristics;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.properties.PropertyValueType;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public enum TestProperty implements Property {
	
	TYPE01_ENUM		("Testtype (Enum)", 		PropertyValueType.ENUM, 	TestEnum.ENUMVAL02, 				false),
	TYPE02_INTEGER	("Testtype (Integer)", 		PropertyValueType.INTEGER, 	42, 								false),
	TYPE03_DOUBLE	("Testtype (Double)", 		PropertyValueType.DOUBLE, 	36.4, 								false),
	TYPE04_BOOLEAN	("Testtype (Boolean)", 		PropertyValueType.BOOLEAN, 	true, 								false),
	TYPE05_COLOR	("Testtype (Color)", 		PropertyValueType.COLOR, 	Color.green, 						false),
	TYPE06_LIST		("Testtype (List-enum)", 	PropertyValueType.LIST, 	Arrays.asList(TestEnum.ENUMVAL01),	true),
	TYPE07_LIST		("Testtype (List-String)", 	PropertyValueType.LIST, 	new ArrayList<String>(),			true),
	TYPE08_LIST		("Testtype (List-Integer)", PropertyValueType.LIST, 	new ArrayList<Integer>(),			true),
	TYPE09_STRING	("Testtype (String)", 		PropertyValueType.STRING, 	"", 								true);
	
	static {
		TYPE01_ENUM.getPropertyCharacteristics().setEnumClass(TestEnum.class);
		TYPE06_LIST.getPropertyCharacteristics().setListElementType(ListPropertyElementType.ENUM);
		TYPE06_LIST.getPropertyCharacteristics().setListElementClass(TestEnum.class);
		TYPE07_LIST.getPropertyCharacteristics().setListElementType(ListPropertyElementType.STRING);
		TYPE08_LIST.getPropertyCharacteristics().setListElementType(ListPropertyElementType.INTEGER);
	}
	
	private static final String FORMAT_INVALID_VALUE = "Value restriction violated: %s";
	
	private PropertyCharacteristics propertyCharacteristics;

	private TestProperty(String description, PropertyValueType valueType, Object defaultValue, boolean acceptsNull) {
		this.propertyCharacteristics = new PropertyCharacteristics(toString(), description, valueType, defaultValue, acceptsNull);
	}
	
	@Override
	public PropertyCharacteristics getPropertyCharacteristics() {
		return propertyCharacteristics;
	}

	@Override
	public void validate(Object value) throws PropertyException {
		System.out.println("Validate: " + value + " (Type: " + value.getClass().getSimpleName() + ")");
		String restriction = null;
		try {
			switch (this) {
			case TYPE01_ENUM:
				Validate.notNull(value);
				break;
			case TYPE02_INTEGER:
				restriction = ">=20";
				Validate.bigger((Integer) value, 20);
				break;
			default:
				break;
			}
		} catch (ParameterException e) {
			throw new PropertyException(this, value, String.format(FORMAT_INVALID_VALUE, restriction));
		}
	}

}
