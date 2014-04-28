package edu.tum.cs.cadmos.analysis.ui.constraints;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SubsystemFilter extends ViewerFilter{

	private final String subsystem;
	
	public SubsystemFilter(String sub){
		subsystem = sub;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		String ass = (String) element;
		if(ass.contains(subsystem)){
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SubsystemFilter) {
			SubsystemFilter other = (SubsystemFilter) obj;
			return subsystem.equals(other.subsystem);
		}
		return super.equals(obj);
	}

}
