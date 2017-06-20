package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.component.ListWithAccessibleRenderedComponents;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class ValueChooserDialog<O> extends AbstractDialog<List<O>> {
	
	private static final long serialVersionUID = -2157336521383579034L;
	
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	public static final int DEFAULT_SELECTION_MODE = ListSelectionModel.SINGLE_SELECTION;

	private DefaultListModel<O> stringListModel = new DefaultListModel<O>();
	private Collection<O> possibleValues;
	private int selectionMode;
	private int minWidth;
	private ListWithAccessibleRenderedComponents<O> stringList;
	private Comparator<O> valueComparator;
	
	private Map<Long,KeyEvent> keyEventsByTime = new ConcurrentSkipListMap<>();
	private String actualSearchString = null;
	private static final int MAX_STOREGE_TIME_FOR_KEY_EVENTS_IN_MILLIS = 700;

	
	
	public ValueChooserDialog(Window owner, String title, Collection<O> possibleValues) throws Exception {
		super(owner, title);
		setPossibleValues(possibleValues);
	}
	
	public ValueChooserDialog(Window owner, String title, Collection<O> possibleValues, int selectionMode) {
		super(owner, title);
		setPossibleValues(possibleValues);
		this.selectionMode = selectionMode;
	}
	
	public ValueChooserDialog(Window owner, String title, Collection<O> possibleValues, int selectionMode, int minWidth) {
		super(owner, title);
		setPossibleValues(possibleValues);
		this.selectionMode = selectionMode;
		this.minWidth = minWidth;
	}
	
	private void setPossibleValues(Collection<O> possibleValues) throws ParameterException{
		Validate.notNull(possibleValues);
		Validate.notEmpty(possibleValues);
		Validate.noNullElements(possibleValues);
		
		this.possibleValues = possibleValues;
	}
	
	public Comparator<O> getValueComparator() {
		return valueComparator;
	}

	public void setValueComparator(Comparator<O> valueComparator) {
		this.valueComparator = valueComparator;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		if(prefSize.width >= minWidth)
			return prefSize;
		return new Dimension(minWidth, prefSize.height);
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension minSize = super.getMinimumSize();
		if(minSize.width >= minWidth)
			return minSize;
		return new Dimension(minWidth, minSize.height);
	}

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(getListValues());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel().add(scrollPane);
	}

	@Override
	protected void okProcedure() {
		if(!stringListModel.isEmpty()){
			List<O> values = new ArrayList<>();
			for(O o: stringList.getSelectedValuesList())
				values.add(o);
			setDialogObject(values);
			super.okProcedure();
		} else {
			JOptionPane.showMessageDialog(ValueChooserDialog.this, "Value list is empty.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void setTitle() {}
	
	private ListWithAccessibleRenderedComponents<O> getListValues(){
		if(stringList == null){
			Collection<O> elementsToInsert = possibleValues;
			if(getValueComparator() != null){
				List<O> sortedList = new ArrayList<>(possibleValues);
				Collections.sort(sortedList, getValueComparator());
				elementsToInsert = sortedList;
			}
			for(O possibleValue: elementsToInsert){
				stringListModel.addElement(possibleValue);
			}
			stringList = new ListWithAccessibleRenderedComponents<O>(stringListModel);
			stringList.setCellRenderer(new AlternatingRowColorListCellRenderer<O>());
			stringList.setFixedCellHeight(20);
			stringList.setVisibleRowCount(10);
			stringList.getSelectionModel().setSelectionMode(selectionMode);
			stringList.setBorder(null);
			stringList.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					updateKeys(e);
					searchValue();
				}
				
			});
		}
		return stringList;
	}
	
	private void updateKeys(KeyEvent keyEvent){
		long now = System.currentTimeMillis();
		actualSearchString = null;
		if(keyEvent.getKeyChar() != '\uFFFF' && keyEvent.getKeyCode() != KeyEvent.VK_DELETE)
			keyEventsByTime.put(now, keyEvent);
		StringBuilder builder = new StringBuilder();
		for(Long time: keyEventsByTime.keySet()){
			if(now - time > MAX_STOREGE_TIME_FOR_KEY_EVENTS_IN_MILLIS){
				keyEventsByTime.remove(time);
			} else {
				builder.append(keyEventsByTime.get(time).getKeyChar());
			}
		}
		actualSearchString = builder.toString().toLowerCase();
	}
	
	private void searchValue() {
		for(int i=0; i<getListValues().getModel().getSize(); i++){
			if(getListValues().getStringRepOfRenderedComponentAtIndex(i).toLowerCase().startsWith(actualSearchString)){
				getListValues().setSelectedIndex(i);
				break;
			}
		}
	}
	
	public void setListCellRenderer(ListCellRenderer<? super O> renderer){
		getListValues().setCellRenderer(renderer);
	}
	
	public static <T> List<T> showDialog(Window owner, String title, Collection<T> values) throws Exception{
		ValueChooserDialog<T> dialog = new ValueChooserDialog<T>(owner, title, values);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static <T> List<T> showDialog(Window owner, String title, Collection<T> values, int selectionMode) throws Exception{
		ValueChooserDialog<T> dialog = new ValueChooserDialog<T>(owner, title, values, selectionMode);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static <T> List<T> showDialog(Window owner, String title, Collection<T> values, int selectionMode, int minWidth) throws Exception{
		ValueChooserDialog<T> dialog = new ValueChooserDialog<T>(owner, title, values, selectionMode, minWidth);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
//	public static void main(String[] args) throws Exception {
//		ValueChooserDialog.showDialog(null, "Test", Arrays.asList("Gerd", "Hans", "Idefix", "Insel", "Isolde", "Kurt"));
//	}
	
}
