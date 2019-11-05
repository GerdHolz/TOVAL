package de.invation.code.toval.element;

import de.invation.code.toval.event.AbstractListenerSupport;

public class ElementListenerSupport<I extends IElement<O,E>, O extends Comparable<O>, E extends Enum<E>> extends AbstractListenerSupport<IElementListener<I,O,E>> {

	private static final long serialVersionUID = -1706914072670792326L;
	
	public void notifyNameChange(I element, String oldName, String newName) throws Exception{
		for(IElementListener<I,O,E> listener: listeners)
			listener.nameChanged(element, oldName, newName);
	}
	
	public void notifyIDChange(I element, O oldID, O newID) throws Exception{
		for(IElementListener<I,O,E> listener: listeners)
			listener.idChanged(element, oldID, newID);
	}
	
	public void notifyTypeChange(I element, E oldType, E newType) throws Exception{
		for(IElementListener<I,O,E> listener: listeners)
			listener.typeChanged(element, oldType, newType);
	}

}
