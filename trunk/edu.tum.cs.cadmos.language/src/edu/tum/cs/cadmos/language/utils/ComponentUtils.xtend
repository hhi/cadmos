package edu.tum.cs.cadmos.language.utils

import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.Port

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


}
