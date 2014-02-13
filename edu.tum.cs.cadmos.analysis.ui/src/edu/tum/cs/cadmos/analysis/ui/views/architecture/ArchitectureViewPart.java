package edu.tum.cs.cadmos.analysis.ui.views.architecture;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.tum.cs.cadmos.analysis.architecture.model.DFGTranslator;
import edu.tum.cs.cadmos.analysis.architecture.model.Edge;
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.ui.CadmosUi;
import edu.tum.cs.cadmos.language.ui.ICadmosEditorSelectionChangedListener;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ArchitectureViewPart extends ViewPart {

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.views.architecture.ArchitectureViewPart"; //$NON-NLS-1$

	public ArchitectureViewPart() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			tv = new TreeViewer(container, SWT.BORDER);
			Tree tree = tv.getTree();
			tree.setHeaderVisible(true);
			tree.setLinesVisible(true);
			
			
			tv.setContentProvider(new ITreeContentProvider(){

				@Override
				public void dispose() {
					//nothing to do
				}

				@Override
				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
					//
					
				}

				@Override
				public Object[] getElements(Object inputElement) {
					DirectedSparseMultigraph<Vertex, Edge> g = (DirectedSparseMultigraph<Vertex, Edge>) inputElement;
					return g.getVertices().toArray();
				}

				@Override
				public Object[] getChildren(Object parentElement) {
					Vertex v = (Vertex) parentElement;
					Collection<Edge> outEdges = graph.getOutEdges(v);
					
					ArrayList<Vertex> children = new ArrayList<Vertex>();
					for(Edge e: outEdges){
						children.add(graph.getOpposite(v, e));
					}
					
					
					return children.toArray();
					
				}

				@Override
				public Object getParent(Object element) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public boolean hasChildren(Object element) {
					Vertex v = (Vertex) element;
					Collection<Edge> outEdges = graph.getOutEdges(v);
					
					return outEdges.size()>0; 
				}
				
			});
			tv.setLabelProvider(new ILabelProvider() {
				
				@Override
				public void removeListener(ILabelProviderListener listener) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isLabelProperty(Object element, String property) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void dispose() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void addListener(ILabelProviderListener listener) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public String getText(Object element) {
					return ((Vertex)element).getId();
				}
				
				@Override
				public Image getImage(Object element) {
					// TODO Auto-generated method stub
					return null;
				}
			});
		}

//		architectureCanvas = new ArchitectureCanvas(container,
//				SWT.DOUBLE_BUFFERED);
		
		

		createActions();
		initializeToolBar();
		initializeMenu();

		CadmosUi.getInstance()
				.addListener(cadmosEditorSelectionChangedListener);
	}

	@Override
	public void dispose() {
		CadmosUi.getInstance().removeListener(
				cadmosEditorSelectionChangedListener);
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	@SuppressWarnings("unused")
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	@SuppressWarnings("unused")
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	private ICadmosEditorSelectionChangedListener cadmosEditorSelectionChangedListener = new ICadmosEditorSelectionChangedListener() {
		@Override
		public void selectionChanged(XtextEditor editor, EObject selectedObject) {
			final Component component = EcoreUtil2.getContainerOfType(
					selectedObject, Component.class);
//			getArchitectureCanvas().setRootComponent(component);
//			getArchitectureCanvas().setSelectedObject(selectedObject);
			
//			ModelTraverser mt = new ModelTraverser();
//			System.out.println(mt.traverseRoot(component));
//			
			DFGTranslator dfg = new DFGTranslator(component);
//			graph = dfg.translateFlatGraphToDFG();
			graph = dfg.translateToDFG();
			tv.setInput(graph);
			
		};
	};
	
	private DirectedSparseMultigraph<Vertex, Edge> graph;

	private ArchitectureCanvas architectureCanvas;

	private TreeViewer tv;

	public ArchitectureCanvas getArchitectureCanvas() {
		return architectureCanvas;
	}
}
