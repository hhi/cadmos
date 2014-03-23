package edu.tum.cs.cadmos.analysis.schedule;

public class UnsatCorePreferences {

	//Activate certain sections of the SMT script
	public static boolean GENERATE__PERIOD_TIMES = true;
	public static boolean GENERATE__DURATION_TIMES = true;
	public static boolean GENERATE__UPPER_LOWER_LIMIT = true;
	public static boolean GENERATE__MULTIRATE_CONSTANT_PERIOD = true;
	public static boolean GENERATE__MULTIRATE_ALLOCATION_CONSISTENCY = true;
	public static boolean GENERATE__PRECEDENCE_CONSTRAINTS = true;
	public static boolean GENERATE__TRANSMISSION_LATENCY_COSTS = true;
	public static boolean GENERATE__TRANSMISSION_DURATION_CONSTRAINTS = true;
	public static boolean GENERATE__ATOMIC_ON_SAME_CORE_ASSUMPTION = true;
	public static boolean GENERATE__ROBUSTNESS_REQUIREMENTS = true;
	public static boolean GENERATE__LATENCY_REQUIREMENTS = true;
	public static boolean GENERATE__OVERLAP_CONSTRAINTS = true;

	//Activate UNSAT core generation for certain sections of the SMT script
	public static boolean CORE__PERIOD_TIMES = true;
	public static boolean CORE__DURATION_TIMES = false;  //FIXME
	public static boolean CORE__UPPER_LOWER_LIMIT = true;
	public static boolean CORE__MULTIRATE_CONSTANT_PERIOD = true;
	public static boolean CORE__MULTIRATE_ALLOCATION_CONSISTENCY = true;
	public static boolean CORE__PRECEDENCE_CONSTRAINTS = true;
	public static boolean CORE__TRANSMISSION_LATENCY_COSTS = true;
	public static boolean CORE__TRANSMISSION_DURATION_CONSTRAINTS = true;
	public static boolean CORE__ATOMIC_ON_SAME_CORE_ASSUMPTION = true;
	public static boolean CORE__ROBUSTNESS_REQUIREMENTS = true;
	public static boolean CORE__LATENCY_REQUIREMENTS = true;
	public static boolean CORE__OVERLAP_CONSTRAINTS = true;
	

}
