package com.endlessloopsoftware.ego.client;

import com.cim.dlgedit.loader.DialogResource;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JButton;

public class TestPanel
extends JPanel{
	private final GridBagLayout gblayout2 = new GridBagLayout();
	private JButton JB1;
	public TestPanel(){
	try{
		JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/client/localSelect.gui_xml");
		JB1 = (JButton)DialogResource.getComponentByName(panel,"Test Button");
		jbInit();
		this.setLayout(new GridLayout());
		this.add(panel);
	}
	catch(Exception e){}
	}

	private void jbInit() throws Exception{
	
	}
}