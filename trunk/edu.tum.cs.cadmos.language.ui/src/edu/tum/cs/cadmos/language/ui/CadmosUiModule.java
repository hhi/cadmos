/*
 * generated by Xtext
 */
package edu.tum.cs.cadmos.language.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class CadmosUiModule extends
		edu.tum.cs.cadmos.language.ui.AbstractCadmosUiModule {
	public CadmosUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public void configureCadmosXtextEditorCallback(Binder binder) {
		binder.bind(IXtextEditorCallback.class)
				.annotatedWith(Names.named("CadmosXtextEditorCallback"))
				.to(CadmosXtextEditorCallback.class);
	}
}