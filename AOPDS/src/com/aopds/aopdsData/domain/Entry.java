package com.aopds.aopdsData.domain;

import java.io.Serializable;

public class Entry implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String definition;
	private String example;
	private String information;

	public Entry() {
	};

	public Entry(long id, String definition, String example, String information) {
		super();
		this.id = id;
		this.definition = definition;
		this.example = example;
		this.information = information;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String toString() {

		return this.definition + '\n' + this.example + '\n' + this.information;
	}

}
