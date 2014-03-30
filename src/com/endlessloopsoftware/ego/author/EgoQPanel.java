/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.endlessloopsoftware.ego.author;
import javax.swing.JPanel;

import org.egonet.model.question.Question;



public abstract class EgoQPanel extends JPanel
{
    protected final Class<? extends Question> questionType;
	abstract public void fillPanel();
	abstract public void clearPanel();
	public EgoQPanel(Class<? extends Question> questionType)
	{
	    this.questionType = questionType;
	}
}