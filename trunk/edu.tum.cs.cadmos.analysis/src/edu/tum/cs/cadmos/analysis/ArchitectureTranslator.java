package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.common.ListUtils;
import edu.tum.cs.cadmos.common.ObjectUtils;
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosFactory;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.ComponentElement;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.NamedComponentElement;
import edu.tum.cs.cadmos.language.cadmos.Parameter;
import edu.tum.cs.cadmos.language.cadmos.ParameterAssignment;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortRef;
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
		// Phase I: translate ports and embeddings
		for (final ComponentElement element : ListUtils.filter(
				component.getElements(), NamedComponentElement.class)) {
			translateComponentElement(element, parent, parameters);
		}
		// Phase II: translate channels, which reference ports and embedded
		// ports
		for (final ComponentElement element : ModelUtils.getChannels(component)) {
			translateComponentElement(element, parent, parameters);
		}
	}

	private void translateComponentElement(ComponentElement element,
			Node parent, List<Parameter> parameters) {
		Assert.assertNotNull(element, "element");
		if (element instanceof Port) {
			translatePort((Port) element, parent, parameters);
		} else if (element instanceof Embedding) {
			translateEmbedding((Embedding) element, parent, parameters);
		} else if (element instanceof Channel) {
			translateChannel((Channel) element, parent, parameters);
		} else {
			Assert.fails(
					"Expected 'element' to be Port, Embedding or Channel, but was '%s'",
					element.getClass());
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
		// Source side.
		final PortRef srcRef = channel.getSource();
		final Embedding srcEmbedding = srcRef.getEmbedding();
		final Port srcPort = srcRef.getPort();
		final int srcEmbeddingCardinality;
		final int srcPortCardinality;
		if (srcEmbedding != null) {
			// Port belongs to an embedded component.
			final List<Parameter> embeddedParameters = deferEmbeddedParameters(
					srcEmbedding, parameters);
			srcEmbeddingCardinality = eval(srcEmbedding.getCardinality(),
					parameters, 1);
			srcPortCardinality = eval(srcPort.getCardinality(),
					embeddedParameters, 1);
		} else {
			// Port belongs to the current component.
			srcEmbeddingCardinality = 1;
			srcPortCardinality = eval(srcPort.getCardinality(), parameters, 1);
		}
		final String srcEmbeddingIndexId = srcRef.getEmbeddingIndex();
		final String srcPortIndexId = srcRef.getPortIndex();

		// Destination side.
		final PortRef dstRef = channel.getDestination();
		final Embedding dstEmbedding = dstRef.getEmbedding();
		final Port dstPort = dstRef.getPort();
		final int dstEmbeddingCardinality;
		final int dstPortCardinality;
		if (dstEmbedding != null) {
			// Port belongs to an embedded component.
			final List<Parameter> embeddedParameters = deferEmbeddedParameters(
					dstEmbedding, parameters);
			dstEmbeddingCardinality = eval(dstEmbedding.getCardinality(),
					parameters, 1);
			dstPortCardinality = eval(dstPort.getCardinality(),
					embeddedParameters, 1);
		} else {
			// Port belongs to the current component.
			dstEmbeddingCardinality = 1;
			dstPortCardinality = eval(dstPort.getCardinality(), parameters, 1);
		}
		final String dstEmbeddingIndexId = dstRef.getEmbeddingIndex();
		final String dstPortIndexId = dstRef.getPortIndex();

		// Check index names.
		Assert.assertNotEquals(srcEmbeddingIndexId, srcPortIndexId,
				"srcEmbeddingIndexId", "srcPortIndexId", false);
		Assert.assertNotEquals(dstEmbeddingIndexId, dstPortIndexId,
				"dstEmbeddingIndexId", "dstPortIndexId", false);

		// Create linking nodes.
		int channelIndex = 0;
		for (int i = 0; i < dstEmbeddingCardinality; i++) {
			for (int j = 0; j < dstPortCardinality; j++) {
				final int srcEmbeddingIndex;
				final int srcPortIndex;
				final int dstEmbeddingIndex = i;
				final int dstPortIndex = j;
				srcEmbeddingIndex = (srcEmbeddingIndexId == null) ? 0
						: ObjectUtils.equalsInterpretNullAsDefinedValue(
								srcEmbeddingIndexId, dstPortIndexId) ? j : i;
				srcPortIndex = (srcPortIndexId == null) ? 0 : ObjectUtils
						.equalsInterpretNullAsDefinedValue(srcPortIndexId,
								dstEmbeddingIndexId) ? i : j;
				final Node node = new Node(parent, channel, channelIndex++);
				node.addReference(findPortNode(parent, srcRef,
						srcEmbeddingIndex, srcPortIndex));
				node.addReference(findPortNode(parent, dstRef,
						dstEmbeddingIndex, dstPortIndex));
			}
		}
	}

	private static Node findPortNode(Node node, PortRef ref,
			int embeddingIndex, int portIndex) {
		if (ref.getEmbedding() == null) {
			return node.findChild(ref.getPort(), portIndex);
		}
		final Node embeddingNode = node.findChild(ref.getEmbedding(),
				embeddingIndex);
		return embeddingNode.findChild(ref.getPort(), portIndex);
	}

}
