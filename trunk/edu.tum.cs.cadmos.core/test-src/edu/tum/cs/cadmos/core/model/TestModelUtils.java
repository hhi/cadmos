package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.List;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.core.expressions.ConstantExpression;
import edu.tum.cs.cadmos.core.expressions.IExpression;

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
	public void test_getInboundChildChannels() {
		final ICompositeComponent parent = new CompositeComponent("parent",
				null);
		final IAtomicComponent c1 = new AtomicComponent("c1", parent);
		final IAtomicComponent c2 = new AtomicComponent("c2", parent);
		final IAtomicComponent c3 = new AtomicComponent("c3", parent);
		@SuppressWarnings("unused")
		final IChannel x_internal = new Channel("x", c1, c2, 0);
		final IChannel x_inbound1 = new Channel("x", null, c1, 0);
		final IChannel x_inbound3 = new Channel("x", null, c3, 0);
		final List<IChannel> result = ModelUtils.getInboundChildChannels(
				parent, x_inbound1);
		assertEquals(asList(x_inbound1, x_inbound3), result);
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
	public void test_getDstPaths() {
		final ICompositeComponent root = new CompositeComponent("root", null);
		final ICompositeComponent comp1 = new CompositeComponent("comp1", root);
		final ICompositeComponent comp2 = new CompositeComponent("comp2", root);
		final IAtomicComponent a1 = new AtomicComponent("a1", comp1);
		final IAtomicComponent a2 = new AtomicComponent("a2", comp2);
		final IAtomicComponent a3 = new AtomicComponent("a3", comp2);
		final IChannel a_a1 = new Channel("a", a1, null, 0);
		final IChannel a_comp1_comp2 = new Channel("a", comp1, comp2, 0);
		final IChannel a_a2 = new Channel("a", null, a2, 0);
		final IChannel a_a3 = new Channel("a", null, a3, 0);

		final List<Deque<IChannel>> paths = ModelUtils.getDstPaths(a_a1, root);
		assertEquals(2, paths.size());
		assertEquals(asList(a_a1, a_comp1_comp2, a_a2), paths.get(0));
		assertEquals(asList(a_a1, a_comp1_comp2, a_a3), paths.get(1));
	}

	@Test
	public void test_transformAtomicComponentNetwork_Single_AtomicComponent() {
		final IAtomicComponent root = new AtomicComponent("root", null);
		final IChannel a_root = new Channel("a", null, root, 2);
		final IChannel root_b = new Channel("b", root, null, 2);
		final IChannel root_c = new Channel("c", root, null, 2);

		final IListSet<IAtomicComponent> network = ModelUtils
				.transformAtomicComponentNetwork(root);
		assertEquals(1, network.size());
		assertEquals(root, network.getFirst());
		assertNotSame(root, network.getFirst());

		assertEquals(1, network.getFirst().getIncoming().size());
		assertEquals(a_root, network.getFirst().getIncoming().getFirst());
		assertNotSame(a_root, network.getFirst().getIncoming().getFirst());
		assertEquals(2, network.getFirst().getIncoming().getFirst().getDelay());

		assertEquals(2, network.getFirst().getOutgoing().size());

		assertEquals(root_b, network.getFirst().getOutgoing().getFirst());
		assertNotSame(root_b, network.getFirst().getOutgoing().getFirst());
		assertEquals(2, network.getFirst().getOutgoing().getFirst().getDelay());

		assertEquals(root_c, network.getFirst().getOutgoing().getLast());
		assertNotSame(root_c, network.getFirst().getOutgoing().getLast());
		assertEquals(2, network.getFirst().getOutgoing().getLast().getDelay());
	}

	@Test
	public void test_transformAtomicComponentNetwork() {
		final ICompositeComponent root = new CompositeComponent("root", null);
		final IChannel a_root = new Channel("a", null, VOID, null, root,
				asList((IExpression) new ConstantExpression("alpha")), 2, 3);
		final IChannel root_b = new Channel("b", root, null, 0);
		final IChannel root_c = new Channel("c", root, null, 0);

		final ICompositeComponent comp1 = new CompositeComponent("comp1", root);
		final ICompositeComponent comp2 = new CompositeComponent("comp2", root);
		final IAtomicComponent a1 = new AtomicComponent("a1", comp1);
		final IAtomicComponent a2 = new AtomicComponent("a2", comp2);
		final IAtomicComponent a3 = new AtomicComponent("a3", comp2);

		final IChannel a_comp1 = new Channel("a", null, VOID, null, comp1,
				asList((IExpression) new ConstantExpression("beta")), 2, 2);
		final IChannel comp1_x_comp2 = new Channel("x", comp1, comp2, 1);
		final IChannel comp2_b = new Channel("b", comp2, null, 0);
		final IChannel comp2_c = new Channel("c", comp2, null, 0);

		final IChannel a_a1 = new Channel("a", null, VOID, null, a1,
				asList((IExpression) new ConstantExpression("gamma")), 1, 2);
		final IChannel a1_x = new Channel("x", a1, null, 1);
		final IChannel x_a2 = new Channel("x", null, a2, 1);
		final IChannel x_a3 = new Channel("x", null, a3, 0);
		final IChannel a2_b = new Channel("b", a2, null, 0);
		final IChannel a3_c = new Channel("c", a3, null, 0);

		final IListSet<IAtomicComponent> network = ModelUtils
				.transformAtomicComponentNetwork(root);

		/* Assert that all 3 atomic components are present and have been cloned. */
		assertEquals(3, network.size());
		assertEquals(a1, network.get("a1"));
		assertNotSame(a1, network.get("a1"));
		assertEquals(a2, network.get("a2"));
		assertNotSame(a2, network.get("a2"));
		assertEquals(a3, network.get("a3"));
		assertNotSame(a3, network.get("a3"));

		/*
		 * Assert that rewiring works and delays as well as rates are correctly
		 * calculated.
		 */
		assertEquals(1, network.get("a1").getIncoming().size());
		final IChannel a = network.get("a1").getIncoming().getFirst();
		assertEquals("a", a.getId());
		assertNotSame(a_root, a);
		assertNotSame(a_comp1, a);
		assertNotSame(a_a1, a);
		assertEquals(1, a.getSrcRate());
		assertEquals(3, a.getDstRate());
		assertEquals(3, a.getDelay());
		assertEquals("gamma",
				((ConstantExpression) a.getInitialMessages().get(0)).getValue());
		assertEquals("beta", ((ConstantExpression) a.getInitialMessages()
				.get(1)).getValue());
		assertEquals("alpha",
				((ConstantExpression) a.getInitialMessages().get(2)).getValue());

		assertEquals(1, network.get("a2").getOutgoing().size());
		final IChannel b = network.get("a2").getOutgoing().getFirst();
		assertEquals("b", b.getId());
		assertNotSame(a2_b, b);
		assertNotSame(comp2_b, b);
		assertNotSame(root_b, b);
		assertEquals(1, b.getSrcRate());
		assertEquals(1, b.getDstRate());
		assertEquals(0, b.getDelay());

		assertEquals(2, network.get("a1").getOutgoing().size());
		final IChannel a1_a2 = network.get("a1").getOutgoing().getFirst();
		assertEquals(a2, a1_a2.getDst());
		assertNotSame(comp1_x_comp2, a1_a2);
		assertNotSame(a1_x, a1_a2);
		assertNotSame(x_a2, a1_a2);
		assertEquals(3, a1_a2.getDelay());
		final IChannel a1_a3 = network.get("a1").getOutgoing().getLast();
		assertEquals(a3, a1_a3.getDst());
		assertNotSame(comp1_x_comp2, a1_a3);
		assertNotSame(a1_x, a1_a3);
		assertNotSame(x_a3, a1_a3);
		assertEquals(2, a1_a3.getDelay());

		assertEquals(1, network.get("a3").getOutgoing().size());
		final IChannel c = network.get("a3").getOutgoing().getFirst();
		assertEquals("c", c.getId());
		assertNotSame(a3_c, c);
		assertNotSame(comp2_c, c);
		assertNotSame(root_c, c);

	}

}
