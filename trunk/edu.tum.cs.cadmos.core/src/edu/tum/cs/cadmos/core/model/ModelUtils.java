package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.IListSet;
import edu.tum.cs.cadmos.commons.ListSet;

public class ModelUtils {

	public static IListSet<IComponent> dissolveComponent(
			ICompositeComponent component) {
		final IListSet<IComponent> result = new ListSet<>();
		for (final IComponent child : component.getChildren()) {
			final IComponent childCopy;
			if (child instanceof IAtomicComponent) {
				childCopy = new AtomicComponent(child.getId(), child.getName(),
						null, ((IAtomicComponent) child).getMachine());
			} else if (child instanceof ICompositeComponent) {
				childCopy = new CompositeComponent(child.getId(),
						child.getName(), null);
			} else {
				throw new ClassCastException(
						"'child' is neither IAtomicComponent nor ICompositeComponent");
			}
			result.add(childCopy);
		}
		return result;
	}

}
