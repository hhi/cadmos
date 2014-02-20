package edu.tum.cs.cadmos.analysis.ui.schedule;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import edu.tum.cs.cadmos.analysis.schedule.ScheduleManager;
import edu.tum.cs.cadmos.language.cadmos.Deployment;

public class ScheduleDeploymentHandler extends AbstractHandler implements
		IElementUpdater {

	private static final String DEPLOYMENT_COMMAND_STATE = "edu.tum.cs.cadmos.analysis.ui.ScheduleDeploymentHandlerState";

	private final ScheduleManager scheduleManager = ScheduleManager
			.getInstance();

	private Boolean isSelected = false;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor(event);
		if (xtextEditor != null) {
			final ISelectionProvider selectionProvider = xtextEditor
					.getSelectionProvider();

			final ITextSelection selection = (ITextSelection) selectionProvider
					.getSelection();
			if (selection != null) {
				xtextEditor.getDocument().readOnly(
						new IUnitOfWork<Object, XtextResource>() {
							@Override
							public Object exec(XtextResource resource)
									throws Exception {

								final IParseResult parseResult = resource
										.getParseResult();
								if (parseResult == null) {
									return null;
								}
								final ILeafNode leafNode = NodeModelUtils
										.findLeafNodeAtOffset(
												parseResult.getRootNode(),
												selection.getOffset());
								final EObject selectedObject = NodeModelUtils
										.findActualSemanticObjectFor(leafNode);
								if (selectedObject instanceof Deployment) {
									Deployment deployment = (Deployment) selectedObject;
									final ICommandService service = (ICommandService) HandlerUtil
											.getActiveWorkbenchWindowChecked(
													event).getService(
													ICommandService.class);
									final Command command = event.getCommand();
									final State state = command
											.getState(DEPLOYMENT_COMMAND_STATE);
									if (state != null) {
										isSelected = !(Boolean) state
												.getValue();
										state.setValue(isSelected);
										if (!isSelected) {
											scheduleManager
													.deleteSoftwareComponent();
											scheduleManager
													.deleteProcessingComponent();
											scheduleManager.deleteCostmodel();
										} else {
											scheduleManager
													.addProcessingComponent(deployment
															.getPlc());
											scheduleManager
													.addSoftwareComponent(deployment
															.getSwc());
											scheduleManager
													.addCostmodel(deployment
															.getCost());
										}
									}
									service.refreshElements(command.getId(),
											null);

									if (isSelected) {
										System.out.println("Start scheduling!");
										if (scheduleManager.readyToSchedule()) {
											scheduleManager.schedule();
										}
									}

								} else {
									MessageDialog
											.openError(
													HandlerUtil
															.getActiveWorkbenchWindow(
																	event)
															.getShell(),
													"Can not schedule!",
													"The selection is not a deployment object!");
								}

								return null;
							}

						});
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(isSelected);
	}

}
