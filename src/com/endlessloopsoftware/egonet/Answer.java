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
package com.endlessloopsoftware.egonet;
import electric.xml.Element;
import electric.xml.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.text.*;

public class Answer implements Cloneable {
    /**
     * Unique ID for every question
     */
    public Long questionId;

    /**
     * Represents the alter or alter pair that this answer is about. It may be a
     * single alter for alter questions, or two alters for an alter pair
     * question.
     */
    private List<Integer> alters;

    public boolean answered;

    public boolean adjacent;

    private int value;

    private int index;

    public String string;

    public String timestamp;

    public static final int NO_ANSWER = -1;

    public static final int ALL_ADJACENT = -2;

    public Answer(Long Id) {
        this(Id, null);
    }

    public Answer(Long Id, int[] alters) {
        //System.out.println("New answer object created with id="+Id+" and alters: " + Arrays.asList(alters));
        questionId = Id;
        answered = false;
        adjacent = false;
        setValue(-1);
        string = "";
        timestamp = DateFormat.getDateInstance().format(new Date());

        if (alters == null) {
            this.alters = new ArrayList<Integer>();
        } else {
            this.alters = new ArrayList<Integer>(alters.length);
            for(Integer a : alters)
                this.alters.add(a);
        }
    }
    
    public Integer firstAlter()
    {
        return alters.get(0);
    }
    
    public Integer secondAlter()
    {
        return alters.get(1);
    }

    public boolean hasTwoAlters()
    {
        return alters.size() > 1;
    }
    
    public boolean hasAtLeastOneAlter()
    {
        return alters.size() > 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return (super.clone());
    }

    /***************************************************************************
     * Add answer information to an xml structure for output to a file
     * 
     * @param e
     *            XML Element, parent of answer tree
     */
    public void writeAnswer(Element e, Question question, Interview interview) {
        Element answerElement = new Element("Answer");

        
        answerElement.addComment("Answer element: " + toString());
        if(question != null)
            answerElement.addComment("Corresp question: " + question.getString());
        
        
        answerElement.addElement("QuestionId").setLong(questionId.longValue());
        answerElement.addElement("Answered").setBoolean(answered);

        if (answered) {
            if(questionId.equals(1205185478364L)) System.err.println("Printed a value into the XML file that was zero: " + getString());
            answerElement.addElement("Value").setInt(getValue());
            answerElement.addElement("Index").setInt(getIndex());
            answerElement.addElement("Adjacent").setBoolean(adjacent);
            answerElement.addElement("String").setText(string);
            answerElement.addElement("TimeStamp").setText(timestamp);
        }

        if (alters.size() > 0) {
            String[] alterList = interview.getAlterList();
            Element altersElement = answerElement.addElement("Alters");
            for (int i = 0; i < alters.size(); i++) {
                int alterNumber = alters.get(i);
                
                // alter may not have a name yet
                String alterName = alterList.length > alterNumber ? alterList[alterNumber] : "Undefined alter name (#"+alterNumber+")";
                
                Element thisAlterElement = altersElement.addElement("Index");
                thisAlterElement.setInt(alterNumber);
                
                // handy extra attribute
                thisAlterElement.setAttribute("name", alterName);
            }
        }

        e.addElement(answerElement);
    }

    /***************************************************************************
     * Read alter list from an xml tree
     * 
     * @param interview
     *            Interview to read answers into
     * @param e
     *            XML Element, parent of alter list
     */
    public static Answer readAnswer(Study study, Element e) {
        int qAlters[] = null;
        Long qId = new Long(e.getLong("QuestionId"));
        Question q = (Question) study.getQuestions().getQuestion(qId);
        Element alterElem = e.getElement("Alters");

        if (alterElem != null) {
            Elements alterElems = alterElem.getElements("Index");
            qAlters = new int[alterElems.size()];

            for (int i = 0; i < alterElems.size(); i++) {
                qAlters[i] = alterElems.next().getInt();
            }
        }

        Answer r = new Answer(qId, qAlters);

        r.answered = e.getBoolean("Answered");

        if (r.answered) {
            r.string = e.getString("String");
            r.setValue(e.getInt("Value"));
            r.setIndex(e.getInt("Index"));
            r.adjacent = q.selectionAdjacent(r.getValue());
        } else {
            r.string = null;
        }

        System.out.println("Read answer: " + r.getString());
        return r;
    }

    public String toString() {
        String str = null;
        if (string == null) {
            Integer val = getValue();
            str = val.toString();
            ;
        } else {
            str = string;
        }
        return str;
    }

    public String getString() {
        String str = "";
        str = "questionId=" + questionId + ", answered=" + answered + ", string=" + string + ", index="+getIndex()+", value=" + getValue();
        return str;

    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public List<Integer> getAlters()
    {
        return Collections.unmodifiableList(alters);
    }
}