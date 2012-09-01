package edu.tum.cs.cadmos.language;

import java.util.List;

import edu.tum.cs.cadmos.common.ListUtils;
import edu.tum.cs.cadmos.common.Predicate;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.ComponentElement;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortDirection;

public class ModelUtils {

	private static final class PortDirectionPredicate implements
			Predicate<ComponentElement> {
		private final PortDirection direction;

		public PortDirectionPredicate(PortDirection direction) {
			this.direction = direction;
		}

		@Override
		public boolean holdsFor(ComponentElement element) {
			return (element instanceof Port)
					&& ((Port) element).getDirection() == direction;
		}
	}

	public static <E extends ComponentElement> List<E> getComponentElements(
			Component component, Class<E> clazz) {
		return ListUtils.filter(component.getElements(), clazz);
	}

	public static List<Port> getPorts(Component component) {
		return getComponentElements(component, Port.class);
	}

	public static List<Embedding> getEmbeddings(Component component) {
		return getComponentElements(component, Embedding.class);
	}

	public static List<Channel> getChannels(Component component) {
		return getComponentElements(component, Channel.class);
	}

	public static List<Port> getPorts(Component component,
			PortDirection direction) {
		return ListUtils.filter(component.getElements(),
				new PortDirectionPredicate(direction));
	}

	public static List<Port> getIncomingPorts(Component component) {
		return getPorts(component, PortDirection.INCOMING);
	}

	public static List<Port> getOutgoingPorts(Component component) {
		return getPorts(component, PortDirection.OUTGOING);
	}

}
