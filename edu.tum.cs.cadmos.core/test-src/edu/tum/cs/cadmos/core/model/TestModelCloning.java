/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2012 Technische Universitaet Muenchen                     |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+--------------------------------------------------------------------------*/

package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.expressions.ConstantExpression.EMPTY_MESSAGE;
import static edu.tum.cs.cadmos.core.model.EPortDirection.INBOUND;
import static edu.tum.cs.cadmos.core.model.EPortDirection.OUTBOUND;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import edu.tum.cs.cadmos.core.types.BooleanType;
import edu.tum.cs.cadmos.core.types.IType;

/**
 * Tests for clone(...) methods of the model elements.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class TestModelCloning {

	@Test
	public void test_Variable_Clone() {
		final IAtomicComponent c1 = new AtomicComponent("c1", null);
		final IAtomicComponent c2 = new AtomicComponent("c2", null);
		final IType t = new BooleanType();
		final IVariable v = new Variable("x", t, c1);
		final IVariable clone = v.clone(c2);
		assertNotSame(v, clone);
		assertEquals("x", clone.getId());
		assertSame(v.getName(), clone.getName());
		assertSame(t, clone.getType());
		assertSame(c2, clone.getScope());
	}

	@Test
	public void test_Channel_Clone() {
		final IAtomicComponent c1 = new AtomicComponent("c1", null);
		final IAtomicComponent c2 = new AtomicComponent("c2", null);
		final IAtomicComponent c3 = new AtomicComponent("c3", null);
		final IAtomicComponent c4 = new AtomicComponent("c4", null);
		final IType t = new BooleanType();
		final IPort p1 = new Port("x", null, t, c1, OUTBOUND);
		final IPort p2 = new Port("x", null, t, c2, INBOUND);
		final IPort p3 = new Port("x", null, t, c3, OUTBOUND);
		final IPort p4 = new Port("x", null, t, c4, INBOUND);
		final IChannel c = new Channel("x", null, p1, p2,
				asList(EMPTY_MESSAGE), 2, 3);
		final IChannel clone = c.clone(p3, p4);
		assertNotSame(c, clone);
		assertEquals("x", clone.getId());
		assertSame(c.getName(), clone.getName());
		assertSame(c3, clone.getSrcComponent());
		assertSame(c4, clone.getDstComponent());
		assertEquals(1, clone.getDelay());
		assertEquals(2, clone.getSrcRate());
		assertEquals(3, clone.getDstRate());
	}

	@Test
	public void test_AtomicComponent_Clone() {
		final ICompositeComponent p1 = new CompositeComponent("p1", null);
		final ICompositeComponent p2 = new CompositeComponent("p2", null);
		final IAtomicComponent c = new AtomicComponent("c", p1);
		final IVariable v = new Variable("x", c);
		final IAtomicComponent clone = (IAtomicComponent) c.clone(p2);
		assertNotSame(c, clone);
		assertEquals("c", clone.getId());
		assertSame("c", clone.getName());
		assertSame(p2, clone.getParent());
		assertEquals(1, clone.getVariables().size());
		assertEquals(v, clone.getVariables().getFirst());
	}

	@Test
	public void test_CompositeComponent_Clone() {
		final ICompositeComponent p = new CompositeComponent("p", null);
		final IAtomicComponent c1 = new AtomicComponent("c1", p);
		final IAtomicComponent c2 = new AtomicComponent("c2", p);
		final IPort x1 = new Port("x1", null, VOID, p, INBOUND);
		final IPort x2 = new Port("x2", null, VOID, c1, INBOUND);
		final IPort x3 = new Port("x3", null, VOID, c1, OUTBOUND);
		final IPort x4 = new Port("x4", null, VOID, c2, INBOUND);
		final IPort x5 = new Port("x5", null, VOID, c2, OUTBOUND);
		final IPort x6 = new Port("x6", null, VOID, p, OUTBOUND);
		final IChannel x1x2 = new Channel("x1x2", x1, x2, 1);
		final IChannel x3x4 = new Channel("x3x4", x3, x4, 1);
		final IChannel x5x6 = new Channel("x5x6", x5, x6, 1);
		final ICompositeComponent clone = (ICompositeComponent) p.clone(null);
		assertNotSame(p, clone);
		assertEquals("p", clone.getId());
		assertSame("p", clone.getName());
		assertSame(null, clone.getParent());
		assertEquals(2, clone.getChildren().size());
		final IComponent firstChild = clone.getChildren().getFirst();
		assertEquals(c1, firstChild);
		final IComponent secondChild = clone.getChildren().getLast();
		assertEquals(c2, secondChild);
		assertNotSame(c1, firstChild);
		assertNotSame(c2, secondChild);
		assertEquals(1, firstChild.getInbound().size());
		assertEquals(x1x2, firstChild.getInbound().getFirst().getIncoming());
		assertEquals(1, firstChild.getOutbound().size());
		assertEquals(1, firstChild.getOutbound().getFirst().getOutgoing()
				.size());
		assertEquals(x3x4, firstChild.getOutbound().getFirst().getOutgoing()
				.getFirst());
		assertEquals(1, secondChild.getInbound().size());
		assertEquals(x3x4, secondChild.getInbound().getFirst().getIncoming());
		assertEquals(1, secondChild.getOutbound().size());
		assertEquals(1, secondChild.getOutbound().getFirst().getOutgoing()
				.size());
		assertEquals(x5x6, secondChild.getOutbound().getFirst().getOutgoing()
				.getFirst());
	}

}
