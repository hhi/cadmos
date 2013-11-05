package edu.tum.cs.cadmos.lib.model;

public interface IChannel<SourceType extends SinkType, SinkType> {

	IPort<SourceType> getSource();

	IPort<SinkType> getSink();

}
