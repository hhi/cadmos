package edu.tum.cs.cadmos.language.ui;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.tum.cs.cadmos.language.common.Assert;

public class CadmosXtextEditorCallback extends IXtextEditorCallback.NullImpl {

	private CadmosXtextEditorSelectionChangedListener selectionChangedListener;

	@Override
	public void afterCreatePartControl(XtextEditor editor) {
		super.afterCreatePartControl(editor);
		Assert.assertNull(selectionChangedListener, "selectionChangedListener");
		selectionChangedListener = new CadmosXtextEditorSelectionChangedListener(
				editor);
		final ISelectionProvider provider = editor.getSelectionProvider();
		if (provider instanceof IPostSelectionProvider) {
			((IPostSelectionProvider) provider)
					.addPostSelectionChangedListener(selectionChangedListener);
		} else {
			provider.addSelectionChangedListener(selectionChangedListener);
		}
	}

	@Override
	public void beforeDispose(XtextEditor editor) {
		Assert.assertNotNull(selectionChangedListener,
				"selectionChangedListener");
		final ISelectionProvider provider = editor.getSelectionProvider();
		if (provider instanceof IPostSelectionProvider) {
			((IPostSelectionProvider) provider)
					.removePostSelectionChangedListener(selectionChangedListener);
		} else {
			provider.removeSelectionChangedListener(selectionChangedListener);
		}
		selectionChangedListener = null;
		super.beforeDispose(editor);
	}

}