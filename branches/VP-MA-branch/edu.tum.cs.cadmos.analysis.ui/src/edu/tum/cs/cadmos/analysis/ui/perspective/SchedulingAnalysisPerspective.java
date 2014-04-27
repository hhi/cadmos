package edu.tum.cs.cadmos.analysis.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;

public class SchedulingAnalysisPerspective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
		layout.addPlaceholder(ConstraintView.ID, IPageLayout.RIGHT, 0.7f, IPageLayout.ID_EDITOR_AREA);
		layout.addPlaceholder(ScheduleChart.ID, IPageLayout.BOTTOM, 0.7f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(ConstraintView.ID, IPageLayout.RIGHT, 0.7f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(ComplementConstraintView.ID, IPageLayout.BOTTOM, 0.7f, ConstraintView.ID);
		layout.addView(SchedulingAnalysisView.ID, IPageLayout.RIGHT, 0.5f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(DebugIOView.ID, IPageLayout.BOTTOM, 0.8f, IPageLayout.ID_EDITOR_AREA);
		
		IViewLayout viewLayout = layout.getViewLayout(ComplementConstraintView.ID);
        viewLayout.setCloseable(false);
	}

	/**
	 * Add fast views to the perspective.
	 */
	private void addFastViews(IPageLayout layout) {
	}

	/**
	 * Add view shortcuts to the perspective.
	 */
	private void addViewShortcuts(IPageLayout layout) {
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
	}

}
