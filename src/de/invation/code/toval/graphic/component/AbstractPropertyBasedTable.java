package de.invation.code.toval.graphic.component;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.types.ElementWithTypedProperties;
import de.invation.code.toval.validate.Validate;

public abstract class AbstractPropertyBasedTable<E extends Enum<E>, O extends ElementWithTypedProperties<E>> extends AbstractRowSelectionTable<AbstractTableModel> {

	private static final long serialVersionUID = -3891196745792785590L;
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private Map<Integer,Boolean> lastSortMode = new HashMap<>();

	public AbstractPropertyBasedTable() {
		super();
		getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        try {
		        	int column = columnAtPoint(e.getPoint());
		        	if(column == -1)
		        		return;
		        	if(!lastSortMode.containsKey(column)){
		        		lastSortMode.put(column, false);
		        	} else {
		        		lastSortMode.put(column, !lastSortMode.get(column));
		        		
		        	}
					((PropertyBasedTableModel) getModel()).sort(columnAtPoint(e.getPoint()), lastSortMode.get(column));
				} catch (Exception e1) {
					ExceptionDialog.showException(SwingUtilities.getWindowAncestor(AbstractPropertyBasedTable.this), "Exception during column sort", e1, true, true);
				}
		    }
		});
	}
	
	protected abstract E[] getPropertiesOfInterest();
	
	protected E[] getEditableProperties() {
		return null;
	}
	
	protected abstract List<O> getElements();
	
	@Override
	protected AbstractTableModel createNewTableModel(){
		return new PropertyBasedTableModel(getElements());
	}
	
	public void removeSelectedElement(){
		int selectedRow = getSelectedRow();
		if(selectedRow == -1)
			return;
		((PropertyBasedTableModel) getModel()).removeElementAt(selectedRow);
	}
	
	public void removeSelectedElements(){
		for(int selectedRow: getSelectedRows())
			((PropertyBasedTableModel) getModel()).removeElementAt(selectedRow);
	}
	
	public O getSelectedElement(){
		if(getSelectedRow() == -1)
			return null;
		return ((PropertyBasedTableModel) getModel()).getElementAt(getSelectedRow());
	}
	
	public List<O> getSelectedElements(){
		List<O> result = new ArrayList<O>();
		for(int selectedRow: getSelectedRows())
			result.add(((PropertyBasedTableModel) getModel()).getElementAt(selectedRow));
		return result;
	}
	
	public void selectElement(O review){
		int index = ((PropertyBasedTableModel) getModel()).indexOf(review);
		if(index == -1)
			return;
		setRowSelectionInterval(index, index);
		scrollRectToVisible(new Rectangle(getCellRect(index, 0, true)));
	}
	
	public int indexOf(O element){
		return ((PropertyBasedTableModel) getModel()).indexOf(element);
	}
	
	public O getElementAt(int rowIndex){
		return ((PropertyBasedTableModel) getModel()).getElementAt(rowIndex);
	}

	public void sort(E property, boolean descending) throws Exception{
		try {
			((PropertyBasedTableModel) getModel()).sort(property, descending);
		} catch (Exception e) {
			throw new Exception("Sorting operation for property \"" + property + "\" failed.", e);
		}
	}
	
	public String getPropertyValueOfSelectedRow(E property){
		if(getSelectedRow() == -1)
			return null;
		return ((PropertyBasedTableModel) getModel()).getPropertyValueOfElementInRowAsString(property, getSelectedRow());
	}
	
	public E getPropertyOfColumn(int columnIndex){
		return getPropertiesOfInterest()[columnIndex];
	}
	
	public int getColumnIndexOfProperty(E property){
		for(int i=0; i<getPropertiesOfInterest().length; i++)
			if(getPropertyOfColumn(i).equals(property))
				return i;
		return -1;
	}
	
	protected String convertPropertyValueToString(O element, E property, Object propertyValue) {
		if(propertyValue == null)
			return "";
		if(propertyValue instanceof Date)
			return getDefaultDateFormat().format((Date) propertyValue);
		if(propertyValue instanceof String)
			return (String) propertyValue;
		return propertyValue.toString();
	}
	
	protected DateFormat getDefaultDateFormat() {
		return DEFAULT_DATE_FORMAT;
	}

	@Override
	public void loadTableData() throws Exception {
		List<O> previouslySelectedElements = getSelectedElements();
		super.loadTableData();
		if(!previouslySelectedElements.isEmpty() && PropertyBasedTableModel.class.isAssignableFrom(getModel().getClass())){
			for(O selectedElement: previouslySelectedElements){
				int indexOfElement = indexOf(selectedElement);
				if(indexOfElement > -1)
					addRowSelectionInterval(indexOfElement, indexOfElement);
			}
		} else {
			if(getRowCount() > 0)
				setRowSelectionInterval(0, 0);
		}
	}

	public class PropertyBasedTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 4620848935486253765L;
		
		private static final String EXCEPTION_DURING_PROPERTY_EXTRACTION = "! Property Value Extraction Exception !";
		
		private List<O> modelElements = null;

		public PropertyBasedTableModel(List<O> elements) {
			super();
			Validate.notNull(elements);
			this.modelElements = elements;
		}
		
		public void sort(E property, boolean descending) throws Exception{
			sort(colIndexOfProperty(property), descending);
		}
		
		public void sort(int column, boolean descending) throws Exception{
			if(modelElements.isEmpty() || column < 0 || column > getPropertiesOfInterest().length)
				return;
			Comparator comparator = modelElements.get(0).getComparator(getPropertiesOfInterest()[column], descending);
			if(comparator == null)
				return;
			try {
				Collections.sort(modelElements, comparator);
			} catch (Exception e) {
				throw new Exception("Sorting operation failed.", e);
			}
			fireTableDataChanged();
		}
		
		private int colIndexOfProperty(E property){
			for(int i=0;i<getPropertiesOfInterest().length;i++){
				if(getPropertiesOfInterest()[i].equals(property))
					return i;
			}
			return -1;
		}
		
		@Override
		public String getColumnName(int column) { 
			return getPropertiesOfInterest()[column].toString(); 
		}

		@Override
		public int getRowCount() {
			return modelElements.size();
		}

		@Override
		public int getColumnCount() {
			return getPropertiesOfInterest().length;
		}
		
		public String getPropertyValueOfElementInRowAsString(E property, int rowIndex){
			return getValueAt(rowIndex, colIndexOfProperty(property));
		}
		
		public O getElementAt(int rowIndex){
			return modelElements.get(rowIndex);
		}
		
		public int indexOf(O element){
			for(int index=0; index<modelElements.size(); index++){
				if(modelElements.get(index).equals(element))
					return index;
			}
			return -1;
		}

		@Override
		public String getValueAt(int rowIndex, int columnIndex) {
			O element = modelElements.get(rowIndex);
			E propertyOfInterest = getPropertiesOfInterest()[columnIndex];
			try {
				return convertPropertyValueToString(element, propertyOfInterest, element.getProperty(propertyOfInterest));
			} catch (Exception e) {
				return EXCEPTION_DURING_PROPERTY_EXTRACTION;
			}
		}
		
		public boolean isCellEditable(int row, int column) {
			if(getEditableProperties() == null)
				return false;
			return ArrayUtils.contains(getEditableProperties(), getPropertiesOfInterest()[column]);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(!isCellEditable(rowIndex, columnIndex))
				return;
			
			E colProperty = getPropertiesOfInterest()[columnIndex];
			O element = getElementAt(rowIndex);
			if(element == null)
				return;
			try {
				element.setProperty(colProperty, aValue);
				fireTableCellUpdated(rowIndex, columnIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void removeElementAt(int index){
			modelElements.remove(index);
			fireTableDataChanged();
		}
		
	}

}
