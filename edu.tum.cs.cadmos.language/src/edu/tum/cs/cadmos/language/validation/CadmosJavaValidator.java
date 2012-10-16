package edu.tum.cs.cadmos.language.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.common.ListUtils;
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosPackage;
import edu.tum.cs.cadmos.language.cadmos.Callable;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Parameter;
import edu.tum.cs.cadmos.language.cadmos.ParameterRef;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortDirection;
import edu.tum.cs.cadmos.language.cadmos.PortRef;
import edu.tum.cs.cadmos.language.cadmos.Value;
import edu.tum.cs.cadmos.language.cadmos.Variable;

public class CadmosJavaValidator extends AbstractCadmosJavaValidator {

	@Check
	public void checkChannelDirections(Channel channel) {
		final PortRef src = channel.getSource();
		final PortRef dst = channel.getDestination();
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
		if (src.getEmbedding() == null && dst.getEmbedding() == null) {
			error("Component-level ports cannot be linked directly",
					CadmosPackage.Literals.CHANNEL__DESTINATION);
		}
		if (dst.getEmbedding() == null && dst.getPort() != null
				&& dst.getPort().getDirection() != PortDirection.OUTBOUND) {
			error("Component-level destination port must be outbound",
					CadmosPackage.Literals.CHANNEL__DESTINATION);
		}
		if (dst.getEmbedding() != null && dst.getPort() != null
				&& dst.getPort().getDirection() != PortDirection.INBOUND) {
			error("Embedding-level destination port must be inbound",
					CadmosPackage.Literals.CHANNEL__DESTINATION);
		}
	}

	@Check
	public void checkPortLinked(Port port) {
		final Component component = EcoreUtil2.getContainerOfType(port,
				Component.class);
		final List<Channel> channels = ModelUtils.getChannels(component);
		final List<Embedding> embeddings = ModelUtils.getEmbeddings(component);
		if (channels.isEmpty() && embeddings.isEmpty()) {
			return; // This is a "leaf" component.
		}
		for (final Channel channel : channels) {
			if (port.getDirection() == PortDirection.INBOUND
					&& channel.getSource().getPort() == port) {
				return;
			}
			if (port.getDirection() == PortDirection.OUTBOUND) {
				if (channel.getDestination().getPort() == port) {
					return;
				}
			}
		}
		warning("Port " + component.getName() + "." + port.getName()
				+ " is unused", CadmosPackage.Literals.CALLABLE__NAME);
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
					if (channel.getDestination().getPort() == port) {
						linked = true;
						break;
					}
				}
			}
			if (!linked) {
				warning("Port " + embedding.getName() + "." + port.getName()
						+ " is unused", CadmosPackage.Literals.EMBEDDING__NAME);
			}
		}
	}

	@Check
	public void checkUniqueComponentElementNames(Component component) {
		final Map<String, Integer> names = new HashMap<>();
		final List<EObject> namedComponentElements = new ArrayList<>();
		namedComponentElements.addAll(component.getParameters());
		namedComponentElements.addAll(ListUtils.filter(component.getElements(),
				Port.class));
		namedComponentElements.addAll(ListUtils.filter(component.getElements(),
				Variable.class));
		namedComponentElements.addAll(ListUtils.filter(component.getElements(),
				Embedding.class));
		for (final EObject e : namedComponentElements) {
			final String name = ModelUtils.getEObjectName(e);
			if (names.containsKey(name)) {
				names.put(name, names.get(name) + 1);
			} else {
				names.put(name, 1);
			}
		}
		for (final EObject e : namedComponentElements) {
			final String name = ModelUtils.getEObjectName(e);
			if (names.get(name) > 1) {
				EStructuralFeature feature;
				if (e instanceof Callable) {
					feature = CadmosPackage.Literals.CALLABLE__NAME;
				} else if (e instanceof Embedding) {
					feature = CadmosPackage.Literals.EMBEDDING__NAME;
				} else {
					feature = null;
					Assert.fails("Unknown named element's class '%s'", e);
				}
				error("Element name " + name + " is already defined", e,
						feature, 0);
			}
		}
	}

	@Check
	public void checkUnusedComponentParameters(Component component) {
		for (final Parameter p : component.getParameters()) {
			boolean parameterUsed = false;
			for (final Embedding embedding : ModelUtils
					.getEmbeddings(component)) {
				for (final Value value : embedding.getParameterValues()) {
					if (value instanceof ParameterRef
							&& ((ParameterRef) value).getParameter() == p) {
						parameterUsed = true;
						break;
					}
				}
				if (embedding.getCardinality() instanceof ParameterRef) {
					final ParameterRef ref = (ParameterRef) embedding
							.getCardinality();
					if (ref.getParameter() == p) {
						parameterUsed = true;
						break;
					}
				}
				if (parameterUsed) {
					break;
				}
			}
			for (final Port port : ModelUtils.getPorts(component)) {
				if (port.getCardinality() instanceof ParameterRef) {
					final ParameterRef ref = (ParameterRef) port
							.getCardinality();
					if (ref.getParameter() == p) {
						parameterUsed = true;
						break;
					}
				}
				if (parameterUsed) {
					break;
				}
			}
			if (!parameterUsed) {
				warning("Parameter " + p.getName() + " of component "
						+ component.getName() + " is unused", component,
						CadmosPackage.Literals.COMPONENT__PARAMETERS, component
								.getParameters().indexOf(p));
			}
		}
	}

	@Check
	public void checkNumberOfParameterValues(Embedding embedding) {
		final Component component = embedding.getComponent();
		final int actualParameters = embedding.getParameterValues().size();
		if (component == null) {
			return;
		}
		final int expectedParameters = component.getParameters().size();
		if (actualParameters != expectedParameters) {
			final String expectedNames = ModelUtils.getEObjectNames(
					component.getParameters(), ", ");
			final String actualNames = ModelUtils.getValueNames(
					embedding.getParameterValues(), ", ");
			error("Illegal number of parameters. Expected signature ("
					+ expectedNames + "), but was (" + actualNames + ")",
					CadmosPackage.Literals.EMBEDDING__COMPONENT);
		}
	}

	@Check
	public void checkAcyclicEmbedding(Embedding embedding) {
		final Component parentComponent = EcoreUtil2.getContainerOfType(
				embedding, Component.class);
		final Set<Component> onpath = new LinkedHashSet<>();
		onpath.add(parentComponent);
		final Deque<Embedding> work = new LinkedList<>();
		work.addLast(embedding);
		while (!work.isEmpty()) {
			final Embedding current = work.removeFirst();
			final Component component = current.getComponent();
			if (component != null) {
				if (onpath.contains(component)) {
					error("Embedding of "
							+ embedding.getComponent().getName()
							+ " creates infinite cyclic composition ("
							+ ModelUtils.getEObjectNames(Arrays.asList(onpath
									.toArray(new Component[onpath.size()])),
									", ") + ", "
							+ ModelUtils.getEObjectName(component) + ", ...)",
							CadmosPackage.Literals.EMBEDDING__COMPONENT);
					break; // leave dynamic DFS while-loop
				}
				onpath.add(component);
				for (final Embedding componentEmbedding : ModelUtils
						.getEmbeddings(component)) {
					work.add(componentEmbedding);
				}
			}
		}
	}
}
