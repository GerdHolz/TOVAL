package de.invation.code.toval.graphic.component;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import de.invation.code.toval.graphic.component.event.RestrictedTextFieldListener;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public class RestrictedTextField extends JTextField {

	private static final long serialVersionUID = 3020973663591477811L;
	
	private String oldValue = null;
	private Restriction restriction = null;
	private boolean validateOnTyping = false;
	
	private Set<RestrictedTextFieldListener> listeners = new HashSet<RestrictedTextFieldListener>();

	public RestrictedTextField(Restriction restriction, Document doc, String text, int columns) {
		super(doc, text, columns);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction, int columns) {
		super(columns);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction, String text, int columns) {
		super(text, columns);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction, String text) {
		super(text);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction){
		super();
		initialize(restriction);
	}
	
	public void addListener(RestrictedTextFieldListener listener){
		this.listeners.add(listener);
	}
	
	public void removeListener(RestrictedTextFieldListener listener){
		this.listeners.remove(listener);
	}
	
	private void initialize(Restriction restriction){
		this.restriction = restriction;
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				oldValue = getText();
			}

			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				validateInput();
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyTyped(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					validateInput();
				}
			}
			
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(validateOnTyping)
					validateInput();
			}
		});
	}
	
	private void validateInput() {
		try{
			switch (restriction) {
			case NEGATIVE_DOUBLE:
				Validate.negativeDouble(getText());
				break;
			case NEGATIVE_INTEGER:
				Validate.negativeInteger(getText());
				break;
			case POSITIVE_DOUBLE:
				Validate.positiveDouble(getText());
				break;
			case POSITIVE_INTEGER:
				Validate.positiveInteger(getText());
				break;
			case ZERO_OR_NEGATIVE_DOUBLE:
				Validate.notPositiveDouble(getText());
				break;
			case ZERO_OR_NEGATIVE_INTEGER:
				Validate.notPositiveInteger(getText());
				break;
			case ZERO_OR_POSITIVE_DOUBLE:
				Validate.notNegativeDouble(getText());
				break;
			case ZERO_OR_POSITIVE_INTEGER:
				Validate.notNegativeInteger(getText());
				break;
			case NOT_EMPTY:
				Validate.notEmpty(getText());
				break;
			case NONE:
				break;
			}
		}catch(ParameterException e){
			setText(oldValue);
		}
		notifyValueChanged(oldValue, getText());
	}
	
	private void notifyValueChanged(String oldValue, String newValue){
		if(oldValue.equals(newValue))
			return;
		for(RestrictedTextFieldListener listener: listeners){
			listener.valueChanged(oldValue, newValue);
		}
		this.oldValue = newValue;
	}
	
	public boolean validatesOnTyping() {
		return validateOnTyping;
	}

	public void setValidateOnTyping(boolean validateOnTyping) {
		this.validateOnTyping = validateOnTyping;
	}
	
	public enum Restriction {
		POSITIVE_INTEGER, NEGATIVE_INTEGER, POSITIVE_DOUBLE, NEGATIVE_DOUBLE,
		ZERO_OR_POSITIVE_INTEGER, ZERO_OR_NEGATIVE_INTEGER, ZERO_OR_POSITIVE_DOUBLE, ZERO_OR_NEGATIVE_DOUBLE,
		NOT_EMPTY, NONE;
	}
	
//	public static void main(String[] args) {
//		DummyPanel panel = new DummyPanel();
//		RestrictedTextField t1 = new RestrictedTextField(Restriction.POSITIVE_INTEGER, "20");
//		t1.setPreferredSize(new Dimension(200, 20));
//		t1.addListener(panel);
//		t1.setValidateOnTyping(true);
//		RestrictedTextField t2 = new RestrictedTextField(Restriction.POSITIVE_DOUBLE, "20.5");
//		panel.add(t1);
//		panel.add(t2);
//		new DisplayFrame(panel, true);
//	}
//	
//	private static class DummyPanel extends JPanel implements RestrictedTextFieldListener {
//
//		@Override
//		public void valueChanged(String oldValue, String newValue) {
//			System.out.println("old value: " + oldValue);
//			System.out.println("new value: " + newValue);
//			System.out.println();
//		}
//		
//	}

}
