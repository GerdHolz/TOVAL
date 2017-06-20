package de.invation.code.toval.graphic.component;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListWithAccessibleRenderedComponents<E> extends JList<E> {

	private static final long serialVersionUID = 3278047751203642519L;
	
	private static final String METHOD_NAME_GET_TEXT = "getText";
	private static final String EMPTY_STRING = "";
	
	private List<String> stringRep = null;

	public ListWithAccessibleRenderedComponents() {
		super();
		setStringRep();
	}

	public ListWithAccessibleRenderedComponents(E[] listData) {
		super(listData);
		setStringRep();
	}

	public ListWithAccessibleRenderedComponents(ListModel<E> dataModel) {
		super(dataModel);
		setStringRep();
	}

	public ListWithAccessibleRenderedComponents(Vector<? extends E> listData) {
		super(listData);
		setStringRep();
	}
	
	
	@Override
	public void setModel(ListModel<E> model) {
		super.setModel(model);
		setStringRep();
	}

	@Override
	public void setCellRenderer(ListCellRenderer<? super E> cellRenderer) {
		super.setCellRenderer(cellRenderer);
		setStringRep();
	}

	private void setStringRep(){
		stringRep = new ArrayList<>();
		Object comp = null;
		Method method = null;
		Object compValue = null;
		for(int i=0; i<getModel().getSize(); i++){
			comp = getRenderedComponentAtIndex(i);
			try {
				method = comp.getClass().getMethod(METHOD_NAME_GET_TEXT);
				compValue = method.invoke(comp);
				stringRep.add(String.valueOf(compValue));
			} catch (Exception e) {
				stringRep.add(EMPTY_STRING);
			}
		}
	}
	
	public String getStringRepOfRenderedComponentAtIndex(int index){
		try {
			return stringRep.get(index);
		} catch(Exception e){
			return EMPTY_STRING;
		}
	}

	public Component getRenderedComponentAtIndex(int index) {
        if (index < 0 || index >= getModel().getSize()) {
            return null;
        }
        if ((getModel() != null) && getCellRenderer() != null) {
            E value = this.getModel().getElementAt(index);
            boolean isSelected = isSelectedIndex(index);
            boolean isFocussed = isFocusOwner() && (index == getLeadSelectionIndex());
            return getCellRenderer().getListCellRendererComponent(
                    this,
                    value,
                    index,
                    isSelected,
                    isFocussed);
        } else {
            return null;
        }
    }

}
