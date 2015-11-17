// Hive Development Framework
// Copyright (c) 2008-2015 Hive Solutions Lda.
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

package pt.hive.eclipse.plugins.nature.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import pt.hive.eclipse.plugins.nature.HiveNature;

@SuppressWarnings("rawtypes")
public class ToggleNatureAction implements IObjectActionDelegate {

    private ISelection selection;

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        // in case the selection is of type structured selection
        if (selection instanceof IStructuredSelection) {
            // iterates over all the values of the selection
            for (Iterator iterator = ((IStructuredSelection) selection).iterator(); iterator.hasNext();) {
                // retrieves the next element from the iterator
                Object element = iterator.next();

                // sets the project as null
                IProject project = null;

                // in case the element is a project
                if (element instanceof IProject) {
                    // sets the project as the element
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    // sets the project as the adapter value
                    project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
                }

                // in case the project is defined
                if (project != null) {
                    // toggles the nature in the project (add or remove)
                    toggleNature(project);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
     * .IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
     * action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    /**
     * Toggles sample nature on a project
     *
     * @param project
     *            to have sample nature added or removed
     */
    private void toggleNature(IProject project) {
        try {
            // retrieves the project description
            IProjectDescription description = project.getDescription();

            // retrieves the natures of the project
            String[] natures = description.getNatureIds();

            // iterates over all the natures
            for (int i = 0; i < natures.length; ++i) {

                // in case the nature is the one we're trying to find
                if (HiveNature.NATURE_ID.equals(natures[i])) {
                    // allocates a new array for the new commands
                    String[] newNatures = new String[natures.length - 1];

                    // copies the commands to the new commands
                    System.arraycopy(natures, 0, newNatures, 0, i);
                    System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);

                    // sets the natures in the project description
                    description.setNatureIds(newNatures);

                    // sets the project description
                    project.setDescription(description, null);

                    // returns immediately
                    return;
                }
            }

            // adds the nature
            String[] newNatures = new String[natures.length + 1];

            // copies the array contents to the new natures array
            System.arraycopy(natures, 0, newNatures, 0, natures.length);

            // sets the last nature as the hive one
            newNatures[natures.length] = HiveNature.NATURE_ID;

            // sets the natures in the description
            description.setNatureIds(newNatures);

            // sets the project description
            project.setDescription(description, null);
        } catch (CoreException exception) {
        }
    }
}
