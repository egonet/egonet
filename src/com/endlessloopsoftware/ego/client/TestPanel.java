package com.endlessloopsoftware.ego.client;

import com.cim.dlgedit.loader.DialogResource;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class TestPanel
extends JPanel{
	public TestPanel(){
	try{
		JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/client/localSelect.gui_xml");
		jbInit();
		this.setLayout(new GridLayout());
		this.add(panel);
	}
	catch(Exception e){}
	}

	private void jbInit() throws Exception{
	
	}
}