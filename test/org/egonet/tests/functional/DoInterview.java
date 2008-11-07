package org.egonet.tests.functional;


import static org.fest.swing.core.matcher.JButtonByTextMatcher.*;

import org.egonet.util.CardPanel;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.NameMatcher;
import org.fest.swing.core.TypeMatcher;
import org.fest.swing.core.matcher.DialogByTitleMatcher;
import org.fest.swing.finder.JFileChooserFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.text.JTextComponent;

import com.endlessloopsoftware.ego.client.EgoClient;

public class DoInterview {

	private File studyFile;
	
	private FrameFixture window;

	private static final String studyName = DesignStudy.studyName;
	private static final String location = DesignStudy.location + File.separator + studyName + File.separator + studyName + ".ego";
	
	@Before
	public void setUp() throws Exception
	{
		studyFile = new File(location);
		
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
		
		while(true)
		{
			handleQuestion();
			System.out.println("*** Clicking next question!");
			
			if(window.button("questionButtonNext").component().getText().equals("Next Question"))
			{
				window.button("questionButtonNext").click();
				continue;
			}
			
			if(window.button("questionButtonNext").component().getText().equals("Study Complete"))
			{
				window.button("questionButtonNext").click();
				break;
			}
				

		}
		
		DialogFixture dialog = WindowFinder.findDialog(DialogByTitleMatcher.withTitle("Interview Complete")).withTimeout(5000).using(window.robot);
		dialog.button(withText("OK")).click();

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
		
		window.robot.waitForIdle();
		String card = cp.getVisibleCard();
		System.out.println("*** New visible card " + card + ", attempting to handle it!");
		
		if("".equals(card)) {
			throw new IllegalStateException("No question card is displayed");
		} else if(card.equals(ALTER_CARD)) {
			while(!window.button(withText("Next Question")).component().isEnabled())
			{
				JTextComponentFixture firstName = new JTextComponentFixture(
						window.robot,
						(JTextComponent)
						cardFixture.robot.finder().find(cp, 
								new NameMatcher("firstName")
						)
						);
				
				firstName.enterText(randomString(5));
				
				JTextComponentFixture lastName = new JTextComponentFixture(
						window.robot,
						(JTextComponent)
						cardFixture.robot.finder().find(cp, 
								new NameMatcher("lastName")
						)
						);
				
				lastName.enterText(randomString(5));
				
				// hit enter
				KeyPressInfo keyPressInfo = KeyPressInfo.keyCode(KeyEvent.VK_ENTER);
				lastName.pressAndReleaseKey(keyPressInfo);
			}
		} else if(card.equals(TEXT_CARD)) {
			cardFixture.textBox().enterText(randomString(5));
		} else if(card.equals(NUMERICAL_CARD)) {
			cardFixture.textBox().enterText("1234");
		} else if(card.equals(RADIO_CARD)) {
		    Collection<Component> comps = cardFixture.robot.finder().findAll(cp, new TypeMatcher(JRadioButton.class, true));
		    comps = cardFixture.robot.finder().findAll(cp, new TypeMatcher(JRadioButton.class, true));
			
			Assert.assertTrue("radio cards must have at least 1 radio button", comps.size() > 0);
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
