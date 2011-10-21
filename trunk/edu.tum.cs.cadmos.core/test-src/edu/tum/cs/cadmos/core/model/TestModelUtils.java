package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.model.ModelUtils.find;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class TestModelUtils {

	@Test
	public void test_dissolveComponent_AtomicComponent() {
		final IComponent c = new AtomicComponent("c", null);
		final Set<IComponent> network = ModelUtils.dissolveComponent(c);
		assertEquals(1, network.size());
		assertTrue(network.contains(c));
		assertNotSame(c, network.iterator().next());
	}

	@Test
	public void test_dissolveComponent_CompositeComponent_With_AtomicComponents() {
		final IComponent p = new CompositeComponent("p", null);
		final IComponent c1 = new AtomicComponent("c1", null);
		final IComponent c2 = new AtomicComponent("c2", null);

		final IChannel x_p = new Channel("x", null, p, 1);
		final IChannel y_p = new Channel("y", p, null, 1);

		final IChannel x_c1 = new Channel("x", null, c1, 1);
		final IChannel x_c1_c2 = new Channel("x", c1, c2, 1);
		final IChannel y_c2 = new Channel("y", c2, null, 1);

		final Set<IComponent> network = ModelUtils.dissolveComponent(p);
		assertEquals(2, network.size());
		assertTrue(network.contains(c1));
		assertTrue(network.contains(c2));

		for (final IComponent component : network) {
			assertNotSame("Expected c1 to be a copy, but was same object", c1,
					component);
			assertNotSame("Expected c1 to be a copy, but was same object", c2,
					component);
		}

		final IComponent new_c1 = find("c1", network);
		final IComponent new_c2 = find("c2", network);
		assertNotNull(new_c1);
		assertNotNull(new_c2);
		assertEquals(1, new_c1.getIncoming());
		assertEquals(1, new_c1.getOutgoing());
		assertEquals(1, new_c2.getIncoming());
		assertEquals(1, new_c2.getOutgoing());

		assertNotSame(x_p, new_c1.getIncoming().iterator().next());
		assertNotSame(x_c1, new_c1.getIncoming().iterator().next());
		assertNotSame(x_c1_c2, new_c1.getOutgoing().iterator().next());
		assertNotSame(x_c1_c2, new_c2.getIncoming().iterator().next());
		assertNotSame(y_c2, new_c2.getOutgoing().iterator().next());
		assertNotSame(y_p, new_c2.getOutgoing().iterator().next());
	}

}
