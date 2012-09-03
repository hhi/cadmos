package edu.tum.cs.cadmos.language.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;

import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosPackage;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.NamedComponentElement;
import edu.tum.cs.cadmos.language.cadmos.Parameter;
import edu.tum.cs.cadmos.language.cadmos.ParameterAssignment;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortDirection;
import edu.tum.cs.cadmos.language.cadmos.PortRef;

public class CadmosJavaValidator extends AbstractCadmosJavaValidator {

	// Use this template:
	//
	// @Check
	// public void checkGreetingStartsWithCapital(Greeting greeting) {
	// if (!Character.isUpperCase(greeting.getName().charAt(0))) {
	// warning("Name should start with a capital",
	// MyDslPackage.Literals.GREETING__NAME);
	// }
	// }

	@Check
	public void checkChannelDirections(Channel channel) {
		final PortRef src = channel.getSource();
		if (src.getEmbedding() == null && src.getPort() != null
				&& src.getPort().getDirection() != PortDirection.INBOUND) {
			error("Component-level source port must be inbound",
					CadmosPackage.Literals.CHANNEL__SOURCE);
		}
		if (src.getEmbedding() != null && src.getPort() != null
				&& src.getPort().getDirection() != PortDirection.OUTBOUND) {
			error("Embedding-level source port must be outbound",
					CadmosPackage.Literals.CHANNEL__SOURCE);
		}
		final EList<PortRef> dsts = channel.getDestinations();
		for (int index = 0; index < dsts.size(); index++) {
			final PortRef dst = dsts.get(index);
			if (src.getEmbedding() == null && dst.getEmbedding() == null) {
				error("Component-level ports cannot be linked directly",
						CadmosPackage.Literals.CHANNEL__DESTINATIONS, index);
			}
			if (dst.getEmbedding() == null && dst.getPort() != null
					&& dst.getPort().getDirection() != PortDirection.OUTBOUND) {
				error("Component-level destination port must be outbound",
						CadmosPackage.Literals.CHANNEL__DESTINATIONS, index);
			}
			if (dst.getEmbedding() != null && dst.getPort() != null
					&& dst.getPort().getDirection() != PortDirection.INBOUND) {
				error("Embedding-level destination port must be inbound",
						CadmosPackage.Literals.CHANNEL__DESTINATIONS, index);
			}
		}
	}

	@Check
	public void checkPortLinked(Port port) {
		final Component component = EcoreUtil2.getContainerOfType(port,
				Component.class);
		for (final Channel channel : ModelUtils.getChannels(component)) {
			if (port.getDirection() == PortDirection.INBOUND
					&& channel.getSource().getPort() == port) {
				return;
			}
			if (port.getDirection() == PortDirection.OUTBOUND) {
				for (final PortRef dst : channel.getDestinations()) {
					if (dst.getPort() == port) {
						return;
					}
				}
			}
		}
		warning("Port " + component.getName() + "." + port.getName()
				+ " is unused",
				CadmosPackage.Literals.NAMED_COMPONENT_ELEMENT__NAME);
	}

	@Check
	public void checkEmbeddingPortsLinked(Embedding embedding) {
		final Component component = EcoreUtil2.getContainerOfType(embedding,
				Component.class);
		final List<Channel> channels = ModelUtils.getChannels(component);
		for (final Port port : ModelUtils.getPorts(embedding.getComponent())) {
			boolean linked = false;
			for (final Channel channel : channels) {
				if (port.getDirection() == PortDirection.OUTBOUND
						&& channel.getSource().getPort() == port) {
					linked = true;
					break;
				}
				if (port.getDirection() == PortDirection.INBOUND) {
					for (final PortRef dst : channel.getDestinations()) {
						if (dst.getPort() == port) {
							linked = true;
							break;
						}
					}
				}
			}
			if (!linked) {
				warning("Port " + embedding.getName() + "." + port.getName()
						+ " is unused",
						CadmosPackage.Literals.NAMED_COMPONENT_ELEMENT__NAME);
			}
		}
	}

	@Check
	public void checkUniqueComponentElementNames(Component component) {
		final Map<String, Integer> names = new HashMap<>();
		for (final NamedComponentElement e : ModelUtils.getComponentElements(
				component, NamedComponentElement.class)) {
			final String name = e.getName();
			if (names.containsKey(name)) {
				names.put(name, names.get(name) + 1);
			} else {
				names.put(name, 1);
			}
		}
		for (final NamedComponentElement e : ModelUtils.getComponentElements(
				component, NamedComponentElement.class)) {
			if (names.get(e.getName()) > 1) {
				error("Element name " + e.getName() + " is already defined", e,
						CadmosPackage.Literals.NAMED_COMPONENT_ELEMENT__NAME, 0);
			}
		}
	}

	@Check
	public void checkUniqueComponentParameterNames(Component component) {
		for (final Parameter p1 : component.getParameters()) {
			for (final Parameter p2 : component.getParameters()) {
				if (p1 != p2 && p1.getName().equals(p2.getName())) {
					error("Parameter name " + p1.getName()
							+ " is already defined", p1,
							CadmosPackage.Literals.PARAMETER__NAME, 0);
					break;
				}
			}
		}
	}

	@Check
	public void checkUniqueParameterAssignments(Embedding embedding) {
		for (final ParameterAssignment p1 : embedding.getParameterAssignments()) {
			for (final ParameterAssignment p2 : embedding
					.getParameterAssignments()) {
				if (p1 != p2 && p1.getLeft().equals(p2.getLeft())) {
					error("Parameter " + p1.getLeft().getName()
							+ " is already assigned", p1,
							CadmosPackage.Literals.PARAMETER_ASSIGNMENT__LEFT,
							0);
					break;
				}
			}
		}
	}

}
