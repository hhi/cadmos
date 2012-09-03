package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.common.Assert;
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
		return translateComponent(rootComponent, null,
				rootComponent.getParameters());
	}

	private Node translateComponent(Component component, Node parent,
			List<Parameter> parameters) {
		final Node node = new Node(parent, component, 0);
		for (final ComponentElement element : component.getElements()) {
			translateComponentElement(element, node, parameters);
		}
		return node;
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

	private static int evalCardinality(boolean multiple, Value cardinality,
			List<Parameter> parameters) {
		if (cardinality != null && multiple) {
			return ModelUtils.eval(cardinality, parameters);
		}
		return 1;
	}

	private static void translatePort(Port port, Node parent,
			List<Parameter> parameters) {
		final int cardinality = evalCardinality(port.isMultiple(),
				port.getCardinality(), parameters);
		for (int i = 0; i < cardinality; i++) {
			new Node(parent, port, i);
		}
	}

	private void translateEmbedding(Embedding embedding, Node parent,
			List<Parameter> parameters) {
		// Create list of optionally overwritten parameters for embedded
		// component.
		final Component component = embedding.getComponent();
		final List<Parameter> embeddedParameters = new ArrayList<>();
		for (final Parameter parameter : component.getParameters()) {
			final Parameter embeddedParameter = factory.createParameter();
			embeddedParameters.add(embeddedParameter);
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
		// Translate embedding and create instance of embedded component.
		final int cardinality = evalCardinality(embedding.isMultiple(),
				embedding.getCardinality(), parameters);
		for (int i = 0; i < cardinality; i++) {
			final Node embeddingNode = new Node(parent, embedding, i);
			translateComponent(component, embeddingNode, embeddedParameters);
		}
	}

	private void translateChannel(Channel channel, Node parent,
			List<Parameter> parameters) {

	}

}
