package de.invation.code.toval.element;

import java.util.Collection;

public class TestElementContainer extends ElementContainer<TestElement, Integer, TestElementType> {

	public TestElementContainer() {
		super();
	}

	public TestElementContainer(boolean allowNameDuplicates) {
		super(allowNameDuplicates);
	}

	public TestElementContainer(Collection<TestElement> elements, boolean allowNameDuplicates) {
		super(elements, allowNameDuplicates);
	}

	public TestElementContainer(Collection<TestElement> elements) {
		super(elements);
	}

	
}
