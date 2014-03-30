package org.egonet.model.question;

public class StudyQuestion extends Question {
	
	public StudyQuestion(String str) {
		super(str);
	}
	
	@Override
	public String getNiceName() {
		return "Study";
	}

	@Override
	public String getTitle() {
		return "Study questions";
	}
}
