package de.invation.code.toval.graphic.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.graphic.dialog.ValueEditingDialog;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.validate.Validate;

public abstract class ListModificationPanel<O> extends JPanel {

	private static final long serialVersionUID = 1275694714155568361L;
	
	private static final String DEFAULT_LIST_TITLE = "Values:";
	private static final String DEFAULT_BUTTON_TITLE = "Modify";
	private static final String DEFAULT_DIALOG_TITLE = "Modify list values";
	
	private JList<O> listValues;
	private JButton btnModifyList;
	private DefaultListModel<O> listModel;
	private Collection<O> validListValues;
	private String buttonTitle;
	private String dialogTitle;
	private String listTitle;
	private boolean allowsDuplicates = false;
	private Comparator<O> valueComparator;
	
	public ListModificationPanel() {
		this(true);
	}
	
	public ListModificationPanel(boolean setUpGUI) {
		super();
		if(setUpGUI){
			setUpGUI();
		}
	}
	
	public void setAllowsDuplicates(boolean allowsDuplicates) {
		this.allowsDuplicates = allowsDuplicates;
	}

	protected ListCellRenderer<? super O> getListCellRenderer(){
		return new AlternatingRowColorListCellRenderer<O>();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		btnModifyList.setEnabled(enabled);
	}

	public void setUpGUI(){
		setLayout(new BorderLayout());
		add(new JLabel(getListTitle()), BorderLayout.PAGE_START);
		
		JScrollPane scrollPane = new JScrollPane(getListValues());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		btnModifyList = new JButton(getButtonTitle());
		btnModifyList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonProcedure();
			}
		});
		JPanel buttonPanel = new JPanel();
		BoxLayout layout = new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS);
		buttonPanel.setLayout(layout);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(btnModifyList);
		buttonPanel.add(Box.createHorizontalGlue());
		add(buttonPanel, BorderLayout.PAGE_END);
	}
	
	public Comparator<O> getValueComparator() {
		return valueComparator;
	}

	public void setValueComparator(Comparator<O> valueComparator) {
		this.valueComparator = valueComparator;
	}

	public String getButtonTitle() {
		if(buttonTitle == null){
			buttonTitle = DEFAULT_BUTTON_TITLE;
		}
		return buttonTitle;
	}	

	public String getListTitle() {
		if(listTitle == null){
			listTitle = DEFAULT_LIST_TITLE;
		}
		return listTitle;
	}

	public String getDialogTitle() {
		if(dialogTitle == null){
			dialogTitle = DEFAULT_DIALOG_TITLE;
		}
		return dialogTitle;
	}

	public Collection<O> getValidListValues() throws Exception {
		if(validListValues == null){
			validListValues = new ArrayList<O>();
		}
		return validListValues;
	}

	public void setValidListValues(Collection<O> validListValues) {
		Validate.notNull(validListValues);
		Validate.noNullElements(validListValues);
		this.validListValues = validListValues;
	}

	protected DefaultListModel<O> getListModel(){
		if(listModel == null){
			listModel = new DefaultListModel<O>();
		}
		return listModel;
	}
	
	protected JList<O> getListValues(){
		if(listValues == null){
			listValues = new JList<O>(getListModel());
			listValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listValues.setCellRenderer(getListCellRenderer());
			listValues.setFixedCellHeight(20);
			listValues.setVisibleRowCount(10);
			listValues.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listValues.setBorder(null);
		}
		return listValues;
	}
	
	protected void clearList(){
		listModel.removeAllElements();
	}
	
	protected void resetListModel(Collection<O> newValues){
		listModel.removeAllElements();
		for(O newValue: newValues){
			listModel.addElement(newValue);
		}
	}
	
	protected void buttonProcedure(){
		try {
			ValueEditingDialog<O> dialog = new ValueEditingDialog<O>(SwingUtilities.getWindowAncestor(ListModificationPanel.this), getDialogTitle(), (Class<O>) getValueClass(), (Collection<O>) Arrays.asList(getListModel().toArray()));
			dialog.setPermittedValues(getValidListValues());
			dialog.setValueComparator(getValueComparator());
			dialog.setListCellRenderer(getListCellRenderer());
			dialog.setUpGUI();
			List<O> newValueList = dialog.getDialogObject();
			
			if(newValueList == null)
				return;
			if(!allowsDuplicates){
				Set<O> newSet = new HashSet<>(newValueList);
				newValueList = new ArrayList<>(newSet);
			}
			getListModel().clear();
			for(O newValue: newValueList){
				getListModel().addElement(newValue);
			}
			sortValueList();
		} catch (Exception e1) {
			ExceptionDialog.showException(SwingUtilities.getWindowAncestor(ListModificationPanel.this), "Internal Exception", new Exception("Cannot launch value editing dialog", e1), true, true);
		}
	}
	
	public List<O> getValues(){
		List<O> result = new ArrayList<O>();
		Enumeration<O> listModelElements = getListModel().elements();
		while(listModelElements.hasMoreElements()){
			result.add(listModelElements.nextElement());
		}
		return result;
	}
	
	private void sortValueList() {
		if(getValueComparator() == null)
			return;
		List<O> list = (List<O>) Arrays.asList(listModel.toArray());
		Collections.sort(list, getValueComparator());
		listModel.clear();
		for(O sortedElement: list){
			listModel.addElement(sortedElement);
		}
	}
	
	protected abstract Class<?> getValueClass();

}
