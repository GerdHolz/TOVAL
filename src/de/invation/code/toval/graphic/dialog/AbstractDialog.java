package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractDialog<O> extends JDialog {

    private static final long serialVersionUID = -5864654213215817665L;
    
    private static final Dimension DEFAULT_PREFERRED_SIZE = new Dimension(400,300);	

    private static final ButtonPanelLayout DEFAULT_BUTTON_LAYOUT = ButtonPanelLayout.LEFT_RIGHT;
    public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private final JPanel panelContent = new JPanel();
    protected JPanel panelButtons = null;

    protected JButton btnOK = null;
    protected JButton btnCancel = null;
    protected O dialogObject;
    protected ButtonPanelLayout buttonLayout = DEFAULT_BUTTON_LAYOUT;
    protected Window owner = null;

    protected String okButtonText = "OK";
    protected boolean includeCancelButton = true;
    
    protected AbstractDialog() {
        super();
    }

    protected AbstractDialog(Window owner) {
        super(owner);
        this.owner = owner;
        initialize();
    }
    
    protected AbstractDialog(String title) {
    	this();
    	Validate.notNull(title);
        setTitle(title);
    }

    protected AbstractDialog(Window owner, String title) {
        this(owner);
        Validate.notNull(title);
        setTitle(title);
    }
    
    protected AbstractDialog(ButtonPanelLayout buttonLayout) {
        this();
        setButtonPanelLayout(buttonLayout);
    }

    protected AbstractDialog(Window owner, ButtonPanelLayout buttonLayout) {
        this(owner);
        setButtonPanelLayout(buttonLayout);
    }
    
    protected AbstractDialog(String title, ButtonPanelLayout buttonLayout) {
        this(title);
        setButtonPanelLayout(buttonLayout);
    }
    
    public void setButtonPanelLayout(ButtonPanelLayout buttonLayout){
        Validate.notNull(buttonLayout);
        this.buttonLayout = buttonLayout;
    }

    protected void initialize() {
    	setPreferredSize(getDefaultPreferredSize());
    }

    public void setUpGUI() throws Exception {
        this.setResizable(true);
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addListeners();

        setTitle();

        mainPanel().setBorder(getBorder());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel(), BorderLayout.CENTER);
        getContentPane().add(getPanelButtons(), BorderLayout.SOUTH);
        getRootPane().setDefaultButton(getButtonDefault());

        addComponents();

        initializeContent();

        pack();
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    protected void initializeContent() throws Exception {}

    private void addListeners() {
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                closingProcedure();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = AbstractDialog.this.getSize();
                Dimension minD = AbstractDialog.this.getMinimumSize();
                if (d.width < minD.width) {
                    d.width = minD.width;
                }
                if (d.height < minD.height) {
                    d.height = minD.height;
                }
                AbstractDialog.this.setSize(d);
            }
        });
    }

    public void setOKButtonText(String buttonText) {
        Validate.notNull(buttonText);
        okButtonText = buttonText;
        if (btnOK != null) {
            btnOK.setText(buttonText);
        }
    }

    public void setIncludeCancelButton(boolean includeCancelButton) {
        this.includeCancelButton = includeCancelButton;
    }

    protected Border getBorder() {
        return DEFAULT_BORDER;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public O getDialogObject() {
        return dialogObject;
    }

    protected void setDialogObject(O object) {
        this.dialogObject = object;
    }
    
    protected Dimension getDefaultPreferredSize(){
    	return DEFAULT_PREFERRED_SIZE;
    }

    protected abstract void addComponents() throws Exception;

    protected abstract void setTitle();

    protected JPanel mainPanel() {
        return panelContent;
    }

    protected JPanel getPanelButtons() throws Exception {
		if (panelButtons == null) {
			panelButtons = new JPanel();
			if (!getButtons().isEmpty()) {
				BoxLayout l = new BoxLayout(panelButtons, BoxLayout.PAGE_AXIS);
				panelButtons.setLayout(l);
				JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
				panelButtons.add(separator);
				panelButtons.add(Box.createHorizontalStrut(5));
				JPanel buttons = new JPanel();
				BoxLayout layout = new BoxLayout(buttons, BoxLayout.LINE_AXIS);
				buttons.setLayout(layout);
				switch (buttonLayout) {
				case CENTERED:
					buttons.add(Box.createHorizontalGlue());
					for (JButton button : getButtons()) {
						buttons.add(button);
					}
					buttons.add(Box.createHorizontalGlue());
					break;
				case LEFT_RIGHT:
					for (JButton lefthandButton : getButtonsLefthand()) {
						buttons.add(lefthandButton);
					}
					buttons.add(Box.createHorizontalGlue());
					for (JButton righthandButton : getButtonsEditingValues()) {
						buttons.add(righthandButton);
					}
					break;
				}
				panelButtons.add(buttons);
			}
		}
		return panelButtons;
	}

    protected List<JButton> getButtons() throws Exception {
        List<JButton> buttons = getButtonsLefthand();
        buttons.addAll(getButtonsEditingValues());
        return buttons;
    }

    protected List<JButton> getButtonsLefthand() throws Exception {
        return new ArrayList<JButton>();
    }

    protected List<JButton> getButtonsEditingValues() {
        ArrayList<JButton> result = new ArrayList<JButton>();
        result.add(getButtonOK());
        if (includeCancelButton) {
            result.add(getButtonCancel());
        }
        return result;
    }

    protected JButton getButtonOK() {
        if (btnOK == null) {
            btnOK = new JButton(okButtonText);
            btnOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okProcedure();
                }
            });
            btnOK.setActionCommand("OK");
        }
        return btnOK;
    }

    protected JButton getButtonCancel() {
        if (btnCancel == null) {
            btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelProcedure();
                }
            });
            btnCancel.setActionCommand("Cancel");
        }
        return btnCancel;
    }

    protected JButton getButtonDefault() {
        return btnOK;
    }

    protected void okProcedure() {
        dispose();
    }

    protected void cancelProcedure() {
        dispose();
    }

    protected void closingProcedure() {
        cancelProcedure();
    }

    public enum ButtonPanelLayout {
        CENTERED, LEFT_RIGHT;
    }

    private void errorMessage(String title, String message, Exception exception, boolean stackTraceOption) {
        ExceptionDialog.showException(AbstractDialog.this, title, new Exception(message, exception), true, stackTraceOption);
    }

    protected void invalidFieldContentException(Exception exception) {
        errorMessage("Invalid Parameter", "Invalid field content.", exception, false);
    }

    protected void internalException(String message, Exception exception) {
        errorMessage("Internal Exception", message, exception, true);
    }
    
    protected void exception(String message, Exception exception) {
        errorMessage("Internal Exception", message, exception, true);
    }

}
