package com.aopds.aopdsData.domain;

import java.util.Date;

import com.aopds.aopdsData.AopdsDatabase;

public class Suggestion extends AbstractWord {
	private static final String ACTION_TYPE_ADDITION = AopdsDatabase.ACTION_TYPE_ADDITION;
	private static final String ACTION_TYPE_MODIFICATION = AopdsDatabase.ACTION_TYPE_MODIFICATION;
	private static final String ACTION_TYPE_DELETION = AopdsDatabase.ACTION_TYPE_DELETION;

	public static final String ACTION_TYPE_FIELD_WORD = "w";
	public static final String ACTION_TYPE_FIELD_ENTRY = "e";
	public static final String ACTION_TYPE_FIELD_PRONUNCIATION = "p";
	public static final String ACTION_TYPE_FIELD_PHONETIC = "f";
	public static final String ACTION_TYPE_FIELD_ALL = "a";

	public static final String SYNCHRO_STATUS_UNSYNCHRONIZED = "u";
	public static final String SYNCHRO_STATUS_WAITING = "p";
	public static final String SYNCHRO_STATUS_ACCEPTED = "a";
	public static final String SYNCHRO_STATUS_REFUSED = "d";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String actionType;
	private String synchroStatus;
	private Headword headword;
	private Boolean pronunciationRecorded;
	private Date creationDate;
	private int dictionaryVersion;

	public Suggestion() {
	}

	public Suggestion(long id, String word, String entry, String phonetic,
			Dictionary dictionary, Boolean pronunciationRecorded) {

		super(id, word, entry, phonetic, dictionary);
		this.pronunciationRecorded = pronunciationRecorded;
	}

	@Override
	public String toString() {

		String hs = "";
		if (headword != null) {
			hs = headword.getId() + "";
		}

		return "Suggestion [ " + super.toString() + ", actionType="
				+ actionType + ", synchroStatus=" + synchroStatus
				+ ", headword=" + hs + ", pronunciationRecorded="
				+ pronunciationRecorded + "]";
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getDictionaryVersion() {
		return dictionaryVersion;
	}

	public void setDictionaryVersion(int dictionaryVersion) {
		this.dictionaryVersion = dictionaryVersion;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setIsModifyActionType(String fields) {
		actionType = ACTION_TYPE_MODIFICATION + fields;
	}

	public void mergeModifyActionType(String fields) {

		int i = 0;
		String current;

		for (i = 0; i < fields.length(); i++) {
			current = "" + fields.charAt(i);

			if (!actionType.contains(current)) {
				actionType += current;
			}
		}
	}

	public void setIsAddActionType() {
		actionType = ACTION_TYPE_ADDITION;
	}

	public void setIsDeleteActionType() {
		actionType = ACTION_TYPE_DELETION;
	}

	public Boolean isModifyActionType() {
		return actionType.startsWith(ACTION_TYPE_MODIFICATION);
	}

	public Boolean isDeleteActionType() {
		return actionType.startsWith(ACTION_TYPE_DELETION);
	}

	public Boolean isAddActionType() {
		return actionType.startsWith(ACTION_TYPE_ADDITION);
	}

	public String getSynchroStatus() {
		return synchroStatus;
	}

	public void setSynchroStatus(String synchroStatus) {
		this.synchroStatus = synchroStatus;
	}

	public Headword getHeadword() {
		return headword;
	}

	public void setHeadword(Headword headword) {
		this.headword = headword;
	}

	public Boolean getPronunciationRecorded() {
		return pronunciationRecorded;
	}

	public void setPronunciationRecorded(Boolean pronunciationRecorded) {
		this.pronunciationRecorded = pronunciationRecorded;
	}

}
