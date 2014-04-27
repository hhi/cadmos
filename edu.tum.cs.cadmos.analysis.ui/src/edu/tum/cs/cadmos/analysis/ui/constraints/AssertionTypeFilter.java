package edu.tum.cs.cadmos.analysis.ui.constraints;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class AssertionTypeFilter extends ViewerFilter{
	
	public static final AssertionTypeFilter multirate_alloc_consistency = new AssertionTypeFilter(EAssertionPrefixType.multirate_alloc_consistency);
	public static final AssertionTypeFilter multirate_const_period = new AssertionTypeFilter(EAssertionPrefixType.multirate_const_period);
	public static final AssertionTypeFilter iteration_period = new AssertionTypeFilter(EAssertionPrefixType.iteration_period);
	public static final AssertionTypeFilter transmission_duration_constraints = new AssertionTypeFilter(EAssertionPrefixType.transmission_duration_constraints);
	public static final AssertionTypeFilter transmission_latency = new AssertionTypeFilter(EAssertionPrefixType.transmission_latency);
	public static final AssertionTypeFilter precedence = new AssertionTypeFilter(EAssertionPrefixType.precedence);
	public static final AssertionTypeFilter latency_requirement = new AssertionTypeFilter(EAssertionPrefixType.latency_requirement);
	public static final AssertionTypeFilter robustness_requirement = new AssertionTypeFilter(EAssertionPrefixType.robustness_requirement);
	public static final AssertionTypeFilter finish = new AssertionTypeFilter(EAssertionPrefixType.finish);
	public static final AssertionTypeFilter duration = new AssertionTypeFilter(EAssertionPrefixType.duration);
	
	private static final AssertionTypeFilter[] all = new AssertionTypeFilter[]{
		precedence,
		duration,
		finish,
		iteration_period,
		multirate_alloc_consistency,
		multirate_const_period,
		transmission_duration_constraints,
		transmission_latency,
		latency_requirement,
		robustness_requirement
		};
	
	public static AssertionTypeFilter[] getAll(){
		return all;
	}
	
	private final String type;
	
	private AssertionTypeFilter(EAssertionPrefixType type){
		this.type = type.name();
	}
	

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		String ass = (String) element;
		if(!ass.startsWith(type)){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return type;
	}
	
	public enum EAssertionPrefixType {
		precedence,
		duration,
		finish,
		iteration_period,
		multirate_alloc_consistency,
		multirate_const_period,
		transmission_duration_constraints,
		transmission_latency,
		latency_requirement,
		robustness_requirement
	}

}
