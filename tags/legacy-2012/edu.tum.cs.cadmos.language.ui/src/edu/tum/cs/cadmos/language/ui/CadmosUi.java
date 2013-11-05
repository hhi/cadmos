package edu.tum.cs.cadmos.language.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Model;

public class CadmosUi {

	private static List<ICadmosModelSelectionChangedListener> listeners = new ArrayList<>();

	public static void addListener(ICadmosModelSelectionChangedListener listener) {
		Assert.assertNotContainedIn(listener, listeners, "listener",
				"listeners");
		listeners.add(listener);
	}

	public static void removeListener(
			ICadmosModelSelectionChangedListener listener) {
		Assert.assertContainedIn(listener, listeners, "listener", "listeners");
		listeners.remove(listener);
	}

	public static void fireSelectionChanged(XtextEditor editor, Model model,
			Component selectedComponent, EObject selectedObject) {
		for (final ICadmosModelSelectionChangedListener listener : listeners) {
			listener.selectionChanged(editor, model, selectedComponent,
					selectedObject);
		}
	}

}
