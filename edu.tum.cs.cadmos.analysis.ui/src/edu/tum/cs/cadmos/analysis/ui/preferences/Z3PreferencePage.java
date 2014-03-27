/**
 * 
 */
package edu.tum.cs.cadmos.analysis.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.xtext.ui.editor.preferences.fields.LabelFieldEditor;

import edu.tum.cs.cadmos.analysis.ui.AnalysisUi;

/**
 * @author doebber
 * 
 */
public class Z3PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public Z3PreferencePage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(AnalysisUi.getDefault().getPreferenceStore());
		setDescription("Properties concerning Z3 and the SMT scripts");
	}

	@Override
	protected void createFieldEditors() {
//		addField(new DirectoryFieldEditor("PATH_Z3", "&Z3 location:",
//				getFieldEditorParent()));
		addField(new FileFieldEditor("PATH_Z3", "&Z3 location:", true, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
//		addField(new BooleanFieldEditor("BOOLEAN_VALUE",
//				"&An example of a boolean preference", getFieldEditorParent()));
		addField(new LabelFieldEditor("//TODO \n"
				+ "For further details concerning SMT script generation check UnsatCorePreferences.class", getFieldEditorParent()));
	}

}
