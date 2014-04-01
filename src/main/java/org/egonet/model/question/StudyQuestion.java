package org.egonet.model.question;

public class StudyQuestion extends Question {
	
	public StudyQuestion() {
		super();
	}
	
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
	
	public String toString() {
		if(getTitle() != null && !getTitle().equals(""))
			return "StudyQuestion("+getTitle()+")";
		return "StudyQuestion()";
	}
}
