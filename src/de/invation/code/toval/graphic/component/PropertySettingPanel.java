package de.invation.code.toval.graphic.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.invation.code.toval.graphic.component.ColorChooserPanel.ColorMode;
import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.graphic.dialog.StringDialog;
import de.invation.code.toval.graphic.util.SpringUtilities;
import de.invation.code.toval.misc.CollectionUtils;
import de.invation.code.toval.properties.AbstractTypedProperties;
import de.invation.code.toval.properties.ListPropertyElementType;
import de.invation.code.toval.properties.Property;
import de.invation.code.toval.properties.PropertyException;

public class PropertySettingPanel<E extends AbstractTypedProperties<P>, P extends Enum<P> & Property> extends JPanel {

	private static final long serialVersionUID = -5108652483546760137L;

	private static final String FORMAT_PROPERTY_COMPONENT_LABEL = "%s:";
	
	protected Map<P,JLabel> mapComponentLabels = new HashMap<>();
	protected Map<P,JComponent> mapComponents = new HashMap<>();
	
	protected JPanel pnlSettings;
	
	protected E properties = null;

	public PropertySettingPanel(E properties) throws PropertyException, IOException {
		super(new SpringLayout());
		this.properties = properties;
		setUpGui();
	}
	
	private void setUpGui() throws PropertyException, IOException {
		setLayout(new BorderLayout());
		add(getPanelSettings(), BorderLayout.PAGE_START);
		add(Box.createGlue(), BorderLayout.CENTER);
	}
	
	protected JComponent getcomponent(P property){
		return mapComponents.get(property);
	}
	
	private JPanel getPanelSettings() throws PropertyException{
		if(pnlSettings == null){
			pnlSettings = new JPanel(new BorderLayout());
			pnlSettings.setLayout(new SpringLayout());
			addSettingComponents();
			SpringUtilities.makeCompactGrid(pnlSettings, properties.getPropertyClass().getEnumConstants().length, 2, 5, 5, 5, 5);
		}
		return pnlSettings;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JComponent createComponent(P property) throws PropertyException{
//		System.out.println("Create component for property: " + property.getName());
		JComponent result = null;
		Object actualPropertyValue = properties.getProperty(property);
		switch(property.getPropertyCharacteristics().getValueType()){
		case BOOLEAN:
			JCheckBox chkBox = new JCheckBox();
			chkBox.setSelected((Boolean) actualPropertyValue);
			result = chkBox;
			break;
		case DOUBLE:
		case INTEGER:
		case STRING:
			JTextField txtField = new JTextField();
			txtField.setText(properties.getPropertyValueAsString(property, actualPropertyValue));
			result = txtField;
			break;
		case ENUM:
			EnumComboBox enumComboBox = new EnumComboBox(property.getPropertyCharacteristics().getEnumClass());
			enumComboBox.setSelectedItem(actualPropertyValue);
			result = enumComboBox;
			break;
		case COLOR:
			ColorChooserPanel pnlColor = new ColorChooserPanel(ColorMode.HEX, (Color) actualPropertyValue);
			result = pnlColor;
			break;
		case LIST:
			result = new ListPropertyEditingPanel(property, actualPropertyValue);
			if(property.getPropertyCharacteristics().getListElementType() == ListPropertyElementType.ENUM){
				CollectionUtils.print((List) actualPropertyValue); 
			}
			break;
		default:
			result = new JLabel("- UNKNOWN PROPERTY VALUE TYPE -");
			break;
		}
		return result;
	}
	
	protected JLabel getComponentLabel(P property){
		JLabel newLabel = new JLabel(String.format(FORMAT_PROPERTY_COMPONENT_LABEL, property.getPropertyCharacteristics().getDisplayName()), SwingConstants.RIGHT);
		newLabel.setToolTipText(property.getPropertyCharacteristics().getDescription());
		newLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
					StringDialog.showDialog(SwingUtilities.getWindowAncestor(PropertySettingPanel.this), property.getPropertyCharacteristics().getDisplayName(), property.getPropertyCharacteristics().getDescription(), false);
				} catch (Exception e1) {
					ExceptionDialog.showException(SwingUtilities.getWindowAncestor(PropertySettingPanel.this), "Internal Exceoption", e1, true, true);
				}
            }

        });
		return newLabel;
	}

	@SuppressWarnings("unchecked")
	protected void addSettingComponents() throws PropertyException{
		for (Object propertyObject : properties.getPropertyClass().getEnumConstants()) {
			P property = (P) propertyObject;
			JLabel propertyLabel = getComponentLabel(property);
			mapComponentLabels.put(property, propertyLabel);
			pnlSettings.add(propertyLabel);
			JComponent propertyComponent = createComponent(property);
			mapComponents.put(property, propertyComponent);
			pnlSettings.add(propertyComponent);
		}
	}
	
	public Object getPropertyValue(P property) throws PropertyException{
		JComponent propertyComponent = mapComponents.get(property);
		switch(property.getPropertyCharacteristics().getValueType()){
		case BOOLEAN:
			return ((JCheckBox) propertyComponent).isSelected();
		case DOUBLE:
		case INTEGER:
			String propertyValueAsText = ((JTextField) propertyComponent).getText();
			return properties.getPropertyValueFromString(property, propertyValueAsText);
		case STRING:
			return ((JTextField) propertyComponent).getText();
		case ENUM:
			return ((JComboBox<?>) propertyComponent).getSelectedItem();
		case COLOR:
			return ((ColorChooserPanel) propertyComponent).getChosenColor();
		case LIST:
			return ((ListPropertyEditingPanel) propertyComponent).getChosenValues();
		default:
			break;
		}
		return null;
	}
	

}
