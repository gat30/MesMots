package com.aopds.aopdsData.domain;

import java.io.Serializable;

public class Language implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int code;
	private String abreviation;
	private String name;
	private String nameInEnglish;

	public Language(int code, String abreviation, String name,
			String nameInEnglish) {
		super();
		this.code = code;
		this.abreviation = abreviation;
		this.name = name;
		this.nameInEnglish = nameInEnglish;
	}

	public Language(int code) {
		super();
		this.code = code;
	}

	public Language() {
	}

	@Override
	public String toString() {
		return "Language [code=" + code + ", abreviation=" + abreviation
				+ ", name=" + name + ", nameInEnglish=" + nameInEnglish + "]";
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getAbreviation() {
		return abreviation;
	}

	public void setAbreviation(String abreviation) {
		this.abreviation = abreviation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameInEnglish() {
		return nameInEnglish;
	}

	public void setNameInEnglish(String nameInEnglish) {
		this.nameInEnglish = nameInEnglish;
	}

}
