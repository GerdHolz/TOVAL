package de.invation.code.toval.graphic.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.os.OSType;
import de.invation.code.toval.os.OSUtils;

public abstract class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = -2953880281470574624L;

	public static final Dimension MINIMUM_SIZE = new Dimension(1280, 800);
	
	public static final boolean DEFAULT_STACK_TRACE_OPTION_IN_EXCEPTION_DIALOG = true;
	public static final boolean DEFAULT_CONCAT_CAUSE_MESSAGES_IN_EXCEPTION_DIALOG = true;
	public static final String DEFAULT_EXCEPTION_DIALOG_TITLE = "Exception";
	public static final boolean DEFAULT_INCLUDE_MENU_BAR = true;
	
	private JPanel 			pnlContent 		= null;
	private JComponent 		compTop 		= null;
	private JComponent 		compBottom 		= null;
	private JComponent 		compCenter 		= null;
	private JComponent 		compRight 		= null;
	private JComponent 		compLeft 		= null;

	public ApplicationFrame() throws Exception{
		try {
			startupProcedure();
		} catch(Exception e){
			ExceptionDialog.showException("Exception", e);
			System.exit(0);
		}
	}
	
	protected void startupProcedure() throws Exception {}
	
	
	protected abstract String getToolName();

	public final void setUpGUI() throws Exception{
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLookAndFeel();
		
		addListeners();
		listenersAdded();
		
		setTitle(getToolName());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanelContent(), BorderLayout.CENTER);
		if(includesMenuBar())
			setJMenuBar(createMenuBar());
		componentsAdded();

		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);	
	}
	
	protected boolean includesMenuBar(){
		return DEFAULT_INCLUDE_MENU_BAR;
	}
	
	protected JMenuBar createMenuBar() throws Exception {
		return new JMenuBar();
	}

	protected void listenersAdded() throws Exception {}
	
	protected void componentsAdded() throws Exception {}
	
	private final JComponent getPanelContent() throws Exception {
        if (pnlContent == null) {
            pnlContent = new JPanel(new BorderLayout());
            pnlContent.add(getComponentCenter(), BorderLayout.CENTER);
            JComponent cmpLeft = getComponentLeft();
            if (cmpLeft != null) {
                pnlContent.add(cmpLeft, BorderLayout.LINE_START);
            }
            JComponent cmpRight = getComponentRight();
            if (cmpRight != null) {
                pnlContent.add(cmpRight, BorderLayout.LINE_END);
            }
            JComponent cmpTop = getComponentTop();
            if (cmpTop != null) {
                pnlContent.add(cmpTop, BorderLayout.PAGE_START);
            }
            JComponent cmpBottom = getComponentBottom();
            if (cmpBottom != null) {
                pnlContent.add(cmpBottom, BorderLayout.PAGE_END);
            }
        }
        return pnlContent;
    }
	
	protected JComponent getComponentCenter() throws Exception {
        if (compCenter == null) {
        	compCenter = createComponentCenter();
        }
        return compCenter;
    }

	protected JComponent getComponentRight() throws Exception {
        if (compRight == null) {
        	compRight = createComponentRight();
        }
        return compRight;
    }
    
    protected JComponent getComponentLeft() throws Exception {
        if (compLeft == null) {
        	compLeft = createComponentLeft();
        }
        return compLeft;
    }
    
    protected JComponent getComponentTop() throws Exception {
        if (compTop == null) {
        	compTop = createComponentTop();
        }
        return compTop;
    }
    
    protected JComponent getComponentBottom() throws Exception {
    	if (compBottom == null) {
        	compTop = createComponentBottom();
        }
        return compTop;
    }
    
    protected JComponent createComponentCenter() throws Exception {
		return null;
	}
    
    protected JComponent createComponentTop() throws Exception {
		return null;
	}
    
    protected JComponent createComponentBottom() throws Exception {
		return null;
	} 
    
    protected JComponent createComponentLeft() throws Exception {
		return null;
	}
    
    protected JComponent createComponentRight() throws Exception {
		return null;
	}
	
    protected void setLookAndFeel() {
        if (OSUtils.getCurrentOS() == OSType.OS_LINUX || OSUtils.getCurrentOS() == OSType.OS_SOLARIS) {
            try {
                setLocationByPlatform(true);
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            }
        } else if (OSUtils.getCurrentOS() == OSType.OS_WINDOWS) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            }
        }
    }
    
	@Override
	public Dimension getPreferredSize(){
		return MINIMUM_SIZE;
	}
	
	@Override
	public Dimension getMinimumSize(){
		return MINIMUM_SIZE;
	}
 
    private void addListeners() {
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            	procedureWindowOpened();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            	procedureWindowIconified();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            	procedureWindowDeiconified();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            	procedureWindowDeactivated();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                procedureWindowClosing();
            }

			@Override
            public void windowClosed(WindowEvent e) {
				procedureWindowClosed();
            }

            @Override
            public void windowActivated(WindowEvent e) {
            	procedureWindowActivated();
            }
        });
        
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = ApplicationFrame.this.getSize();
                Dimension minD = ApplicationFrame.this.getMinimumSize();
                if (d.width < minD.width) {
                    d.width = minD.width;
                }
                if (d.height < minD.height) {
                    d.height = minD.height;
                }
                ApplicationFrame.this.setSize(d);
            }
        });
    }
    
    protected void procedureWindowOpened() {}
    
	protected void procedureWindowClosing() {}
	
	protected void procedureWindowClosed() {}
	
	protected void procedureWindowActivated() {}
	
	protected void procedureWindowDeactivated() {}
	
	protected void procedureWindowIconified() {}
	
	protected void procedureWindowDeiconified() {}
	
	protected void showException(Exception exception){
		showException(DEFAULT_EXCEPTION_DIALOG_TITLE, exception);
	}
	
	protected void showException(String title, Exception exception){
		showException(title, exception, DEFAULT_CONCAT_CAUSE_MESSAGES_IN_EXCEPTION_DIALOG, DEFAULT_STACK_TRACE_OPTION_IN_EXCEPTION_DIALOG);
	}
	
	protected void showException(String title, Exception exception, boolean concatCauseMessages, boolean stackTraceOption){
		ExceptionDialog.showException(ApplicationFrame.this, title, exception, concatCauseMessages, stackTraceOption);
	}

}
