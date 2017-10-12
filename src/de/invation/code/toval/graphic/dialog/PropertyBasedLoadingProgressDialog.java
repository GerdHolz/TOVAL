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
	private JTextField txtTask;
	private JTextField txtStep;
	private JPanel panelBars;

	public PropertyBasedLoadingProgressDialog(Component component, String title) {
		this(SwingUtilities.getWindowAncestor(component), title);
	}
	
	public PropertyBasedLoadingProgressDialog(Window owner, String title) {
		super(owner);
		this.setResizable(true);
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanelBars(), BorderLayout.PAGE_START);
		getContentPane().add(new JPanel(), BorderLayout.CENTER);
		setTitle(title);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
		pack();
        this.setLocationRelativeTo(owner);
	}
	
	private JPanel getPanelBars(){
		if(panelBars == null){
			panelBars = new JPanel(new SpringLayout());
			fillAndLayoutPanelBars();
		}
		return panelBars;
	}
	
	private void fillAndLayoutPanelBars(){
		panelBars.add(new JLabel("Task:", SwingConstants.LEADING));
		panelBars.add(getTextFieldTask());
		panelBars.add(new JLabel("Step:", SwingConstants.LEADING));
		panelBars.add(getTextFieldStep());
		panelBars.add(new JLabel("Progress:"));
		panelBars.add(getProgressBarStep());
		SpringUtilities.makeCompactGrid(panelBars, 3, 2, 10, 10, 10, 10);
	}
	
	private JTextField getTextFieldTask(){
		if(txtTask == null){
			txtTask = new JTextField();
			txtTask.setEditable(false);
			txtTask.setBorder(BorderFactory.createEmptyBorder());
		}
		return txtTask;
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
	
	public void setTask(String task){
		getTextFieldTask().setText(task);
		panelBars.removeAll();
		fillAndLayoutPanelBars();
		invalidate();
		pack();
		repaint();
	}
	
	public void setStep(String step){
		getTextFieldStep().setText(step);
		panelBars.removeAll();
		fillAndLayoutPanelBars();
		invalidate();
		pack();
		repaint();
	}
	
	public void setStepProgress(int stepProgress){
		getProgressBarStep().setValue(stepProgress);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SimpleSwingTask.PROPERTY_NAME_TASK_ACTION)){
			setTask((String) evt.getNewValue());
			setStep("");
		} else if(evt.getPropertyName().equals(SimpleSwingTask.PROPERTY_NAME_STEP_ACTION)){
			setStep((String) evt.getNewValue());
		} else if (evt.getPropertyName().equals(SimpleSwingTask.PROPERTY_NAME_TASK_STEP_PROGRESS)) {
			setStepProgress((Integer) evt.getNewValue());
		} 
	}
	
	public static void main(String[] args) throws InterruptedException {
		PropertyBasedLoadingProgressDialog dialog = new PropertyBasedLoadingProgressDialog(null, "Test");
		TestTask loadingTaskAdonis = new TestTask() {

			@Override
			protected void done() {
				super.done();
				dialog.dispose();
			}

		};
		loadingTaskAdonis.addPropertyChangeListener(dialog);
		loadingTaskAdonis.execute();
		dialog.setVisible(true);
	}
	
	private static class TestTask extends SimpleSwingTask<Integer, Integer> {

		@Override
		protected Integer internalProcedure() throws Exception {
			Thread.sleep(1000);
			notifyStepAction("Teststep 1");
			notifyStepProgress(20);
			Thread.sleep(1000);
			notifyStepAction("Teststep 2 -sfsfd-sdf-sdfsfd-sd-f-sdf-s-df-s-df-s-df--s-dgf-d-fg-d-fg-d-fg--dfg");
			notifyStepProgress(40);
			Thread.sleep(2000);
			notifyStepAction("Teststep 3");
			notifyStepProgress(60);
			Thread.sleep(1000);
			notifyStepAction("Teststep 4");
			notifyStepProgress(80);
			return 1;
		}
		
	}

}
