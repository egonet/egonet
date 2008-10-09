package org.egonet.tests.functional;


import static org.fest.swing.core.matcher.JButtonByTextMatcher.*;

import org.fest.swing.core.TypeMatcher;
import org.fest.swing.finder.JFileChooserFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.elsutils.layout.CardPanel;

public class DoInterview {

	private File studyFile;
	
	private FrameFixture window;

	@Before
	public void setUp() throws Exception
	{
		studyFile = new File("/home/martins/Desktop/egonet tmp/example chris's class/SNA Class 2008 Personal Network Study2.ego");
		
		window = new FrameFixture(EgoClient.getInstance().getFrame());
		window.show(); // shows the frame to test
	}

	@After
	public void tearDown()
	{
		window.cleanUp();
	}
	
	@Test
	public void fullInterview() throws Exception
	{
		window.button(withText("Select Study")).click();
		
		JFileChooserFixture fileChooser = JFileChooserFinder.findFileChooser().using(window.robot);
		fileChooser.fileNameTextBox().enterText(studyFile.getAbsolutePath());
		fileChooser.approve();
		
		// activate
		window.button(withText("Start Interview")).click();

		// first and last
		window.textBox("firstNameField").enterText(randomString(8));
		window.textBox("lastNameField").enterText(randomString(8));
		window.button(withText("Start Interview")).click();
		
		for(int i = 0; i < 1000; i++)
			handleQuestion();
		Thread.sleep(10*1000);

	}
	
	private final String ALTER_CARD = "ALTER";

	private final String TEXT_CARD = "TEXT";

	private final String NUMERICAL_CARD = "NUMERICAL";

	private final String RADIO_CARD = "RADIO";

	private final String MENU_CARD = "MENU";
	
	private void handleQuestion()
	{
		CardPanel cp = window.robot.finder().findByType(CardPanel.class);
		JPanelFixture cardFixture = new JPanelFixture(window.robot,cp);
		
		String card = cp.getVisibleCard();
		System.out.println("New visible card " + card + ", attempting to handle it!");
		
		if("".equals(card)) {
			throw new IllegalStateException("No question card is displayed");
		} else if(card.equals(ALTER_CARD)) {
			
		} else if(card.equals(TEXT_CARD)) {
			cardFixture.textBox().enterText(randomString(5));
		} else if(card.equals(NUMERICAL_CARD)) {
			cardFixture.textBox().enterText("1234");
		} else if(card.equals(RADIO_CARD)) {
			Collection<Component> comps = cardFixture.robot.finder().findAll(cp, new TypeMatcher(JRadioButton.class, false));
			ArrayList<Component> buttons = new ArrayList<Component>(comps);
			Collections.shuffle(buttons);
			JRadioButton btn = (JRadioButton)buttons.remove(0);
			btn.doClick();
		} else if(card.equals(MENU_CARD)) {
			JComboBox box = (JComboBox)cardFixture.robot.finder().find(cp, new TypeMatcher(JComboBox.class, false));
			JComboBoxFixture boxFix = new JComboBoxFixture(window.robot,box);

			String [] poss = boxFix.contents();
			
			int sel = (int)(Math.random()*poss.length);
			boxFix.selectItem(sel);
		}
		
		window.button(withText("Next Question")).click();
	}

	protected static String randomString()
	{
		return randomString(5);
	}

	protected static String randomString(int len)
	{
		StringBuilder sb = new StringBuilder();

		// create some randomness so we always make a brand spanking new string/name/loginname/etc
		for(int i = 0; i < len; i++)
			sb.append((char)('a' + (Math.random() * ('z' - 'a' + 0.5))));

		return sb.toString();
	}
}
