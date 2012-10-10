package edu.tum.cs.cadmos.language;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.language.cadmos.PrimitiveTypeRef;
import edu.tum.cs.cadmos.language.cadmos.TypeRef;

public class TypeUtils {

	public static String getTypeName(TypeRef typeRef) {
		if (typeRef == null) {
			return "*";
		}
		if (typeRef instanceof PrimitiveTypeRef) {
			return ((PrimitiveTypeRef) typeRef).getType().getLiteral();
		}
		Assert.fails("Unexpected TypeRef class '%s'", typeRef.getClass());
		return "";
	}

}
