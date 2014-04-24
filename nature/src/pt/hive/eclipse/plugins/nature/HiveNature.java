// Hive Development Framework
// Copyright (C) 2008-2014 Hive Solutions Lda.
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
// __copyright__ = Copyright (c) 2008-2014 Hive Solutions Lda.
// __license__   = GNU General Public License (GPL), Version 3

package pt.hive.eclipse.plugins.nature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * The hive nature to be used.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class HiveNature implements IProjectNature {

    /**
     * Id of this project nature.
     */
    public static final String NATURE_ID = "pt.hive.eclipse.plugins.nature.hiveNature";

    /**
     * The reference to the project.
     */
    private IProject project;

    /*
     * Configures the java nature.
     *
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
        // retrieves the project description
        IProjectDescription projectDescription = project.getDescription();

        // retrieves the build specification
        ICommand[] commands = projectDescription.getBuildSpec();

        // iterates over all the commands
        for (int i = 0; i < commands.length; i++) {
            // retrieves the current command
            ICommand currentCommand = commands[i];

            // retrieves the current command builder name
            String currentCommandBuilderName = currentCommand.getBuilderName();

            // in case the current command builder name is the
            // hive nature builder id
            if (currentCommandBuilderName.equals(HiveNatureBuilder.BUILDER_ID)) {
                // returns immediately
                return;
            }
        }

        // retrieves the new commands
        ICommand[] newCommands = new ICommand[commands.length + 1];

        // copies the new commands to the commands array
        System.arraycopy(commands, 0, newCommands, 0, commands.length);

        // creates a new project command
        ICommand command = projectDescription.newCommand();

        // sets the builder name of the command as the hive nature builder id
        command.setBuilderName(HiveNatureBuilder.BUILDER_ID);

        // adds the command to the new commands
        newCommands[newCommands.length - 1] = command;

        // sets the project description build specification
        projectDescription.setBuildSpec(newCommands);

        // sets the "new" project description
        project.setDescription(projectDescription, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        // retrieves the project description
        IProjectDescription description = getProject().getDescription();

        // retrieves the project commands
        ICommand[] commands = description.getBuildSpec();

        // iterates over all the commands
        for (int i = 0; i < commands.length; ++i) {
            // retrieves the current command
            ICommand currentCommand = commands[i];

            // retrieves the current command builder name
            String currentCommandBuilderName = currentCommand.getBuilderName();

            // in case the current command builder name
            // is the hive nature builder id
            if (currentCommandBuilderName.equals(HiveNatureBuilder.BUILDER_ID)) {
                // allocates a new array for the new commands
                ICommand[] newCommands = new ICommand[commands.length - 1];

                // copies the commands to the new commands
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i,
                        commands.length - i - 1);

                // sets the build specification with the new commands
                description.setBuildSpec(newCommands);

                // returns immediately
                return;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    public IProject getProject() {
        return project;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
     * .resources.IProject)
     */
    public void setProject(IProject project) {
        this.project = project;
    }
}
