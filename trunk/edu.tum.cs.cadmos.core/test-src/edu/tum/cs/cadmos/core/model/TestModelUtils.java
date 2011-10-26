package edu.tum.cs.cadmos.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Deque;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.core.IListSet;

public class TestModelUtils {

	@Test
	public void test_getAtomicComponents() {
		final ICompositeComponent root = new CompositeComponent("root", null);
		final ICompositeComponent comp1 = new CompositeComponent("comp1", root);
		final ICompositeComponent comp2 = new CompositeComponent("comp2", root);
		final IAtomicComponent a1 = new AtomicComponent("a1", comp1);
		final IAtomicComponent a2 = new AtomicComponent("a2", comp2);
		final IAtomicComponent a3 = new AtomicComponent("a3", comp2);
		final IListSet<IAtomicComponent> components = ModelUtils
				.getAtomicComponents(root);
		assertEquals(3, components.size());
		assertTrue(components.contains(a1));
		assertTrue(components.contains(a2));
		assertTrue(components.contains(a3));
	}

	@Test
	public void test_getOutboundChildChannel() {
		final ICompositeComponent parent = new CompositeComponent("parent",
				null);
		final IAtomicComponent c1 = new AtomicComponent("c1", parent);
		final IAtomicComponent c2 = new AtomicComponent("c2", parent);
		@SuppressWarnings("unused")
		final IChannel x_internal = new Channel("x", c1, c2, 0);
		final IChannel x_outbound = new Channel("x", c2, null, 0);
		final IChannel result = ModelUtils.getOutboundChildChannel(parent,
				x_outbound);
		assertSame(x_outbound, result);
	}

	@Test
	public void test_getInboundChildChannel() {
		final ICompositeComponent parent = new CompositeComponent("parent",
				null);
		final IAtomicComponent c1 = new AtomicComponent("c1", parent);
		final IAtomicComponent c2 = new AtomicComponent("c2", parent);
		@SuppressWarnings("unused")
		final IChannel x_internal = new Channel("x", c1, c2, 0);
		final IChannel x_inbound = new Channel("x", null, c1, 0);
		final IChannel result = ModelUtils.getInboundChildChannel(parent,
				x_inbound);
		assertSame(x_inbound, result);
	}

	@Test
	public void test_getSrcPath() {
		final ICompositeComponent root = new CompositeComponent("root", null);
		final ICompositeComponent comp1 = new CompositeComponent("comp1", root);
		final ICompositeComponent comp2 = new CompositeComponent("comp2", root);
		final IAtomicComponent a1 = new AtomicComponent("a1", comp1);
		final IAtomicComponent a2 = new AtomicComponent("a2", comp2);
		final IAtomicComponent a3 = new AtomicComponent("a3", comp2);
		final IChannel a_comp1 = new Channel("a", null, comp1, 0);
		final IChannel a_a1 = new Channel("a", null, a1, 0);
		final IChannel x_a1 = new Channel("x", a1, null, 0);
		final IChannel x_comp1_comp2 = new Channel("x", comp1, comp2, 0);
		final IChannel x_a2 = new Channel("x", null, a2, 0);
		final IChannel y_a2_a3 = new Channel("y", a2, a3, 0);
		final IChannel z_a3 = new Channel("z", a3, null, 0);
		final IChannel z_comp2 = new Channel("z", comp2, null, 0);

		final Deque<IChannel> path1 = ModelUtils.getSrcPath(x_a2, root);
		assertSame(x_a1, path1.pollFirst());
		assertSame(x_comp1_comp2, path1.pollFirst());
		assertSame(x_a2, path1.pollFirst());
		assertTrue(path1.isEmpty());

		final Deque<IChannel> path2 = ModelUtils.getSrcPath(y_a2_a3, root);
		assertSame(y_a2_a3, path2.pollFirst());
		assertTrue(path2.isEmpty());

		final Deque<IChannel> path3 = ModelUtils.getSrcPath(z_comp2, root);
		assertSame(z_a3, path3.pollFirst());
		assertSame(z_comp2, path3.pollFirst());
		assertTrue(path3.isEmpty());

		final Deque<IChannel> path4 = ModelUtils.getSrcPath(a_a1, root);
		assertSame(a_comp1, path4.pollFirst());
		assertSame(a_a1, path4.pollFirst());
		assertTrue(path4.isEmpty());

		final Deque<IChannel> path5 = ModelUtils.getSrcPath(x_a2, comp2);
		assertSame(x_a2, path5.pollFirst());
		assertTrue(path5.isEmpty());
	}

	@Test
	public void test_transformAtomicComponentNetwork() {
		// final ICompositeComponent p = new CompositeComponent("p", null);
		// final IComponent c1 = new AtomicComponent("c1", p);
		// final IComponent c2 = new AtomicComponent("c2", p);
		//
		// final IChannel x_p = new Channel("x", null, p, 1);
		// final IChannel y_p = new Channel("y", p, null, 1);
		//
		// final IChannel x_c1 = new Channel("x", null, c1, 1);
		// final IChannel x_c1_c2 = new Channel("x", c1, c2, 1);
		// final IChannel y_c2 = new Channel("y", c2, null, 1);
		//
		// final IListSet<IComponent> network = ModelUtils
		// .analyzeFlatAtomicComponentNetwork(p);
		// assertEquals(2, network.size());
		// assertTrue(network.contains(c1));
		// assertTrue(network.contains(c2));
		//
		// for (final IComponent component : network) {
		// assertNotSame("Expected c1 to be a copy", c1, component);
		// assertNotSame("Expected c2 to be a copy", c2, component);
		// }
		//
		// final IComponent new_c1 = network.get("c1");
		// final IComponent new_c2 = network.get("c2");
		// assertNotNull(new_c1);
		// assertNotNull(new_c2);
		// assertEquals(1, new_c1.getIncoming().size());
		// assertEquals(1, new_c1.getOutgoing().size());
		// assertEquals(1, new_c2.getIncoming().size());
		// assertEquals(1, new_c2.getOutgoing().size());
		//
		// assertNotSame(x_p, new_c1.getIncoming().getFirst());
		// assertNotSame(x_c1, new_c1.getIncoming().getFirst());
		// assertNotSame(x_c1_c2, new_c1.getOutgoing().getFirst());
		// assertNotSame(x_c1_c2, new_c2.getIncoming().getFirst());
		// assertNotSame(y_c2, new_c2.getOutgoing().getFirst());
		// assertNotSame(y_p, new_c2.getOutgoing().getFirst());
	}

}
