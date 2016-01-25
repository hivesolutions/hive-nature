// Hive Development Framework
// Copyright (c) 2008-2016 Hive Solutions Lda.
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
// __copyright__ = Copyright (c) 2008-2016 Hive Solutions Lda.
// __license__   = GNU General Public License (GPL), Version 3

package pt.hive.eclipse.plugins.nature;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The hive builder class.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
@SuppressWarnings("rawtypes")
public class HiveNatureBuilder extends IncrementalProjectBuilder {

    /**
     * Id of this builder.
     */
    public static final String BUILDER_ID = "pt.hive.eclipse.plugins.nature.hiveNatureBuilder";

    /**
     * Flag that controls if the full build is enabled for the hive nature, this
     * values must be carefully handled as it may create performance issues.
     */
    private static final boolean FULL_BUILD_ENABLED = false;

    /**
     * The set of file extensions that are considered valid for the touching of
     * the files, this filtering is required so that the performance does not
     * degrade over large dimension projects. This extensions should refer only
     * source code file extensions.
     */
    private static final String[] VALID_EXTENSIONS = { "py", "rb", "php", "c", "cpp", "java" };

    /**
     * The runtime valid extensions list that is used as singleton object to
     * search for the valid extension in the environment.
     */
    private List<String> validExtensions = null;

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map arguments, IProgressMonitor monitor) throws CoreException {
        // in case the building is not enabled
        if (!Activator.isBuildingEnabled()) {
            // returns immediately
            return null;
        }

        // in case the kind is full build, build in which all the elements
        // will be run
        if (kind == FULL_BUILD) {
            // calls the full build method in case the full build
            // mode is enabled, this conditional execution is meant
            // to provide some performance during initial build
            if (HiveNatureBuilder.FULL_BUILD_ENABLED) {
                fullBuild(monitor);
            }
        } else {
            // retrieves the delta value (changed files)
            IResourceDelta delta = getDelta(getProject());

            // in case the delta is invalid (full build)
            if (delta == null) {
                // calls the full build method
                fullBuild(monitor);
            } else {
                // calls the incremental build method
                incrementalBuild(delta, monitor);
            }
        }

        // returns null
        return null;
    }

    /**
     * Touches the given resource, changing the date for it. The sub-resource
     * are also touched.
     *
     * @param resource
     *            The resource to be touched.
     * @throws CoreException
     */
    void touchResource(IResource resource) throws CoreException {
        // retrieves the resource project
        IProject project = resource.getProject();

        // iterates over the project
        this.iterate(project);
    }

    void iterate(IContainer container) throws CoreException {
        // retrieves the container resources
        IResource[] resources = container.members();

        // iterates over all the resources
        for (IResource resource : resources) {
            // in case the resource is a container must iterate over
            // it's resources in order to touch them (recursion step)
            if (resource instanceof IContainer) {
                // iterates over the resource
                iterate((IContainer) resource);
            }
            // otherwise it must be a normal resource and the touch
            // action must be performed
            else {
                // checks if the resource is considered valid and in case
                // it's not returns immediately to avoid touch
                boolean isValid = validResource(resource);
                if (!isValid) {
                    continue;
                }

                // "creates" a new date and uses it to retrieve
                // the current system timestamp so that it's possible
                // to touch the resource (change its internal date)
                Date currentDate = new Date();
                long currentTimestamp = currentDate.getTime();
                resource.setLocalTimeStamp(currentTimestamp);
            }
        }
    }

    private boolean validResource(IResource resource) {
        // in case the valid extensions list is not defined
        // it must be created from the array (for fast performance)
        if (this.validExtensions == null) {
            this.validExtensions = Arrays.asList(HiveNatureBuilder.VALID_EXTENSIONS);
        }

        // retrieves the (file) extension from the resource and uses
        // it against the valid extension list to check for validation
        String extension = resource.getFileExtension();
        return this.validExtensions.contains(extension);
    }

    protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
        try {
            // retrieves the current project
            IProject project = getProject();

            // creates a new touch resource visitor
            TouchResourceVisitor touchResourceVisitor = new TouchResourceVisitor();

            // accepts the new touch resource visitor in the project
            project.accept(touchResourceVisitor);
        } catch (CoreException exception) {
        }
    }

    protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
        // creates a new touch resource delta visitor
        TouchResourceDeltaVisitor touchResourceDeltaVisitor = new TouchResourceDeltaVisitor();

        // accepts the new touch resource delta visitor in the delta
        delta.accept(touchResourceDeltaVisitor);
    }

    /**
     * Class used for visiting resources for touching (changing date).
     */
    class TouchResourceVisitor implements IResourceVisitor {
        public boolean visit(IResource resource) {
            try {
                // touches the resource
                touchResource(resource);
            } catch (CoreException exception) {
                // prints the stack trace
                exception.printStackTrace();
            }

            // return true to continue visiting children.
            return true;
        }
    }

    /**
     * Class used for visiting (delta mode) resources for touching (changing
     * date).
     */
    class TouchResourceDeltaVisitor implements IResourceDeltaVisitor {
        /*
         * (non-Javadoc)
         *
         * @see
         * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
         * .core.resources.IResourceDelta)
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            // retrieves the resource from the delta
            IResource resource = delta.getResource();

            // retrieve the delta kind
            int deltaKind = delta.getKind();

            // switched over the delta kind
            switch (deltaKind) {
            case IResourceDelta.ADDED:
                // touches the added resource
                touchResource(resource);

                // breaks switch
                break;
            case IResourceDelta.REMOVED:
                // breaks switch
                break;
            case IResourceDelta.CHANGED:
                // touches the changed resource
                touchResource(resource);

                // breaks switch
                break;
            }

            // return true (continue visiting children)
            return true;
        }
    }
}
