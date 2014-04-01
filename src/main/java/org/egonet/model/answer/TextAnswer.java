package org.egonet.model.answer;

public class TextAnswer extends Answer {

	public TextAnswer() {
		super();
	}

	public TextAnswer(Long qId) {
		super(qId);
	}
	
	public TextAnswer(Long qId, int[] qAlters) {
		super(qId, qAlters);
	}

}
