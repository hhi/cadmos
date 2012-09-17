package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class NodeUtils {

	@SafeVarargs
	public static Collection<Node> filterBySemanticObject(
			Collection<Node> nodes, Class<? extends EObject>... classes) {
		final Collection<Node> result = new ArrayList<>();
		for (final Node node : nodes) {
			for (final Class<? extends EObject> c : classes) {
				if (node != null
						&& c.isAssignableFrom(node.getSemanticObject()
								.getClass())) {
					result.add(node);
				}
			}
		}
		return result;
	}

}
