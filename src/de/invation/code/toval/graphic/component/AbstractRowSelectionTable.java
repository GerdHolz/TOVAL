package de.invation.code.toval.graphic.component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

public abstract class AbstractRowSelectionTable<T extends AbstractTableModel> extends JTable {

	private static final long serialVersionUID = 8044533155183692980L;
	public static final int DEFAULT_ROW_HEIGHT = 25;
	public static final int DEFAULT_SELECTION_MODE = ListSelectionModel.SINGLE_SELECTION;

	public AbstractRowSelectionTable() {
		super();
		setSelectionMode(DEFAULT_SELECTION_MODE);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	
	protected abstract int[] getColumnWidths();
	
	public int getColumnWithSum(){
		int result = 0;
		for(int colWidth: getColumnWidths())
			result += colWidth;
		return result;
	}
	
	protected abstract AbstractTableModel createNewTableModel();
	
	public void loadTableData() throws Exception{
		setModel(createNewTableModel());
		if(getModel().getRowCount() == 0)
			return;
		setRowSelectionInterval(0, 0);
		for(int i=0; i<getColumnModel().getColumnCount();i++){
			if(i >= getColumnWidths().length)
				break;
			getColumnModel().getColumn(i).setMinWidth(getColumnWidths()[i]);
			getColumnModel().getColumn(i).setMaxWidth(getColumnWidths()[i]);
		}
		setRowHeight(DEFAULT_ROW_HEIGHT);
		((AbstractTableModel) getModel()).fireTableDataChanged();
	}

}
