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

package pt.hive.eclipse.plugins.nature;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plugin id.
     */
    public static final String PLUGIN_ID = "pt.hive.eclipse.plugins.nature";

    /**
     * The shared instance.
     */
    private static Activator plugin;

    /**
     * The configuration file
     */
    private static File configurationFile;

    /**
     * The building enabled flag.
     */
    private static boolean buildingEnabled = true;

    /**
     * The constructor.
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        // sets the plugin reference
        plugin = this;

        // sets the configuration file
        configurationFile = context.getDataFile("configuration");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
     * BundleContext )
     */
    public void stop(BundleContext context) throws Exception {
        // unsets the plugin reference
        plugin = null;

        super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return The shared instance.
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     *
     * @param path
     *            the path.
     * @return The image descriptor.
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Retrieves the configuration file.
     *
     * @return The configuration file.
     */
    public static File getConfigurationFile() {
        return configurationFile;
    }

    /**
     * Returns the building enabled.
     *
     * @return The building enabled.
     */
    public static boolean isBuildingEnabled() {
        return buildingEnabled;
    }

    /**
     * Sets the building enabled.
     *
     * @param buildingEnabled
     *            The building enabled.
     */
    public static void setBuildingEnabled(boolean buildingEnabled) {
        Activator.buildingEnabled = buildingEnabled;
    }
}
