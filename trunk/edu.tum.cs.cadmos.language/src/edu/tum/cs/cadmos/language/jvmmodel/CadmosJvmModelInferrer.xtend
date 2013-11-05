package edu.tum.cs.cadmos.language.jvmmodel

import com.google.inject.Inject
import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.ForScope
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.tum.cs.cadmos.language.cadmos.PortRef
import edu.tum.cs.cadmos.language.cadmos.Transition
import edu.tum.cs.cadmos.language.utils.ComponentUtils
import edu.tum.cs.cadmos.lib.model.InPort
import edu.tum.cs.cadmos.lib.model.InPortList
import edu.tum.cs.cadmos.lib.model.OutPort
import edu.tum.cs.cadmos.lib.model.OutPortList
import edu.tum.cs.cadmos.lib.model.SubComponentList
import java.util.ArrayList
import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.JvmFormalParameter
import org.eclipse.xtext.common.types.JvmMember
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.Primitives
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class CadmosJvmModelInferrer extends AbstractModelInferrer {

	@Inject extension ComponentUtils
	@Inject extension InferrerUtils
	@Inject extension JvmTypesBuilder
	@Inject extension IQualifiedNameProvider
	@Inject extension Primitives
	@Inject extension TypeReferenceSerializer
	@Inject TypeReferences typeReferences

	def dispatch void infer(Component component, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
		acceptor.accept(component.toClass(component.fullyQualifiedName)).initializeLater(
			[
				documentation = component.documentation
				/* Get safe features so that syntax issues cannot cause update problems with NPEs and proxies. */
				val safeVars = component.variables.filter[name != null && type != null]
				val safePorts = component.ports.filter[name != null && type != null]
				val safeEmbeddings = component.embeddings.filter[it.component != null && !it.component.eIsProxy]
				val safeChannels = component.safeChannels
				val safeForScopes = component.safeForScopes
				val safeTransitions = component.safeTransitions
				/* Add type parameters. */
				for (p : component.typeParams) {
					typeParameters.add(p.cloneWithProxies)
				}
				/* Add component parameters as final fields. */
				for (p : component.params) {
					members += p.toField(p.name, p.parameterType, true)
				}
				/* Add variables/constants (must be declared early, so they can be referenced by expressions). */
				for (v : safeVars) {
					members += v.toField(v.name, v.type, !v.writeable, v.initializer)
				}
				/* Add ports as fields with public getters and setters. */
				for (p : safePorts) {
					val fieldType = p.portType
					members += p.toField(p.name, fieldType)
					if (p.multiplicity != null) {
						members += p.toField(p.multiplicityFieldName, typeReferences.getTypeForName(int, p), true,
							p.multiplicity)
					}
					members += p.toGetter(p.name, fieldType)
					members += p.toSetter(p.name, fieldType)
				}
				/* Add embeddings as final fields. */
				for (e : safeEmbeddings) {
					var fieldType = typeReferences.getTypeForName(e.component.fullyQualifiedName.toString, e,
						e.typeArgs)
					if (e.multiplicity != null) {
						fieldType = e.newTypeRef(List, fieldType)
						members += e.toField(e.multiplicityFieldName, typeReferences.getTypeForName(int, e), true,
							e.multiplicity)
					}
					members += e.toField(e.name, fieldType, true)

					val c = Math::min(e.args.size, e.component.params.size)
					for (i : 0 ..< c) {
						val a = e.args.get(i)
						val p = e.component.params.get(i)
						members += a.toField(e.argumentFieldName(i), p.parameterType, true, a)
					}

				/* FIXME: add "rate" and "delay" fields in future. */
				}
				/* Add channels as list (optionally). */
				if (!safeChannels.empty) {
					members += component.toField(channelsFieldName,
						component.newTypeRef(List, component.newTypeRef(edu.tum.cs.cadmos.lib.model.Channel)))[final = true]
				}
				/* Add synthetic channel src/snk index methods. */
				for (c : safeChannels) {
					val src = c.src
					val snk = c.snk
					src.subIndex?.inferSyntheticChannelIndexMethod(members, c.syntheticFieldName + "_srcEmbedding")
					src.portIndex?.inferSyntheticChannelIndexMethod(members, c.syntheticFieldName + "_srcPort")
					snk.subIndex?.inferSyntheticChannelIndexMethod(members, c.syntheticFieldName + "_snkEmbedding")
					snk.portIndex?.inferSyntheticChannelIndexMethod(members, c.syntheticFieldName + "_snkPort")
				}
				/* Add synthetic ranges of "for scopes". */
				for (f : safeForScopes) {
					members += f.toMethod(f.rangeField, f.newTypeRef(Iterable, f.newTypeRef(Integer))) [
						f.inferParentForScopeParameters(parameters)
						visibility = JvmVisibility::PRIVATE
						body = f.range
					]
				}
				/* Add state machine transitions (boolean methods for conditions and void methods for actions). 
				 * TODO: make this recursive to handle sub-transitions in the future.
				 */
				for (t : safeTransitions) {
					if (!t.^default) { /* Condition of default transition is always "true". */
						members += t.toMethod(t.syntheticFieldName + "_condition", t.newTypeRef(boolean)) [
							visibility = JvmVisibility::PROTECTED
							body = t.condition
						]
					}
					members += t.toMethod(t.syntheticFieldName + "_action", t.newTypeRef(Void::TYPE)) [
						visibility = JvmVisibility::PROTECTED
						body = t.action
					]
				}
				/* Add constructor with component parameters that initializes ports, embeddings and channels. */
				members += component.toConstructor [
					for (p : component.params) {
						parameters += p.toParameter(p.name, p.parameterType)
					}
					val Procedure1<ITreeAppendable> proc = [
						/* Parameters */
						append('''«FOR p : component.params SEPARATOR "\n"»this.«p.name» = «p.name»;«ENDFOR»''')
						/* Ports */
						for (p : safePorts) {
							newLine

							//							append('''this.«p.name» = new «p.portClass.simpleName»<>(''')
							//							if(p.multiplicity != null) append(p.multiplicityFieldName)
							//							append(");")
							}
							newLine
							/* Embeddings */
							for (e : safeEmbeddings) {
								newLine
								if (e.multiplicity != null) {
									append(
										'''
										final java.util.List<«e.component.name»> «e.name»_list = new java.util.ArrayList<>();
										for (int i = 0; i < «e.multiplicityFieldName»; i++) {
											«e.name»_list.add(new «e.component.name»(''')
								} else {
									append('''this.«e.name» = new «e.component.name»(''')
								}
								val c = Math::min(e.args.size, e.component.params.size)
								append('''«FOR i : 0 ..< c SEPARATOR ", "»«e.argumentFieldName(i)»«ENDFOR»''')
								if (e.multiplicity != null) {
									append("));}")
									newLine
									append('''this.«e.name» = new SubComponentList(«e.name»_list);''')
								} else {
									append(");")
								}
							}
							/* Channels */
							if (!safeChannels.empty) {
								newLine
								newLine
								append("final ")
								component.serializeType(it, List, edu.tum.cs.cadmos.lib.model.Channel)
								append('''«channelsFieldName»_list = new ''')
								component.serializeType(it, ArrayList, edu.tum.cs.cadmos.lib.model.Channel)
								append("();")
							}
							for (c : safeChannels) {
								newLine
								append('''«channelsFieldName»_list.add(new Channel<>(''')
								c.src.compile(it)
								append(", ")
								c.snk.compile(it)
								append("));")
							}
							for (f : safeForScopes) {
								newLine
								f.compile(it)
							}
							if (!safeChannels.empty) {
								newLine
								append(
									'''«channelsFieldName» = java.util.Collections.unmodifiableList(«channelsFieldName»_list);''')
							}
						]
						body = proc
					]
				])
		}

		def argumentFieldName(Embedding e, Integer index) {
			"_" + e.name + "_" + e.component.params.get(index).name + "_argument"
		}

		def multiplicityFieldName(Embedding e) {
			"_" + e.name + "_multiplicity"
		}

		def portType(Port p) {
			if (p.multiplicity == null) {
				p.type
			} else {
				p.newTypeRef(List, p.type)
			}
		}

		def multiplicityFieldName(Port p) {
			"_" + p.name + "_multiplicity"
		}

		def safeChannels(Component c) {
			c.eAllContents.filter(Channel).filter[src != null && snk != null].toIterable
		}

		def safeForScopes(Component c) {
			c.eAllContents.filter(ForScope).filter[name != null && range != null].toIterable
		}

		def syntheticFieldName(Channel c) {
			"_channel_" + EcoreUtil2::getContainerOfType(c, Component).safeChannels.indexOf(c)
		}

		def channelsFieldName() {
			"_channels"
		}

		def syntheticFieldName(ForScope f) {
			"_for_" + f.parentForScopeNames.fold("", [r, t|r + t + "_"]) + f.name + "_" +
				EcoreUtil2::getContainerOfType(f, Component).safeForScopes.indexOf(f)
		}

		def rangeField(ForScope f) {
			syntheticFieldName(f) + "_range"
		}

		def safeTransitions(Component c) {
			c.transitions.filter[(^default || condition != null) && action != null]
		}

		def syntheticFieldName(Transition t) {
			"_transition_" + EcoreUtil2::getContainerOfType(t, Component).safeTransitions.indexOf(t)
		}

		def void compile(PortRef p, ITreeAppendable it) {
			val channel = p.eContainer as Channel
			val direction = if(p == channel.src) "src" else "snk"
			if (p.embedding != null) {
				append(p.embedding.name)
				if (p.subIndex != null) {
					append('''.get(«channel.syntheticFieldName»_«direction»Embedding(''')
					append(channel.parentForScopeArgs)
					append("))")
				}
				append(".")
			}
			append('''get«p.port.name.toFirstUpper»()''')
			if (p.portIndex != null) {
				append('''.get(«channel.syntheticFieldName»_«direction»Port(''')
				append(channel.parentForScopeArgs)
				append("))")
			}
		}

		def <T> indexOf(Iterable<T> iterable, T element) {
			var i = 0
			for (candidate : iterable) {
				if (candidate == element) {
					return i
				}
				i = i + 1
			}
			return -1
		}

		def void serializeType(EObject context, ITreeAppendable result, Class<?> type, Class<?>... typeArgs) {
			val typeRefArgs = newArrayOfSize(typeArgs.length)
			typeArgs.forEach[arg, index|typeRefArgs.set(index, context.newTypeRef(arg))]
			context.newTypeRef(type, typeRefArgs).serialize(context, result)
		}

		def List<ForScope> parentForScopes(EObject obj) {
			if (obj.eContainer instanceof ForScope) {
				val result = parentForScopes(obj.eContainer)
				result.add(obj.eContainer as ForScope)
				return result
			}
			if (!(obj.eContainer == null || obj.eContainer instanceof Component)) {

				// Continue bottom-up recursion if neither top-level model nor component has been traversed yet. 
				return parentForScopes(obj.eContainer)
			}
			return newLinkedList()
		}

		def parentForScopeNames(EObject obj) {
			parentForScopes(obj).map[name]
		}

		def void compile(ForScope f, ITreeAppendable it) {
			append('''for (Integer «f.name» : «f.rangeField»(«f.parentForScopeArgs»)) {''')
			increaseIndentation
			newLine
			for (s : f.subScopes) {
				s.compile(it)
			}
			for (c : f.channels) {
				if(c != f.channels.head) newLine
				append('''«channelsFieldName»_list.add(new Channel<>(''')
				c.src.compile(it)
				append(", ")
				c.snk.compile(it)
				append("));")
			}
			decreaseIndentation
			newLine
			append("}");
		}

		def void inferParentForScopeParameters(EObject ctx, EList<JvmFormalParameter> parameters) {
			for (f : ctx.parentForScopeNames) {
				parameters += ctx.toParameter(f, ctx.newTypeRef(Integer))
			}
		}

		def parentForScopeArgs(EObject ctx) '''«FOR f : ctx.parentForScopeNames SEPARATOR ", "»«f»«ENDFOR»'''

		def void inferSyntheticChannelIndexMethod(XExpression expr, EList<JvmMember> members, String name) {
			members += expr.toMethod(name, expr.newTypeRef(int)) [
				expr.inferParentForScopeParameters(parameters)
				visibility = JvmVisibility::PRIVATE
				body = expr
			]
		}

	}
	