package de.invation.code.toval.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ElementContainer<O extends IElement<T,E>, T extends Comparable<T>, E extends Enum<E>> 
implements IElementListener<O,T,E>, IElementReceiver<O> {
	
	public static final boolean DEFAULT_ALLOW_NAME_DUPLICATES = true;
	private static final boolean DEFAULT_ALLOW_ELEMENT_DUPLICATES = false;
	
	protected List<O>						elementList			= new ArrayList<>();
	protected Map<T,O> 						elementMapID 		= new HashMap<>();
	protected Map<String,List<O>> 			elementMapName 		= new HashMap<>();
	protected Map<E,Map<T,O>> 				elementTypeMapID 	= new HashMap<>();
	protected Map<E,Map<String,List<O>>> 	elementTypeMapName 	= new HashMap<>();
	
	private boolean allowNameDuplicates = DEFAULT_ALLOW_NAME_DUPLICATES;
	private boolean allowElementDuplicates = DEFAULT_ALLOW_ELEMENT_DUPLICATES;
	
	public ElementContainer() {
		super();
	}
	
	public ElementContainer(boolean allowNameDuplicates) {
		super();
		this.allowNameDuplicates = allowNameDuplicates;
	}
	
	public ElementContainer(boolean allowNameDuplicates, boolean allowElementDuplicates) {
		super();
		this.allowNameDuplicates = allowNameDuplicates;
		this.allowElementDuplicates = allowElementDuplicates;
	}
	
	public ElementContainer(Collection<O> elements){
		super();
		addElements(elements);
	}
	
	public ElementContainer(Collection<O> elements, boolean allowNameDuplicates){
		this(allowNameDuplicates);
		addElements(elements);
	}
	
	protected boolean allowNameDuplicates(){
		return allowNameDuplicates;
	}
	
	protected boolean allowElementDuplicates(){
		return allowElementDuplicates;
	}

	public void clear(){
		elementMapID.clear();
		elementMapName.clear();
		elementTypeMapID.clear();
		elementTypeMapName.clear();
	}
	
	public O getElement(T elementID){
		if(!containsElement(elementID))
			throw new NoSuchElementException("No element with ID " + elementID);
		return elementMapID.get(elementID);
	}
	
	public List<O> getElements(String elementName){
		if(!containsElements(elementName))
			throw new NoSuchElementException("No elements with name " + elementName);
		return Collections.unmodifiableList(elementMapName.get(elementName));
	}
	
	public boolean containsElement(O element){
		return elementList.contains(element);
	}
	
	public boolean containsElement(T elementID){
		return elementMapID.containsKey(elementID);
	}
	
	public boolean containsElements(String elementName){
		return elementMapName.containsKey(elementName);
	}
	
	public Map<T,O> getElementMapID(){
		return Collections.unmodifiableMap(elementMapID);
	}
	
	public Map<String,List<O>> getElementMapName(){
		return Collections.unmodifiableMap(elementMapName);
	}
	
	public Collection<O> getElements(){
		return Collections.unmodifiableList(elementList);
	}
	
	public List<O> getElementList(Comparator<? super O> comparator){
		List<O> elementList = getElementList();
		Collections.sort(elementList, comparator);
		return elementList;
	}
	
	public List<O> getElementList(){
		return new ArrayList<>(getElements());
	}
	
	public void addElements(Collection<O> elements){
		for(O element: elements)
			addElement(element);
	}
	
	public boolean addElement(O element){
		if(element == null)
			return false;
		if(!allowElementDuplicates() && containsElement(element))
			return false;
		
		if(element.storesID())
			addElementToIDMaps(element);
		if(element.storesName()){
			if(!allowNameDuplicates()){
				try {
					if(element.hasName() && containsElements(element.getName()))
						return false;
				} catch (Exception e) {}
			}
			addElementToNameMaps(element);
		}
		if(element.storesType()){
			addElementToTypeMaps(element);
		}
		elementList.add(element);
		element.addListener(this);
		return true;
	}
	
	private boolean addElementToIDMaps(O element){
		if(!element.hasID())
			return false;
		
		T elementID = null;
		try {
			elementID = element.getID();
		} catch (Exception e) {
			// Cannot occur since hasID() is true
		}
		if(containsElement(elementID))
			return false;
		
		elementMapID.put(elementID, element);
		return true;
	}
	
	private boolean addElementToNameMaps(O element){
		if(!element.hasName())
			return false;
		
		String elementName = null;
		try {
			elementName = element.getName();
		} catch (Exception e) {
			// Cannot occur since hasName() is true
		}
		
		if (!elementMapName.containsKey(elementName))
			elementMapName.put(elementName, new ArrayList<O>());
		elementMapName.get(elementName).add(element);
		return true;
	}
	
	private boolean addElementToTypeMaps(O element){
		if(!element.hasType() || (!element.hasID() && !element.hasName()))
			return false;
		
		E elementType = null;
		try {
			elementType = element.getType();
		} catch (Exception e) {
			// Cannot occur since hasType() is true
		}
		addElementToTypeIDMaps(element, elementType);
		addElementToTypeNameMaps(element, elementType);
		return true;
	}
	
	private boolean addElementToTypeIDMaps(O element, E elementType){
		if(!element.hasID())
			return false;
		
		T elementID = null;
		try {
			elementID = element.getID();
		} catch (Exception e) {
			// Cannot occur since hasID() is true
		}

		return addElementToTypeIDMaps(element, elementType, elementID);
	}
	
	private boolean addElementToTypeIDMaps(O element, E elementType, T elementID){
		if (!elementTypeMapID.containsKey(elementType))
			elementTypeMapID.put(elementType, new HashMap<T, O>());
		elementTypeMapID.get(elementType).put(elementID, element);
		return true;
	}
	
	private boolean addElementToTypeNameMaps(O element, E elementType){
		if(!element.hasName())
			return false;
		
		String elementName = null;
		try {
			elementName = element.getName();
		} catch (Exception e) {
			// Cannot occur since hasName() is true
		}
		
		return addElementToTypeNameMaps(element, elementType, elementName);
	}
	
	private boolean addElementToTypeNameMaps(O element, E elementType, String elementName){
		if (!elementTypeMapName.containsKey(elementType)) {
			elementTypeMapName.put(elementType, new HashMap<String, List<O>>());
		}
		if (!elementTypeMapName.get(elementType).containsKey(elementName)) {
			elementTypeMapName.get(elementType).put(elementName, new ArrayList<O>());
		}
		elementTypeMapName.get(elementType).get(elementName).add(element);
		return true;
	}
	
	public Collection<O> getElements(E elementType){
		if(!elementTypeMapID.containsKey(elementType))
			return new ArrayList<O>();
		return elementTypeMapID.get(elementType).values();
	}
	
	public Collection<O> getElements(Collection<E> elementTypes){
		Collection<O> result = new ArrayList<>();
		for(E elementType: elementTypes){
			result.addAll(getElements(elementType));
		}
		return result;
	}
	
	public Collection<O> getElements(E[] elementTypes){
		return getElements(Arrays.asList(elementTypes));
	}
	
	public List<O> getElementList(E elementType, Comparator<? super O> comparator){
		List<O> elementList = getElementList(elementType);
		Collections.sort(elementList, comparator);
		return elementList;
	}
	
	public List<O> getElementList(E[] elementTypes){
		return new ArrayList<>(getElements(elementTypes));
	}
	
	public List<O> getElementList(E elementType){
		return new ArrayList<>(getElements(elementType));
	}
	
	public Set<T> getElementIDs(E elementType){
		if(!elementTypeMapID.containsKey(elementType))
			return new HashSet<T>();
		return Collections.unmodifiableSet(elementTypeMapID.get(elementType).keySet());
	}
	
	public Set<String> getElementNames(E elementType){
		if(!elementTypeMapName.containsKey(elementType))
			return new HashSet<String>();
		return Collections.unmodifiableSet(elementTypeMapName.get(elementType).keySet());
	}
	
	public List<T> getElementIDList(E elementType, boolean sort){
		if(!elementTypeMapID.containsKey(elementType))
			return new ArrayList<T>();
		List<T> elementIDList = new ArrayList<>(elementTypeMapID.get(elementType).keySet());
		if(sort) Collections.sort(elementIDList);
		return elementIDList;
	}
	
	public List<String> getElementNameList(E elementType, boolean sort){
		if(!elementTypeMapName.containsKey(elementType))
			return new ArrayList<String>();
		List<String> elementNameList = new ArrayList<>(elementTypeMapName.get(elementType).keySet());
		if(sort) Collections.sort(elementNameList);
		return elementNameList;
	}
	
	public Map<T,O> getElementMapID(E elementType){
		if(!elementTypeMapID.containsKey(elementType))
			return new HashMap<T,O>();
		return elementTypeMapID.get(elementType);
	}
	
	public Map<String,List<O>> getElementMapName(E elementType){
		if(!elementTypeMapName.containsKey(elementType))
			return new HashMap<String,List<O>>();
		return elementTypeMapName.get(elementType);
	}
	
	public boolean containsElements(E elementType){
		if(!elementTypeMapID.containsKey(elementType))
			return false;
		if(elementTypeMapID.get(elementType).isEmpty())
			return false;
		return true;
	}
	
	public Set<E> getElementTypes(){
		return Collections.unmodifiableSet(elementTypeMapID.keySet());
	}
	
	public int getElementCount(E... elementTypes){
		return getElementCount(Arrays.asList(elementTypes));
	}
	
	public int getElementCount(Collection<E> elementTypes){
		int result = 0;
		for(E elementType: elementTypes){
			if(!elementTypeMapID.containsKey(elementType))
				continue;
			result += elementTypeMapID.get(elementType).keySet().size();
		}
		return result;
	}
	
//	/**
//	 * Clears the type maps and adds all elements again to these maps.<br>
//	 * This makes sense when the type of contained elements changed.<br>
//	 * These changes are not recognized automatically and thus can lead to inconsistent states of the type maps maintained by this class.
//	 */
//	public void rebuildTypeMaps(){
//		elementTypeMapID.clear();
//		elementTypeMapName.clear();
//		for(O element: getElements())
//			addElementToTypeMaps(element);
//	}
	
	public Set<T> getElementIDs(){
		return Collections.unmodifiableSet(elementMapID.keySet());
	}
	
	public Set<String> getElementNames(){
		return Collections.unmodifiableSet(elementMapName.keySet());
	}
	
	public List<T> getElementIDList(boolean sort){
		List<T> elementIDList = new ArrayList<>(elementMapID.keySet());
		if(sort) Collections.sort(elementIDList, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return o1.compareTo(o2);
			}
		});
		return elementIDList;
	}
	
	public List<String> getElementNameList(boolean sort){
		List<String> elementNameList = new ArrayList<>(elementMapName.keySet());
		if(sort) Collections.sort(elementNameList);
		return elementNameList;
	}

	@Override
	public void elementFound(O element) {
		addElement(element);
	}
	
	public int getElementCount(){
		return elementList.size();
	}
	
	public boolean isEmpty(){
		return getElementCount() == 0;
	}
	
	public boolean removeElement(O element){
		if(element == null)
			return false;
		if(!elementList.contains(element))
			return false;
		
		elementList.remove(element);
		removeElementFromIDMaps(element);
		removeElementFromNameMaps(element);
		removeElementFromTypeMaps(element);
		element.removeListener(this);
		return true;
	}
	
	private boolean removeElementFromIDMaps(O element){
		if(!element.hasID())
			return false;
		
		T elementID = null;
		try {
			elementID = element.getID();
		} catch (Exception e) {
			// Cannot occur since hasID() is true
		}
		return removeElementFromIDMaps(elementID);
	}
	
	private boolean removeElementFromIDMaps(T elementID){
		if(!elementMapID.containsKey(elementID))
			return false;
		elementMapID.remove(elementID);
		return true;
	}
	
	private boolean removeElementFromNameMaps(O element){
		if(!element.hasName())
			return false;
		
		String elementName = null;
		try {
			elementName = element.getName();
		} catch (Exception e) {
			// Cannot occur since hasName() is true
		}
		
		return removeElementFromNameMaps(element, elementName);
	}
	
	private boolean removeElementFromNameMaps(O element, String elementName){
		if(!elementMapName.containsKey(elementName))
			return false;
		if(!elementMapName.get(elementName).remove(element))
			return false;
		
		if(elementMapName.get(elementName).isEmpty())
			elementMapName.remove(elementName);
		
		return true;
	}
	
	private boolean removeElementFromTypeMaps(O element){
		if(!element.hasType())
			return false;
		
		E elementType = null;
		try {
			elementType = element.getType();
		} catch (Exception e) {
			// Cannot occur since hasType() is true
		}
		
		return removeElementFromTypeMaps(element, elementType);
	}
	
	private boolean removeElementFromTypeMaps(O element, E elementType){
		boolean removed = false;
		if(removeElementFromTypeIDMaps(element, elementType))
			removed = true;
		if(removeElementFromTypeNameMaps(element, elementType))
			removed = true;
		return removed;
	}
	
	private boolean removeElementFromTypeIDMaps(O element, E elementType){
		if(!elementTypeMapID.containsKey(elementType) || !element.hasID())
			return false;
		
		T elementID = null;
		try {
			elementID = element.getID();
		} catch (Exception e) {
			// Cannot occur since hasID() is true
		}
		return removeElementFromTypeIDMaps(element, elementType, elementID);
	}
	
	private boolean removeElementFromTypeIDMaps(O element, E elementType, T elementID){
		if(elementTypeMapID.get(elementType).remove(elementID) == null)
			return false;
		
		if(elementTypeMapID.get(elementType).isEmpty())
			elementTypeMapID.remove(elementType);
		return true;
	}
	
	private boolean removeElementFromTypeNameMaps(O element, E elementType){
		if(!elementTypeMapName.containsKey(elementType) || !element.hasName())
			return false;
		
		String elementName = null;
		try {
			elementName = element.getName();
		} catch (Exception e) {
			// Cannot occur since hasName() is true
		}
		return removeElementFromTypeNameMaps(element, elementType, elementName);
	}
	
	private boolean removeElementFromTypeNameMaps(O element, E elementType, String elementName){
		if(!elementTypeMapName.get(elementType).containsKey(elementName))
			return false;
		if (!elementTypeMapName.get(elementType).get(elementName).remove(element))
			return false;
		
		if (elementTypeMapName.get(elementType).get(elementName).isEmpty())
			elementTypeMapName.get(elementType).remove(elementName);
		if (elementTypeMapName.get(elementType).isEmpty())
			elementTypeMapName.remove(elementType);
		
		return true;
	}
	
	public O removeElement(T elementID){
		if(!containsElement(elementID))
			return null;
		O element = getElement(elementID);
		removeElement(element);
		return element;
	}
	
	public Collection<O> removeElements(String... elementNames){
		return removeElements(Arrays.asList(elementNames));
	}
	
	public Collection<O> removeElements(Collection<String> elementNames){
		Collection<O> result = new ArrayList<>();
		for(String elementName: elementNames){
			result.addAll(removeElements(elementName));
		}
		return result;
	}
	
	public Collection<O> removeElements(String elementName){
		Collection<O> result = new ArrayList<>();
		if(!containsElements(elementName))
			return result;
		List<O> elementsToRemove = getElements(elementName);
		for(O elementToRemove: elementsToRemove){
			removeElement(elementToRemove);
			result.add(elementToRemove);
		}
		return result;
	}

	@Override
	public void nameChanged(O element, String oldName, String newName) throws Exception {
		if(!containsElement(element))
			return;
		if(containsElements(newName))
			throw new Exception("Container already contains an element with same name");
		
		if(oldName != null){
			removeElementFromNameMaps(element, oldName);
			if(element.hasType()){
				try{
					removeElementFromTypeNameMaps(element, element.getType(), oldName);
				} catch(Exception e){}
			}
		}
		addElementToNameMaps(element);
		if(element.hasType()){
			try{
				addElementToTypeNameMaps(element, element.getType(), newName);
			} catch(Exception e){}
		}
		
//		System.out.println(MapUtils.toString(elementTypeMapID));
//		System.out.println(MapUtils.toString(elementTypeMapName));
	}

	@Override
	public void typeChanged(O element, E oldType, E newType) throws Exception {
		if(!containsElement(element))
			return;
		
		if(oldType != null)
			removeElementFromTypeMaps(element, oldType);
		addElementToTypeMaps(element);
		
//		System.out.println(MapUtils.toString(elementTypeMapID));
//		System.out.println(MapUtils.toString(elementTypeMapName));
	}

	@Override
	public void idChanged(O element, T oldID, T newID) throws Exception {
		if(!containsElement(element))
			return;
		
		if(oldID != null){
			removeElementFromIDMaps(oldID);
			if(element.hasType()){
				try{
					removeElementFromTypeIDMaps(element, element.getType(), oldID);
				} catch(Exception e){}
			}
		}
		addElementToIDMaps(element);
		if(element.hasType()){
			try{
				addElementToTypeIDMaps(element, element.getType(), newID);
			} catch(Exception e){}
		}
		
//		System.out.println(MapUtils.toString(elementTypeMapID));
//		System.out.println(MapUtils.toString(elementTypeMapName));
	}
	
	

}
