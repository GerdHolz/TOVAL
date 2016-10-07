package de.invation.code.toval.graphic.dialog;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import de.invation.code.toval.graphic.util.SpringUtilities;


public class StringListGeneratorDialog extends AbstractDialog<List<String>> {

	private static final long serialVersionUID = -3983005550619271102L;
	
	private JTextField numberField;
	private JTextField prefixField;
	private JTextField postfixField;
	
	protected StringListGeneratorDialog(Window owner, String title) {
		super(owner, title);
	}
	
	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new SpringLayout());
		
		JLabel lblNumber = new JLabel("Number:");
		lblNumber.setHorizontalAlignment(SwingConstants.RIGHT);
		mainPanel().add(lblNumber);
		numberField = new JTextField("10", 10);
		mainPanel().add(numberField);
		
		JLabel lblPrefix = new JLabel("Prefix:");
		lblPrefix.setHorizontalAlignment(SwingConstants.RIGHT);
		mainPanel().add(lblPrefix);
		prefixField = new JTextField(10);
		mainPanel().add(prefixField);
			
		JLabel lblPostfix = new JLabel("Postfix:");
		lblPostfix.setHorizontalAlignment(SwingConstants.RIGHT);
		mainPanel().add(lblPostfix);
		postfixField = new JTextField(10);
		mainPanel().add(postfixField);
		
		SpringUtilities.makeCompactGrid(mainPanel(), 3, 2, 0, 0, 5, 5);
	}
	
	
	
	@Override
	protected void okProcedure() {
		int number = 0;
		try{
			number = Integer.parseInt(numberField.getText());
		}catch(Exception exception){
			JOptionPane.showMessageDialog(StringListGeneratorDialog.this, "Content in number field is not a natural number!", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
			return;
		}
		setDialogObject(createStringList(number, prefixField.getText(), postfixField.getText()));
		super.okProcedure();
	}

	@Override
	protected void setTitle() {}
	
	private List<String> createStringList(int number, String prefix, String postfix) {
		return createStringList(number, prefix + "%s" + postfix);
	}
	
	private List<String> createStringList(int number, String stringFormat) {
		List<String> result = new ArrayList<String>(number);
		for(int i=1; i<=number; i++){
			result.add(String.format(stringFormat, i));
		}
		return result;
	}
	
	public static List<String> showDialog(Window owner, String title) throws Exception{
		StringListGeneratorDialog dialog = new StringListGeneratorDialog(owner, title);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}

}
