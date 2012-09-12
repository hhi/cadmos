package edu.tum.cs.cadmos.language.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import edu.tum.cs.cadmos.analysis.Architecture2Node;
import edu.tum.cs.cadmos.analysis.Graph;
import edu.tum.cs.cadmos.analysis.HFRNodeLayout;
import edu.tum.cs.cadmos.analysis.Node;
import edu.tum.cs.cadmos.analysis.Node2Graph;
import edu.tum.cs.cadmos.analysis.Vector2D;
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Port;

public class CadmosXtextModelSelectionChangedListener implements
		ISelectionChangedListener {

	private final XtextEditor editor;

	public CadmosXtextModelSelectionChangedListener(XtextEditor editor) {
		this.editor = editor;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection().isEmpty()
				|| !(event.getSelection() instanceof ITextSelection)) {
			return;
		}
		final ITextSelection selection = (ITextSelection) event.getSelection();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				final IParseResult parseResult = resource.getParseResult();
				if (parseResult == null) {
					return;
				}
				final ICompositeNode rootNode = parseResult.getRootNode();
				final ILeafNode leafNode = NodeModelUtils.findLeafNodeAtOffset(
						rootNode, selection.getOffset());
				EObject selectedObject = NodeModelUtils
						.findActualSemanticObjectFor(leafNode);

				// FIXME: The following is for debugging only
				if (!(selectedObject instanceof Component)) {
					selectedObject = EcoreUtil2.getContainerOfType(
							selectedObject, Component.class);
					if (selectedObject == null) {
						return;
					}
				}
				final Component component = (Component) selectedObject;

				System.out.println();
				System.out.println("----------------------------------------");

				final Architecture2Node architecture2Node = new Architecture2Node(
						component);
				long t = System.nanoTime();
				final Node node = architecture2Node.translate();
				t = System.nanoTime() - t;
				System.out.println();
				System.out.println(node);
				System.out.println("Architecture2Node translation: "
						+ (t / 1000) / 1000.0 + "ms");

				t = System.nanoTime();
				final Graph graph = Node2Graph.translate(node);
				t = System.nanoTime() - t;
				System.out.println();
				for (final Node v : graph) {
					System.out.println(v.getId());
				}
				for (final Pair<Node, Node> e : graph.getEdges()) {
					System.out.println("(" + e.getFirst().getId() + ", "
							+ e.getSecond().getId() + ")");
				}
				System.out.println("Node2Graph translation: " + (t / 1000)
						/ 1000.0 + "ms");

				final int size = 400;
				final HFRNodeLayout layout = new HFRNodeLayout(graph, size * 2,
						size);

				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				final JPanel panel = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						((Graphics2D) g).setRenderingHint(
								RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						final Color fc = new Color(0, 0, 0);
						final Color bc = new Color(255, 255, 255);
						g.setColor(bc);
						g.fillRect(0, 0, size * 2 + 40, size + 40);
						g.setColor(fc);
						for (final Pair<Node, Node> e : graph.getEdges()) {
							final Vector2D p1 = layout.get(e.getFirst());
							final Vector2D p2 = layout.get(e.getSecond());
							g.fillOval(20 + p1.roundX() - 2,
									20 + p1.roundY() - 2, 4, 4);
							g.drawLine(20 + p1.roundX(), 20 + p1.roundY(),
									20 + p2.roundX(), 20 + p2.roundY());
						}
						for (final Node v : graph) {
							final Vector2D pos = layout.get(v);
							if (v.getSemanticObject() instanceof Port) {
								g.setColor(fc);
								g.drawOval(20 + pos.roundX() - 4,
										20 + pos.roundY() - 4, 7, 7);
							} else {
								final Vector2D r = new Vector2D(8, 8);
								g.setColor(bc);
								g.fillRoundRect(20 + pos.roundX() - r.roundX(),
										20 + pos.roundY() - r.roundY(),
										r.roundX() * 2, r.roundY() * 2, 8, 8);
								g.setColor(fc);
								g.drawRoundRect(20 + pos.roundX() - r.roundX(),
										20 + pos.roundY() - r.roundY(),
										r.roundX() * 2, r.roundY() * 2, 8, 8);
								if (layout.done()) {
									final String name = ModelUtils
											.getEObjectName(v
													.getSemanticObject())
											+ "[" + v.getIndex() + "]";
									final Rectangle2D bounds = g
											.getFontMetrics().getStringBounds(
													name, g);
									g.drawString(
											name,
											20 + pos.roundX()
													- (int) bounds.getWidth()
													/ 2,
											20 + pos.roundY()
													+ (int) bounds.getHeight()
													+ r.roundY());
								}
							}
						}
						int i = 0;
						while (!layout.done() && i++ < 10) {
							layout.step();
						}
					}
				};
				f.add(panel);
				f.setSize(size * 2 + 50, size + 80);
				f.setVisible(true);
				Display.getDefault().timerExec(100, new Runnable() {
					@Override
					public void run() {
						if (!f.isVisible()) {
							return;
						}
						panel.repaint();
						Display.getDefault().timerExec(30, this);
					}
				});
			}
		});
	}
}
