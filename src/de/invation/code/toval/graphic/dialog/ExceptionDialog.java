package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import de.invation.code.toval.graphic.component.ContentSensitiveTextPane;
import de.invation.code.toval.validate.Validate;

public class ExceptionDialog extends JDialog {

    private static final long serialVersionUID = -2350754359368195069L;

    public static final boolean DEFAULT_CONCAT_CAUSE_MESSAGES = false;
    private static final Dimension MIN_DIMENSION = new Dimension(400, 200);
    private static final Dimension PREFERRED_SIZE_STACK_TRACE_DIALOG = new Dimension(800, 800);
    public static final boolean DEFAULT_STACK_TRACE_OPTION = false;

    private JPanel panelButtons;
    private JButton btnStackTrace;
    private JButton btnOK;
    
    private boolean concatCauseMessages = DEFAULT_CONCAT_CAUSE_MESSAGES;
    private Exception exception = null;
    private boolean stackTraceOption = true;

    protected ExceptionDialog(Window owner, String title, Exception exception, boolean concatCauseMessages, boolean stackTraceOption) {
        super(owner, title);
        Validate.notNull(exception);
        this.exception = exception;
        this.concatCauseMessages = concatCauseMessages;
        this.stackTraceOption = stackTraceOption;
        setUpGUI(owner);
    }
    
    

    private void setUpGUI(Window owner){
        this.setResizable(true);
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        getContentPane().setLayout(new BorderLayout());
        if (concatCauseMessages) {
            getContentPane().add(getConcatenatedCauseMessages(), BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel(exception.getMessage());
            label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            label.setBackground(ExceptionDialog.this.getBackground());
            getContentPane().add(label, BorderLayout.PAGE_START);
        }
        getContentPane().add(getButtonPanel(), BorderLayout.PAGE_END);
        
        pack();
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }
    
    protected JPanel getButtonPanel() {
        if (panelButtons == null) {
            panelButtons = new JPanel();
            BoxLayout l = new BoxLayout(panelButtons, BoxLayout.PAGE_AXIS);
            panelButtons.setLayout(l);
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            panelButtons.add(separator);
            panelButtons.add(Box.createHorizontalStrut(5));
            JPanel buttons = new JPanel();
            BoxLayout layout = new BoxLayout(buttons, BoxLayout.LINE_AXIS);
            buttons.setLayout(layout);
            panelButtons.add(buttons);
            if(stackTraceOption){
                buttons.add(getButtonStackTrace());
                buttons.add(Box.createHorizontalGlue());
                buttons.add(getOKButton());
            } else {
                buttons.add(Box.createHorizontalGlue());
                buttons.add(getOKButton());
                buttons.add(Box.createHorizontalGlue());
            }
        }
        return panelButtons;
    }
    
    protected JButton getOKButton() {
        if (btnOK == null) {
            btnOK = new JButton("OK");
            btnOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            btnOK.setActionCommand("OK");
        }
        return btnOK;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return MIN_DIMENSION;
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private JButton getButtonStackTrace() {
        if (btnStackTrace == null) {
            btnStackTrace = new JButton("Stack Trace");
            btnStackTrace.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                    	StringDialog dialog = new StringDialog(ExceptionDialog.this, "Stack Trace", getStackTrace(), false);
                    	dialog.setPreferredSize(PREFERRED_SIZE_STACK_TRACE_DIALOG);
                    	dialog.setUpGUI();
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(ExceptionDialog.this, "Cannot launch StringDialog.\nReason: " + e1.getMessage(), "Internal Exception", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return btnStackTrace;
    }

    private String getStackTrace() {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        exception.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static void showException(String title, Exception exception) {
        showException(title, exception, DEFAULT_CONCAT_CAUSE_MESSAGES);
    }

    public static void showException(String title, Exception exception, boolean concatCauseMessages) {
        showException(null, title, exception, concatCauseMessages);
    }

    public static void showException(Window owner, String title, Exception exception) {
        showException(owner, title, exception, DEFAULT_CONCAT_CAUSE_MESSAGES);
    }
    
    public static void showException(Window owner, String title, Exception exception, boolean concatCauseMessages) {
        showException(owner, title, exception, concatCauseMessages, DEFAULT_STACK_TRACE_OPTION);
    }

    public static void showException(Window owner, String title, Exception exception, boolean concatCauseMessages, boolean stackTraceOption) {
        try {
            ExceptionDialog dialog = new ExceptionDialog(owner, title, exception, concatCauseMessages, stackTraceOption);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(owner, "Cannot launch ExceptionDialog.\nReason: " + e.getMessage(), "Internal Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent getConcatenatedCauseMessages() {
        List<String> messages = new ArrayList<String>();
        if (exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            messages.add(exception.getMessage());
        }
        Throwable cause = exception;
        while ((cause = cause.getCause()) != null) {
            if (cause.getMessage() != null && !cause.getMessage().isEmpty()) {
                messages.add(cause.getMessage());
            }
        }
        ContentSensitiveTextPane textPane = new ContentSensitiveTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        textPane.setBackground(ExceptionDialog.this.getBackground());
        StringBuilder builder = new StringBuilder();
        for (String message : messages) {
            builder.append(message);
            builder.append("<br>");
        }
        textPane.setEditable(false);
        textPane.setText(builder.toString());
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(null);
        return scrollPane;
    }
    
    public static void main(String[] args) {
    	List list = Arrays.asList("eins", "zwei");
    	try {
    		List<Integer> list2 = (List<Integer>) list;
    		Integer value = list2.get(0);
    	} catch(Exception e){
    		ExceptionDialog.showException(null, "Titel", new Exception("Testtext", e), true, true);
    	}
    	try{
    		throw new Exception("Testexception");
    	} catch(Exception e){
    		ExceptionDialog.showException(null, "Titel", new Exception("Testtext", e), true, true);
    	}
    }

}
