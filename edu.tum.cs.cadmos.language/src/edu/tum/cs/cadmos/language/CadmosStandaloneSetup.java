/*
* generated by Xtext
*/
package edu.tum.cs.cadmos.language;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class CadmosStandaloneSetup extends CadmosStandaloneSetupGenerated{

	public static void doSetup() {
		new CadmosStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

