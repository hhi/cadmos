package edu.tum.cs.cadmos.analysis.schedule

import java.util.HashMap
import org.eclipse.emf.ecore.EObject
import java.util.HashSet
import java.util.ArrayList

class AssertionNameMapping {
	public static var SINGLETON = new AssertionNameMapping
	
	public static val listeners = new ArrayList<IUnsatCoreListener>()
	
	val map = new HashMap<String, EObject>
	val satSet = new HashSet<String>
	val unsatSet = new HashSet<String>
	val relaxSet = new HashSet<String>();
	
	def put (String s, EObject e){
		map.put(s,e)
	}
	
	
	def addUnsat(String s){
		unsatSet.add(s)
	}
	
	def isUnsat(String s){
		SINGLETON.unsatSet.contains(s)
 	}
	def isSat(String s){
		SINGLETON.satSet.contains(s)
 	}
 	
 	def addRelax(String ass){
 		relaxSet.add(ass)
 		satSet.remove(ass)
 		unsatSet.remove(ass)
 		
 		notifyAllListeners
 	}
 	def removeRelax(String ass){
 		relaxSet.remove(ass)
 		
 		notifyAllListeners
 	}
 	def isRelax(String ass){
 		relaxSet.contains(ass)
 	}
 	
	def clear(){
		map.clear
		unsatSet.clear
		satSet.clear
//		relaxSet.clear
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
		satSet.addAll(map.keySet.filter[!unsatSet.contains(it)&&!relaxSet.contains(it)])
		notifyAllListeners
	}
}
