/*
 * generated by Xtext
 */
package edu.tum.cs.cadmos.language.generator

import com.google.inject.Inject
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.IntegerLiteral
import edu.tum.cs.cadmos.language.cadmos.Parameter
import edu.tum.cs.cadmos.language.cadmos.ParameterRef
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.tum.cs.cadmos.language.cadmos.Value
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.emf.ecore.EObject
import edu.tum.cs.cadmos.language.cadmos.Model
import org.eclipse.xtext.EcoreUtil2
import edu.tum.cs.cadmos.common.Assert
import edu.tum.cs.cadmos.language.cadmos.PrimitiveTypeRef
import edu.tum.cs.cadmos.language.cadmos.TypeRef
import edu.tum.cs.cadmos.language.cadmos.PrimitiveTypes

class CadmosGenerator implements IGenerator {
	
	@Inject extension IQualifiedNameProvider
	
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
		generatePortClass(fsa);
		for(c: resource.allContents.toIterable.filter(typeof(Component))) {
			fsa.generateFile(c.fullyQualifiedName.toString("/") + ".java", c.compile)
		}
	}
	
	def generatePortClass(IFileSystemAccess access) { 
		access.generateFile("utils/Port.java", compilePort());
	}
	
	def String compilePort() '''
		package utils;
		
		import java.util.LinkedList;
		
		public class Port<T> {
		
			private final LinkedList<T> buffer;
		
			public Port() {
				buffer = new LinkedList<>();
			}
		
			public synchronized void push(T e) {
				buffer.add(e);
			}
		
			public synchronized T pop() {
				if (buffer.size() == 0) {
					return null;
				}
				
				T e = buffer.getFirst();
				buffer.removeFirst();
				return e;
			}

		}
	'''
	
	
	def String compile(Component c) '''
		«val packageName = c.model.fullyQualifiedName»
		«IF packageName != null»
			package «packageName.toString(".")»;
		«ENDIF»
		
		import utils.*;
		
		
		import utils.*;
		
		public class «c.name» {
			
			«FOR e : c.elements»
				«switch e {
					Port : e.compileDecl
					Embedding : e.compileDecl
				}»
			«ENDFOR»
			«c.parameters.compileDecl»
			
			/**
			 * Constructor for embedding «c.name.article» <i>«c.name»</i>«IF !c.parameters.isEmpty» with arbitrary parameters«ENDIF».
			 «FOR p : c.parameters SEPARATOR "\n"»* @param «p.name» where «p.name» &ge; 0«ENDFOR»
			 */
			public «c.name»(«c.parameters.compileDeclArgument») {
				«FOR p : c.parameters»
					assert «p.name» >= 0;
				«ENDFOR»
				«c.parameters.compileInit»
				
				«FOR e : c.elements»
					«switch e {
						Port : e.compileInstantiation
						Embedding : e.compileInstantiation
					}»
				«ENDFOR»
			}
			
			«IF !c.parameters.empty »
			/**
			 * Default constructor for using «c.name.article» <i>«c.name»</i> with default parameters.
			 * <ul>
			 «FOR p : c.parameters SEPARATOR "\n"»*   <li> «p.name» = «p.value»«ENDFOR»
			 * </ul>
			 */
			public «c.name»() {
				this(«FOR p : c.parameters SEPARATOR ", "»«p.value»«ENDFOR»);
			}
			«ENDIF»
		}
	'''
	
	def compileInstantiation(Embedding e) '''
		«if(e.eIsSet(e.eClass.getEStructuralFeature("cardinality"))) {
			'''
			«e.name» = new «e.component.name»[«e.cardinality.compile»];
			for(int i = 0; i < «e.cardinality.compile»; ++ i)
				«e.name»[i] = new «e.component.name»(«FOR v : e.parameterValues SEPARATOR ", "»«v.compile»«ENDFOR»);
			'''
		} else {
			'''
			«e.name» = new «e.component.name»(«FOR v : e.parameterValues SEPARATOR ", "»«v.compile»«ENDFOR»);
			'''
		}»
	'''
	def compile(Value v) {
		if (v instanceof IntegerLiteral) {
			var IntegerLiteral il = v as IntegerLiteral
			'''«il.value»'''
		} else if (v instanceof ParameterRef) {
			var ParameterRef pr = v as ParameterRef
			'''«pr.parameter.name»'''
		}
	}


	def String compileDecl(Embedding e) '''
		«if(e.eIsSet(e.eClass.getEStructuralFeature("cardinality"))) {
			'''private final «e.component.name»[] «e.name»;'''
		} else {
			'''private final «e.component.name» «e.name»;'''
		}»
	'''

	
	def compileDecl(EList<Parameter> list) { 
		'''«FOR p : list BEFORE "private final int " SEPARATOR "\nprivate final int "»«p.name»;«ENDFOR»'''
	}

	def compileInitDefault(EList<Parameter> list) {
		'''«FOR p : list BEFORE "this." SEPARATOR "\nthis."»«p.name» = «p.value»;«ENDFOR»'''
	}

	def compileInit(EList<Parameter> list) { 
		'''«FOR p : list BEFORE "this." SEPARATOR "\nthis."»«p.name» = «p.name»;«ENDFOR»'''
	}

	def compileDeclArgument(EList<Parameter> list) { 
		'''«FOR p : list BEFORE "int " SEPARATOR ", int "»«p.name»«ENDFOR»'''
	}
	
	def compileInstantiation(Port p) '''
		«if(p.eIsSet(p.eClass.getEStructuralFeature("cardinality"))) {
			'''
			«p.identifier» = new Port[«p.cardinality.compile»];
			for(int i = 0; i < «p.cardinality.compile»; ++ i)
				«p.identifier»[i] = new Port<«p.typeRef.typeName»>();
			'''
		} else {
			'''
			«p.identifier» = new Port<«p.typeRef.typeName»>();
			'''
		}»
	'''
	
	def Model model(Component c) {
		EcoreUtil2::getContainerOfType(c, typeof(Model))
	}
	
	def String compileDecl(Port p) '''
		«if(p.eIsSet(p.eClass.getEStructuralFeature("cardinality"))) {
			'''public final Port<«p.typeRef.typeName»>[] «p.identifier»;'''
		} else {
			'''public final Port<«p.typeRef.typeName»> «p.identifier»;'''
		}»
	'''	
	
	def String identifier(EObject obj) {
		val nameFeature = obj.eClass.getEStructuralFeature("name")
		Assert::assertNotNull(nameFeature, "nameFeature")
		obj.eGet(nameFeature) as String
	}
	
	def String article(String name) {
		if (name.startsWithVocal()) {
			return "an"
		}
		return "a"
	} 
	
	def boolean startsWithVocal(String s) {
		if (s.length == 0) return false
		val c = s.substring(0, 1).toLowerCase
		return (c.equals("a") || c.equals("e") || c.equals("i") || c.equals("o") || c.equals("u"))
	}
	
	def String typeName(TypeRef ref) {
		switch ref {
			PrimitiveTypeRef : ref.type.primitiveTypeName
			default: "Object"
		}
	}

	def String primitiveTypeName(PrimitiveTypes t) {
		switch t {
			case PrimitiveTypes::BOOLEAN : "Boolean"
			case PrimitiveTypes::INTEGER : "Integer"
			case PrimitiveTypes::REAL : "Float"
		}
	}
	
}