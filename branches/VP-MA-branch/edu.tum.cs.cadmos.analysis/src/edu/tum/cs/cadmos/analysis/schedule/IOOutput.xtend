package edu.tum.cs.cadmos.analysis.schedule

import java.util.ArrayList

class IOOutput {
	public static val listeners = new ArrayList<IOOutputListener>()
	public static val s = new StringBuilder
	
	
	def public static registerListener(IOOutputListener listener){
		listeners.add(listener)
	}
	def public static unregisterListener(IOOutputListener listener){
		listeners.remove(listener)
	}
	def static notifyAllListeners(){
		for(l : listeners){
			l.notifyIOOutputChange
		}
	}
	
	def public static getIOContents(){
		return s.toString
	}
	
	def public static print(String msg){
		s.append(msg+"\n")
		notifyAllListeners
	}
	
	
}