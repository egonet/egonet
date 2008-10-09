package org.egonet.tests.functional;

import java.io.File;

import javax.swing.JFileChooser;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;

import com.endlessloopsoftware.ego.author.EgoNet;

public class DoInterview {

	private File studyFile;
	
	private FrameFixture window;

	@Before
	public void setUp() throws Exception
	{
		JFileChooser chooser = new JFileChooser();
		int choice = chooser.showOpenDialog(null);
		if(choice == JFileChooser.CANCEL_OPTION)
			return;
		
		studyFile = chooser.getSelectedFile();
		System.out.println(studyFile.getName());
		
		window = new FrameFixture(EgoNet.getInstance().getFrame());
		window.show(); // shows the frame to test
	}

	@After
	public void tearDown()
	{
		window.cleanUp();
	}
	
}
