package edu.tum.cs.cadmos.language.validation;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;

import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosPackage;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
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
				&& src.getPort().getDirection() != PortDirection.INCOMING) {
			error("Component-level source port must be incoming",
					CadmosPackage.Literals.CHANNEL__SOURCE);
		}
		if (src.getEmbedding() != null && src.getPort() != null
				&& src.getPort().getDirection() != PortDirection.OUTGOING) {
			error("Embedding-level source port must be outgoing",
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
					&& dst.getPort().getDirection() != PortDirection.OUTGOING) {
				error("Component-level destination port must be outgoing",
						CadmosPackage.Literals.CHANNEL__DESTINATIONS, index);
			}
			if (dst.getEmbedding() != null && dst.getPort() != null
					&& dst.getPort().getDirection() != PortDirection.INCOMING) {
				error("Embedding-level destination port must be incoming",
						CadmosPackage.Literals.CHANNEL__DESTINATIONS, index);
			}
		}
	}

	@Check
	public void checkPortLinked(Port port) {
		final Component component = EcoreUtil2.getContainerOfType(port,
				Component.class);
		for (final Channel channel : ModelUtils.getChannels(component)) {
			if (port.getDirection() == PortDirection.INCOMING
					&& channel.getSource().getPort() == port) {
				return;
			}
			if (port.getDirection() == PortDirection.OUTGOING) {
				for (final PortRef dst : channel.getDestinations()) {
					if (dst.getPort() == port) {
						return;
					}
				}
			}
		}
		warning("Port " + component.getName() + "." + port.getName()
				+ " is not used", CadmosPackage.Literals.PORT__NAME);
	}

	@Check
	public void checkEmbeddingPortsLinked(Embedding embedding) {
		final Component component = EcoreUtil2.getContainerOfType(embedding,
				Component.class);
		final List<Channel> channels = ModelUtils.getChannels(component);
		for (final Port port : ModelUtils.getPorts(embedding.getComponent())) {
			boolean linked = false;
			for (final Channel channel : channels) {
				if (port.getDirection() == PortDirection.OUTGOING
						&& channel.getSource().getPort() == port) {
					linked = true;
					break;
				}
				if (port.getDirection() == PortDirection.INCOMING) {
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
						+ " is not used",
						CadmosPackage.Literals.EMBEDDING__NAME);
			}
		}
	}
}
