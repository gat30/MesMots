package com.aopds.aopdsData.domain;

import java.io.Serializable;

public abstract class AbstractWord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String word;
	private String entry;
	private String phonetic;
	private Dictionary dictionary;
	
	public AbstractWord() { }
	
	public AbstractWord(long id, String word, String entry, String phonetic,Dictionary dictionary) {
		super();
		this.id = id;
		this.word = word;
		this.entry = entry;
		this.phonetic = phonetic;
	}

	@Override
	public String toString() {
		return "AbstractWord [id=" + id + ", word=" + word + ", entry=" + entry
				+ ", phonetic=" + phonetic + ", dictionary=" + dictionary + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String getPhonetic() {
		return phonetic;
	}

	public void setPhonetic(String phonetic) {
		this.phonetic = phonetic;
	}
	
	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	
	
}
