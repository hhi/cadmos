package edu.tum.cs.cadmos.core.types;

import java.util.Collections;
import java.util.List;

public class RecordType extends AbstractType {

	private final List<RecordMember> members;

	public RecordType(List<RecordMember> members) {
		super(EType.RECORD);
		this.members = Collections.unmodifiableList(members);
	}

	public List<RecordMember> getMembers() {
		return members;
	}

}
