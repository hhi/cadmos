package edu.tum.cs.cadmos.analysis.schedule

import java.util.HashMap
import org.eclipse.emf.ecore.EObject
import java.util.HashSet

class AssertionNameMapping {
	public static var SINGLETON = new AssertionNameMapping
	
	
	val map = new HashMap<String, EObject>
	
	def put (String s, EObject e){
		map.put(s,e)
	}
	
	val unsatSet = new HashSet<String>
	
	def addUnsat(String s){
		unsatSet.add(s)
	}
	
	def clear(){
		map.clear
		unsatSet.clear
	}
	
	def isUnsat(String s){
		SINGLETON.unsatSet.contains(s)
	}
	
	def static getContents(){
		SINGLETON.map.keySet
	}
	
	def static print(){
		for(String s : SINGLETON.map.keySet){
			var line = "["
			if(SINGLETON.unsatSet.contains(s)){
				line =line + "UNSAT] "
			} else {
				line =line + "  SAT] "
			}
			println(line + s)
		}
	}
}
