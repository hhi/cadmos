package edu.tum.cs.cadmos.analysis.architecture.model

import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.tum.cs.cadmos.language.extensions.ModelExtensions
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import org.eclipse.emf.ecore.EObject

class DFGTranslator {

	extension ModelExtensions = new ModelExtensions

	val Component root
	val g = new DirectedSparseMultigraph<Vertex, Edge>
	val Map<String, Vertex> visited = new HashMap<String, Vertex>
	val Map<String, Integer> edgeMultiplicity = new HashMap<String, Integer>

	new(Component c) {
		this.root = c
	}

	def translateToDFG() {

		root.findPathsToAtomicSinksInternally.forEach [
			//create source vertex
			val sourceObject = it.getPathSource  // should be Port
			val sourceVertex = createVertex(sourceObject, (sourceObject as Port).name)
			
			//create sink vertex
			val sinkObject = it.getPathSink	// should be Embedding -> name already covered by path
			var sinkID = it.getID
			val sinkVertex = createVertex(sinkObject, sinkID)
			
			//connect
			val candidateId = "(" + sourceVertex.id + "," + sinkVertex.id + ")"
			var index = candidateId.incrementEdgeMultiplicity
			val edge = new Edge(candidateId + "[" + index + "]", index)
			g.addEdge(edge, sourceVertex, sinkVertex)
		]
		

		root.findAtomicEmbeddings().forEach[
			val path = it
			val c = path.last.component
			c.findPathsToAtomicSinksTrailing.forEach[
				//create source vertex
				val sourceObject = it.getPathSource	//should be Embedding -> name already covered by path
				val sourceID = getID(path, it)
				val sourceVertex = createVertex(sourceObject, sourceID)
				
				//create sink vertex
				val sinkObject = it.getPathSink
				var sinkID = ""
				if(sinkObject instanceof Port){
					//context is root - path should be empty
					sinkID = sinkObject.name
				} else {
					//case when sink is Embedding somewhere
					sinkID = getID(path, it) 
				}
				val sinkVertex = createVertex(sinkObject, sinkID)
				
				//connect
				val candidateId = "(" + sourceVertex.id + "," + sinkVertex.id + ")"
				var index = candidateId.incrementEdgeMultiplicity
				val edge = new Edge(candidateId + "[" + index + "]", index)
				g.addEdge(edge, sourceVertex, sinkVertex)
			]
			
		]

		return g
	}

	def String getID(List<Channel> path) {
		getID(new ArrayList<Embedding>, path)
	}
	def String getID(List<Embedding> context, List<Channel> path) {
		val id = new ArrayList<Embedding>
		id.addAll(context)
		for(Channel ch : path){
			traverse(context, ch)
		}
		return '''«FOR e : id SEPARATOR "."»«e.name»«ENDFOR»'''.toString
	}
	
	def traverse(List<Embedding> path, Channel ch) {
		//TODO remove sanity checks
		if(path.empty && ch.src.embedding != null){
				//should never happen once algorithm works
				throw new AssertionError("inconsistent path")
		}
		if(path.last != ch.src.embedding){
				//should never happen once algorithm works
				throw new AssertionError("inconsistent path")
		}
		
		//actual work
		if(ch.connectsToChild){
			path.add(ch.snk.embedding)
		} else if (ch.connectsToParent) {
			path.remove(path.last)
		} else if(ch.connectsToSibling){
			path.remove(path.last)
			path.add(ch.snk.embedding)
		}
	}
	
	def getId(List<Embedding> embeddingPath) {
		'''«FOR e : embeddingPath SEPARATOR "."»«e.name»«ENDFOR»'''.toString
	}
	
	def EObject getPathSource(List<Channel> path) {
		val ch = path.head
		if (ch.src.embedding == null){ //&& ch.src.port.eContainer == root){
			return ch.src.port
		}
		return ch.src.embedding
	}

	def EObject getPathSink(List<Channel> path) {
		val ch = path.last
		if (ch.snk.embedding == null){ //&& ch.snk.port.eContainer == root){
			return ch.snk.port
		}
		return ch.snk.embedding
	}
	
	def List<List<Embedding>> atomicEmbeddings(Component c) {
		return findAtomicEmbeddings(c, new ArrayList<Embedding>())
	}

	def List<List<Embedding>> findAtomicEmbeddings(Component c) {
		findAtomicEmbeddings(c, new ArrayList<Embedding>)
	}
	
	def List<List<Embedding>> findAtomicEmbeddings(Component c, List<Embedding> path) {
		val list = new ArrayList<List<Embedding>>
		if (c.isAtomic) {
			list.add(path)
			return list
		}

		//not atomic
		c.embeddings.forEach [
			var pathDown = path.duplicate
			pathDown.add(it)
			list.addAll(findAtomicEmbeddings(it.component, pathDown))
		]
		return list
	}

	// only internally!
	def findPathsToAtomicSinksInternally(Component c) {
		val sinks = new ArrayList<List<Channel>>
		c.ports.filter[inbound].forEach [
			sinks.addAll(it.findAtomicSinks(new ArrayList<Channel>))
		]
		return sinks
	}
	
	// use starting from atomic components
	def findPathsToAtomicSinksTrailing(Component c) {
		val sinks = new ArrayList<List<Channel>>
		c.ports.filter[!inbound].forEach [
			sinks.addAll(it.findAtomicSinks(new ArrayList<Channel>))
		]
		return sinks
	}

	def List<List<Channel>> findAtomicSinks(Port p, List<Channel> path) {
		val list = new ArrayList<List<Channel>>
		if (p.inbound && p.getComponent.isAtomic) {
			list.add(path)
			return list
		}
		p.getTrailingChannels.forEach [
			val pathFurther = path.duplicateCh
			pathFurther.add(it)
			it.snk.port.findAtomicSinks(pathFurther)
		]
		return list
	}
	
	def createVertex(EObject data, String id) {
		var vertex = visited.get(id)
		if (vertex == null) {
			vertex = new Vertex(id, data)
			g.addVertex(vertex)
			visited.put(id, vertex)
		}
		return vertex
	}

	def getName(EObject object) {
		object.eGet(object.eClass.getEStructuralFeature("name")) as String
	}

	def incrementEdgeMultiplicity(String id) {
		var index = edgeMultiplicity.get(id)
		if (index == null) {
			index = 0
		} else {
			index = index + 1
		}
		edgeMultiplicity.put(id, index)
		return index
	}

	def List<Embedding> duplicate(List<Embedding> list) {
		val l2 = new ArrayList<Embedding>
		l2.addAll(list)
		return l2
	}

	def List<Channel> duplicateCh(List<Channel> list) {
		val l2 = new ArrayList<Channel>
		l2.addAll(list)
		return l2
	}

	def newVertex(DirectedSparseMultigraph<Vertex, Edge> g, String id, EObject data) {
		val v = new Vertex(id, data)
		g.addVertex(v)
		return v
	}
}
