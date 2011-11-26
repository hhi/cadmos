package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.model.ModelUtils.createChannel;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.List;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.core.expressions.ConstantExpression;

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
	public void test_getSrcPath() {
		final ICompositeComponent root = new CompositeComponent("root", null);
		final ICompositeComponent comp1 = new CompositeComponent("comp1", root);
		final ICompositeComponent comp2 = new CompositeComponent("comp2", root);
		final IAtomicComponent a1 = new AtomicComponent("a1", comp1);
		final IAtomicComponent a2 = new AtomicComponent("a2", comp2);
		final IAtomicComponent a3 = new AtomicComponent("a3", comp2);
		final IChannel root_comp1 = createChannel("a", root, comp1, 0);
		final IChannel comp1_a1 = createChannel("a", comp1, a1, 0);
		final IChannel a1_comp1 = createChannel("x", a1, comp1, 0);
		final IChannel comp1_comp2 = createChannel("x", comp1, comp2, 0);
		final IChannel comp2_a2 = createChannel("x", comp2, a2, 0);
		final IChannel a2_a3 = createChannel("y", a2, a3, 0);
		final IChannel a3_comp2 = createChannel("z", a3, comp2, 0);
		final IChannel comp2_root = createChannel("z", comp2, root, 0);

		final Deque<IChannel> path1 = ModelUtils.getSrcPath(comp2_a2.getDst(),
				root);
		assertSame(a1_comp1, path1.pollFirst());
		assertSame(comp1_comp2, path1.pollFirst());
		assertSame(comp2_a2, path1.pollFirst());
		assertTrue(path1.isEmpty());

		final Deque<IChannel> path2 = ModelUtils.getSrcPath(a2_a3.getDst(),
				root);
		assertSame(a2_a3, path2.pollFirst());
		assertTrue(path2.isEmpty());

		final Deque<IChannel> path3 = ModelUtils.getSrcPath(
				comp2_root.getDst(), root);
		assertSame(a3_comp2, path3.pollFirst());
		assertSame(comp2_root, path3.pollFirst());
		assertTrue(path3.isEmpty());

		final Deque<IChannel> path4 = ModelUtils.getSrcPath(comp1_a1.getDst(),
				root);
		assertSame(root_comp1, path4.pollFirst());
		assertSame(comp1_a1, path4.pollFirst());
		assertTrue(path4.isEmpty());

		final Deque<IChannel> path5 = ModelUtils.getSrcPath(comp2_a2.getDst(),
				comp2);
		assertSame(comp2_a2, path5.pollFirst());
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
		final IChannel a1_comp1 = createChannel("a", a1, comp1, 0);
		final IChannel comp1_comp2 = createChannel("a", comp1, comp2, 0);
		final IChannel comp2_a2 = createChannel("a", comp2, a2, 0);
		final IChannel comp2_a3 = createChannel("a", comp2, a3, 0);

		final List<Deque<IChannel>> paths = ModelUtils.getDstPaths(
				a1_comp1.getSrc(), root);
		assertEquals(2, paths.size());
		assertEquals(asList(a1_comp1, comp1_comp2, comp2_a2), paths.get(0));
		assertEquals(asList(a1_comp1, comp1_comp2, comp2_a3), paths.get(1));
	}

	@Test
	public void test_transformAtomicComponentNetwork_Single_AtomicComponent() {
		final ICompositeComponent root = new CompositeComponent("root", null);
		final IAtomicComponent comp = new AtomicComponent("atomic", root);
		final IChannel root_a_comp = createChannel("a", root, comp, 2);
		final IChannel comp_b_root = createChannel("b", comp, root, 2);
		final IChannel comp_c_root = createChannel("c", comp, root, 2);

		final ICompositeComponent network = ModelUtils
				.transformAtomicComponentNetwork(root);
		final IListSet<IComponent> atomics = network.getChildren();
		assertEquals(1, atomics.size());
		final IComponent atomic = atomics.getFirst();
		assertEquals(comp, atomic);
		assertNotSame(comp, atomic);

		assertEquals(1, atomic.getInbound().size());
		final IChannel incoming = atomic.getInbound().getFirst().getIncoming();
		assertEquals(root_a_comp, incoming);
		assertNotSame(root_a_comp, incoming);
		assertEquals(2, incoming.getDelay());

		assertEquals(2, atomic.getOutbound().size());

		assertEquals(comp_b_root, atomic.getOutbound().getFirst().getOutgoing()
				.getFirst());
		assertNotSame(comp_b_root, atomic.getOutbound().getFirst()
				.getOutgoing().getFirst());
		assertEquals(2, atomic.getOutbound().getFirst().getOutgoing()
				.getFirst().getDelay());

		assertEquals(comp_c_root, atomic.getOutbound().getLast().getOutgoing()
				.getFirst());
		assertNotSame(comp_c_root, atomic.getOutbound().getLast().getOutgoing()
				.getFirst());
		assertEquals(2, atomic.getOutbound().getLast().getOutgoing().getFirst()
				.getDelay());
	}

	@Test
	public void test_transformAtomicComponentNetwork_Two_AtomicComponents() {
		final ICompositeComponent root = new CompositeComponent("root", null);

		final ICompositeComponent comp1 = new CompositeComponent("comp1", root);
		final ICompositeComponent comp2 = new CompositeComponent("comp2", root);
		final IAtomicComponent a1 = new AtomicComponent("a1", comp1);
		final IAtomicComponent a2 = new AtomicComponent("a2", comp2);
		final IAtomicComponent a3 = new AtomicComponent("a3", comp2);

		final IChannel root_a_comp1 = createChannel("a", root, comp1, 0);
		root_a_comp1.getInitialMessages().add(new ConstantExpression("alpha"));
		final IChannel comp1_x_comp2 = createChannel("x", comp1, comp2, 1);
		final IChannel comp2_b_root = createChannel("b", comp2, root, 0);
		createChannel("c", comp2, root, 0);

		final IChannel comp1_a_a1 = createChannel("a", comp1, a1, 0);
		comp1_a_a1.getInitialMessages().add(new ConstantExpression("beta"));
		final IChannel a1_x_comp1 = createChannel("x", a1, comp1, 1);
		final IChannel comp2_x_a2 = createChannel("x", comp2, a2, 1);
		final IChannel comp2_x_a3 = createChannel("x", comp2, a3, 0);
		final IChannel a2_b_comp2 = createChannel("b", a2, comp2, 0);
		createChannel("c", a3, comp2, 0);

		final ICompositeComponent network = ModelUtils
				.transformAtomicComponentNetwork(root);
		final IListSet<IComponent> atomics = network.getChildren();

		/*
		 * Assert that all 3 atomic components are present and have been cloned.
		 */
		assertEquals(3, atomics.size());
		assertEquals(a1, atomics.get("a1"));
		assertNotSame(a1, atomics.get("a1"));
		assertEquals(a2, atomics.get("a2"));
		assertNotSame(a2, atomics.get("a2"));
		assertEquals(a3, atomics.get("a3"));
		assertNotSame(a3, atomics.get("a3"));

		/*
		 * Assert that rewiring works and delays as well as rates are correctly
		 * calculated.
		 */
		assertEquals(1, atomics.get("a1").getInbound().size());
		final IChannel a = atomics.get("a1").getInbound().getFirst()
				.getIncoming();
		assertEquals("root.a::a1.a", a.getId());
		assertEquals(2, a.getDelay());
		assertEquals("beta", ((ConstantExpression) a.getInitialMessages()
				.get(0)).getValue());
		assertEquals("alpha",
				((ConstantExpression) a.getInitialMessages().get(1)).getValue());

		assertEquals(1, atomics.get("a2").getOutbound().size());
		final IChannel b = atomics.get("a2").getOutbound().getFirst()
				.getOutgoing().getFirst();
		assertEquals("a2.b::root.b", b.getId());
		assertNotSame(a2_b_comp2, b);
		assertNotSame(comp2_b_root, b);
		assertEquals(1, b.getSrcRate());
		assertEquals(1, b.getDstRate());
		assertEquals(0, b.getDelay());

		assertEquals(1, atomics.get("a1").getOutbound().size());
		assertEquals(2, atomics.get("a1").getOutbound().getFirst()
				.getOutgoing().size());
		final IChannel a1_a2 = atomics.get("a1").getOutbound().get("x")
				.getOutgoing().getFirst();
		assertEquals(a2, a1_a2.getDst().getComponent());
		assertNotSame(comp1_x_comp2, a1_a2);
		assertNotSame(a1_x_comp1, a1_a2);
		assertNotSame(comp2_x_a2, a1_a2);
		assertEquals(3, a1_a2.getDelay());
		final IChannel a1_a3 = atomics.get("a1").getOutbound().get("x")
				.getOutgoing().getLast();
		assertEquals(a3, a1_a3.getDst().getComponent());
		assertNotSame(comp1_x_comp2, a1_a3);
		assertNotSame(a1_x_comp1, a1_a3);
		assertNotSame(comp2_x_a3, a1_a3);
		assertEquals(2, a1_a3.getDelay());
	}

}
