package com.aopds.aopdsData.domain;

public class Headword extends AbstractWord {

	private static final long serialVersionUID = 1L;

	private boolean pronunciationExists;
	private Suggestion lastModification;
	private boolean hasDeletionBeenSuggested;

	public Headword() {
	}

	public Headword(long id, String word, String entry, String phonetic,
			Dictionary dictionary, Boolean pronunciationExists) {

		super(id, word, entry, phonetic, dictionary);
		this.pronunciationExists = pronunciationExists;
	}

	public boolean pronunciationExists() {
		return pronunciationExists;
	}

	public boolean isHasDeletionBeenSuggested() {
		return hasDeletionBeenSuggested;
	}

	public void setHasDeletionBeenSuggested(boolean hasDeletionBeenSuggested) {
		this.hasDeletionBeenSuggested = hasDeletionBeenSuggested;
	}

	public Suggestion getLastModification() {
		return lastModification;
	}

	public void setLastModification(Suggestion lastModification) {
		this.lastModification = lastModification;
	}

	@Override
	public String toString() {

		String lms = "";
		if (lastModification != null) {
			lms = lastModification.getId() + "";
		}

		return "Headword [ " + super.toString() + ",pronunciationExists="
				+ pronunciationExists + ", lastModification=" + lms
				+ ", hasDeletionBeenSuggested=" + hasDeletionBeenSuggested
				+ "]";
	}

}
