package edu.tum.cs.cadmos.analysis.architecture.model.utils

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
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.analysis.architecture.model.Edge

class DFGTranslator {

	extension ModelExtensions = new ModelExtensions

	val Component root
	val g = new DirectedSparseMultigraph<Vertex, Edge>
	val Map<String, Vertex> visited = new HashMap<String, Vertex>
	val Map<String, Integer> edgeMultiplicity = new HashMap<String, Integer>

	new(Component c) {
		this.root = c
	}
	
	def getSinkName(Channel ch){
		if (ch.snk.embedding != null) {
			ch.snk.embedding.name	
		} else {
			ch.snk.port.name
		}
	}
	
	def getSourceName(Channel ch){
		if (ch.src.embedding != null) {
			ch.src.embedding.name	
		} else {
			ch.src.port.name
		}
	}

	def translateFlatGraphToDFG() {
		val channels = this.root.eContents.filter(Channel)
		val embeddings = this.root.eContents.filter(Embedding)
		val environment = this.root.eContents.filter(Port)
		visited.clear
		
		// add environment input/outputs
		environment.forEach[
			val vertex = new Vertex(it.name, it)
			visited.put(it.name, vertex)
			g.addVertex(vertex)
		]
		
		// add embeddings
		embeddings.forEach[
			val vertex = new Vertex(it.name, it)
			visited.put(it.name, vertex)
			g.addVertex(vertex)
		]
		
		//add channels
		channels.forEach[
			val srcVertex = visited.get(it.sourceName)
			val snkVertex = visited.get(it.sinkName)
			if(srcVertex != null && snkVertex != null) {
				val edge = new Edge("(" + srcVertex.id + ", " + snkVertex.id + ")", 0)
				g.addEdge(edge, srcVertex, snkVertex)
			}
		]
		g
	}

	def translateToDFG() {
		if(root.embeddings.empty){
			return g
		}
		
		root.findPathsToAtomicSinksInternally.forEach [
			//create source vertex
			val sourceObject = it.pathSource  // should be Port
			val sourceVertex = createVertex(sourceObject, (sourceObject as Port).name)
			
			//create sink vertex
			val sinkObject = it.pathSink	// should be Embedding -> name already covered by path
			var sinkID = it.ID
			val sinkVertex = createVertex(sinkObject, sinkID)
			
			//connect
			val candidateId = "(" + sourceVertex.id + "," + sinkVertex.id + ")"
			var index = candidateId.incrementEdgeMultiplicity
			val edge = new Edge(candidateId + "[" + index + "]", index)
			g.addEdge(edge, sourceVertex, sinkVertex)
		]
		
		
		root.findAtomicEmbeddings.forEach[
			val path = it
			findPathsToAtomicSinksTrailing(path).forEach[
				
				//create source vertex
				val sourceObject = it.pathSource	//should be Embedding -> name already covered by path
				val sourceID = getID(path, new ArrayList<Channel>)
				val sourceVertex = createVertex(sourceObject, sourceID)
				
				//create sink vertex
				val sinkObject = it.pathSink
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
		for(Channel ch : path){
			traverse(context, ch)
		}
		id.addAll(context)
		return '''«FOR e : id SEPARATOR "."»«e.name»«ENDFOR»'''.toString
	}
	
	def traverse(List<Embedding> path, Channel ch) {
		
		//actual work
		if(ch.connectsToChild){
			path.add(ch.snk.embedding)
		} else if (ch.connectsToParent) {
			path.remove(path.last)
		} else if(ch.connectsToSibling){
			path.remove(path.last)
			path.add(ch.snk.embedding)
		}
		return path
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

	def ArrayList<List<Embedding>> findAtomicEmbeddings(Component c) {
		findAtomicEmbeddings(c, new ArrayList<Embedding>)
	}
	
	def ArrayList<List<Embedding>> findAtomicEmbeddings(Component c, List<Embedding> path) {
		val list = new ArrayList<List<Embedding>>
		if (c.atomic) {
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
	def findPathsToAtomicSinksTrailing(List<Embedding> path) {
		val sinks = new ArrayList<List<Channel>>
		path.last.component.ports.filter[!inbound].forEach [
			sinks.addAll(it.findAtomicSinks2(path, new ArrayList<Channel>))
		]
		return sinks
	}

	def List<List<Channel>> findAtomicSinks(Port p, List<Channel> path) {
		val list = new ArrayList<List<Channel>>
		if (p.inbound && p.component.atomic) {
			list.add(path)
			return list
		}
		p.trailingChannels.forEach [
			val pathFurther = path.duplicateCh
			pathFurther.add(it)
			list.addAll(it.snk.port.findAtomicSinks(pathFurther))
		]
		return list
	}
	def List<List<Channel>> findAtomicSinks2(Port p, List<Embedding> context, List<Channel> path) {
		val list = new ArrayList<List<Channel>>
		if ((p.inbound && p.component.atomic )|| context.empty) {
			list.add(path)
			return list
		}
		val t = getTrailingChannels2(p,context.last)
		t.forEach[
			val pathFurther = path.duplicateCh
			pathFurther.add(it)
			val context2 = context.duplicate
			if(it.connectsToParent){
				context2.remove(context2.last)
			} else if(it.connectsToChild){
				context2.add(it.snk.embedding)
			} else if (it.connectsToSibling){
				context2.remove(context2.last)
				context2.add(it.snk.embedding)
			}
			list.addAll(it.snk.port.findAtomicSinks2(context2,pathFurther))
		]
		return list
	}
	
	//works upwards through embeddings
	def getTrailingChannels2(Port p, Embedding context){
		var up =(context.eContainer as Component).channels.filter[it.src.port == p].filter[it.src.embedding == context]
		var down = context.component.channels.filter[it.src.port == p].filter[it.src.embedding == null]
		return up + down
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
	
	def printListC(List<Channel> list){
		val StringBuffer s = new StringBuffer
		
		list.forEach[
			s.append("."+it.sourceName+"->"+it.sinkName)
		]
		println(s)
	}
	
	def printListE(List<Embedding> list){
		val StringBuffer s = new StringBuffer
		
		list.forEach[
			s.append("."+it.name)
		]
		println(s)
	}
}
