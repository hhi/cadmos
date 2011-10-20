package edu.tum.cs.cadmos.core.model;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

public class TestModelElements {

	@Test
	public void test_Add_Channels_To_Component() {
		final IComponent c = new AtomicComponent("C", null);
		final IChannel x = new Channel("x", null, c, 0);
		final IChannel y = new Channel("y", c, null, 0);
		assertEquals(1, c.getIncoming().size());
		assertTrue(c.getIncoming().contains(x));
		assertEquals(1, c.getOutgoing().size());
		assertTrue(c.getOutgoing().contains(y));
	}

	@Test
	public void test_Add_Loop_Channel_To_Component() {
		final IComponent c = new AtomicComponent("C", null);
		final IChannel loop = new Channel("loop", c, c, 0);
		assertEquals(1, c.getIncoming().size());
		assertEquals(1, c.getOutgoing().size());
		assertTrue(c.getIncoming().contains(loop));
		assertTrue(c.getOutgoing().contains(loop));
	}

	@Test
	public void test_Wire_Components_In_Same_Parent_OK() {
		final ICompositeComponent p = new CompositeComponent("Parent", null);
		final IComponent c1 = new AtomicComponent("C1", p);
		final IComponent c2 = new AtomicComponent("C2", p);
		assertEquals(p, c1.getParent());
		assertEquals(p, c2.getParent());
		assertEquals(new HashSet<>(asList(c1, c2)), p.getChildren());
		final IChannel x = new Channel("x", c1, c2, 0);
		assertEquals(1, c1.getOutgoing().size());
		assertEquals(1, c2.getIncoming().size());
		assertTrue(c1.getOutgoing().contains(x));
		assertTrue(c2.getIncoming().contains(x));
	}

	@SuppressWarnings("unused")
	@Test
	public void test_Wire_Components_In_Different_Parents_ERR() {
		final ICompositeComponent p1 = new CompositeComponent("Parent1", null);
		final ICompositeComponent p2 = new CompositeComponent("Parent2", null);
		final IComponent c1 = new AtomicComponent("C1", p1);
		final IComponent c2 = new AtomicComponent("C2", p2);
		assertEquals(p1, c1.getParent());
		assertEquals(p2, c2.getParent());
		try {
			new Channel("x", c1, c2, 0);
			throw new Error(
					"Expected AssertionError when wiring two components in different parents");
		} catch (final AssertionError e) {
			// AssertionError is expected => OK
		}
	}

	@SuppressWarnings("unused")
	@Test(expected = AssertionError.class)
	public void test_Add_Components_Same_Ids_ERR() {
		final ICompositeComponent p = new CompositeComponent("Parent", null);
		new AtomicComponent("C", p);
		new AtomicComponent("C", p);
		throw new Error(
				"Expected AssertionError when adding two components with equal id to same parent");
	}

	@SuppressWarnings("unused")
	@Test(expected = AssertionError.class)
	public void test_Add_Channels_Incoming_Same_Ids_ERR() {
		final IComponent c = new AtomicComponent("C", null);
		new Channel("x", null, c, 0);
		new Channel("x", null, c, 0);
		throw new Error(
				"Expected AssertionError when adding two incoming channels with equal id to same component");
	}

	@SuppressWarnings("unused")
	@Test(expected = AssertionError.class)
	public void test_Add_Channels_Outgoing_Same_Ids_ERR() {
		final IComponent c = new AtomicComponent("C", null);
		new Channel("x", c, null, 0);
		new Channel("x", c, null, 0);
		throw new Error(
				"Expected AssertionError when adding two outgoing channels with equal id to same component");
	}

	@SuppressWarnings("unused")
	@Test(expected = AssertionError.class)
	public void test_Create_Channel_With_Null_Src_And_Null_Dst_ERR() {
		new Channel("x", null, null, 0);
		throw new Error(
				"Expected AssertionError when creating a channel with src == dst == null");
	}

}
