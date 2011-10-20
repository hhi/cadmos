package edu.tum.cs.cadmos.core.types;

import java.util.Collections;
import java.util.List;

public class EnumerationType extends AbstractType {

	private final List<String> elements;

	public EnumerationType(List<String> elements) {
		super(EType.ENUMERATION);
		this.elements = Collections.unmodifiableList(elements);
	}

	public List<String> getElements() {
		return elements;
	}

}
