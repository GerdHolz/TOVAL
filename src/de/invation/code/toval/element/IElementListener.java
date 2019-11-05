package de.invation.code.toval.element;

public interface IElementListener<I extends IElement<O,E>, O extends Comparable<O>, E extends Enum<E>> {
	
	public void nameChanged(I element, String oldName, String newName) throws Exception;
	
	public void typeChanged(I element, E oldType, E newType) throws Exception;
	
	public void idChanged(I element, O oldID, O newID) throws Exception;

}
