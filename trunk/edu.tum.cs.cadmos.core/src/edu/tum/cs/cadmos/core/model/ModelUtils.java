package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.commons.IListSet;
import edu.tum.cs.cadmos.commons.ListSet;

public class ModelUtils {

	public static List<IAtomicComponent> getAtomicComponents(
			IComponent component) {
		if (component instanceof IAtomicComponent) {
			return asList((IAtomicComponent) component);
		}
		assertTrue(component instanceof ICompositeComponent,
				"Expected ICompositeComponent, but was '%s'", component);
		final List<IAtomicComponent> result = new ArrayList<>();
		for (final IComponent child : ((ICompositeComponent) component)
				.getChildren()) {
			result.addAll(getAtomicComponents(child));
		}
		return result;
	}

	public static IListSet<IComponent> analyzeFlatAtomicComponentNetwork(
			ICompositeComponent component) {
		/* Clone the network of children components. */
		final IListSet<IComponent> network = new ListSet<>();
		for (final IComponent child : component.getChildren()) {
			network.add(child.clone(null));
		}
		// /* Rewire the channels connected to the children components. */
		// for (final IComponent child : network) {
		// for (final IChannel c : child.getIncoming()) {
		// if (c.getSrc() == null) {
		// assertNotNull(c.getDst(), "c.getDst()");
		// final IComponent newDst = network.get(c.getDst());
		// assertNotNull(newDst, "newDst");
		// final IChannel link = component.getIncoming().get(c);
		// assertNotNull(link, "link");
		// c.clone(newSrc, newDst);
		// }
		// }
		// for (final IChannel c : child.getOutgoing()) {
		// final IComponent newSrc = cloneChildren.get(c.getSrc());
		// final IComponent newDst;
		// if (c.getDst() == null) {
		// newDst = null;
		// } else {
		// newDst = cloneChildren.get(c.getDst());
		// }
		// c.clone(newSrc, newDst);
		// }
		// }

		return network;
	}

}
