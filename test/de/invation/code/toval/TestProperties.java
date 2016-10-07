package de.invation.code.toval;

import de.invation.code.toval.properties.AbstractTypedProperties;

public class TestProperties extends AbstractTypedProperties<TestProperty>{

	protected TestProperties(Class<TestProperty> propertyClass) throws Exception {
		super(propertyClass);
	}

	public TestProperties(Class<TestProperty> propertyClass, String fileName) throws Exception {
		super(propertyClass, fileName);
	}

}
