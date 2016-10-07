package de.invation.code.toval.graphic.dialog;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.util.SpringUtilities;
import de.invation.code.toval.misc.StringUtils;

public class ValueDefinitionDialog<O> extends AbstractDialog<List<O>> {

	private static final long serialVersionUID = 6102535150943274087L;

	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	public static final Dimension MINIMUM_SIZE = new Dimension(400, 40);
	public static final String REFLECTION_METHOD_NAME = "valueOf";
	private static final String FORMAT_EXCEPTION_INVOCATION_METHOD = "Cannot determine string conversion method of class \"%s\"";
	private static final String FORMAT_EXCEPTION_INVOCATION_METHOD_CALL = "Cannot invoke string conversion method, please check input.\nValue: %s, target type: %s";

	private static final String INPUT_FIELD_DEFAULT_TEXT = "Valid separators: (semi-)colon, space";

	private JTextField inputField;
	private Class<O> elementClass;
	private Method invocationMethod;

	protected ValueDefinitionDialog(String title, Class<O> elementClass) throws Exception {
		super(title);
		setElementClass(elementClass);
	}

	protected ValueDefinitionDialog(Window owner, String title, Class<O> elementClass) throws Exception {
		super(owner, title);
		setElementClass(elementClass);
	}
	
	private void setElementClass(Class<O> elementClass) throws Exception{
		this.elementClass = elementClass;
		if (!String.class.isAssignableFrom(elementClass)) {
			try {
				invocationMethod = elementClass.getMethod(REFLECTION_METHOD_NAME, String.class);
			} catch (Exception e) {
				throw new Exception(String.format(FORMAT_EXCEPTION_INVOCATION_METHOD, elementClass.getSimpleName()), e);
			}
		}
	}
	
	public static <OO> boolean isCompatible(Class<OO> elementClass) {
		if (String.class.isAssignableFrom(elementClass))
			return true;
		try {
			elementClass.getMethod(REFLECTION_METHOD_NAME, String.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected void addComponents() throws Exception {
		setResizable(true);
		mainPanel().setLayout(new SpringLayout());
		JLabel lblNumber = new JLabel("Number:");
		lblNumber.setHorizontalAlignment(SwingConstants.RIGHT);
		mainPanel().add(lblNumber);
		mainPanel().add(getInputField());
		SpringUtilities.makeCompactGrid(mainPanel(), 1, 2, 0, 0, 5, 0);
	}

	private JTextField getInputField() {
		if (inputField == null) {
			inputField = new JTextField(INPUT_FIELD_DEFAULT_TEXT);
			inputField.setColumns(10);
			inputField.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (inputField.getText().equals(INPUT_FIELD_DEFAULT_TEXT))
						inputField.setText("");
				}
			});
		}
		return inputField;
	}

	@Override
	protected void setTitle() {
	}


	@Override
	protected Dimension getDefaultPreferredSize() {
		return MINIMUM_SIZE;
	}

	@Override
	protected void okProcedure() {
		if (inputField.getText().isEmpty()) {
			ExceptionDialog.showException(ValueDefinitionDialog.this, "Invalid Parameter",
					new Exception("Cannot proceed with empty string."), true, true);
			return;
		}
		int colons = StringUtils.countOccurrences(inputField.getText(), ',');
		int semicolons = StringUtils.countOccurrences(inputField.getText(), ';');
		if (colons > 0 && semicolons > 0) {
			ExceptionDialog.showException(ValueDefinitionDialog.this, "Invalid Parameter",
					new Exception("String contains more than one possible delimiter."), true, true);
			return;
		}
		String delimiter = " ";
		if (colons > 0)
			delimiter = ",";
		if (semicolons > 0)
			delimiter = ";";

		setDialogObject(new ArrayList<O>());
		StringTokenizer tokenizer = new StringTokenizer(inputField.getText(), delimiter);
		if (tokenizer.hasMoreTokens()) {
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (!delimiter.equals(" "))
					token = StringUtils.removeSurrounding(token, ' ');

				if (String.class.isAssignableFrom(elementClass)) {
					getDialogObject().add((O) token);
				} else {
					O element = null;
					try {
						element = (O) invocationMethod.invoke(null, token);
					} catch (Exception e) {
						ExceptionDialog.showException(ValueDefinitionDialog.this, "Conversion Exception", new Exception(String.format(FORMAT_EXCEPTION_INVOCATION_METHOD_CALL, token, elementClass.getSimpleName()), e), true, true);
						return;
					}
					getDialogObject().add(element);
				}
			}
		}
		super.okProcedure();
	}

	public static <O> List<O> showDialog(String title, Class<O> elementClass) throws Exception {
		return showDialog(null, title, elementClass);
	}

	public static <O> List<O> showDialog(Window owner, String title, Class<O> elementClass) throws Exception {
		ValueDefinitionDialog<O> dialog = new ValueDefinitionDialog<O>(owner, title, elementClass);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}

//	public static void main(String[] args) throws Exception {
//		CollectionUtils.print(StringListDefinitionDialog.showDialog("Test", String.class));
//	}

}
