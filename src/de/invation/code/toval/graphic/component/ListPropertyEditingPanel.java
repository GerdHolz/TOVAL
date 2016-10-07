package de.invation.code.toval.graphic.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.graphic.dialog.ValueEditingDialog;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.misc.CollectionUtils;
import de.invation.code.toval.properties.AbstractTypedProperties;
import de.invation.code.toval.properties.ListPropertyElementType;
import de.invation.code.toval.properties.Property;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.properties.PropertyValueType;

public class ListPropertyEditingPanel<E extends AbstractTypedProperties<P>, P extends Enum<P> & Property> extends JPanel{
	
	private static final long serialVersionUID = -5670589916523273637L;
	
	private static final Dimension PREFERRED_SIZE_VALUE_LIST = new Dimension(250,100);
	
	private P property;
	private List<String> actualStringValues = null;
	private List<Object> actualObjectValues = null;
	
	private JList valueList;
	JButton btnEdit;
	protected DefaultListModel listModel = new DefaultListModel<>();

	public ListPropertyEditingPanel(P property, Object actualPropertyValue) throws PropertyException {
		super();
		this.property = property;
		if(property.getPropertyCharacteristics().getListElementType() == ListPropertyElementType.STRING){
			actualStringValues = (List<String>) actualPropertyValue;
		} else {
			actualObjectValues = (List<Object>) actualPropertyValue;
		}
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(getListValues());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(PREFERRED_SIZE_VALUE_LIST);
		add(scrollPane, BorderLayout.CENTER);
		add(getButtonEdit(), BorderLayout.PAGE_END);
		updateValueList();
	}
	
	private Component getButtonEdit() {
		if(btnEdit == null){
			btnEdit = new JButton("Edit");
			btnEdit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if(property.getPropertyCharacteristics().getValueType() == PropertyValueType.LIST){
							Window windowAncestor = SwingUtilities.getWindowAncestor(ListPropertyEditingPanel.this);
							if(property.getPropertyCharacteristics().getListElementType() == ListPropertyElementType.STRING){
								List<String> chosenStringValues = ValueEditingDialog.showDialog(windowAncestor, "Edit value list", String.class, actualStringValues);
								if(chosenStringValues == null)
									return;
								actualStringValues.clear();
								actualStringValues.addAll(chosenStringValues);
							} else {
								List<Object> permittedValues = property.getPropertyCharacteristics().getPermittedValues();
								List<Object> chosenObjectValues = ValueEditingDialog.showDialogWithValueRestriction(windowAncestor, "Edit value list", property.getPropertyCharacteristics().getListElementClass(), actualObjectValues, permittedValues);
								if(chosenObjectValues == null)
									return;
								actualObjectValues.clear();
								actualObjectValues.addAll(chosenObjectValues);
							}
							updateValueList();
						}
					} catch (Exception e1) {
						ExceptionDialog.showException(SwingUtilities.getWindowAncestor(ListPropertyEditingPanel.this), "Cannot open dialog", e1, true, true);
					}
				}
			});
		}
		return btnEdit;
	}

	private void updateValueList(){
		CollectionUtils.print(getChosenValues());
		for(Object o: getChosenValues()){
			listModel.addElement(o);
		}
	}
	
	private JList getListValues(){
		if(valueList == null){
			valueList = new JList(listModel);
			valueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			valueList.setCellRenderer(new AlternatingRowColorListCellRenderer());
			valueList.setFixedCellHeight(20);
			valueList.setVisibleRowCount(10);
			valueList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			valueList.setBorder(null);
			
			valueList.addListSelectionListener(
	        		new ListSelectionListener(){
	        			public void valueChanged(ListSelectionEvent e) {
	        			    if ((e.getValueIsAdjusting() == false) && (valueList.getSelectedValue() != null)) {
	        			    	
	        			    }
	        			}
	        		}
	        );
		}
		return valueList;
	}

	public List<?> getChosenValues() {
		if(actualStringValues != null)
			return actualStringValues;
		return actualObjectValues;
	}

}
