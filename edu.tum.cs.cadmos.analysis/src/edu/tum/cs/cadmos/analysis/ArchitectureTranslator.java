package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.common.ListUtils;
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosFactory;
import edu.tum.cs.cadmos.language.cadmos.CadmosPackage;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.ComponentElement;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Parameter;
import edu.tum.cs.cadmos.language.cadmos.ParameterAssignment;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.Value;

public class ArchitectureTranslator {

	private final Component rootComponent;
	private final CadmosFactory factory;

	public ArchitectureTranslator(Component rootComponent) {
		this.rootComponent = rootComponent;
		this.factory = CadmosFactory.eINSTANCE;
	}

	public Node translate() {
		final Node rootNode = new Node(null, rootComponent, 0);
		translateComponent(rootComponent, rootNode,
				rootComponent.getParameters());
		return rootNode;
	}

	private void translateComponent(Component component, Node parent,
			List<Parameter> parameters) {
		for (final ComponentElement element : component.getElements()) {
			translateComponentElement(element, parent, parameters);
		}
	}

	private void translateComponentElement(ComponentElement element,
			Node parent, List<Parameter> parameters) {
		final int classifierID = element.eClass().getClassifierID();
		switch (classifierID) {
		case CadmosPackage.PORT:
			translatePort((Port) element, parent, parameters);
			break;
		case CadmosPackage.EMBEDDING:
			translateEmbedding((Embedding) element, parent, parameters);
			break;
		case CadmosPackage.CHANNEL:
			translateChannel((Channel) element, parent, parameters);
			break;
		default:
			Assert.fails(
					"Expected PORT='%s', EMBEDDING='%s' or CHANNEL='%s', but was '%s'",
					CadmosPackage.PORT, CadmosPackage.EMBEDDING,
					CadmosPackage.CHANNEL, classifierID);
		}
	}

	private static int eval(Value value, List<Parameter> parameters,
			int defaultValue) {
		if (value != null) {
			return ModelUtils.eval(value, parameters);
		}
		return defaultValue;
	}

	private static void translatePort(Port port, Node parent,
			List<Parameter> parameters) {
		final int cardinality = eval(port.getCardinality(), parameters, 1);
		for (int i = 0; i < cardinality; i++) {
			new Node(parent, port, i);
		}
	}

	private void translateEmbedding(Embedding embedding, Node parent,
			List<Parameter> parameters) {
		// Create list of optionally overwritten parameters for embedded
		// component.
		final List<Parameter> embeddedParameters = deferEmbeddedParameters(
				embedding, parameters);
		// Translate embedding and create instance of embedded component.
		final int cardinality = eval(embedding.getCardinality(), parameters, 1);
		for (int i = 0; i < cardinality; i++) {
			final Node embeddingNode = new Node(parent, embedding, i);
			translateComponent(embedding.getComponent(), embeddingNode,
					embeddedParameters);
		}
	}

	private List<Parameter> deferEmbeddedParameters(Embedding embedding,
			List<Parameter> parameters) {
		final List<Parameter> result = new ArrayList<>();
		final Component component = embedding.getComponent();
		if (component == null) {
			return result;
		}
		for (final Parameter parameter : ListUtils.nullIsEmpty(component
				.getParameters())) {
			final Parameter embeddedParameter = factory.createParameter();
			result.add(embeddedParameter);
			final String name = parameter.getName();
			embeddedParameter.setName(name);
			int value = parameter.getValue(); // Begin with default value.
			for (final ParameterAssignment assignment : embedding
					.getParameterAssignments()) {
				if (assignment.getLeft().getName().equals(name)) {
					// Overwrite default value with assigned value.
					value = ModelUtils.eval(assignment.getRight(), parameters);
					break;
				}
			}
			embeddedParameter.setValue(value);
		}
		return result;
	}

	private void translateChannel(Channel channel, Node parent,
			List<Parameter> parameters) {
		// final PortRef srcRef = channel.getSource();
		// final Embedding srcEmbedding = srcRef.getEmbedding();
		// final Port srcPort = srcRef.getPort();
		// final int srcEmbeddingCardinality = srcEmbedding != null ?
		// evalOrdinalValue(
		// srcEmbedding.isMultiple(), srcEmbedding.getCardinality(),
		// parameters) : 1;
		// final int srcPortCardinality = evalOrdinalValue(srcPort.isMultiple(),
		// srcPort.getCardinality(), parameters);
		// for (final PortRef dstRef : channel.getDestinations()) {
		// final Embedding dstEmbedding = dstRef.getEmbedding();
		// final Port dstPort = dstRef.getPort();
		// final int dstEmbeddingCardinality = dstEmbedding != null ?
		// evalOrdinalValue(
		// dstEmbedding.isMultiple(), dstEmbedding.getCardinality(),
		// parameters) : 1;
		// final int dstPortCardinality = evalOrdinalValue(
		// dstPort.isMultiple(), dstPort.getCardinality(), parameters);
		// final Node channelNode = new Node(parent, channel, 0);
		// // for (int i = 0; i < cardinality; i++) {
		// // new Node(channelNode, dstRef, i);
		// // }
		// }
	}

}
