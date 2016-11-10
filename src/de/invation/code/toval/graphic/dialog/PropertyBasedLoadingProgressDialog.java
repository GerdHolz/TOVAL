package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.invation.code.toval.graphic.util.SpringUtilities;
import de.invation.code.toval.misc.SimpleSwingTask;

public class PropertyBasedLoadingProgressDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = -6065059569175534353L;
	
	private JProgressBar progressBarStep;
	private JTextField txtStep;

	public PropertyBasedLoadingProgressDialog(Component component, String title) {
		this(SwingUtilities.getWindowAncestor(component), title);
	}
	
	public PropertyBasedLoadingProgressDialog(Window owner, String title) {
		super(owner);
		this.setResizable(true);
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		JPanel panelBars = new JPanel(new SpringLayout());
		panelBars.add(new JLabel("Step:", SwingConstants.TRAILING));
		panelBars.add(getTextFieldStep());
		panelBars.add(new JLabel("Progress:"));
		panelBars.add(getProgressBarStep());
		SpringUtilities.makeCompactGrid(panelBars, 2, 2, 10, 10, 10, 10);
		getContentPane().add(panelBars, BorderLayout.PAGE_START);
		getContentPane().add(new JPanel(), BorderLayout.CENTER);
		setTitle(title);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
		pack();
        this.setLocationRelativeTo(owner);
	}
	
	private JTextField getTextFieldStep(){
		if(txtStep == null){
			txtStep = new JTextField();
			txtStep.setEditable(false);
			txtStep.setBorder(BorderFactory.createEmptyBorder());
		}
		return txtStep;
	}
	
	private JProgressBar getProgressBarStep(){
		if(progressBarStep == null){
			progressBarStep = new JProgressBar(0, 100);
	        progressBarStep.setValue(0);
	        progressBarStep.setStringPainted(true);
		}
		return progressBarStep;
	}
	
	public void setStep(String step){
		txtStep.setText(step);
	}
	
	public void setStepProgress(int stepProgress){
		getProgressBarStep().setValue(stepProgress);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SimpleSwingTask.PROPERTY_NAME_TASK_ACTION)){
			setStep((String) evt.getNewValue());
		} else if (evt.getPropertyName().equals(SimpleSwingTask.PROPERTY_NAME_TASK_STEP_PROGRESS)) {
			setStepProgress((Integer) evt.getNewValue());
		} 
	}

}
