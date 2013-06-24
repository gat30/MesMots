package com.aopds.error;

public class AopdsFormError {
	/*
	 * Class of objects that contain - a boolean saying if the form was well
	 * filled - a string saying what is wrong if something is wrong
	 */

	private boolean errorcode;
	private String errordisplay = "";

	public void setErrorcode(boolean errorcode) {
		this.errorcode = errorcode;
	}

	public boolean getErrorcode() {
		return errorcode;
	}

	// adds the parameter String to the error display
	public void addToErrordisplay(String s) {
		this.errordisplay = this.errordisplay + s;
	}

	public void setErrordisplay(String errordisplay) {
		this.errordisplay = errordisplay;
	}

	public String getErrordisplay() {
		return errordisplay;
	}

}
