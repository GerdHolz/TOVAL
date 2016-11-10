package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.validate.Validate;

public class ValueEditingDialog<O> extends AbstractDialog<List<O>> {

	private static final long serialVersionUID = -1366222220771016486L;

	private static final Dimension MINIMUM_SIZE = new Dimension(350,300);
	
	private JList<O> valueList;
	private JButton btnAdd;
	private JButton btnDefine;
	private JButton btnGenerate;
	
	private Collection<O> permittedValues;
	
	private boolean visibilityButtonAdd;
	private boolean visibilityButtonDefine;
	private boolean visibilityButtonGenerate;
	
	protected DefaultListModel<O> listModel;
	
	protected Class<O> elementClass;
	private boolean allowsDuplicates = false;
	private Comparator<O> valueComparator;
	
	public ValueEditingDialog(String title, Class<O> elementClass) throws Exception {
		super(title);
		setElementClass(elementClass);
	}
	
	public ValueEditingDialog(String title, Class<O> elementClass, Collection<O> initialValues) throws Exception {
		super(title);
		setElementClass(elementClass);
		setInitialValues(initialValues);
	}
	
	public ValueEditingDialog(Window owner, String title, Class<O> elementClass) throws Exception {
		super(owner, title);
		setElementClass(elementClass);
	}
	
	public ValueEditingDialog(Window owner, String title, Class<O> elementClass, Collection<O> initialValues) throws Exception {
		super(owner, title);
		setElementClass(elementClass);
		setInitialValues(initialValues);
	}
	
	public void setListCellRenderer(ListCellRenderer<? super O> renderer){
		getListValues().setCellRenderer(renderer);
	}
	
	private void setElementClass(Class<O> elementClass) throws Exception{
		this.elementClass = elementClass;
		if(elementClass.isEnum()){
			setPermittedValues(Arrays.asList(elementClass.getEnumConstants()));
			visibilityButtonDefine = false;
			visibilityButtonGenerate = false;
			visibilityButtonAdd = true;
		} else {
			visibilityButtonAdd = permittedValues != null && !permittedValues.isEmpty();
			visibilityButtonDefine = ValueDefinitionDialog.isCompatible(elementClass);
			visibilityButtonGenerate = String.class.isAssignableFrom(elementClass);
		}
	}

	@Override
	protected Dimension getDefaultPreferredSize() {
		return MINIMUM_SIZE;
	}

	@Override
	protected void initialize() {
		super.initialize();
		setIncludeCancelButton(true);
		listModel = new DefaultListModel<O>();
		permittedValues = new ArrayList<O>();
	}
	
	public void setInitialValues(Collection<O> initialValues){
		for(O initialValue: initialValues)
			listModel.addElement(initialValue);
	}
	
	public void setPermittedValues(Collection<O> permittedValues){
		if(permittedValues != null){
			Validate.notEmpty(permittedValues);
			Validate.noNullElements(permittedValues);
			this.permittedValues = new ArrayList<>(permittedValues);
			visibilityButtonAdd = true;
		}
	}

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(getListValues());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel().add(scrollPane, BorderLayout.CENTER);
	}
	
	@Override
	protected List<JButton> getButtonsLefthand() throws Exception {
		List<JButton> buttons = super.getButtonsLefthand();
        if(visibilityButtonAdd)
			buttons.add(getButtonAdd());
		if(visibilityButtonDefine)
			buttons.add(getButtonDefine());
		if(visibilityButtonGenerate)
			buttons.add(getButtonGenerate());
        return buttons;
	}

	private JButton getButtonDefine(){
		if(btnDefine == null){
			btnDefine = new JButton("Define...");
			btnDefine.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<O> definedValues = null;
					try {
						definedValues = ValueDefinitionDialog.showDialog(ValueEditingDialog.this, "Define " + getTitle(), elementClass);
					} catch (Exception e1) {
						internalException("Cannot launch string list definition dialog.", e1);
						return;
					}
					if(definedValues == null)
						return;
					for (O string : definedValues) {
						listModel.addElement(string);
					}
					
				}
			});
		}
		return btnDefine;
	}
	
	private JButton getButtonGenerate(){
		if(btnGenerate == null){
			btnGenerate = new JButton("Generate...");
			btnGenerate.addActionListener(new ActionListener() {
				@SuppressWarnings("unchecked")
				public void actionPerformed(ActionEvent e) {
					List<String> generatedStrings = null;
					try {
						generatedStrings = StringListGeneratorDialog.showDialog(ValueEditingDialog.this, "Generate " + getTitle());
					} catch (Exception e1) {
						internalException("Cannot launch string list generation dialog.", e1);
						return;
					}
					if(generatedStrings != null){
						listModel.clear();
						for(String string: generatedStrings){
							listModel.addElement((O) string);
						}
						
					}
				}
			});
		}
		return btnGenerate;
	}
	
	private JButton getButtonAdd(){
		if(btnAdd == null){
			btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					List<O> chosenValues = null;
					try {
						ValueChooserDialog<O> dialog = new ValueChooserDialog<O>(ValueEditingDialog.this, "Please choose values", permittedValues, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						dialog.setValueComparator(getValueComparator());
						dialog.setListCellRenderer(getListValues().getCellRenderer());
						dialog.setUpGUI();
						chosenValues = dialog.getDialogObject();
					} catch (Exception e1) {
						internalException("Cannot launch value chooser dialog.", e1);
						return;
					}
					if(chosenValues == null || chosenValues.isEmpty())
						return;
					
					if(!allowsDuplicates){
						Set<O> newSet = new HashSet<>(chosenValues);
						chosenValues = new ArrayList<>(newSet);
					}
					
					for(O chosenValue: chosenValues){
						listModel.addElement(chosenValue);
					}
					sortValueList();
				}

			});
		}
		return btnAdd;
	}
	
	public void setValueComparator(Comparator<O> valueComparator) {
		this.valueComparator = valueComparator;
	}

	public Comparator<O> getValueComparator() {
		return valueComparator;
	}

	private void sortValueList() {
		if(valueComparator == null)
			return;
		List<O> list = (List<O>) Arrays.asList(listModel.toArray());
		Collections.sort(list, valueComparator);
		listModel.clear();
		for(O sortedElement: list){
			listModel.addElement(sortedElement);
		}
	}
	
	@Override
	protected void setTitle() {}
	
	@Override
	protected void okProcedure() {
		if(!listModel.isEmpty()){
			if(getDialogObject() == null)
				setDialogObject(new ArrayList<O>());
			for(int i=0; i<listModel.size(); i++)
				getDialogObject().add(listModel.getElementAt(i));
			super.okProcedure();
		} else {
			JOptionPane.showMessageDialog(ValueEditingDialog.this, "Value list is empty.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private JList<O> getListValues(){
		if(valueList == null){
			valueList = new JList<O>(listModel);
			valueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			valueList.setCellRenderer(new AlternatingRowColorListCellRenderer<O>());
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
			valueList.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "deleteSelectedValues");
			valueList.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteSelectedValues");
			valueList.getActionMap().put("deleteSelectedValues", new DeleteSelectedValuesAction());
		}
		return valueList;
	}
	
	private class DeleteSelectedValuesAction extends AbstractAction {

		private static final long serialVersionUID = 5595311016427023916L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Object selectedObject : getListValues().getSelectedValuesList()) {
            	listModel.removeElement(selectedObject);
            }
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static <O> List<O> showDialog(String title, Class elementClass) throws Exception{
		return showDialog(null, title, elementClass);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <O> List<O> showDialog(Window owner, String title, Class elementClass) throws Exception{
		ValueEditingDialog<O> dialog = new ValueEditingDialog<O>(owner, title, elementClass);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	@SuppressWarnings("rawtypes")
	public static <O> List<O> showDialog(String title, Class elementClass, Collection<O> initialValues) throws Exception{
		return showDialog(null, title, elementClass, initialValues);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <O> List<O> showDialog(Window owner, String title, Class elementClass, Collection<O> initialValues) throws Exception{
		ValueEditingDialog<O> dialog = new ValueEditingDialog<O>(owner, title, elementClass, initialValues);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	@SuppressWarnings("rawtypes")
	public static <O> List<O> showDialogWithValueRestriction(String title, Class elementClass, Collection<O> permittedValues) throws Exception{
		return showDialogWithValueRestriction(null, title, elementClass, permittedValues);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <O> List<O> showDialogWithValueRestriction(Window owner, String title, Class elementClass, Collection<O> permittedValues) throws Exception{
		ValueEditingDialog<O> dialog = new ValueEditingDialog<O>(owner, title, elementClass);
		dialog.setPermittedValues(permittedValues);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	@SuppressWarnings("rawtypes")
	public static <O> List<O> showDialogWithValueRestriction(String title, Class elementClass, Collection<O> initialValues, Collection<O> permittedValues) throws Exception{
		return showDialogWithValueRestriction(null, title, elementClass, initialValues, permittedValues);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <O> List<O> showDialogWithValueRestriction(Window owner, String title, Class elementClass, Collection<O> initialValues, Collection<O> permittedValues) throws Exception{
		ValueEditingDialog<O> dialog = new ValueEditingDialog<O>(owner, title, elementClass, initialValues);
		dialog.setPermittedValues(permittedValues);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	

//	public static void main(String[] args) throws Exception {
//		CollectionUtils.print(ValueEditingDialog.showDialog("Test", PseudoEnum.class, Arrays.asList(PseudoEnum.DREI)));
////		CollectionUtils.print(ValueEditingDialog.showDialog("Test", String.class, Arrays.asList("Eins", "Zwei")));
////		CollectionUtils.print(ValueEditingDialog.showDialog("Test", Integer.class, Arrays.asList(1,2,3,4,5)));
//	}
//	
//	private enum PseudoEnum {
//		EINS, ZWEI, DREI;
//	}

}