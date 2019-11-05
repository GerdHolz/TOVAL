package de.invation.code.toval.element;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ElementUtils {
	
	public static <T extends Comparable<T>, E extends Enum<E>> Set<T> extractIDs(Collection<? extends IElement<T,E>> elements){
		return extractIDs(elements, false);
	}
	
	public static <T extends Comparable<T>, E extends Enum<E>> Set<T> extractIDs(Collection<? extends IElement<T,E>> elements, boolean includeNullValues){
		Set<T> result = new HashSet<>();
		for(IElement<T,E> element: elements){
			if(!element.storesID())
				continue;
			if(!includeNullValues && !element.hasID())
				continue;
			try {
				result.add(element.getID());
			} catch (Exception e) {
				// Cannot occur since element.storesID() is true
			}
		}
		return result;
	}

}
