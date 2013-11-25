package edu.tum.cs.cadmos.analysis.ui.views.architecture;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;

import edu.tum.cs.cadmos.language.cadmos.Component;

public class ArchitectureControl extends Composite {

	private Component rootComponent;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ArchitectureControl(Composite parent, int style) {
		super(parent, style);

	}

	public Component getRootComponent() {
		return rootComponent;
	}

	public void setRootComponent(Component rootComponent) {
		this.rootComponent = rootComponent;
		MessageDialog
				.openInformation(null, "CHANGED", rootComponent.toString());
	}

}
