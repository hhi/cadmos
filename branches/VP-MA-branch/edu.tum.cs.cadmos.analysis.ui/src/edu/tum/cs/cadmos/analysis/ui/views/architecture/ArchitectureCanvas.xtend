package edu.tum.cs.cadmos.analysis.ui.views.architecture

import edu.tum.cs.cadmos.analysis.ui.commons.PeriodicRedrawTimer
import edu.tum.cs.cadmos.language.cadmos.Component
import org.eclipse.emf.ecore.EObject
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite

class ArchitectureCanvas extends Canvas {

	new(Composite parent, int style) {
		super(parent, style)
		addPaintListener(paintListener)
		new PeriodicRedrawTimer(this, 30, 15, [|true])
	}

	val PaintListener paintListener = [
		
	]

	@Property Component rootComponent

	def void setRootComponent(Component rootComponent) {
		_rootComponent = rootComponent
	}

	@Property EObject selectedObject

	def void setSelectedObject(EObject selectedObject) {
		_selectedObject = selectedObject
	}
}
