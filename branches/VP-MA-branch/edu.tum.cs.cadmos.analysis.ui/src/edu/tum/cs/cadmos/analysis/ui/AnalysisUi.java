package edu.tum.cs.cadmos.analysis.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AnalysisUi extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.tum.cs.cadmos.analysis.ui"; //$NON-NLS-1$

	// The shared instance
	private static AnalysisUi plugin;
	
	/**
	 * The constructor
	 */
	public AnalysisUi() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AnalysisUi getDefault() {
		return plugin;
	}

}
