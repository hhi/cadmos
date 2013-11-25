package edu.tum.cs.cadmos.language.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.XtextEditor;

public interface ICadmosEditorSelectionChangedListener {

	void selectionChanged(XtextEditor editor, EObject selectedObject);

}
