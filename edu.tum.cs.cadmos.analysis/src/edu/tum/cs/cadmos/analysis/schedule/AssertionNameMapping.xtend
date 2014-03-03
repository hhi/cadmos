package edu.tum.cs.cadmos.analysis.schedule

import java.util.HashMap
import org.eclipse.emf.ecore.EObject

class AssertionNameMapping {
	val map = new HashMap<String, EObject>
	
	def put (String s, EObject e){
		map.put(s,e)
	}
}