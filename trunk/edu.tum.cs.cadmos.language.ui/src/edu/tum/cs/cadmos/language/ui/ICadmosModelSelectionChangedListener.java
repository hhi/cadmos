package edu.tum.cs.cadmos.language.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Model;

public interface ICadmosModelSelectionChangedListener {

	void selectionChanged(XtextEditor editor, Model model,
			Component selectedComponent, EObject selectedObject);

}
