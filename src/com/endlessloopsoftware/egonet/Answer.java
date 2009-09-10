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
import java.util.ArrayList;
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
        //logger.info("New answer object created with id="+Id+" and alters: " + Arrays.asList(alters));
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

    public String toString() {
    	return string == null ? getValue()+"" : string;
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