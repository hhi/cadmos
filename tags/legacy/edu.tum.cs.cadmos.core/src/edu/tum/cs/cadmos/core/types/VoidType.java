package edu.tum.cs.cadmos.core.types;

public class VoidType extends AbstractType {

	public static final IType VOID = new VoidType();

	public VoidType() {
		super(EType.VOID);
	}

}
