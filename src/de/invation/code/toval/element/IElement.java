package de.invation.code.toval.element;

/**
 * 
 * @author StockerT
 *
 * @param <O> Type of IDs (e.g. Int or String)
 */
public interface IElement<O extends Comparable<O>, E extends Enum<E>> extends Comparable<Object> {
	
	default boolean hasID(){
		if(!storesID())
			return false;
		try {
			return getID() != null;
		} catch (Exception e) {
			// Cannot occur
			return false;
		}
	}
	
	default boolean hasName(){
		if(!storesName())
			return false;
		try {
			return getName() != null;
		} catch (Exception e) {
			// Cannot occur
			return false;
		}
	}
	
	default boolean hasType(){
		if(!storesType())
			return false;
		try {
			return getType() != null;
		} catch (Exception e) {
			// Cannot occur
			return false;
		}
	}
	
	public <T extends IElement<O,E>> void addListener(IElementListener<T, O, E> listener);
	
	public <T extends IElement<O,E>> void removeListener(IElementListener<T, O, E> listener);
	
	public boolean storesID();
	
	public boolean storesName();
	
	public boolean storesType();
	
	public O getID() throws Exception;
	
	public String getName() throws Exception;
	
	public E getType() throws Exception;
	
	/**
	 * Must notify all listeners about ID change.
	 * @param ID
	 * @throws Exception
	 */
	public void setID(O ID) throws Exception;
	
	/**
	 * Must notify all listeners about name change
	 * @param name
	 * @throws Exception
	 */
	public void setName(String name) throws Exception;
	
	/**
	 * Must notify all listeners about type change
	 * @param type
	 * @throws Exception
	 */
	public void setType(E type) throws Exception;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	default int compareTo(Object o) {
		if(o == null)
			return 0;
		if(!getClass().isAssignableFrom(o.getClass()))
			return 0;
		IElement<?,?> oCasted = (IElement<?,?>) o;
		
		boolean nameComparisonPossible = hasName() || oCasted.hasName();
		boolean idComparisonPossible = hasID() || oCasted.hasID();
		boolean typeComparisonPossible = hasType() || oCasted.hasType();

		int result = 0;
		try {
			if(result == 0 && typeComparisonPossible){
				if(storesType() && oCasted.storesType() && hasType() && oCasted.hasType()){
					if(getType().getClass().equals(oCasted.getType().getClass())){
						result = getType().compareTo((E) oCasted.getType());
					}
				} else {
					result = oCasted.hasType() ? 1 : -1;
				}
			}
			if(result == 0 && nameComparisonPossible){
				if(storesName() && oCasted.storesName() && hasName() && oCasted.hasName()){
					result = getName().compareTo(oCasted.getName());
				} else {
					result = oCasted.hasName() ? 1 : -1;
				}
			} 
			if(result == 0 && idComparisonPossible){
				if(hasID() && oCasted.hasID()){
					if(storesID() && oCasted.storesID() && getID().getClass().equals(oCasted.getID().getClass())){
						result = ((Comparable) this).compareTo((Comparable) oCasted);
					}
				} else {
					result = oCasted.hasID() ? 1 : -1;
				}
			}
		} catch(Exception e){}
		return result;
	}

}
