package org.egonet.model.question;

public class AlterQuestion extends Question {
	public AlterQuestion() {
		super();
	}
	
	@Override
	public String getNiceName() {
		return "Alter";
	}

	@Override
	public String getTitle() {
		return "<html><p>Questions About <nobr><b>$$1</b></nobr></p></html>";
	}
}
