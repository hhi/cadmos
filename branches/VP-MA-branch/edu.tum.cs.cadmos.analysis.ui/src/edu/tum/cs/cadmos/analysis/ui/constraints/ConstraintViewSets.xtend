package edu.tum.cs.cadmos.analysis.ui.constraints

import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping
import edu.tum.cs.cadmos.analysis.schedule.IOOutput
import edu.tum.cs.cadmos.analysis.ui.perspective.ComplementConstraintView
import edu.tum.cs.cadmos.analysis.ui.perspective.ConstraintView
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.ui.IViewPart
import org.eclipse.ui.IWorkbenchPage
import org.eclipse.ui.PlatformUI
import edu.tum.cs.cadmos.analysis.schedule.IUnsatCoreListener

class ConstraintViewSets implements IUnsatCoreListener{
	public static val SINGLETON = new ConstraintViewSets
	
	public new(){
		AssertionNameMapping.registerListener(this)
	}
	var ComplementConstraintView complementView = null
	val views = new HashMap<ConstraintView, ArrayList<ViewerFilter>>()
	val contents = new HashMap<ConstraintView, HashSet<String>>()
	var HashSet<String> complementContents

	def registerView(ConstraintView cv) {
		views.put(cv, getDefaultFilters)
		contents.put(cv, new HashSet<String>)
		triggerUpdate
	}
	
	def registerComplementView(ComplementConstraintView cv){
		complementView = cv
	}
	
	
	
	def ArrayList<ViewerFilter> getDefaultFilters() {
		var defaultList = new ArrayList<ViewerFilter>()
//		defaultList.add(SATFilter.ANY)
		defaultList
	}

	def unregisterView(ConstraintView cv) {
		views.remove(cv)
		contents.remove(cv)
		triggerUpdate
	}

	def triggerUpdate() {
		applyFilters
		calculateComplement
		notifyViews
	}
	
	def getContents(ConstraintView cv){
		contents.get(cv)
	}
	
	def getComplementContents(){
		complementContents
	}
	
	def notifyViews() {
		for(cv : views.keySet){
			cv.notifyUnsatCoreChange
		}
		if(complementView != null)
			complementView.notifyUnsatCoreChange
	}
	
	def calculateComplement() {
		var all = new HashSet<String>
		all.addAll(AssertionNameMapping.contents)
		
		for(c : contents.values){
			all.removeAll(c)
		}
		
		complementContents = all
	}
	
	def applyFilters() {
		for(ConstraintView cv : views.keySet){
			var filtered = new HashSet<String>
			filtered.addAll(AssertionNameMapping.contents)
			
			for(ViewerFilter filter : views.get(cv)){
				var toRemove = new HashSet<String>
				for (String s : filtered) {
					if (!filter.select(null, null, s)) {
						toRemove.add(s)
					}
				}
				filtered.removeAll(toRemove)
			}
			contents.put(cv, filtered)
		}
		
	}
	

	def adjustFilter(ConstraintView cv, ViewerFilter filter, boolean remove) {
		val list = views.get(cv)

		if (remove) {
			list.remove(filter)
		} else {
			list.add(filter)
		}
		
		triggerUpdate
	}

	def checkSanity(ConstraintView cv, ViewerFilter filter, boolean remove) {

		//sanity checks
		if (!views.containsKey(cv)) {
			IOOutput.print("error: unregistered view! " + cv);
			return false
		}
		if (remove && !views.get(cv).contains(filter)) {
			IOOutput.print("error: unregistered filter! " + filter);
			return false
		}
		true
	}

	def private ComplementConstraintView getComplementView() {
		val IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		val IViewPart view = page.findView(ComplementConstraintView.ID);
		view as ComplementConstraintView
	}
	
	override notifyUnsatCoreChange() {
		triggerUpdate
	}
	
}
