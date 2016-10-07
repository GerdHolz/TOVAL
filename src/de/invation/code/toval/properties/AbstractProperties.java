package de.invation.code.toval.properties;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.misc.StringUtils;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;

public class AbstractProperties {

	public static final String COLLECTION_VALUES_QUOTE_CHAR = "'";

	private static final String FORMAT_EXCEPTION_PROPERTY_VALUE_FROM_STRING = "Cannot extract property value from String \"%s\". Expected type: %s";
	private static final String FORMAT_EXCEPTION_PROPERTY_VALUE_TO_STRING = "Cannot convert property value (class: %s) to String. Expected type: %s";

	private static final boolean DEFAULT_STORE_FILE_NAME = false;
	private static final String FORMAT_COLLECTION_ENCAPSULATION = COLLECTION_VALUES_QUOTE_CHAR + "%s"
			+ COLLECTION_VALUES_QUOTE_CHAR;

	protected Properties props;

	protected String fileName;

	public AbstractProperties(String fileName) throws IOException {
		load(fileName);
	}

	public AbstractProperties() {
		this(true);
	}

	public AbstractProperties(boolean loadDefaultProperties) {
		if (loadDefaultProperties){
			loadDefaultProperties();
		} else {
			props = new Properties();
		}
	}

	protected AbstractProperties(Properties properties) {
		setProperties(properties);
	}

	public Properties getProperties() {
		return props;
	}
	
	public boolean containsFileName(){
		return fileName != null;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void store() throws Exception {
		if (getFileName() == null)
			throw new Exception("File name not set.");
		if (getFileName().isEmpty())
			throw new Exception("Empty file name.");
		store(getFileName());
	}

	public void store(String filename) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(filename)) {
			props.store(fos, "");
		} catch (Exception e) {
			throw new IOException("Cannot store properties file to disk.", e);
		}
	}

	public final void load(String fileName) throws IOException {
		load(fileName, DEFAULT_STORE_FILE_NAME);
	}

	public final void load(String fileName, boolean storeFileName) throws IOException {
		props = new Properties();
		try (FileInputStream fis = new FileInputStream(fileName)) {
			props.load(fis);
			if (storeFileName) {
				setFileName(fileName);
			}
		}
	}

	public final void setProperties(Properties properties) {
		this.props = properties;
	}

	protected void loadDefaultProperties() {
		props = getDefaultProperties();
	}

	protected Properties getDefaultProperties() {
		return new Properties();
	}

	// ------- Helper methods
	// --------------------------------------------------------------

	protected Object getObjectFromString(String propertyValueAsString, PrimitivePropertyValueType valueType)
			throws Exception {
		try {
			switch (valueType) {
			case BOOLEAN:
				return Boolean.parseBoolean(propertyValueAsString);
			case COLOR:
				if (propertyValueAsString.isEmpty())
					return null;
				return Color.decode(propertyValueAsString);
			case DOUBLE:
				return Double.valueOf(propertyValueAsString);
			case INTEGER:
				return Integer.valueOf(propertyValueAsString);
			case STRING:
				return propertyValueAsString;
			default:
				return null;
			}
		} catch (Exception e) {
			throw new Exception(String.format(FORMAT_EXCEPTION_PROPERTY_VALUE_FROM_STRING, propertyValueAsString, valueType), e);
		}
	}
	
	protected String getStringFromObject(Object propertyValue, PrimitivePropertyValueType valueType)
			throws Exception {
		try {
			switch (valueType) {
			case STRING:
			case BOOLEAN:
			case DOUBLE:
			case INTEGER:
				return propertyValue.toString();
			case COLOR:
				return Integer.valueOf(((Color) propertyValue).getRGB()).toString();
			default:
				return propertyValue.toString();
			}
		} catch (Exception e) {
			throw new Exception(String.format(FORMAT_EXCEPTION_PROPERTY_VALUE_FROM_STRING, propertyValue.getClass().getSimpleName(), valueType), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Enum getEnumFromString(String propertyValueAsString, Class<? extends Enum> enumClass) {
		return Enum.valueOf(enumClass, propertyValueAsString);
	}

	protected List<String> extractValues(String collString) {
		return StringUtils.splitArrayStringQuoted(collString);
	}

	protected <T extends Object> String getCollectionAsString(Collection<T> values) {
		return ArrayUtils.toString(encapsulateValues(values));
	}

	protected <T extends Object> String[] encapsulateValues(Collection<T> values) {
		String[] result = new String[values.size()];
		int count = 0;
		for (Object value : values) {
			result[count++] = String.format(FORMAT_COLLECTION_ENCAPSULATION, value.toString());
		}
		return result;
	}

	// ------- Validation
	// -------------------------------------------------------------------
	public static void validateStringValue(String value) throws ParameterException {
		Validate.notNull(value);
		Validate.notEmpty(value);
	}

	public static void validatePath(String logPath) throws ParameterException {
		validateStringValue(logPath);
		File cPath = new File(logPath);
		if (!cPath.isDirectory()) {
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, logPath + " is not a valid path!");
		}
	}

	public static void validateStringCollection(Collection<String> coll) throws ParameterException {
		Validate.notNull(coll);
		Validate.notEmpty(coll);
		for (String string : coll) {
			validateStringValue(string);
		}
	}
}
