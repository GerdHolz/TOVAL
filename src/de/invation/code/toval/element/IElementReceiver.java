package de.invation.code.toval.element;

public interface IElementReceiver<O extends IElement<?,?>> {

	public void elementFound(O element);
	
}
