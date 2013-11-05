package edu.tum.cs.cadmos.core.types;

import edu.tum.cs.cadmos.commons.core.IListSet;

public class RecordType extends AbstractType {

	private final IListSet<RecordMember> members;

	public RecordType(IListSet<RecordMember> members) {
		super(EType.RECORD);
		this.members = members;
	}

	public IListSet<RecordMember> getMembers() {
		return members;
	}

}
