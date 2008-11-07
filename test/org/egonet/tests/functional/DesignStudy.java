package org.egonet.tests.functional;

import static org.fest.swing.core.matcher.JButtonByTextMatcher.*;

import java.awt.Component;
import java.io.File;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import junit.framework.Assert;

import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.NameMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.core.TypeMatcher;
import org.fest.swing.core.matcher.DialogByTitleMatcher;
import org.fest.swing.finder.JFileChooserFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.endlessloopsoftware.ego.author.AuthoringQuestionPanel;
import com.endlessloopsoftware.ego.author.EgoNet;
import com.endlessloopsoftware.ego.author.PromptPanel;
import com.endlessloopsoftware.egonet.Shared.QuestionType;

public class DesignStudy {
	
	public final static String studyName = "Sample Egonet Study for Test";
	public final static String location = System.getProperty("user.home") + File.separator+ "Desktop";
	
	private FrameFixture window;

	@Before
	public void setUp() throws Exception
	{
		window = new FrameFixture(EgoNet.getInstance().getFrame());
		window.show(); // shows the frame to test
	}

	@After
	public void tearDown()
	{
		window.cleanUp();
	}

	@Test
	public void fullStudyDesign() throws Exception {
	    Assert.assertTrue("Location must exist: " + location, new File(location).exists());
	        
		window.menuItemWithPath("File", "New Study").click();
		JFileChooserFixture fileChooser = JFileChooserFinder.findFileChooser().using(window.robot);
		fileChooser.fileNameTextBox().enterText(location + "/" + studyName);
		fileChooser.approve();
		
		window.textBox("study_num_alters_field").deleteText().enterText("15");
		window.radioButton("btnAlterModelRandomSubset").click();
		window.textBox("txtAlterModelRandomSubset").deleteText().enterText("5");
		
		JTabbedPaneFixture tabs = window.tabbedPane();

		// ego questions
		tabs.selectTab("Ego");
		window.robot.waitForIdle(); // there's some funky creation going on here
		AuthoringQuestionPanel egoPanel0 = findQPanel(window.robot.finder(), QuestionType.EGO);
		JPanelFixture egoPanel = new JPanelFixture(window.robot, egoPanel0);
		
		createPlainQuestion(egoPanel, AnswerType.CATEGORICAL);
		createPlainQuestion(egoPanel, AnswerType.NUMERICAL);
		createPlainQuestion(egoPanel, AnswerType.TEXT);

		// alter prompt
		tabs.selectTab("Alter Prompt");
		PromptPanel alterpromptPanel0 = findPromptPanel(window.robot.finder());
		JPanelFixture altrerpromptPanel = new JPanelFixture(window.robot, alterpromptPanel0);
		
		createQuestionTitleQuestionCitation(altrerpromptPanel);
		
		// alter
		tabs.selectTab("Alter");
		AuthoringQuestionPanel alterPanel0 = findQPanel(window.robot.finder(), QuestionType.ALTER);
		JPanelFixture alterPanel = new JPanelFixture(window.robot, alterPanel0);
		
		createPlainQuestion(alterPanel, AnswerType.CATEGORICAL);
		createPlainQuestion(alterPanel, AnswerType.NUMERICAL);
		createPlainQuestion(alterPanel, AnswerType.TEXT);
		
		
		// alter pair
		tabs.selectTab("Alter Pair");
		window.robot.waitForIdle(); // there's some funky creation going on here
		AuthoringQuestionPanel alterPairPanel0 = findQPanel(window.robot.finder(), QuestionType.ALTER_PAIR);
		JPanelFixture alterPairPanel = new JPanelFixture(window.robot, alterPairPanel0);
		
		createPlainQuestion(alterPairPanel, AnswerType.CATEGORICAL, true);
		
		
		window.menuItemWithPath("File", "Quit").click();
		DialogFixture dialog = WindowFinder.findDialog(DialogByTitleMatcher.withTitle("Save Study Changes")).withTimeout(5000).using(window.robot);
		dialog.button(withText("Yes")).click();
	}

	enum AnswerType { CATEGORICAL, NUMERICAL, TEXT};
	private static void createPlainQuestion(JPanelFixture panel, AnswerType type, boolean alterPair)
	{
		createQuestionTitleQuestionCitation(panel, alterPair);
		JComboBoxFixture answerType = new JComboBoxFixture(panel.robot, (JComboBox)panel.robot.finder().find(panel.component(), new NameMatcher("question_answer_type_menu")));
		answerType.selectItem(type.ordinal());

		if(!alterPair && type.equals(AnswerType.CATEGORICAL))
		{
			panel.button(withText("Selections")).click();
			fillInCategoricalQuestions(panel.robot);
		} else if(alterPair) {
			panel.button(withText("Selections")).click();
			
			 DialogFixture dialog = WindowFinder.findDialog("Category Options").withTimeout(10000).using(panel.robot);
			JComboBoxFixture cmbF = dialog.comboBox();
			cmbF.selectItem(2);
			
			dialog.list().selectItem(0);
			dialog.button(withText("Mark selected item adjacent")).click();
			
			dialog.button(withText("OK")).click();

			
			
		}
	}
	
	private static void createPlainQuestion(JPanelFixture egoPanel, AnswerType type)
	{
		createPlainQuestion(egoPanel, type, false);
	}
	
	private static void createQuestionTitleQuestionCitation(JPanelFixture fix)
	{
		createQuestionTitleQuestionCitation(fix, false);
	}
	
	private static void createQuestionTitleQuestionCitation(JPanelFixture fix, boolean alterPair)
	{
		fix.button(withText("New")).click();
		
		NameMatcher titleFieldMatcher = new NameMatcher("question_title_field");
		NameMatcher questionFieldMatcher = new NameMatcher("question_question_field");
		NameMatcher citationFieldMatcher = new NameMatcher("question_citation_field");
		
		JTextComponentFixture titleText = new JTextComponentFixture(fix.robot, (JTextField)fix.robot.finder().find(fix.component(), titleFieldMatcher));
		titleText.enterText("Question about " + randomString());
		
		JTextComponentFixture questionText = new JTextComponentFixture(fix.robot, (JTextArea)fix.robot.finder().find(fix.component(), questionFieldMatcher));
		if(alterPair)
			questionText.enterText("question - does $$1 " + randomString()+" with $$2?");
		else
			questionText.enterText("question - does " + randomString()+"?");
		
		JTextComponentFixture citationText = new JTextComponentFixture(fix.robot, (JTextArea)fix.robot.finder().find(fix.component(), citationFieldMatcher));
		citationText.enterText(randomString(10));
	}
	
	private static void fillInCategoricalQuestions(Robot robot)
	{
		 DialogFixture dialog = WindowFinder.findDialog("Category Options").withTimeout(10000).using(robot);
		JComboBoxFixture cmbF = dialog.comboBox();
		String [] poss = cmbF.contents();
		
		int sel = (int)(Math.random()*poss.length);
		cmbF.selectItem(sel);
		dialog.button(withText("OK")).click();
	}
	
	private static AuthoringQuestionPanel findQPanel(ComponentFinder finder, QuestionType type)
	{
		Collection<Component> questionpanels = finder.findAll(new TypeMatcher(AuthoringQuestionPanel.class, false));
		for(Component c : questionpanels)
		{
			if(c instanceof AuthoringQuestionPanel && ((AuthoringQuestionPanel)c).getQuestionType() == type)
			{
				return (AuthoringQuestionPanel)c;
			}
		}
		
		throw new RuntimeException("Couldn't find type " + type + " using finder " + finder);
	}
	
	private static PromptPanel findPromptPanel(ComponentFinder finder)
	{
		Collection<Component> questionpanels = finder.findAll(new TypeMatcher(PromptPanel.class, false));
		for(Component c : questionpanels)
		{
			if(c instanceof PromptPanel)
			{
				return (PromptPanel)c;
			}
		}
		
		throw new RuntimeException("Couldn't find type " + "prompt panel" + " using finder " + finder);
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
