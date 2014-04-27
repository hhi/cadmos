package edu.tum.cs.cadmos.analysis.schedule;

public class AssertionPrefixes {
	
	public static final String multirate_alloc_consistency ="multirate_alloc_consistency";
	public static final String multirate_const_period ="multirate_const_period";
	public static final String iteration_period ="iteration_period";
	public static final String transmission_duration_constraints ="transmission_duration_constraints";
	public static final String transmission_latency ="transmission_latency";
	public static final String precedence ="precedence";
	public static final String latency_requirement ="latency_requirement";
	public static final String robustness_requirement ="robustness_requirement";
	public static final String finish ="finish";
	public static final String duration ="duration";
	
	private static final String[] all = new String[]{
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
	
	public static String[] getAll(){
		return all;
	}

}
