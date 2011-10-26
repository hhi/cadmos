package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotContainedIn;
import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.ConstantExpression.EMPTY_MESSAGE;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import static java.util.Collections.nCopies;

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.core.expressions.IExpression;
import edu.tum.cs.cadmos.core.types.IType;

public class Channel extends AbstractTypedElement implements IChannel {

	private final IComponent src;

	private final IComponent dst;

	private final List<IExpression> initialMessages = new ArrayList<>();

	private final int srcRate;

	private final int dstRate;

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
			assertNotContainedIn(this, src.getOutgoing(), "this",
					"src.getOutgoing()");
			if (src instanceof IAtomicComponent) {
				assertNotContainedIn(this,
						((IAtomicComponent) src).getVariables(), "this",
						"src.getVariables()");
			}
			src.getOutgoing().add(this);
		}
		if (dst != null) {
			assertNotContainedIn(this, dst.getIncoming(), "this",
					"dst.getIncoming()");
			if (dst instanceof IAtomicComponent) {
				assertNotContainedIn(this,
						((IAtomicComponent) dst).getVariables(), "this",
						"dst.getVariables()");
			}
			dst.getIncoming().add(this);
		}
	}

	public Channel(String id, IComponent src, IComponent dst, int delay) {
		this(id, null, VOID, src, dst, nCopies(delay, EMPTY_MESSAGE), 1, 1);
	}

	@Override
	public IComponent getSrc() {
		return src;
	}

	@Override
	public IComponent getDst() {
		return dst;
	}

	@Override
	public int getDelay() {
		return initialMessages.size();
	}

	/** {@inheritDoc} */
	@Override
	public List<IExpression> getInitialMessages() {
		return initialMessages;
	}

	@Override
	public int getSrcRate() {
		return srcRate;
	}

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

}
