package de.invation.code.toval.misc;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;


public abstract class SimpleSwingTask<T,V> extends SwingWorker<T,V> {
	
	public static final String PROPERTY_NAME_TASK_ACTION = "taskAction";
	public static final String PROPERTY_NAME_STEP_ACTION = "stepAction";
	public static final String PROPERTY_NAME_TASK_STEP_PROGRESS = "stepProgress";
	public static final String PROPERTY_NAME_TASK_STARTED = "taskStarted";
	public static final String PROPERTY_NAME_TASK_COMPLETED = "taskCompleted";
	
	public static final boolean DEFAULT_IGNORE_INTERRUPTIONS = true;
	
	private String actualTaskAction = null;
	private String actualStepAction = null;
	private int actualStepProgress = 0;
	private Exception taskException = null;
	private boolean ignoreInterruptions = DEFAULT_IGNORE_INTERRUPTIONS;
	private T result;

	public SimpleSwingTask() {
		super();
	}

	public boolean isIgnoreInterruptions() {
		return ignoreInterruptions;
	}
	
	public void setIgnoreInterruptions(boolean ignoreInterruptions) {
		this.ignoreInterruptions = ignoreInterruptions;
	}

	@Override
	protected T doInBackground() throws Exception {
		notifyTaskStarted();
		T result = internalProcedure();
		notifyTaskCompleted();
        return result;
	}
	
	protected abstract T internalProcedure() throws Exception; 
	
	@Override
	protected void done() {
		try {
			result = get();
		} catch (InterruptedException e) {
			if(!isIgnoreInterruptions())
				taskException = e;
		} catch (ExecutionException e) {
			taskException = e;
		}
	}

	public T getResult() {
		return result;
	}

	public Exception getLoadingException() {
		return taskException;
	}

	protected void notifyLoadingAction(String loadingAction){
		String lastLoadingAction = actualTaskAction;
		actualTaskAction = loadingAction;
		getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME_TASK_ACTION, lastLoadingAction, actualTaskAction);
	}
	
	protected void notifyStepAction(String stepAction){
		String lastStepAction = actualStepAction;
		actualStepAction = stepAction;
		getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME_STEP_ACTION, lastStepAction, actualStepAction);
	}
	
	protected void notifyStepProgress(int stepProgress){
		int lastStepProgress = actualStepProgress;
		actualStepProgress = stepProgress;
		getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME_TASK_STEP_PROGRESS, lastStepProgress, actualStepProgress);
	}
	
	private void notifyTaskStarted() {
		getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME_TASK_STARTED, null, true);
	}
	
	private void notifyTaskCompleted() {
		getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME_TASK_COMPLETED, null, true);
	}

}