package com.aopds.aopdsData.domain;

import java.io.Serializable;

public class Dictionary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int code;
	private Language languageTo;
	private Language languageFrom;
	private String name;
	private int version;

	public Dictionary() {
	}

	public Dictionary(int code, Language languageTo, Language languageFrom,
			String name, int version) {
		super();
		this.code = code;
		this.languageTo = languageTo;
		this.languageFrom = languageFrom;
		this.name = name;
		this.version = version;
	}

	@Override
	public String toString() {
		return "Dictionary [code=" + code + ", languageTo=" + languageTo
				+ ", languageFrom=" + languageFrom + ", name=" + name
				+ ", version=" + version + "]";
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Language getLanguageTo() {
		return languageTo;
	}

	public void setLanguageTo(Language languageTo) {
		this.languageTo = languageTo;
	}

	public Language getLanguageFrom() {
		return languageFrom;
	}

	public void setLanguageFrom(Language languageFrom) {
		this.languageFrom = languageFrom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
