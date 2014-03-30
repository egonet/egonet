package org.egonet.model.question;

public class AlterPairQuestion extends Question {

	@Override
	public String getNiceName() {
		return "Alter Pair";
	}

	@Override
	public String getTitle() {
		return "<html><p>Questions About <nobr><b>$$1</b></nobr> and <nobr><b>$$2</b></nobr></p></html>";
	}
	
}
