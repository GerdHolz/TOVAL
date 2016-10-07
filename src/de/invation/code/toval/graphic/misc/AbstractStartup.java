package de.invation.code.toval.graphic.misc;

import de.invation.code.toval.graphic.dialog.ExceptionDialog;
import de.invation.code.toval.os.OSType;
import de.invation.code.toval.os.OSUtils;

public abstract class AbstractStartup {

    protected AbstractStartup() {
        if (OSUtils.getCurrentOS() == OSType.OS_MACOSX) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getToolName());
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getToolName());
        }

        try {
            startApplication();
        } catch (Exception e) {
            ExceptionDialog.showException(null, "Internal Exception", e, true);
//			JOptionPane.showMessageDialog(null, "Cannot launch application.\nReason: " + e.getMessage(), "Internal Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected abstract String getToolName();

    protected abstract void startApplication() throws Exception;

}
