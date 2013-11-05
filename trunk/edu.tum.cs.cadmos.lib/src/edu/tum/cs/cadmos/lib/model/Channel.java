package edu.tum.cs.cadmos.lib.model;

public class Channel<SourceType extends SinkType, SinkType> implements
		IChannel<SourceType, SinkType> {

	private final IPort<SourceType> source;
	private final IPort<SinkType> sink;

	public Channel(IPort<SourceType> source, IPort<SinkType> sink) {
		this.source = source;
		this.sink = sink;
	}

	@Override
	public IPort<SourceType> getSource() {
		return source;
	}

	@Override
	public IPort<SinkType> getSink() {
		return sink;
	}

}
