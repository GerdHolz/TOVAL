package de.invation.code.toval.element;

import de.invation.code.toval.element.ElementException.ErrorCode;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public abstract class AbstractElement<O extends Comparable<O>, E extends Enum<E>> implements IElement<O,E> {
	
	protected static final boolean DEFAULT_ACCEPT_NULL_ID = true;
	protected static final boolean DEFAULT_ACCEPT_NULL_NAME = true;
	protected static final boolean DEFAULT_ACCEPT_NULL_TYPE = true;
	protected static final boolean DEFAULT_ACCEPT_EMPTY_NAME = true;
	
	protected static final boolean DEFAULT_DIFFERENTIATE_BY_ID = true;
	protected static final boolean DEFAULT_DIFFERENTIATE_BY_NAME = true;
	protected static final boolean DEFAULT_DIFFERENTIATE_BY_TYPE = true;
	
	protected static final boolean DEFAULT_COMPARE_BY_ID = true;
	protected static final boolean DEFAULT_COMPARE_BY_NAME = true;
	protected static final boolean DEFAULT_COMPARE_BY_TYPE = true;
	
	private static final String toStringformat = "(id: %s, name: %s, type: %s)";
	
	private ElementListenerSupport<IElement<O,E>, O, E> listenerSupport = new ElementListenerSupport<>();
	
	private O ID;
	private String name;
	private E type;
	
	protected AbstractElement() throws Exception {
		super();
	}
	
	public AbstractElement(O ID) throws Exception {
		super();
		setID(ID);
	}
	
	public AbstractElement(O ID, String name) throws Exception {
		super();
		setID(ID);
		setName(name);
	}
	
	public AbstractElement(O ID, E type) throws Exception {
		super();
		setID(ID);
		setType(type);
	}
	
	public AbstractElement(String name, E type) throws Exception {
		super();
		setName(name);
		setType(type);
	}
	
	public AbstractElement(String name) throws Exception {
		super();
		setName(name);
	}
	
	public AbstractElement(O ID, String name, E type) throws Exception {
		super();
		setID(ID);
		setName(name);
		setType(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IElement<O, E>> void addListener(IElementListener<T, O, E> listener) {
		listenerSupport.addListener((IElementListener<IElement<O, E>, O, E>) listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IElement<O, E>> void removeListener(IElementListener<T, O, E> listener) {
		listenerSupport.removeListener((IElementListener<IElement<O, E>, O, E>) listener);
	}

	@Override
	public O getID(){
		if(!storesID())
			throw new ElementException(ErrorCode.ELEMENT_DOES_NOT_STORE_ID);
		return this.ID;
	}
	
	@Override
	public String getName(){
		if(!storesName())
			throw new ElementException(ErrorCode.ELEMENT_DOES_NOT_STORE_NAME);
		return this.name;
	}
	
	@Override
	public E getType() {
		if(!storesType())
			throw new ElementException(ErrorCode.ELEMENT_DOES_NOT_STORE_TYPE);
		return type;
	}
	
	@Override
	public void setID(O ID) throws Exception {
		if(!storesID())
			throw new ElementException(ErrorCode.ELEMENT_DOES_NOT_STORE_ID);
		validateID(ID);
		O oldID = getID();
		this.ID = ID;
		listenerSupport.notifyIDChange(this, oldID, ID);
	}

	@Override
	public void setName(String name) throws Exception {
		if(!storesName())
			throw new Exception("This element does not store name information");
		validateName(name);
		String oldName = getName();
		this.name = name;
		listenerSupport.notifyNameChange(this, oldName, name);
	}
	
	@Override
	public void setType(E type) throws Exception {
		if(!storesType())
			throw new Exception("This element does not store type information");
		validateType(type);
		E oldType = getType();
		this.type = type;
		listenerSupport.notifyTypeChange(this, oldType, type);
	}
	
	public abstract boolean acceptsNullID();

	public abstract boolean acceptsNullName();
	
	public abstract boolean acceptsNullType();

	public abstract boolean acceptsEmptyName();
	
	public abstract boolean storesID();
	
	public abstract boolean storesName();
	
	public abstract boolean storesType();

	/**
	 * Indicates whether ID information is included in the hashCode() method.
	 */
	public boolean isDifferentiatedByID() {
		return DEFAULT_DIFFERENTIATE_BY_ID;
	}

	/**
	 * Indicates whether name information is included in the hashCode() method.
	 */
	public boolean isDifferentiatedByName() {
		return DEFAULT_DIFFERENTIATE_BY_NAME;
	}

	/**
	 * Indicates whether type information is included in the hashCode() method.
	 */
	public boolean isDifferentiatedByType() {
		return DEFAULT_DIFFERENTIATE_BY_TYPE;
	}

	/**
	 * Indicates whether ID information is included in the equals(Object) method.
	 */
	public boolean isComparedByID() {
		return DEFAULT_COMPARE_BY_ID;
	}

	/**
	 * Indicates whether name information is included in the equals(Object) method.
	 */
	public boolean isComparedByName() {
		return DEFAULT_COMPARE_BY_NAME;
	}

	/**
	 * Indicates whether type information is included in the equals(Object) method.
	 */
	public boolean isComparedByType() {
		return DEFAULT_COMPARE_BY_TYPE;
	}
	
	protected void validateName() throws Exception{
		validateName(getName());
	}
	
	protected void validateName(String name) throws Exception{
		try {
			if (!acceptsNullName())
				Validate.notNull(name, "Element does not accept NULL name");
			if (!acceptsEmptyName())
				Validate.notEmpty(name, "Element does not accept empty name");
		} catch (ParameterException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected void validateID() throws Exception{
		validateID(getID());
	}
	
	protected void validateID(O ID) throws Exception{
		try {
			if (!acceptsNullID())
				Validate.notNull(ID, "Element does not accept NULL ID");
		} catch (ParameterException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected void validateType() throws Exception{
		validateType(getType());
	}
	
	protected void validateType(E type) throws Exception{
		try {
			if (!acceptsNullType())
				Validate.notNull(type, "Element does not accept NULL type");
		} catch (ParameterException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void validate() throws Exception{
		if(storesType())
			validateType();
		if(storesID())
			validateID();
		if(storesName())
			validateName();
	}

	@Override
	public String toString(){
		try {
			return String.format(toStringformat, getID(), getName(), getType());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		try{
			if(isDifferentiatedByID() && storesID() && hasID())
				result = prime * result + ((getID() == null) ? 0 : getID().hashCode());
		} catch(Exception e){}
		try{
			if(isDifferentiatedByName() && storesName() && hasName())
				result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		} catch(Exception e){}
		try{
			if(isDifferentiatedByType() && storesType() && hasType())
				result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		} catch(Exception e){}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractElement<?,?> other = (AbstractElement<?,?>) obj;
		
		if(isComparedByType()){
			try{
				if (!hasType()) {
					if (other.hasType())
						return false;
				} else if (!getType().equals(other.getType()))
					return false;
			} catch(Exception e){}
		}
		if(isComparedByID()){
			try{
				if (!hasID()) {
					if (other.hasID())
						return false;
				} else if (!getID().equals(other.getID()))
					return false;
			} catch(Exception e){}
		}
		if(isComparedByName()){
			try {
				if (!hasName()) {
					if (other.hasName())
						return false;
				} else if (!getName().equals(other.getName()))
					return false;
			} catch(Exception e){}
		}
		return true;
	}
	
	public static enum VoidEnum {}
	
}
