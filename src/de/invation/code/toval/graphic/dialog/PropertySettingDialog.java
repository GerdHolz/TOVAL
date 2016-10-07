package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.invation.code.toval.graphic.component.PropertySettingPanel;
import de.invation.code.toval.properties.AbstractTypedProperties;
import de.invation.code.toval.properties.Property;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ParameterException;

public class PropertySettingDialog<E extends AbstractTypedProperties<P>, P extends Enum<P> & Property> extends AbstractDialog<Object> {
	
	private static final long serialVersionUID = 1855588446219004779L;
	
	private static final Dimension PREFERRED_SIZE = new Dimension(400,400);
	
	private PropertySettingPanel<E,P> pnlSetting = null;
	private E properties = null;
	
	public PropertySettingDialog(Window parent, E properties) throws PropertyException, IOException {
		super(parent, ButtonPanelLayout.CENTERED);
		setIncludeCancelButton(false);
		pnlSetting = new PropertySettingPanel<E,P>(properties);
		this.properties = properties;
		setPreferredSize(new Dimension(Math.max(PREFERRED_SIZE.width, getPropertySettingPanel().getPreferredSize().width + 50), PREFERRED_SIZE.height));
//		System.out.println(getPreferredSize().width);
//		System.out.println(getPropertySettingPanel().getPreferredSize().width);
//		if(getPreferredSize().width < getPropertySettingPanel().getPreferredSize().width){
//			setPreferredSize(new Dimension(getPropertySettingPanel().getPreferredSize().width + 50, getPreferredSize().height));
//		}
	}
	
	
	
	@Override
	protected Dimension getDefaultPreferredSize() {
		return PREFERRED_SIZE;
	}



	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		JScrollPane scp = new JScrollPane(getPropertySettingPanel());
		scp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scp.getVerticalScrollBar().setUnitIncrement(16);
		mainPanel().add(scp, BorderLayout.CENTER);
	}

	protected JPanel getPropertySettingPanel(){
		return pnlSetting;
	}

	@Override
	protected void setTitle() {
		setTitle("Edit Properties");
	}

	@Override
	protected void okProcedure() {
		try {
			transferProperties();
		} catch (Exception e) {
			ExceptionDialog.showException(PropertySettingDialog.this, "Cannot store all properties", e, true, true);
			return;
		}
		super.okProcedure();
	}
	
	@SuppressWarnings("unchecked")
	protected void transferProperties() throws ParameterException, PropertyException, IOException, Exception{
		for (Object propertyObject : properties.getPropertyClass().getEnumConstants()) {
			P property = (P) propertyObject;
			Object propertyValueFromPanel = pnlSetting.getPropertyValue(property);
			properties.setProperty(property, propertyValueFromPanel);
		}
		if(properties.containsFileName())
			properties.store();
	}

	public static <EE extends AbstractTypedProperties<PP>, PP extends Enum<PP> & Property> void showDialog(Window parent, EE properties) throws Exception {
		PropertySettingDialog<EE,PP> dialog = new PropertySettingDialog<>(parent, properties);
		dialog.setUpGUI();
	}

}
