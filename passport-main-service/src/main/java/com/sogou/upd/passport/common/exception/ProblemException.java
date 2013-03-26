package com.sogou.upd.passport.common.exception;

import com.google.common.base.Strings;

public class ProblemException extends Exception {

	private String error;
	private String description;

	public ProblemException(String error) {
		this(error, "");
	}

	public ProblemException(String error, String description) {
		super(error + " " + description);
		this.description = description;
		this.error = error;
	}

	public static ProblemException error(String error) {
		return new ProblemException(error);
	}

	public static ProblemException error(String error, String description) {
		return new ProblemException(error, description);
	}

	public ProblemException description(String description) {
		this.description = description;
		return this;
	}

	public String getError() {
		return error;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String getMessage() {
		StringBuffer b = new StringBuffer();
        if (!Strings.isNullOrEmpty(error)) {
			b.append("error:").append(error);
		}
		if (!Strings.isNullOrEmpty(description)) {
			b.append(", ").append("description:").append(description);
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return "ProblemException{" + "error='" + error + '\'' + ", description='" + description + '\'' + '}';
	}

}
