package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.ConstantExpression.EMPTY_MESSAGE;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import static java.util.Collections.nCopies;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.core.expressions.ConstantExpression;
import edu.tum.cs.cadmos.core.expressions.IExpression;
import edu.tum.cs.cadmos.core.types.IType;
import edu.tum.cs.cadmos.core.types.VoidType;

/**
 * A channel is a typed element that connects a source and a destination
 * component.
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
public class Channel extends AbstractTypedElement implements IChannel {

	private final IComponent src;

	private final IComponent dst;

	private final List<IExpression> initialMessages = new ArrayList<>();

	private final int srcRate;

	private final int dstRate;

	/**
	 * Creates a new channel and adds this channel to <i>src outgoing</i> and
	 * <i>dst incoming</i> if either of these or both are not <code>null</code>.
	 * 
	 * @throws AssertionError
	 *             if <i>src</i> and <i>dst</i> are both <code>null</code>.
	 * @see Channel#Channel(String, IComponent, IComponent, int)
	 */
	public Channel(String id, String name, IType type, IComponent src,
			IComponent dst, List<IExpression> initialMessages, int srcRate,
			int dstRate) {
		super(id, name, type);
		assertTrue(src != null || dst != null,
				"Expected 'src' and 'dst' to be not null at the same time");
		if (src != null && dst != null) {
			assertTrue(
					src.getParent() == dst.getParent(),
					"Expected same parent of 'src' and 'dst', but was '%s' and '%s'",
					src.getParent(), dst.getParent());
		}
		for (final IExpression initialMessage : initialMessages) {
			assertNotNull(initialMessage, "initialMessage");
		}
		assertTrue(srcRate > 0, "Expected 'srcRate' to be > 0, but was '%s'",
				srcRate);
		assertTrue(dstRate > 0, "Expected 'dstRate' to be > 0, but was '%s'",
				dstRate);
		this.src = src;
		this.dst = dst;
		this.initialMessages.addAll(initialMessages);
		this.srcRate = srcRate;
		this.dstRate = dstRate;
		if (src != null) {
			src.getOutgoing().add(this);
		}
		if (dst != null) {
			dst.getIncoming().add(this);
		}
	}

	/**
	 * Creates a new channel with {@link VoidType#VOID} as data type and
	 * <i>delay</i> initial messages equal to
	 * {@link ConstantExpression#EMPTY_MESSAGE}.
	 * <p>
	 * This constructor is useful for testing purposes.
	 * 
	 * @throws AssertionError
	 *             if <i>src</i> and <i>dst</i> are both <code>null</code>.
	 * @see #Channel(String, String, IType, IComponent, IComponent, List, int,
	 *      int)
	 */
	public Channel(String id, IComponent src, IComponent dst, int delay) {
		this(id, null, VOID, src, dst, nCopies(delay, EMPTY_MESSAGE), 1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getSrc() {
		return src;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getDst() {
		return dst;
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
	public IChannel clone(IComponent newSrc, IComponent newDst) {
		return new Channel(getId(), getName(), getType(), newSrc, newDst,
				getInitialMessages(), getSrcRate(), getDstRate());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getId() + ", " + getSrc()
				+ "->" + getDst() + ")";

	}

}
