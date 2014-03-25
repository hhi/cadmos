package edu.tum.cs.cadmos.analysis.schedule

import java.util.HashMap
import org.eclipse.emf.ecore.EObject
import java.util.HashSet
import java.util.ArrayList

class AssertionNameMapping {
	public static var SINGLETON = new AssertionNameMapping
	
	public static val listeners = new ArrayList<IUnsatCoreListener>()
	
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
	
	def public static registerListener(IUnsatCoreListener listener){
		listeners.add(listener)
	}
	def public static unregisterListener(IUnsatCoreListener listener){
		listeners.remove(listener)
	}
	def static notifyAllListeners(){
		for(l : listeners){
			l.notifyUnsatCoreChange
		}
	}
	def public coreFinished(){
		notifyAllListeners
	}
}
