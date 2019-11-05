package de.invation.code.toval.element;

import static org.junit.Assert.*;

import org.junit.Test;

import de.invation.code.toval.misc.MapUtils;

public class ElementTests {
	
	private TestElementContainer container;
	
	public ElementTests() {
		super();
		setUp();
	}
	
	private void setUp(){
		container = new TestElementContainer(false);
	}

	@Test
	public void test() throws Exception {
		Integer initialID = 1;
		String initialName = "Testname";
		TestElementType initialType = TestElementType.TESTVAL1;
		
		TestElement element1 = new TestElement(initialID, initialName, initialType);
		if(!container.addElement(element1))
			fail("Element cannot be added to container");
		
		System.out.println(MapUtils.toString(container.getElementMapID()));
		System.out.println(MapUtils.toString(container.getElementMapName()));
		
		if(container.getElement(initialID) != element1)
			fail("Stored element not stored correctly using ID");
		if(!container.getElements(initialType).contains(element1))
			fail("Stored element not stored correctly using type");
		if(!container.getElements(initialName).contains(element1))
			fail("Stored element not stored correctly using name");
		
		System.out.println();
		System.out.println("Change ID");
		Integer changedID = 2;
		element1.setID(changedID);
		
		System.out.println(MapUtils.toString(container.getElementMapID()));
		System.out.println(MapUtils.toString(container.getElementMapName()));
		
		assertFalse("Container still uses old ID as storage key", container.containsElement(initialID));
		assertTrue("Container does not use new ID as storage key", container.containsElement(changedID));
		assertTrue("Container contains element more than once", container.getElementCount() == 1);
		try{
			assertTrue("Element is not stored correctly", container.getElement(changedID) == element1);
		} catch(Exception e){
			fail("Element cannot be extracted from container using new ID");
		}
		if(!container.getElements(initialType).contains(element1))
			fail("Stored element not stored correctly using type after ID change");
		if(!container.getElements(initialName).contains(element1))
			fail("Stored element not stored correctly using name after ID change");
		
		System.out.println();
		System.out.println("Change Name");
		String changedName = "Testname2";
		element1.setName(changedName);
		
		System.out.println(MapUtils.toString(container.getElementMapID()));
		System.out.println(MapUtils.toString(container.getElementMapName()));
		
		assertFalse("Container still uses old name as storage key", container.containsElements(initialName));
		assertTrue("Container does not use new name as storage key", container.containsElements(changedName));
		assertTrue("Container contains element more than once", container.getElementCount() == 1);
		try{
			assertTrue("Element is not stored correctly", container.getElements(changedName).contains(element1));
		} catch(Exception e){
			fail("Element cannot be extracted from container using new name");
		}
		if(!container.getElements(initialType).contains(element1))
			fail("Stored element not stored correctly using type after name change");
		if(container.getElement(changedID) != element1)
			fail("Stored element not stored correctly using ID after name change");
		
		System.out.println();
		System.out.println("Change type");
		TestElementType changedType = TestElementType.TESTVAL2;
		element1.setType(changedType);
		
		System.out.println(MapUtils.toString(container.getElementMapID()));
		System.out.println(MapUtils.toString(container.getElementMapName()));
		
		assertFalse("Container still uses old type as storage key", container.containsElements(initialType));
		assertTrue("Container does not use new type as storage key", container.containsElements(changedType));
		assertTrue("Container contains element more than once", container.getElementCount() == 1);
		try{
			assertTrue("Element is not stored correctly", container.getElements(changedType).contains(element1));
		} catch(Exception e){
			fail("Element cannot be extracted from container using new type");
		}
		if(!container.getElements(changedName).contains(element1))
			fail("Stored element not stored correctly using name after ID change");
		if(container.getElement(changedID) != element1)
			fail("Stored element not stored correctly using ID after name change");
		
		TestElement element2 = new TestElement(3, changedName, TestElementType.TESTVAL1);
		
		assertFalse("Container does not prevent adding elements with same name", container.addElement(element2));
		element2.setName("Testname3");
		assertTrue(container.addElement(element2));
		try{
			element2.setName(changedName);
			fail("Name change is not prevented by container");
		} catch(Exception e){}
	}

}
