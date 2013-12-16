package edu.tum.cs.cadmos.language.ui

import org.eclipse.jface.text.ITextSelection
import org.eclipse.jface.viewers.ISelectionChangedListener
import org.eclipse.jface.viewers.SelectionChangedEvent
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.ui.editor.XtextEditor

class CadmosXtextEditorSelectionChangedListener implements ISelectionChangedListener {

	val XtextEditor editor

	new(XtextEditor editor) {
		this.editor = editor
	}

	override selectionChanged(SelectionChangedEvent e) {
		if (!(e.selection instanceof ITextSelection)) {
			return
		}
		val selection = e.selection as ITextSelection
		editor.document.readOnly [
			val result = it.getParseResult()
			if (result == null) {
				return null
			}
			val leafNode = NodeModelUtils.findLeafNodeAtOffset(result.rootNode, selection.offset)
			val selectedObject = NodeModelUtils.findActualSemanticObjectFor(leafNode)
			CadmosUi.instance.fireSelectionChanged(editor, selectedObject)
			return null
		]
	}

}
