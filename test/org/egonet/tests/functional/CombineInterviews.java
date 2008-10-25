package org.egonet.tests.functional;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.EgoStore;
import com.endlessloopsoftware.egonet.Study;

public class CombineInterviews extends JPanel{
	private JLabel titleLabel = new JLabel("Combine Interviews from Study: ");
	private JLabel studyNameLabel = new JLabel(" ");
	private JButton selectStudyButton = new JButton("Select Study");
	private JButton combineInterviewsButton = new JButton("Combine Interviews");
	
	private static EgoClient egoClient;
	
	public CombineInterviews(){
		super(new GridLayout(3, 2));
       
        titleLabel.setHorizontalTextPosition(JLabel.CENTER);
        add(titleLabel);

        studyNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        studyNameLabel.setHorizontalTextPosition(JLabel.CENTER);
		studyNameLabel.setText(" ");
		add(studyNameLabel);
	
		selectStudyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSelectStudy(e);}});
		add(selectStudyButton);
        
		combineInterviewsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doCombineInterviews(e);}});
		add(combineInterviewsButton);

	}
	
	private static void createComponents(){
		
		JFrame frame = new JFrame("Combine Interview Test Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        frame.add(new CombineInterviews());
        
		//Display the window.
        frame.pack();
        frame.setVisible(true);
	}
	
	private void doSelectStudy(ActionEvent e){
		/* Clear out old data */
		egoClient.setStudy(new Study());
		egoClient.setStorage(new EgoStore(egoClient));
		egoClient.setInterview(null);
		
		/* Read new study */
		egoClient.getStorage().selectStudy();
		egoClient.getStorage().readPackage();
		studyNameLabel.setText(egoClient.getStudy().getStudyName());
		
	}
	
	private void doCombineInterviews(ActionEvent e){
		egoClient.getStorage().setInterviewFile(null);
		egoClient.setInterview(null);
		egoClient.getStorage().combineInterviews();
	}
	
	public static void main(String[] args) throws Exception
	{
		egoClient = EgoClient.getInstance();
		new CombineInterviews();
		createComponents();
	}
}
