package edu.tum.cs.cadmos.language.ui

import edu.tum.cs.cadmos.language.common.Assert
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.ui.editor.XtextEditor

/**
 * The 'singleton' (provided by injection) that collects and dispatches Cadmos
 * user-interface events (e.g. changes in a Cadmos-XtextEditor selection).
 */
class CadmosUi {
	
	static CadmosUi instance = new CadmosUi

	List<ICadmosEditorSelectionChangedListener> listeners = newArrayList
	
	def static getInstance() {
		instance
	}

	def addListener(ICadmosEditorSelectionChangedListener listener) {
		Assert.assertNotContainedIn(listener, listeners, "listener", "listeners")
		listeners.add(listener)
	}

	def removeListener(ICadmosEditorSelectionChangedListener listener) {
		Assert.assertContainedIn(listener, listeners, "listener", "listeners")
		listeners.remove(listener)
	}

	def fireSelectionChanged(XtextEditor editor, EObject selectedObject) {
		listeners.forEach[selectionChanged(editor, selectedObject)]
	}
	
}
