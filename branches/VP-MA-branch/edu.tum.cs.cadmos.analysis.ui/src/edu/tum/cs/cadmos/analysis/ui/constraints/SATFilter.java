package edu.tum.cs.cadmos.analysis.ui.constraints;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping;

public class SATFilter extends ViewerFilter {

	public static final SATFilter ANY = new SATFilter(ESAT.ANY);
	public static final SATFilter SAT = new SATFilter(ESAT.SAT);
	public static final SATFilter UNSAT = new SATFilter(ESAT.UNSAT);
	public static final SATFilter RELAX = new SATFilter(ESAT.RELAX);

	private final ESAT type;

	private SATFilter(ESAT type) {
		this.type = type;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (type == ESAT.ANY) {
			return true;
		}
		if (type == ESAT.SAT) {
			String s = (String) element;
			if (AssertionNameMapping.SINGLETON.isSat(s)) {
				return true;
			}
		}
		if (type == ESAT.UNSAT) {
			String s = (String) element;
			if (AssertionNameMapping.SINGLETON.isUnsat(s)) {
				return true;
			}
		}
		if (type == ESAT.RELAX) {
			String s = (String) element;
			if (AssertionNameMapping.SINGLETON.isRelax(s)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString()+ "type: "+ type.name();
	}

	private enum ESAT {
		ANY, SAT, UNSAT, RELAX;
	}

}
