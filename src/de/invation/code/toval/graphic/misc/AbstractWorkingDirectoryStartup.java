package de.invation.code.toval.graphic.misc;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.graphic.dialog.MessageDialog;
import de.invation.code.toval.misc.wd.AbstractWorkingDirectoryProperties;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ParameterException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

public abstract class AbstractWorkingDirectoryStartup extends AbstractStartup {
    
    @Override
    protected final void startApplication() throws Exception {
        // Check if there is a path to a simulation directory.
        if (!checkSimulationDirectory()) {
            // There is no path and it is either not possible to set a path or the user aborted the corresponding dialog.
            System.exit(0);
        }
        
        MessageDialog.getInstance();
        
        try {
            SwingUtilities.invokeAndWait(() -> {
                try{
                    initializeComponentContainer();
                } catch(Exception e){
                    MessageDialog.getInstance().message("Exception while initializing component container: " + e.getMessage());
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new Exception("Exception during startup.", e);
        }
        createMainClass();
    }
    
    protected abstract void initializeComponentContainer() throws Exception;
    
    protected abstract void createMainClass() throws Exception;
    
    protected boolean chooseWorkingDirectory() {
        String workingDirectory;
        try {
            workingDirectory = launchWorkingDirectoryDialog();
        } catch (Exception e) {
            ExceptionDialog.showException(null, "Internal Exception", new Exception("Cannot launch \""+getWorkingDirectoryDescriptor().toLowerCase()+"\" dialog", e), true);
            return false;
        }
        if (workingDirectory == null) {
            //User aborted 
//            ExceptionDialog.showException(null, "Invalid Directory", new Exception("Chosen \""+getWorkingDirectoryDescriptor().toLowerCase()+"\" is NULL"), true);
            return false;
        }
     
        try {
            getWorkingDirectoryProperties().setWorkingDirectory(workingDirectory, false);
            return true;
        } catch (Exception e1) {
            ExceptionDialog.showException(null, "Internal Exception", new Exception("Cannot set \""+getWorkingDirectoryDescriptor().toLowerCase()+"\"", e1), true);
            return false;
        }
    }
    
    protected boolean checkSimulationDirectory() {
        try {
            getWorkingDirectoryProperties().getWorkingDirectory();
            return true;
        } catch (PropertyException e) {
            // There is no recent simulation directory
            // -> Let the user choose a path for the simulation directory
            return chooseWorkingDirectory();
        } catch (ParameterException e) {
            // Value for simulation directory is invalid, possibly due to moved directories
            // -> Remove entry for actual simulation directory
            try {
                getWorkingDirectoryProperties().removeWorkingDirectory();
            } catch (Exception e1) {
                ExceptionDialog.showException(null, "Internal Exception", new Exception("Cannot fix corrupt property entries.", e), true);
                return false;
            }
            // -> Let the user choose a path for the simulation directory
            return chooseWorkingDirectory();
        } catch (Exception e1) {
            ExceptionDialog.showException(null, "Internal Exception", new Exception("Cannot extract working directory", e1), true);
            return false;
        }
    }
    
    protected abstract String getWorkingDirectoryDescriptor();

    protected abstract String launchWorkingDirectoryDialog() throws Exception;
    
    protected abstract AbstractWorkingDirectoryProperties getWorkingDirectoryProperties() throws Exception;

}
