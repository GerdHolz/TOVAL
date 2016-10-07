package de.invation.code.toval;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.invation.code.toval.graphic.dialog.PropertySettingDialog;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.properties.PropertyListener;

public class TestPropertySettingDialog implements PropertyListener<TestProperty>{

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		TestProperties properties = null;
		System.out.println("Step 1: Create properties and register as listener");
		try {
			properties = new TestProperties(TestProperty.class, "C:\\testproperties.txt");
			boolean listenerAdded = properties.addListener(this);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot create test properties");
		}
		
		System.out.println("Step 2: Extract default property value");
		try {
			System.out.println(properties.getProperty(TestProperty.TYPE01_ENUM));
			System.out.println(properties.getProperty(TestProperty.TYPE02_INTEGER));
			System.out.println(properties.getProperty(TestProperty.TYPE03_DOUBLE));
			System.out.println(properties.getProperty(TestProperty.TYPE04_BOOLEAN));
			System.out.println(properties.getProperty(TestProperty.TYPE05_COLOR));
		} catch (PropertyException e1) {
			e1.printStackTrace();
			fail("Cannot extract property value");
		}
		
		System.out.println("Step 3: Launch property setting dialog");
		try {
			PropertySettingDialog.showDialog(null, properties);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot launch property setting dialog");
		}
	}

	@Override
	public void propertyChange(TestProperty property, Object oldValue, Object newValue) {
		System.out.println("Property change [" + property.getPropertyCharacteristics().getName() + "]: old=" + oldValue + ", new="+newValue);
	}

}
