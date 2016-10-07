package de.invation.code.toval.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractListenerSupport<L extends Object> implements Serializable {

	private static final long serialVersionUID = -7522608459092207941L;
	
	protected HashSet<L> listeners = new HashSet<>();
	
	public void addListeners(Collection<L> listeners) {
		Validate.notNull(listeners);
		for(L listener: listeners)
			addListener(listener);
	}
	
	public boolean addListener(L listener) {
		Validate.notNull(listener);
		return listeners.add(listener);
	}
	
	public boolean removeListener(L listener) {
		Validate.notNull(listener);
		return listeners.remove(listener);
	}
	
	public void removeListeners(){
		listeners.clear();
	}
	
	public void removeListeners(Class<?> listenerClass){
		List<L> listenersToRemove = new ArrayList<>();
		for(L listener: listeners){
			if(listener.getClass().equals(listenerClass))
				listenersToRemove.add(listener);
		}
		listeners.removeAll(listenersToRemove);
	}
	
	public Set<L> getListeners(){
		return Collections.unmodifiableSet(listeners);
	}

}
