package org.egonet.model.question;

public class EgoQuestion extends Question {
	@Override
	public String getNiceName() {
		return "Ego";
	}

	@Override
	public String getTitle() {
		return "Questions About You";
	}
}
