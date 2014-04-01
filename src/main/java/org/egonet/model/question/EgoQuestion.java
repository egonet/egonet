package org.egonet.model.question;

public class EgoQuestion extends Question {
	public EgoQuestion() {
		super();
	}
	
	@Override
	public String getNiceName() {
		return "Ego";
	}

	@Override
	public String getTitle() {
		return "Questions About You";
	}
}
