package de.invation.code.toval.misc.soabase;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.dialog.AbstractEditCreateDialog;
import de.invation.code.toval.graphic.dialog.StringDialog;
import de.invation.code.toval.graphic.dialog.ValueEditingDialog;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.validate.ParameterException;

public class SOABaseDialog extends AbstractEditCreateDialog<SOABase> {

    private static final long serialVersionUID = 1348084157428660980L;
    public static final Dimension PREFERRED_SIZE = new Dimension(499, 350);
    public static final Dimension PREFERRED_FIELD_SIZE = new Dimension(160, 300);

    private JPanel componentsPanel;

    private JList activityList;
    private JList subjectList;
    private JList ObjectList;

    private DefaultListModel activityListModel;
    private DefaultListModel subjectListModel;
    private DefaultListModel ObjectListModel;

    private JTextField txtContextName;

    private JButton btnAddActivities;
    private JButton btnAddSubjects;
    private JButton btnAddObjects;
    private JButton btnShowContext;

    private AbstractAction addActivitiesAction;
    private AbstractAction addSubjectsAction;
    private AbstractAction addObjectsAction;

    protected boolean activitiesAssigned;
    protected boolean subjectsAssigned;
    protected boolean ObjectsAssigned;

    public SOABaseDialog(Window owner) throws Exception {
        super(owner);
    }

    public SOABaseDialog(Window owner, SOABase context) throws Exception {
        super(owner, context);
    }

    @Override
    protected void initialize() {
        super.initialize();
        activityListModel = new DefaultListModel();
        subjectListModel = new DefaultListModel();
        ObjectListModel = new DefaultListModel();
        activitiesAssigned = false;
        subjectsAssigned = false;
        ObjectsAssigned = false;
    }

    @Override
    protected SOABase newDialogObject(Object... parameters) {
        return new SOABase();
    }

    @Override
    protected boolean validateAndSetFieldValues() throws Exception {
        if (getDialogObject() == null || getDialogObject().isEmpty()) {
            throw new ParameterException("Empty context.");
        }

        if (getDialogObject().getName().isEmpty()) {
            throw new ParameterException("Context name cannot be empty.");
        }

        try {
            getDialogObject().setName(txtContextName.getText());
        } catch (Exception e1) {
            throw new ParameterException("Cannot set context name.\nReason: " + e1.getMessage());
        }
        return true;
    }

    @Override
    protected Border getBorder() {
        return BorderFactory.createEmptyBorder(10, 10, 10, 10);
    }

    @Override
    protected void addComponents() {
        mainPanel().setLayout(new BorderLayout());
        JPanel namePanel = new JPanel(new FlowLayout());
        namePanel.add(new JLabel("Name:"));
        namePanel.add(getTextFieldContextName());
        mainPanel().add(namePanel, BorderLayout.PAGE_START);
        addActivitiesAction = new AddActivitiesAction();
        addSubjectsAction = new AddSubjectsAction();
        addObjectsAction = new AddObjectsAction();
        mainPanel().add(getPanelComponents(), BorderLayout.CENTER);
        mainPanel().add(getPanelComponentsExtension(), BorderLayout.PAGE_END);
    }

    @Override
    protected void prepareEditing() {
        txtContextName.setText(getDialogObject().getName());
        updateListActivity(true);
        updateListSubject(true);
        updateListObject(true);
    }

    private Component getPanelComponents() {
        if (componentsPanel == null) {
            componentsPanel = new JPanel(new BorderLayout());
            componentsPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.PAGE_START);

            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridLayout(1, 3, 10, 0));

            gridPanel.add(new ActivitiesPanel());
            gridPanel.add(new SubjectsPanel());
            gridPanel.add(new ObjectsPanel());

            componentsPanel.add(gridPanel, BorderLayout.CENTER);
        }
        return componentsPanel;
    }

    private Component getPanelComponentsExtension() {
        JPanel extensionPanel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(1, 3, 10, 0));
        JPanel activityExtensionPanel = new JPanel();
        List<JButton> activityButtons = getButtonsActivity();
        activityExtensionPanel.setLayout(new GridLayout(activityButtons.size(), 1, 0, 0));
        for(JButton activityButton: activityButtons)
            activityExtensionPanel.add(activityButton);
        JPanel subjectExtensionPanel = new JPanel();
        List<JButton> subjectButtons = getButtonsSubject();
        subjectExtensionPanel.setLayout(new GridLayout(subjectButtons.size(), 1, 0, 0));
        for(JButton subjectButton: subjectButtons)
            subjectExtensionPanel.add(subjectButton);
        JPanel objectExtensionPanel = new JPanel();
        List<JButton> objectButtons = getButtonsObject();
        objectExtensionPanel.setLayout(new GridLayout(objectButtons.size(), 1, 0, 0));
        for(JButton objectButton: objectButtons)
            objectExtensionPanel.add(objectButton);
        gridPanel.add(activityExtensionPanel);
        gridPanel.add(subjectExtensionPanel);
        gridPanel.add(objectExtensionPanel);
        extensionPanel.add(gridPanel, BorderLayout.CENTER);
        JComponent customComponent = getCustomComponent();
        if(customComponent != null){
            JPanel restPanel = new JPanel(new BorderLayout(0, 10));
        restPanel.add(new JSeparator(), BorderLayout.PAGE_START);
        restPanel.add(customComponent, BorderLayout.CENTER);
        extensionPanel.add(restPanel, BorderLayout.PAGE_END);
        }
        return extensionPanel;
    }
    
    protected JComponent getCustomComponent(){
        return null;
    }

    @Override
    public SOABase getDialogObject() {
        return (SOABase) super.getDialogObject();
    }

    @Override
    protected void setTitle() {
        if (editMode()) {
            setTitle("Edit Context");
        } else {
            setTitle("New Context");
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

	//------- BUTTONS --------------------------------------------------------------------------------------------
    private JButton getButtonAddActivities() {
        if (btnAddActivities == null) {
            btnAddActivities = new JButton();
            btnAddActivities.setAction(addActivitiesAction);
        }
        return btnAddActivities;
    }

//	private JButton getImportActivitiesButton(){
//		if(btnImportActivities == null){
//			btnImportActivities = new JButton("Import activities");
//			btnImportActivities.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					if(getDialogObject() != null)
//						JOptionPane.showMessageDialog(ContextDialog.this, "Importing activities will reset all context properties.", "Warning", JOptionPane.WARNING_MESSAGE);
//					PTNet ptNet = null;
//					try {
//						ptNet = PetriNetDialog.showPetriNetDialog(ContextDialog.this);
//					} catch (ParameterException e1) {
//						JOptionPane.showMessageDialog(ContextDialog.this, "<html>Cannot launch Petri net dialog.<br>Reason: " + e1.getMessage() + "</html>", "Internal Exception", JOptionPane.ERROR_MESSAGE);
//					}
//					if(ptNet != null){
//						if(ptNet.getTransitions().isEmpty())
//							JOptionPane.showMessageDialog(ContextDialog.this, "Cannot import activities: Petri net contains no transitions.", "Invalid Argument", JOptionPane.ERROR_MESSAGE);
//						try{
//							newContext(PNUtils.getLabelSetFromTransitions(ptNet.getTransitions(), false));
//						}catch(ParameterException ex){
//							JOptionPane.showMessageDialog(ContextDialog.this, "Cannot extract activity names from Petri net transitions.", "Internal Error", JOptionPane.ERROR_MESSAGE);
//						}
//					}
//				}
//			});
//		}
//		return btnImportActivities;
//	}
    @Override
    protected List<JButton> getButtonsLefthand() {
        List<JButton> lhb = super.getButtonsLefthand();
        lhb.add(getButtonShowContext());
        return lhb;
    }

    private JButton getButtonShowContext() {
        if (btnShowContext == null) {
            btnShowContext = new JButton("Show Context");
            btnShowContext.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getDialogObject() != null) {
                        try {
                            StringDialog.showDialog(SOABaseDialog.this, "Context: " + getDialogObject().getName(), getDialogObject().toString(), false);
                        } catch (Exception e1) {
                            internalException("Cannot launch StringDialog.", e1);
                        }
                    }
                }
            });
        }
        return btnShowContext;
    }

    private JButton getButtonAddSubjects() {
        if (btnAddSubjects == null) {
            btnAddSubjects = new JButton();
            btnAddSubjects.setAction(addSubjectsAction);
        }
        return btnAddSubjects;
    }

    private JButton getButtonAddObjects() {
        if (btnAddObjects == null) {
            btnAddObjects = new JButton();
            btnAddObjects.setAction(addObjectsAction);
        }
        return btnAddObjects;
    }

	//------- OTHER GUI COMPONENTS ----------------------------------------------------------------------------------------
    private JTextField getTextFieldContextName() {
        if (txtContextName == null) {
            txtContextName = new JTextField();
            txtContextName.setText(SOABase.DEFAULT_NAME);
            txtContextName.setColumns(10);
        }
        return txtContextName;
    }

    private JList getListActivity() {
        if (activityList == null) {
            activityList = new JList(activityListModel);
            activityList.setCellRenderer(new AlternatingRowColorListCellRenderer());
            activityList.setFixedCellHeight(20);
            activityList.setVisibleRowCount(10);
            activityList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            activityList.setBorder(null);

            activityList.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (activityList.getSelectedValues() != null) {
                            try {
                                getDialogObject().removeActivities(ArrayUtils.toStringList(activityList.getSelectedValues()));
                                if ((activityListModel.size() - activityList.getSelectedIndices().length == 0)) {
                                    activitiesAssigned = false;
                                }
                            } catch (Exception e1) {
                                internalException("Cannot remove "+getDialogObject().getActivityDescriptorPlural().toLowerCase()+".", e1);
                                return;
                            }
                            updateListActivity(true);
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }
            });
        }
        return activityList;
    }

    private JList getListSubject() {
        if (subjectList == null) {
            subjectList = new JList(subjectListModel);
            subjectList.setCellRenderer(new AlternatingRowColorListCellRenderer());
            subjectList.setFixedCellHeight(20);
            subjectList.setVisibleRowCount(10);
            subjectList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            subjectList.setBorder(null);

            subjectList.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (subjectList.getSelectedValues() != null) {
                            try {
                                getDialogObject().removeSubjects(ArrayUtils.toStringList(subjectList.getSelectedValues()));
                                if ((subjectListModel.size() - subjectList.getSelectedIndices().length == 0)) {
                                    subjectsAssigned = false;
                                }
                            } catch (Exception e1) {
                                internalException("Cannot remove "+getDialogObject().getSubjectDescriptorPlural().toLowerCase()+".", e1);
                                return;
                            }
                            updateListSubject(true);
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }
            });
        }
        return subjectList;
    }

    private JList getListObject() {
        if (ObjectList == null) {
            ObjectList = new JList(ObjectListModel);
            ObjectList.setCellRenderer(new AlternatingRowColorListCellRenderer());
            ObjectList.setFixedCellHeight(20);
            ObjectList.setVisibleRowCount(10);
            ObjectList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ObjectList.setBorder(null);

            ObjectList.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (ObjectList.getSelectedValues() != null) {
                            try {
                                getDialogObject().removeObjects(ArrayUtils.toStringList(ObjectList.getSelectedValues()));
                                if ((ObjectListModel.size() - ObjectList.getSelectedIndices().length == 0)) {
                                    ObjectsAssigned = false;
                                }
                            } catch (Exception e1) {
                                internalException("Cannot remove "+getDialogObject().getObjectDescriptorPlural().toLowerCase()+".", e1);
                                return;
                            }
                            updateListObject(true);
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }
            });
        }
        return ObjectList;
    }

	//------- FUNCTIONALITY ----------------------------------------------------------------------------------------------
    private void updateListActivity(boolean setSelection) {
        activityListModel.clear();
        if (getDialogObject() != null) {
            List<String> activities = new ArrayList<String>(getDialogObject().getActivities());
            Collections.sort(activities);
            for (String activity : activities) {
                activityListModel.addElement(activity);
            }
        }
        if (!activityListModel.isEmpty() && setSelection) {
            activityList.setSelectedIndex(0);
        }
    }

    private void updateListSubject(boolean setSelection) {
        subjectListModel.clear();
        if (getDialogObject() != null) {
            List<String> subjects = new ArrayList<String>(getDialogObject().getSubjects());
            Collections.sort(subjects);
            for (String subject : subjects) {
                subjectListModel.addElement(subject);
            }
        }
        if (!subjectListModel.isEmpty() && setSelection) {
            subjectList.setSelectedIndex(0);
        }
    }

    private void updateListObject(boolean setSelection) {
        ObjectListModel.clear();
        if (getDialogObject() != null) {
            List<String> objects = new ArrayList<String>(getDialogObject().getObjects());
            Collections.sort(objects);
            for (String Object : objects) {
                ObjectListModel.addElement(Object);
            }
        }
        if (!ObjectListModel.isEmpty() && setSelection) {
            ObjectList.setSelectedIndex(0);
        }
    }

	//------- ACTIONS -----------------------------------------------------------------------------------------------------------
    private class AddObjectsAction extends AbstractAction {

        private static final long serialVersionUID = 7917057947622752928L;

        public AddObjectsAction() {
            super("Add " + getDialogObject().getObjectDescriptorPlural());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> objects = null;
            try {
                objects = ValueEditingDialog.showDialog(SOABaseDialog.this, getDialogObject().getObjectDescriptorPlural(), String.class);
            } catch (Exception e2) {
                internalException("Cannot launch value chooser dialog dialog.", e2);
            }
            if (objects != null) {
                try {
                    getDialogObject().addObjects(objects);
                } catch (Exception e1) {
                    internalException("Cannot add " + getDialogObject().getObjectDescriptorPlural().toLowerCase() + " to context.", e1);
                    return;
                }
                ObjectsAssigned = true;
                updateListObject(false);
            }
        }

    }

    private class AddSubjectsAction extends AbstractAction {

        private static final long serialVersionUID = -4148251659616210607L;

        public AddSubjectsAction() {
            super("Add " + getDialogObject().getSubjectDescriptorPlural());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> subjects = null;
            try {
                subjects = ValueEditingDialog.showDialog(SOABaseDialog.this, getDialogObject().getSubjectDescriptorPlural(), String.class);
            } catch (Exception e2) {
                internalException("Cannot launch value chooser dialog dialog.", e2);
            }
            if (subjects != null) {
                try {
                    getDialogObject().addSubjects(subjects);
                } catch (Exception e1) {
                    internalException("Cannot add " + getDialogObject().getSubjectDescriptorPlural().toLowerCase() + " to context.", e1);
                    return;
                }
                subjectsAssigned = true;
                updateListSubject(false);
            }
        }

    }

    private class AddActivitiesAction extends AbstractAction {

        private static final long serialVersionUID = 1979108778175746934L;

        public AddActivitiesAction() {
            super("Add " + getDialogObject().getActivityDescriptorPlural());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> activities = null;
            try {
                activities = ValueEditingDialog.showDialog(SOABaseDialog.this, getDialogObject().getActivityDescriptorPlural(), String.class);
            } catch (Exception e2) {
                internalException("Cannot launch value chooser dialog dialog.", e2);
                return;
            }
            if (activities != null) {
                try {
                    getDialogObject().addActivities(activities);
                } catch (Exception e1) {
                    internalException("Cannot add " + getDialogObject().getActivityDescriptorPlural().toLowerCase() + " to context.", e1);
                    return;
                }
                activitiesAssigned = true;
                updateListActivity(false);
            }
        }

    }

    private abstract class ContextContentPanel extends JPanel {

        private static final long serialVersionUID = 3530688424334811142L;

        public ContextContentPanel(JList content, String description, JButton bottomComponent) {
            super(new BorderLayout());
            add(new JLabel(description + ":"), BorderLayout.PAGE_START);
            JScrollPane scrollPane1 = new JScrollPane();
            scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            add(scrollPane1, BorderLayout.CENTER);
            scrollPane1.setViewportView(content);
            add(bottomComponent, BorderLayout.PAGE_END);
        }
    }

    private class ActivitiesPanel extends ContextContentPanel {

        private static final long serialVersionUID = 4842826076367497019L;

        public ActivitiesPanel() {
            super(getListActivity(), getDialogObject().getActivityDescriptorPlural(), getButtonAddActivities());
        }
        
    }
    
    private JPanel getPanelActivityButtons(){
        JPanel panelActivityButtons = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        List<JButton> activityButtons = getButtonsActivity();
        buttonPanel.setLayout(new GridLayout(activityButtons.size(), 1, 5, 5));
        for(JButton button: activityButtons)
            buttonPanel.add(button);
        panelActivityButtons.add(buttonPanel, BorderLayout.PAGE_START);
        panelActivityButtons.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        return panelActivityButtons;
    }
    
    protected List<JButton> getButtonsActivity(){
        return new ArrayList<>();
    }

    private class SubjectsPanel extends ContextContentPanel {

        private static final long serialVersionUID = -4111381147809133847L;

        public SubjectsPanel() {
            super(getListSubject(), getDialogObject().getSubjectDescriptorPlural(), getButtonAddSubjects());
        }
    }
    
    private JPanel getPanelSubjectButtons(){
        JPanel panelSubjectButtons = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        List<JButton> subjectButtons = getButtonsSubject();
        buttonPanel.setLayout(new GridLayout(subjectButtons.size(), 1, 5, 5));
        for(JButton button: subjectButtons)
            buttonPanel.add(button);
        panelSubjectButtons.add(buttonPanel, BorderLayout.PAGE_START);
        panelSubjectButtons.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        return panelSubjectButtons;
    }
    
    protected List<JButton> getButtonsSubject(){
        return new ArrayList<>();
    }

    private class ObjectsPanel extends ContextContentPanel {

        private static final long serialVersionUID = -2779219101533314867L;

        public ObjectsPanel() {
            super(getListObject(), getDialogObject().getObjectDescriptorPlural(), getButtonAddObjects());
        }
    }
    
    private JPanel getPanelObjectButtons(){
        JPanel panelObjectButtons = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        List<JButton> objectButtons = getButtonsObject();
        buttonPanel.setLayout(new GridLayout(objectButtons.size(), 1, 5, 5));
        for(JButton button: objectButtons)
            buttonPanel.add(button);
        panelObjectButtons.add(buttonPanel, BorderLayout.PAGE_START);
        panelObjectButtons.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        return panelObjectButtons;
    }
    
    protected List<JButton> getButtonsObject(){
        return new ArrayList<>();
    }
	//------- STARTUP ---------------------------------------------------------------------------------------------------------------
    public static SOABase showDialog(Window parentWindow) throws Exception {
        SOABaseDialog contextDialog = new SOABaseDialog(parentWindow);
        contextDialog.setUpGUI();
        return contextDialog.getDialogObject();
    }

    public static boolean showDialog(Window parentWindow, SOABase context) throws Exception {
        SOABaseDialog contextDialog = new SOABaseDialog(parentWindow, context);
        contextDialog.setUpGUI();
        return contextDialog.getDialogObject() != null;
    }

    public static void main(String[] args) throws Exception {
        SOABase c = new SOABase("GerdContext");
        c.setActivities(Arrays.asList("act1", "act2"));
        SOABaseDialog.showDialog(null, c);
        System.out.println(c);
    }
}
