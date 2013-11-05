package edu.tum.cs.cadmos.language.utils

import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.ForScope
import edu.tum.cs.cadmos.language.cadmos.Transition
import edu.tum.cs.cadmos.language.cadmos.Variable
import edu.tum.cs.cadmos.language.cadmos.Embedding

class ComponentUtils {

	def ports(Component c) {
		c.features.filter(Port)
	}

	def embeddings(Component c) {
		c.features.filter(Embedding)
	}

	def channels(Component c) {
		c.features.filter(Channel)
	}

	def forScopes(Component c) {
		c.features.filter(ForScope)
	}

	def transitions(Component c) {
		c.features.filter(Transition)
	}

	def variables(Component c) {
		c.features.filter(Variable)
	}

}
