// Hive Development Framework
// Copyright (C) 2008-2015 Hive Solutions Lda.
//
// This file is part of Hive Development Framework.
//
// Hive Development Framework is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Hive Development Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Hive Development Framework. If not, see <http://www.gnu.org/licenses/>.

// __author__    = João Magalhães <joamag@hive.pt>
// __version__   = 1.0.0
// __revision__  = $LastChangedRevision$
// __date__      = $LastChangedDate$
// __copyright__ = Copyright (c) 2008-2015 Hive Solutions Lda.
// __license__   = GNU General Public License (GPL), Version 3

package pt.hive.eclipse.plugins.nature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * The hive nature action to be used.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class HiveNatureAction implements IWorkbenchWindowActionDelegate {

    /**
     * Constructor of the class.
     */
    public HiveNatureAction() {
    }

    /**
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        // reads the configuration file
        readConfigurationFile();
    }

    /**
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {
        // writes the configuration file
        writeConfigurationFile();
    }

    /**
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action) {
        // in case the action is checked
        if (action.isChecked()) {
            // sets the building enabled
            Activator.setBuildingEnabled(true);
        } else {
            // unsets the building enabled
            Activator.setBuildingEnabled(false);
        }

        // writes the configuration file
        writeConfigurationFile();
    }

    /**
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Reads the configuration file and sets the building flag according to the
     * current value.
     */
    private void readConfigurationFile() {
        // retrieves the configuration file
        File configurationFile = Activator.getConfigurationFile();

        // in case the configuration file does not exist
        if (!configurationFile.exists()) {
            // sets the building as enabled
            Activator.setBuildingEnabled(true);

            // returns immediately
            return;
        }

        try {
            // creates the file input stream from the configuration file
            FileInputStream fileInputStream = new FileInputStream(
                    configurationFile);

            try {
                // retrieves the value from the file input stream
                int value = fileInputStream.read();

                // switches over the value
                switch (value) {
                case 0:
                    // sets the building as disabled
                    Activator.setBuildingEnabled(false);

                    break;
                case 1:
                    // sets the building as enabled
                    Activator.setBuildingEnabled(true);

                    break;
                default:
                    // sets the building as enabled
                    Activator.setBuildingEnabled(true);

                    break;
                }
            } finally {
                // closes the file input stream
                fileInputStream.close();
            }
        } catch (IOException exception) {
            // prints the stack trace
            exception.printStackTrace();
        }
    }

    /**
     * Writes the configuration file value. This value corresponds to the
     * current building flag status.
     */
    private void writeConfigurationFile() {
        // retrieves the configuration file
        File configurationFile = Activator.getConfigurationFile();

        try {
            // creates the file output stream from the configuration file
            FileOutputStream fileOutputStream = new FileOutputStream(
                    configurationFile);

            try {
                // in case the building is enabled
                if (Activator.isBuildingEnabled()) {
                    // writes the one integer value to the
                    // file output stream
                    fileOutputStream.write(1);
                } else {
                    // writes the zero integer value to the
                    // file output stream
                    fileOutputStream.write(0);
                }
            } finally {
                // closes the file output stream
                fileOutputStream.close();
            }
        } catch (IOException exception) {
            // prints the stack trace
            exception.printStackTrace();
        }
    }
}
