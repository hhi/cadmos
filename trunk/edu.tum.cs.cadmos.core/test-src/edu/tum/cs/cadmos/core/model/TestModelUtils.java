package edu.tum.cs.cadmos.core.model;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.IListSet;

public class TestModelUtils {

	@Test
	public void test_dissolveComponent_CompositeComponent() {
		final ICompositeComponent p = new CompositeComponent("p", null);
		final IComponent c1 = new AtomicComponent("c1", p);
		final IComponent c2 = new AtomicComponent("c2", p);

		final IChannel x_p = new Channel("x", null, p, 1);
		final IChannel y_p = new Channel("y", p, null, 1);

		final IChannel x_c1 = new Channel("x", null, c1, 1);
		final IChannel x_c1_c2 = new Channel("x", c1, c2, 1);
		final IChannel y_c2 = new Channel("y", c2, null, 1);

		final IListSet<IComponent> network = ModelUtils.dissolveComponent(p);
		assertEquals(2, network.size());
		assertTrue(network.contains(c1));
		assertTrue(network.contains(c2));

		for (final IComponent component : network) {
			assertNotSame("Expected c1 to be a copy", c1, component);
			assertNotSame("Expected c2 to be a copy", c2, component);
		}

		final IComponent new_c1 = network.get("c1");
		final IComponent new_c2 = network.get("c2");
		assertNotNull(new_c1);
		assertNotNull(new_c2);
		assertEquals(1, new_c1.getIncoming().size());
		assertEquals(1, new_c1.getOutgoing().size());
		assertEquals(1, new_c2.getIncoming().size());
		assertEquals(1, new_c2.getOutgoing().size());

		assertNotSame(x_p, new_c1.getIncoming().getFirst());
		assertNotSame(x_c1, new_c1.getIncoming().getFirst());
		assertNotSame(x_c1_c2, new_c1.getOutgoing().getFirst());
		assertNotSame(x_c1_c2, new_c2.getIncoming().getFirst());
		assertNotSame(y_c2, new_c2.getOutgoing().getFirst());
		assertNotSame(y_p, new_c2.getOutgoing().getFirst());
	}

}
