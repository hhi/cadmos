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

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.ConstantExpression.EMPTY_MESSAGE;
import static edu.tum.cs.cadmos.core.model.EPortDirection.INBOUND;
import static edu.tum.cs.cadmos.core.model.EPortDirection.OUTBOUND;
import static java.util.Collections.nCopies;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.commons.core.AbstractElement;
import edu.tum.cs.cadmos.core.expressions.ConstantExpression;
import edu.tum.cs.cadmos.core.expressions.IExpression;

/**
 * A channel is directed, typed and connects from a source to a destination
 * port.
 * <p>
 * This is the reference implementation of the {@link IChannel} interface.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class Channel extends AbstractElement implements IChannel {

	/** The source port. */
	private final IPort src;

	/** The destination port. */
	private final IPort dst;

	/**
	 * The list of initial messages in ascending order (starting at index 0).
	 * The number of initial messages is also returned as {@link #getDelay()} of
	 * this channel.
	 */
	private final List<IExpression> initialMessages = new ArrayList<>();

	/** The source rate. */
	private final int srcRate;

	/** The destination rate. */
	private final int dstRate;

	/**
	 * Creates a new channel and adds this channel to <i>src outgoing</i> and
	 * sets it as <i>dst incoming</i>.
	 * 
	 * @throws AssertionError
	 *             if <i>src</i> or <i>dst</i> is <code>null</code>.
	 * @see Channel#Channel(String, IPort, IPort, int)
	 */
	public Channel(String id, String name, IPort src, IPort dst,
			List<IExpression> initialMessages, int srcRate, int dstRate) {
		super(id, name);
		assertNotNull(src, "src");
		assertNotNull(dst, "dst");
		for (final IExpression initialMessage : initialMessages) {
			assertNotNull(initialMessage, "initialMessage");
		}
		assertTrue(srcRate > 0, "Expected 'srcRate' to be > 0, but was '%s'",
				srcRate);
		assertTrue(dstRate > 0, "Expected 'dstRate' to be > 0, but was '%s'",
				dstRate);
		final IComponent srcComp = src.getComponent();
		final ICompositeComponent srcParent = srcComp.getParent();
		final IComponent dstComp = dst.getComponent();
		final ICompositeComponent dstParent = dstComp.getParent();
		assertTrue(
				(/* Sibling -> Sibling */srcParent == dstParent)
						|| /* Parent -> Child */(srcComp == dstParent)
						|| /* Child -> Parent */(srcParent == dstComp),
				"Expected channel to connect sibling components with each other "
						+ "or a parent with a child component, but was '%s' -> '%s'",
				srcComp, dstComp);
		final EPortDirection srcDirection = src.getDirection();
		final EPortDirection dstDirection = dst.getDirection();
		if (srcParent == dstParent) { /* Sibling -> Sibling */
			assertTrue(
					srcDirection == OUTBOUND && dstDirection == INBOUND,
					"Expected siblings to connect from OUTBOUND to INBOUND port, but was '%s' -> '%s'",
					srcDirection, dstDirection);
		} else if (srcComp == dstParent) { /* Parent -> Child */
			assertTrue(
					srcDirection == INBOUND && dstDirection == INBOUND,
					"Expected a parent to connect to a child from INBOUND to INBOUND port, but was '%s' -> '%s'",
					srcDirection, dstDirection);
		} else if (srcParent == dstComp) { /* Child -> Parent */
			assertTrue(
					srcDirection == OUTBOUND && dstDirection == OUTBOUND,
					"Expected a child to connect to a parent from OUTBOUND to OUTBOUND port, but was '%s' -> '%s'",
					srcDirection, dstDirection);
		} else {
			assertTrue(false, "Internal error: unhandled case");
		}
		this.src = src;
		this.dst = dst;
		this.initialMessages.addAll(initialMessages);
		this.srcRate = srcRate;
		this.dstRate = dstRate;
		src.getOutgoing().add(this);
		dst.setIncoming(this);
	}

	/**
	 * Creates a new channel with <i>delay</i> initial messages equal to
	 * {@link ConstantExpression#EMPTY_MESSAGE}.
	 * <p>
	 * This constructor is useful for testing purposes.
	 * 
	 * @throws AssertionError
	 *             if <i>src</i> or <i>dst</i> is <code>null</code>.
	 * @see #Channel(String, String, IPort, IPort, List, int, int)
	 */
	public Channel(String id, IPort src, IPort dst, int delay) {
		this(id, null, src, dst, nCopies(delay, EMPTY_MESSAGE), 1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public IPort getSrc() {
		return src;
	}

	/** {@inheritDoc} */
	@Override
	public IPort getDst() {
		return dst;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getSrcComponent() {
		return src.getComponent();
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getDstComponent() {
		return dst.getComponent();
	}

	/** {@inheritDoc} */
	@Override
	public int getDelay() {
		return initialMessages.size();
	}

	/** {@inheritDoc} */
	@Override
	public List<IExpression> getInitialMessages() {
		return initialMessages;
	}

	/** {@inheritDoc} */
	@Override
	public int getSrcRate() {
		return srcRate;
	}

	/** {@inheritDoc} */
	@Override
	public int getDstRate() {
		return dstRate;
	}

	/** {@inheritDoc} */
	@Override
	public IChannel clone(IPort newSrc, IPort newDst) {
		return new Channel(getId(), getName(), newSrc, newDst,
				getInitialMessages(), getSrcRate(), getDstRate());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getId() + ", " + getSrc()
				+ " -> " + getDst() + ")";

	}

}
