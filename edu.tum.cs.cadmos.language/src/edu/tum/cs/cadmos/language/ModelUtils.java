package edu.tum.cs.cadmos.language;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.common.ListUtils;
import edu.tum.cs.cadmos.common.Predicate;
import edu.tum.cs.cadmos.language.cadmos.Case;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.ComponentElement;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Expression;
import edu.tum.cs.cadmos.language.cadmos.IntegerLiteral;
import edu.tum.cs.cadmos.language.cadmos.Parameter;
import edu.tum.cs.cadmos.language.cadmos.ParameterRef;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortDirection;
import edu.tum.cs.cadmos.language.cadmos.Value;
import edu.tum.cs.cadmos.language.cadmos.Variable;

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
		return getPorts(component, PortDirection.INBOUND);
	}

	public static List<Port> getOutgoingPorts(Component component) {
		return getPorts(component, PortDirection.OUTBOUND);
	}

	public static List<Variable> getVariables(Component component) {
		return getComponentElements(component, Variable.class);
	}

	public static List<Case> getCases(Component component) {
		return getComponentElements(component, Case.class);
	}

	public static List<Expression> getActions(Component component) {
		return getComponentElements(component, Expression.class);
	}

	public static int eval(Value value, List<Parameter> parameters) {
		if (value instanceof IntegerLiteral) {
			return ((IntegerLiteral) value).getValue();
		}
		Assert.assertInstanceOf(value, ParameterRef.class, "value");
		final Parameter parameter = ((ParameterRef) value).getParameter();
		final String name = parameter.getName();
		for (final Parameter candidate : parameters) {
			if (candidate.getName().equals(name)) {
				return candidate.getValue();
			}
		}
		throw new AssertionError(
				String.format("Parameter '%s' not found", name));
	}

	@SuppressWarnings("rawtypes")
	public static String getEObjectName(EObject eObject) {
		Assert.assertNotNull(eObject, "eObject");
		final EClass eClass = eObject.eClass();
		final EStructuralFeature nameFeature = eClass
				.getEStructuralFeature("name");
		if (nameFeature == null) {
			if (eObject.eContainer() != null) {
				final EObject eContainer = eObject.eContainer();
				final Object eContainingFeature = eContainer.eGet(eObject
						.eContainingFeature());
				if (eContainingFeature instanceof List) {
					return eClass.getName() + "$"
							+ ((List) eContainingFeature).indexOf(eObject);
				}
			}
			return eClass.getName();
		}
		return String.valueOf(eObject.eGet(nameFeature));
	}

	public static <E extends EObject> String getEObjectNames(List<E> list,
			String separator) {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				s.append(separator);
			}
			s.append(getEObjectName(list.get(i)));
		}
		return s.toString();
	}

	public static <E extends EObject> String getParameterNameValuePairs(
			List<Parameter> list, String separator) {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				s.append(separator);
			}
			s.append(list.get(i).getName() + "=" + list.get(i).getValue());
		}
		return s.toString();
	}
}
