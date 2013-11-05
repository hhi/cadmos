package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.model.EPortDirection.INBOUND;
import static edu.tum.cs.cadmos.core.model.EPortDirection.OUTBOUND;
import static edu.tum.cs.cadmos.core.model.ModelUtils.createChannel;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.core.ListSet;

public class TestModelElements {

	@Test
	public void test_Add_Ports_To_Component() {
		final IComponent c = new AtomicComponent("C", null);
		final IPort x = new Port("x", null, VOID, c, INBOUND);
		final IPort y = new Port("y", null, VOID, c, OUTBOUND);
		assertEquals(1, c.getInbound().size());
		assertTrue(c.getInbound().contains(x));
		assertEquals(1, c.getOutbound().size());
		assertTrue(c.getOutbound().contains(y));
	}

	@Test
	public void test_Add_Loop_Channel_To_Component() {
		final IComponent c = new AtomicComponent("C", null);
		final IChannel loop = createChannel("loop", c, c, 1);
		assertEquals(1, c.getInbound().size());
		assertEquals(1, c.getOutbound().size());
		assertTrue(c.getInboundIncomingChannels().contains(loop));
		assertTrue(c.getOutboundOutgoingChannels().contains(loop));
	}

	@Test
	public void test_Wire_Sibling_Components_OK() {
		final ICompositeComponent p = new CompositeComponent("Parent", null);
		final IComponent c1 = new AtomicComponent("C1", p);
		final IComponent c2 = new AtomicComponent("C2", p);
		assertEquals(p, c1.getParent());
		assertEquals(p, c2.getParent());
		assertEquals(new ListSet<>(c1, c2), p.getChildren());
		final IChannel x = createChannel("x", c1, c2, 0);
		assertEquals(1, c1.getOutbound().size());
		assertEquals(1, c2.getInbound().size());
		assertTrue(c1.getOutboundOutgoingChannels().contains(x));
		assertTrue(c2.getInboundIncomingChannels().contains(x));
	}

	@Test
	public void test_Wire_Components_In_Different_Parents_ERR() {
		final ICompositeComponent p1 = new CompositeComponent("Parent1", null);
		final ICompositeComponent p2 = new CompositeComponent("Parent2", null);
		final IComponent c1 = new AtomicComponent("C1", p1);
		final IComponent c2 = new AtomicComponent("C2", p2);
		final IPort x1 = new Port("x1", null, VOID, c1, OUTBOUND);
		final IPort x2 = new Port("x2", null, VOID, c2, INBOUND);
		assertEquals(p1, c1.getParent());
		assertEquals(p2, c2.getParent());
		try {
			new Channel("x", x1, x2, 0);
			throw new Error(
					"Expected AssertionError when wiring two components in different parents");
		} catch (final AssertionError e) {
			// AssertionError is expected => OK
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Add_Components_Same_Ids_ERR() {
		final ICompositeComponent p = new CompositeComponent("Parent", null);
		new AtomicComponent("C", p);
		new AtomicComponent("C", p);
		throw new Error(
				"Expected IllegalArgumentException when adding two components with equal id to same parent");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Add_Ports_Inbound_Same_Ids_ERR() {
		final IComponent c = new AtomicComponent("C", null);
		new Port("x", null, VOID, c, INBOUND);
		new Port("x", null, VOID, c, INBOUND);
		throw new Error(
				"Expected IllegalArgumentException when adding two inbound ports with equal id to same component");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Add_Ports_Outbound_Same_Ids_ERR() {
		final IComponent c = new AtomicComponent("C", null);
		new Port("x", null, VOID, c, OUTBOUND);
		new Port("x", null, VOID, c, OUTBOUND);
		throw new Error(
				"Expected IllegalArgumentException when adding two outbound ports with equal id to same component");
	}

	@Test(expected = AssertionError.class)
	public void test_Create_Channel_With_Null_Src_And_Null_Dst_ERR() {
		new Channel("x", null, null, 0);
		throw new Error(
				"Expected AssertionError when creating a channel with src == dst == null");
	}

	@Test
	public void test_Add_Variables_To_Component() {
		final IAtomicComponent c = new AtomicComponent("C", null);
		final IVariable v1 = new Variable("v1", c);
		final IVariable v2 = new Variable("v2", c);
		assertEquals(2, c.getVariables().size());
		assertTrue(c.getVariables().contains(v1));
		assertTrue(c.getVariables().contains(v2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Add_Variables_Same_Id_To_Component_ERR() {
		final IAtomicComponent c = new AtomicComponent("C", null);
		new Variable("x", c);
		new Variable("x", c);
		throw new Error(
				"Expected IllegalArgumentException when adding variables with equal ids to same component");
	}

	@Test(expected = AssertionError.class)
	public void test_Add_Variable_Same_Id_As_Inbound_Port_ERR() {
		final IAtomicComponent c = new AtomicComponent("C", null);
		new Port("x", null, VOID, c, INBOUND);
		new Variable("x", c);
		throw new Error(
				"Expected AssertionError when adding variable with an id equal to the id of an inbound port");
	}

	@Test(expected = AssertionError.class)
	public void test_Add_Variable_Same_Id_As_Outbound_Port_ERR() {
		final IAtomicComponent c = new AtomicComponent("C", null);
		new Port("x", null, VOID, c, OUTBOUND);
		new Variable("x", c);
		throw new Error(
				"Expected AssertionError when adding variable with an id equal to the id of an outbound port");
	}

	@Test(expected = AssertionError.class)
	public void test_Add_Inbound_Port_Same_Id_As_Variable_ERR() {
		final IAtomicComponent c = new AtomicComponent("C", null);
		new Variable("x", c);
		new Port("x", null, VOID, c, INBOUND);
		throw new Error(
				"Expected AssertionError when adding an inbound port with an id equal to the id of a variable");
	}

	@Test(expected = AssertionError.class)
	public void test_Add_Variable_Same_Id_As_Channel_To_Destination_ERR() {
		final IAtomicComponent c = new AtomicComponent("C", null);
		new Variable("x", c);
		new Port("x", null, VOID, c, OUTBOUND);
		throw new Error(
				"Expected AssertionError when adding an outbound port with an id equal to the id of a variable");
	}

}
