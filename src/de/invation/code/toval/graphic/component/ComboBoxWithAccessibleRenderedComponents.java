package de.invation.code.toval.graphic.component;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboBoxWithAccessibleRenderedComponents<E> extends JComboBox<E> {

	private static final long serialVersionUID = -1505212588220595611L;
	
	private static final String METHOD_NAME_GET_TEXT = "getText";
	private static final String EMPTY_STRING = "";
	
	private List<String> stringRep = null;
	
	private Map<Long,KeyEvent> keyEventsByTime = new ConcurrentSkipListMap<>();
	private static final int MAX_STOREGE_TIME_FOR_KEY_EVENTS_IN_MILLIS = 700;
	private long lastTimestamp = 1L;

	public ComboBoxWithAccessibleRenderedComponents() {
		super();
		setStringRep();
		addListener();
	}

	public ComboBoxWithAccessibleRenderedComponents(ComboBoxModel<E> aModel) {
		super(aModel);
		setStringRep();
		addListener();
	}

	public ComboBoxWithAccessibleRenderedComponents(E[] items) {
		super(items);
		setStringRep();
		addListener();
	}

	public ComboBoxWithAccessibleRenderedComponents(Vector<E> items) {
		super(items);
		setStringRep();
		addListener();
	}
	
	@Override
	public void setModel(ComboBoxModel<E> aModel) {
		super.setModel(aModel);
		setStringRep();
		addListener();
	}

	@Override
	public void setRenderer(ListCellRenderer<? super E> aRenderer) {
		super.setRenderer(aRenderer);
		setStringRep();
		addListener();
	}
	
	private void addListener(){
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getWhen() == lastTimestamp)
					return;
				lastTimestamp = e.getWhen();
				String searchString = updateKeys(e);
				if(searchString != null)
					searchValue(searchString);
			}
			
		});
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
        if ((getModel() != null) && getRenderer() != null) {
            E value = this.getModel().getElementAt(index);
            boolean isSelected = getSelectedIndex() == index;
            return getRenderer().getListCellRendererComponent(
            		new JList(),
            		value,
                    index,
                    isSelected,
                    true);
        } else {
            return null;
        }
    }
	
	private String updateKeys(KeyEvent keyEvent){
		if(			keyEvent.getKeyChar() == '\uFFFF' 
				|| 	keyEvent.getKeyCode() == KeyEvent.VK_DELETE 
				||	keyEvent.getKeyCode() == KeyEvent.VK_ALT
				||	keyEvent.getKeyCode() == KeyEvent.VK_ALT_GRAPH
				||	keyEvent.getKeyCode() == KeyEvent.VK_CONTROL)
			return null;
			
		long now = System.currentTimeMillis();
		keyEventsByTime.put(now, keyEvent);
		StringBuilder builder = new StringBuilder();
		for(Long time: keyEventsByTime.keySet()){
			if(now - time > MAX_STOREGE_TIME_FOR_KEY_EVENTS_IN_MILLIS){
				keyEventsByTime.remove(time);
			} else {
				builder.append(keyEventsByTime.get(time).getKeyChar());
			}
		}
		return builder.toString().toLowerCase();
	}
	
	private void searchValue(String searchString) {
		for(int i=0; i<getModel().getSize(); i++){
			if(getStringRepOfRenderedComponentAtIndex(i).toLowerCase().startsWith(searchString)){
				setSelectedIndex(i);
				break;
			}
		}
	}

}
