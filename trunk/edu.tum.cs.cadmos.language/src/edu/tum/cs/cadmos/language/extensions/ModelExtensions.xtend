package edu.tum.cs.cadmos.language.extensions

import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.ComponentProperty
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.Mapping
import edu.tum.cs.cadmos.language.cadmos.Model
import edu.tum.cs.cadmos.language.cadmos.Port

class ModelExtensions {

	def components(Model m) {
		m.elements.filter(Component)
	}

	def ports(Component c) {
		c.features.filter(Port)
	}

	def embeddings(Component c) {
		c.features.filter(Embedding)
	}

	def channels(Component c) {
		c.features.filter(Channel)
	}

	def componentProperties(Component c) {
		c.features.filter(ComponentProperty)
	}

	def isComponentMapping(Mapping m) {
		m.port == null
	}

	def isPortMapping(Mapping m) {
		!isComponentMapping(m)
	}

}
