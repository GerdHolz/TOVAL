package de.invation.code.toval.graphic.component;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.invation.code.toval.types.ElementWithTypedProperties;

public abstract class AbstractPropertyBasedTable<E extends Enum<E>, O extends ElementWithTypedProperties<E>> extends AbstractRowSelectionTable<AbstractTableModel> {

	private static final long serialVersionUID = -3891196745792785590L;

	public AbstractPropertyBasedTable() {
		super();
		getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        ((PropertyBasedTableModel) getModel()).sort(columnAtPoint(e.getPoint()));
		    }
		});
	}
	
	protected abstract E[] getPropertiesOfInterest();
	
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

	public void sort(E property){
		((PropertyBasedTableModel) getModel()).sort(property);
	}
	
	public String getSelectedRowProperty(E property){
		if(getSelectedRow() == -1)
			return null;
		return ((PropertyBasedTableModel) getModel()).getElementPropertyAt(property, getSelectedRow());
	}
	
	protected abstract String convertPropertyValueToString(E property, Object propertyValue);
	
	public class PropertyBasedTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 4620848935486253765L;
		
		private List<O> modelElements = null;

		public PropertyBasedTableModel(List<O> reviews) {
			super();
			this.modelElements = reviews;
		}
		
		public void sort(E property){
			sort(colIndexOfProperty(property));
		}
		
		public void sort(int column){
			if(modelElements.isEmpty() || column < 0 || column > getPropertiesOfInterest().length)
				return;
			Comparator comparator = modelElements.get(0).getComparator(getPropertiesOfInterest()[column]);
			if(comparator == null)
				return;
			Collections.sort(modelElements, comparator);
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
		
		public String getElementPropertyAt(E property, int rowIndex){
			Object pathObject = getValueAt(rowIndex, colIndexOfProperty(property));
			return pathObject != null ? convertPropertyValueToString(property, pathObject) : null;
		}
		
		public O getElementAt(int rowIndex){
			return modelElements.get(rowIndex);
		}
		
		public int indexOf(O element){
			for(int index=0; index<=modelElements.size(); index++){
				if(modelElements.get(index).equals(element))
					return index;
			}
			return -1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			O element = modelElements.get(rowIndex);
			E propertyOfInterest = getPropertiesOfInterest()[columnIndex];
			return convertPropertyValueToString(propertyOfInterest, element.getProperty(propertyOfInterest));
		}
		
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public void removeElementAt(int index){
			modelElements.remove(index);
			fireTableDataChanged();
		}
		
	}

}
