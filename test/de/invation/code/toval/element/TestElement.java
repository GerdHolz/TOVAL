package de.invation.code.toval.element;

public class TestElement extends AbstractElement<Integer,TestElementType> {

	public TestElement() throws Exception {
		super();
	}

	public TestElement(Integer ID, String name, TestElementType type) throws Exception {
		super(ID, name, type);
	}

	public TestElement(Integer ID, String name) throws Exception {
		super(ID, name);
	}

	public TestElement(Integer ID, TestElementType type) throws Exception {
		super(ID, type);
	}

	public TestElement(Integer ID) throws Exception {
		super(ID);
	}

	public TestElement(String name, TestElementType type) throws Exception {
		super(name, type);
	}

	public TestElement(String name) throws Exception {
		super(name);
	}

	@Override
	public boolean acceptsNullID() {
		return false;
	}

	@Override
	public boolean acceptsNullName() {
		return false;
	}

	@Override
	public boolean acceptsNullType() {
		return false;
	}

	@Override
	public boolean acceptsEmptyName() {
		return true;
	}

	@Override
	public boolean storesID() {
		return true;
	}

	@Override
	public boolean storesName() {
		return true;
	}

	@Override
	public boolean storesType() {
		return true;
	}
	
	

}
