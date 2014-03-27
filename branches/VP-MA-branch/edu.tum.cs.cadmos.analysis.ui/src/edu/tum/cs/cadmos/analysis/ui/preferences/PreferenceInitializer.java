package edu.tum.cs.cadmos.analysis.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.tum.cs.cadmos.analysis.ui.AnalysisUi;

public class PreferenceInitializer extends AbstractPreferenceInitializer {


	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = AnalysisUi.getDefault().getPreferenceStore();
	    if(System.getProperty("os.name").toLowerCase().contains("windows")){
	    	store.setDefault("PATH_Z3", "C:\\Program Files\\Z3\\z3.exe");
	    } else {
	    	//Mac OS X
	    	store.setDefault("PATH_Z3", "/Applications/Z3/z3");
	    }

	}

}
