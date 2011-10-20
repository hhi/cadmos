package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import edu.tum.cs.cadmos.core.types.IType;
import edu.tum.cs.cadmos.core.types.VoidType;

public class Channel extends AbstractVariable implements IChannel {

	private final IComponent src;

	private final IComponent dst;

	private final int delay;

	private final int srcRate;

	private final int dstRate;

	public Channel(Object id, String name, IType type, IComponent src,
			IComponent dst, int delay, int srcRate, int dstRate) {
		super(id, name, type);
		assertTrue(src != null || dst != null,
				"Expected 'src' and 'dst' to be not null at the same time");
		if (src != null && dst != null) {
			assertTrue(
					src.getParent() == dst.getParent(),
					"Expected same parent of 'src' and 'dst', but was '%s' and '%s'",
					src.getParent(), dst.getParent());
		}
		assertTrue(delay >= 0, "Expected 'delay' to be >= 0, but was '%s'",
				delay);
		assertTrue(srcRate > 0, "Expected 'srcRate' to be > 0, but was '%s'",
				srcRate);
		assertTrue(dstRate > 0, "Expected 'dstRate' to be > 0, but was '%s'",
				dstRate);
		this.src = src;
		this.dst = dst;
		this.delay = delay;
		this.srcRate = srcRate;
		this.dstRate = dstRate;
		if (src != null) {
			assertTrue(!src.getOutgoing().contains(this),
					"Channel with id '%s' is outgoing from '%s' already",
					getId(), src);
			src.getOutgoing().add(this);
		}
		if (dst != null) {
			assertTrue(!dst.getIncoming().contains(this),
					"Channel with id '%s' is incoming in '%s' already",
					getId(), dst);
			dst.getIncoming().add(this);
		}
	}

	public Channel(Object id, IComponent src, IComponent dst, int delay) {
		this(id, null, new VoidType(), src, dst, delay, 1, 1);
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
		return delay;
	}

	@Override
	public int getSrcRate() {
		return srcRate;
	}

	@Override
	public int getDstRate() {
		return dstRate;
	}

}
