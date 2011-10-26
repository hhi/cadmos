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
 * @author $Author$
 * @version $Rev$
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
		final IChannel c = new Channel("x", null, t, c1, c2,
				asList(EMPTY_MESSAGE), 2, 3);
		final IChannel clone = c.clone(c3, c4);
		assertNotSame(c, clone);
		assertEquals("x", clone.getId());
		assertSame(c.getName(), clone.getName());
		assertSame(t, clone.getType());
		assertSame(c3, clone.getSrc());
		assertSame(c4, clone.getDst());
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
		final IChannel x = new Channel("x", null, c1, 1);
		final IChannel y = new Channel("y", c1, c2, 1);
		final IChannel z = new Channel("z", c2, null, 1);
		final ICompositeComponent clone = (ICompositeComponent) p.clone(null);
		assertNotSame(p, clone);
		assertEquals("p", clone.getId());
		assertSame("p", clone.getName());
		assertSame(null, clone.getParent());
		assertEquals(2, clone.getChildren().size());
		assertEquals(c1, clone.getChildren().getFirst());
		assertEquals(c2, clone.getChildren().getLast());
		assertNotSame(c1, clone.getChildren().getFirst());
		assertNotSame(c2, clone.getChildren().getLast());
		assertEquals(1, clone.getChildren().getFirst().getIncoming().size());
		assertEquals(x, clone.getChildren().getFirst().getIncoming().getFirst());
		assertEquals(1, clone.getChildren().getFirst().getOutgoing().size());
		assertEquals(y, clone.getChildren().getFirst().getOutgoing().getFirst());
		assertEquals(1, clone.getChildren().getLast().getIncoming().size());
		assertEquals(y, clone.getChildren().getLast().getIncoming().getFirst());
		assertEquals(1, clone.getChildren().getLast().getOutgoing().size());
		assertEquals(z, clone.getChildren().getLast().getOutgoing().getFirst());
	}

}
