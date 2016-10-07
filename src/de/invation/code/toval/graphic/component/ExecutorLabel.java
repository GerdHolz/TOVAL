package de.invation.code.toval.graphic.component;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.icons.IconFactory;
import de.invation.code.toval.thread.ExecutorListener;
import de.invation.code.toval.thread.SingleThreadExecutorService;
import de.invation.code.toval.validate.Validate;

public abstract class ExecutorLabel<Z> extends JLabel implements ExecutorListener<Z> {
	
	private static final long serialVersionUID = 8368680392887232591L;
	private static final Dimension DEFAULT_SIZE = new Dimension(40,40);
	public static final Color COLOR_INITIAL = Color.BLUE;
	public static final Color COLOR_DONE = Color.GREEN;
	public static final Color COLOR_CANCELLED = Color.LIGHT_GRAY;
	public static final Color COLOR_EXCEPTION = Color.RED;
	
	protected SingleThreadExecutorService<?,Z,?> executorService = null;
	
	private boolean running = false;
	
	public ExecutorLabel(){
		super();
		
		setPreferredSize(DEFAULT_SIZE);
		setMinimumSize(DEFAULT_SIZE);
		setMaximumSize(DEFAULT_SIZE);
		setOpaque(true);
		setBackground(COLOR_INITIAL);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!isEnabled())
					return;
				if(!running){
					// Start executor service
					try {
						startExecutor();
					} catch (Exception e1) {
						ExceptionDialog.showException(SwingUtilities.getWindowAncestor(ExecutorLabel.this), "Execution exception", new Exception("Exception while starting execution.", e1), true);
					}
				} else {
					// Stop executor service
					try {
						stopExecutor();
					} catch (Exception e1) {
						ExceptionDialog.showException(SwingUtilities.getWindowAncestor(ExecutorLabel.this), "Execution exception", new Exception("Exception while stopping execution.", e1), true);
					}
				}
			}
		});
	}

	public ExecutorLabel(SingleThreadExecutorService<?,Z,?> executorService){
		this();
		setExecutor(executorService);
	}
	
	public void setExecutor(SingleThreadExecutorService<?,Z,?> executorService){
		Validate.notNull(executorService);
		executorService.addExecutorListener(this);
		this.executorService = executorService;
	}
	
	protected synchronized void startExecutor() throws Exception{
		executorService.setUpAndRun();
	}
	
	protected void setGraphicsRunning() {
		setOpaque(false);
		setIcon(getLoadingDotsIcon());
		if(executorService.isDone())
			setGraphicsFinished();
	}
	
	protected synchronized void stopExecutor() throws Exception{
		executorService.stop();
	}
	
	protected Color getColorInitial(){
		return COLOR_INITIAL;
	}
	
	protected Color getColorCancelled(){
		return COLOR_CANCELLED;
	}
	
	protected Color getColorDone(){
		return COLOR_DONE;
	}
	
	protected Color getColorException(){
		return COLOR_EXCEPTION;
	}
	
	protected void setGraphicsInitial() {
		setOpaque(true);
		setIcon(null);
		setBackground(getColorInitial());
	}
	
	protected void setGraphicsCancelled() {
		setOpaque(true);
		setIcon(null);
		setBackground(getColorCancelled());
	}
	
	protected void setGraphicsException() {
		setOpaque(true);
		setIcon(null);
		setBackground(getColorException());
	}
	
	protected void setGraphicsFinished() {
		setOpaque(true);
		setIcon(null);
		setBackground(getColorDone());
	}

	static ImageIcon getLoadingDotsIcon() {
		String path = "loading-dots.gif";
		URL imageURL = IconFactory.class.getResource(path);
		return new ImageIcon(imageURL);
	}

	@Override
	public void executorStarted() {
		running = true;
		setGraphicsRunning();
	}

	@Override
	public void executorStopped() {
		running = false;
		setGraphicsCancelled();
	}

	@Override
	public void executorFinished(Z result) {
		running = false;
		setGraphicsFinished();
	}
	
	@Override
	public void executorException(Exception exception) {
		running = false;
		setGraphicsException();
	}

	@Override
	public void progress(double progress) {}
	
	public void reset() throws Exception{
		if(executorService != null && !executorService.isDone()){
			stopExecutor();
		} else {
			setGraphicsInitial();
		}
	}

}
