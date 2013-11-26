package edu.tum.cs.cadmos.analysis.ui.views.architecture

import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.events.PaintListener
import edu.tum.cs.cadmos.language.cadmos.Component
import org.eclipse.emf.ecore.EObject

class ArchitectureCanvas extends Canvas {

	new(Composite parent, int style) {
		super(parent, style)
		addPaintListener(paintListener)
	}

	val PaintListener paintListener = []

	@Property Component rootComponent
	
	def void setRootComponent(Component rootComponent) {
		_rootComponent = rootComponent
		redraw
	}

	@Property EObject selectedObject

	def void setSelectedObject(EObject selectedObject) {
		_selectedObject = selectedObject
		redraw
	}
}
