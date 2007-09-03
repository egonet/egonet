/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import javax.swing.JPasswordField;
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
import com.endlessloopsoftware.elsutils.security.SymmetricKeyEncryption;

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
   private JPasswordField         serverPassword;
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
		JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/client/ServerInterviewChooser.gui_xml");

		// Attach beans to fields.
		loadInterviews = (JButton) DialogResource.getComponentByName(panel, 			 "LoadInterviews");
		select 			= (JButton) DialogResource.getComponentByName(panel, 			 "Select");
		serverPassword = (JPasswordField) DialogResource.getComponentByName(panel,  "serverPassword");
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
		
      final String epassword = SymmetricKeyEncryption.encrypt(new String(serverPassword.getPassword()));
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
                        
                        //System.out.println("Loading " + interviewSelection.getIdentifier());
                        
                        InterviewIdentifier id = (InterviewIdentifier) interviewSelection.getIdentifier();
                        InterviewDataValue interviewData;
                        try
                        {
                           interviewData = interviewSession.fetchUserInterviewData(selection.toString(),
                                                                                                      id.getFirstName(),
                                                                                                      id.getLastName(),
                                                                                                      epassword);
                           
                           //System.out.println("Creating Interview " + interviewSelection.getIdentifier());
                           Interview interview = new Interview(study, interviewData);

                           //System.out.println("Generating Statistics " + interviewSelection.getIdentifier());
                           Question q = study.getFirstStatableQuestion();
                           Statistics statistics = interview.generateStatistics(q);
                           
                           //System.out.println("Most Central Degree Alter " + statistics.mostCentralBetweenAlterName);
                           
                           //System.out.println("To StatRecord " + interviewSelection.getIdentifier());
                           
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
				catch (Exception e)
				{
               JOptionPane.showMessageDialog(this, "Unable to load study.\n" + e.getMessage(), "Server Error",
                                                JOptionPane.ERROR_MESSAGE);
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