/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: ServerInterviewChooser.java,v 1.1 2005/08/02 19:36:00 samag Exp $
 */
package com.endlessloopsoftware.ego.client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.ejb.FinderException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ProgressMonitor;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.cim.dlgedit.loader.DialogResource;
import com.cim.util.swing.DlgUtils;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBRemote;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBRemoteHome;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBUtil;
import com.endlessloopsoftware.egonet.interfaces.StudySBRemote;
import com.endlessloopsoftware.egonet.interfaces.StudySBRemoteHome;
import com.endlessloopsoftware.egonet.interfaces.StudySBUtil;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.util.InterviewIdentifier;
import com.endlessloopsoftware.egonet.util.StudyAndInterviewTransfer;
import com.endlessloopsoftware.egonet.util.StudyDataValue;
import com.endlessloopsoftware.elsutils.SwingWorker;

public class ServerInterviewChooser 
	extends JPanel 
	implements ActionListener, TreeSelectionListener
{
   // Types
   private final int              DECORATION = 0;
   private final int              STUDY      = 1;
   private final int              INTERVIEW  = 2;

   // Declare beans.
   private JButton                loadInterviews;
   private JButton                select;
   private JTextField             serverURL;
   private JTree                  interviewTree;

   // Tree handling variables
   private DefaultMutableTreeNode rootNode   = new DefaultMutableTreeNode();
   private DefaultTreeModel       treeModel  = new DefaultTreeModel(rootNode);

   // Study Server Communication
   StudySBRemote                  studySession;
   InterviewSBRemote              interviewSession;

	// Constructor.
	public ServerInterviewChooser()
	{
		// Load up the dialog contents.
		//JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/client/ServerInterviewChooser.gui_xml");
		java.io.InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/endlessloopsoftware/ego/client/ServerInterviewChooser.gui_xml");
		JPanel panel = DialogResource.load(is);
		
		// Attach beans to fields.
		loadInterviews = (JButton) DialogResource.getComponentByName(panel, 			 "LoadInterviews");
		select 			= (JButton) DialogResource.getComponentByName(panel, 			 "Select");
		serverURL 		= (JTextField) DialogResource.getComponentByName(panel, 		 "serverURL");
		interviewTree 	= (JTree) DialogResource.getComponentByName(panel, 			    "interviewTree");

		// Clear out Tree to start
		interviewTree.addTreeSelectionListener(this);
		interviewTree.setModel(treeModel);
		rootNode.setUserObject(new TreeSelection("No Server Selected", DECORATION));
		interviewTree.setEditable(false);
		interviewTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		interviewTree.setShowsRootHandles(false);		
		treeModel.reload();
		
		select.setEnabled(false);
		
		// Add dialog as ActionListener.
      loadInterviews.setText("Load Studies");
		loadInterviews.addActionListener(this);
		select.addActionListener(this);

		this.setLayout(new GridLayout(1, 1));
		this.add(panel);
	}

	/**
	 * Invoke the onXxx() action handlers.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		DlgUtils.invokeHandler(this, e);
	}

	public void onLoadInterviews()
	{
		fillTree();
	}

	public void onSelect()
	{
		Properties prop = new Properties();
		prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		prop.setProperty("java.naming.provider.url", serverURL.getText()+":1099");
		
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) interviewTree.getLastSelectedPathComponent();
		
		if (node == null)
		{
			return;
		}
		
        final String epassword = "215-121-242-47-99-238-5-61-133-183-0-216-187-250-253-30-115-177-254-142-161-83-108-56"; 
        	//SymmetricKeyEncryption.encrypt(new String(serverPassword.getPassword()));
		final TreeSelection selection = (TreeSelection) node.getUserObject();
		
		int type = selection.getType();
		
		switch (type)
		{
			case STUDY:
			{
            try
            {
               final StatRecord[] statRecords = new StatRecord[node.getChildCount()];
               Shared.setWaitCursor(EgoClient.frame, true);
               StudyDataValue data = studySession.fetchDataByStudyName(selection.toString(), epassword);
               final Study study = new Study(data);
               
               final ProgressMonitor progressMonitor = new ProgressMonitor(EgoClient.frame,
                                                                              "Calculating Statistics", "", 0,
                                                                              node.getChildCount());
               final SwingWorker worker = new SwingWorker() 
               {
                  public Object construct()
                  {
                     for (int i = 0; i < node.getChildCount(); ++i)
                     {
                        DefaultMutableTreeNode interviewNode = (DefaultMutableTreeNode) node.getChildAt(i);
                        TreeSelection interviewSelection = (TreeSelection) interviewNode.getUserObject();
                        
                        System.out.println("Loading " + interviewSelection.getIdentifier());
                        
                        InterviewIdentifier id = (InterviewIdentifier) interviewSelection.getIdentifier();
                        InterviewDataValue interviewData;
                        try
                        {
                           interviewData = interviewSession.fetchUserInterviewData(selection.toString(),
                                                                                                      id.getFirstName(),
                                                                                                      id.getLastName(),
                                                                                                      epassword);
                           
                           System.out.println("Creating Interview " + interviewSelection.getIdentifier());
                           Interview interview = new Interview(study, interviewData);

                           System.out.println("Generating Statistics " + interviewSelection.getIdentifier());
                           Question q = study.getFirstStatableQuestion();
                           Statistics statistics = interview.generateStatistics(q);
                           
                           System.out.println("Most Central Degree Alter " + statistics.mostCentralBetweenAlterName);
                           
                           System.out.println("To StatRecord " + interviewSelection.getIdentifier());
                           
                           statRecords[i] = new StatRecord(statistics);
                        }
                        catch (Exception e)
                        {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                        }
                        
                        //System.out.println("Complete " + interviewSelection.getIdentifier());
                        progressMonitor.setProgress(i);
                     }
                     
                     return statRecords;
                  }
                  
                  public void finished()
                  {
                     Shared.setWaitCursor(EgoClient.frame, false);
                     progressMonitor.close();
                     SummaryPanel.gotoPanel(statRecords);
                  }
               };
               
               progressMonitor.setProgress(0);
               progressMonitor.setMillisToDecideToPopup(0);
               progressMonitor.setMillisToPopup(0);
               
               worker.start();

 				}
				catch (FinderException e)
				{
					e.printStackTrace();
				}
				catch(java.lang.reflect.UndeclaredThrowableException e)
				{
				   e.printStackTrace();
					System.out.println(e.getUndeclaredThrowable());
				}
				catch (Exception e)
				{
               JOptionPane.showMessageDialog(this, "Unable to load study.\n" + e.getMessage(), "Server Error",
                                                JOptionPane.ERROR_MESSAGE);
               e.printStackTrace();
				}
            finally
            {
               Shared.setWaitCursor(EgoClient.frame, false);
            }
			}
			break;
			
			case INTERVIEW:
			{
				DefaultMutableTreeNode 	studyNode 	= (DefaultMutableTreeNode) node.getParent();
				TreeSelection				studySelect	= (TreeSelection) studyNode.getUserObject();

				try
				{
               Shared.setWaitCursor(EgoClient.frame, true);
 
					StudyDataValue studyData = studySession.fetchDataByStudyName(studySelect.toString(), epassword);
               
               InterviewIdentifier id = (InterviewIdentifier) selection.getIdentifier();
               InterviewDataValue interviewData = interviewSession.fetchUserInterviewData(studySelect.toString(),
                                                                                          id.getFirstName(),
                                                                                          id.getLastName(),
                                                                                          epassword);

               EgoClient.uiPath = EgoClient.VIEW_INTERVIEW;

               EgoClient.storage.setInterviewFile(null);
               EgoClient.interview  = null;

               Study study          = new Study(studyData);
               EgoClient.study      = study;

//               System.out.println(EgoClient.study.getQuestions().size());
//               System.out.println(EgoClient.study.getQuestions().dump());
//               
               EgoClient.interview  = new Interview(study, interviewData);

               if (EgoClient.interview != null)
               {
                  ViewInterviewPanel.gotoPanel();
               }
            }
				catch (FinderException e)
				{
               JOptionPane.showMessageDialog(this, "Unable to load interview.", "Server Error",
                                             JOptionPane.ERROR_MESSAGE);
				}
				catch (Exception e)
				{
               JOptionPane.showMessageDialog(this, "Unable to load interview.\n" + e.getMessage(), "Server Error",
                                             JOptionPane.ERROR_MESSAGE);
               e.printStackTrace();
				}
            finally
            {
               Shared.setWaitCursor(EgoClient.frame, false);
            }

			}
			
			default:
				break;
		}
	}
	
	/**
	 * @author admin
	 *
	 * To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Generation - Code and Comments
	 */
	public void fillTree()
	{
		try
		{
			Shared.setWaitCursor(EgoClient.frame, true);
			
			Properties prop = new Properties();
			prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			prop.setProperty("java.naming.provider.url", serverURL.getText()+":1099");
						
			StudySBRemoteHome       studySBHome 		    = StudySBUtil.getHome(prop);
			                        studySession		    = studySBHome.create();
			InterviewSBRemoteHome	   interviewSBHome	    = InterviewSBUtil.getHome(prop);
											interviewSession   = interviewSBHome.create();
			Set							   studyNames         = studySession.getStudyAndInterviewNames();
			
			//System.out.println("Found " + studyNames.size() + " studies.");
         
         rootNode.removeAllChildren();
			
			for (Iterator it = studyNames.iterator(); it.hasNext();)
			{
				StudyAndInterviewTransfer xfer = (StudyAndInterviewTransfer) it.next();
				
				DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(new TreeSelection(xfer.studyName, STUDY));
				
				for (Iterator ints = xfer.interviewIdentifiers.iterator(); ints.hasNext();)
				{
               InterviewIdentifier id = (InterviewIdentifier) ints.next();
					studyNode.add(new DefaultMutableTreeNode(new TreeSelection(id, INTERVIEW)));
				}
				
				rootNode.add(studyNode);
			}
			
			if (studyNames.size() > 0)	rootNode.setUserObject(new TreeSelection("Studies", DECORATION));
			else								rootNode.setUserObject(new TreeSelection("No Studies Found", DECORATION));
			
			treeModel.reload();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
         Shared.setWaitCursor(EgoClient.frame, false);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) interviewTree.getLastSelectedPathComponent();
		
		if (node == null)
		{
			select.setEnabled(false);
			return;
		}
		
		TreeSelection selection = (TreeSelection) node.getUserObject();
      
      if (select != null)
      {
         if (selection.getType() == STUDY)
         {
            select.setText("View Study Summary");
            select.setEnabled(true);
         }
         else if (selection.getType() == INTERVIEW)
         {
            select.setText("View Interview");
            select.setEnabled(true);
         }
         else
         {
            select.setEnabled(false);
         }
      }
      else
      {
         select.setEnabled(false);
      }
		select.setEnabled((selection != null) && ((selection.getType() == STUDY) || (selection.getType() == INTERVIEW)));
	}
	
	private class TreeSelection
	{
		private Object _id;
		private int		_type;
		
		public TreeSelection(Object id, int type)
		{
			_id     = id;
			_type   = type;
		}
		
		public String toString()
		{
			return _id.toString();
		}
		
		public int	getType()
		{
			return _type;
		}
      
      public Object getIdentifier()
      {
         return _id;
      }
	}

}

/**
 * $Log: ServerInterviewChooser.java,v $
 * Revision 1.1  2005/08/02 19:36:00  samag
 * Initial checkin
 *
 * Revision 1.11  2004/04/11 15:19:28  admin
 * Using password to access server
 *
 * Remote study summary in seperate thread with progress monitor
 *
 * Revision 1.10  2004/04/08 15:06:07  admin
 * EgoClient now creates study summaries from Server
 * EgoAuthor now sets active study on server
 *
 * Revision 1.9  2004/04/07 00:08:31  admin
 * updating manifests, jar creation. Removing author specific objects from
 * client specific references
 *
 * Revision 1.8  2004/04/06 15:43:26  admin
 * Moving matrix generation into interview to support Applet Linking UI.
 * An interview generated with applet linking will have no meaningful alter pair
 * questions. The adjacency matrix will be returned in an Athenian manner from
 * the server.
 *
 * Revision 1.7  2004/04/06 14:56:02  admin
 * Work to integrate with Applet Linking UI
 *
 * Revision 1.6  2004/03/29 00:35:09  admin
 * Downloading Interviews
 * Fixing some bugs creating Interviews from Data Objects
 *
 * Revision 1.5  2004/03/28 17:31:32  admin
 * More error handling when uploading study to server
 * Server URL selection dialog for upload
 *
 * Revision 1.4  2004/03/23 14:58:48  admin
 * Update UI
 * Study creation now occurs in instantiators
 *
 * Revision 1.3  2004/03/22 20:09:17  admin
 * Includes interviews in selection box
 *
 * Revision 1.2  2004/03/22 00:00:34  admin
 * Extended text entry area
 * Started work on importing studies from server
 *
 * Revision 1.1  2004/03/20 18:13:59  admin
 * Adding remote selection dialog
 *
 */