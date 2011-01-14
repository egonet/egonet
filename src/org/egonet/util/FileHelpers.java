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
package org.egonet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;


/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: FileHelpers.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */
public class FileHelpers
{
    /****
     * Select a directory in which to store project related files
     * @param title Title to place in file chooser dialog
     * @param filename default filename
     * @param filetype type of file for error messages
     * @param suffix file suffix for filter
     * @param startDir default directory
     * @param parent parent frame for error messages
     * @param enclosingDir true iff we should surround new file with a new folder of same name
     * @return created file
     * @throws IOException
     */
    public static File newFile(String title, String filename, String filetype, String suffix, File startDir,
                               JFrame parent, boolean enclosingDir)
                        throws IOException
    {
        JFileChooser jNewStudyChooser = new JFileChooser();
        File         newFile       = null;
        File         dirFile;
        String       projectPath   = null;
        String       projectName   = null;
        FileFilter   newFileFilter = new ExtensionFileFilter(title, suffix);

        jNewStudyChooser.addChoosableFileFilter(newFileFilter);
        jNewStudyChooser.setDialogTitle(title);
        jNewStudyChooser.setSelectedFile(new File(filename));

        if (startDir == null)
        {
            jNewStudyChooser.setCurrentDirectory(new File("./"));
        }
        else
        {
            jNewStudyChooser.setCurrentDirectory(startDir);
        }

        try
        {
            if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showSaveDialog(parent))
            {
                projectPath = jNewStudyChooser.getSelectedFile().getParent();
                projectName = jNewStudyChooser.getSelectedFile().getName();

                if (enclosingDir)
                {
                    if (projectName.indexOf(".") != -1)
                    {
                        projectName = projectName.substring(0, projectName.indexOf("."));
                    }

                    try
                    {
                        String folder = projectPath.substring(projectPath.lastIndexOf(File.separator) + 1);

                        if (!folder.equals(projectName))
                        {
                            dirFile = new File(projectPath, projectName);
                            dirFile.mkdir();
                            projectPath = dirFile.getPath();
                        }
                    }
                    catch (SecurityException e)
                    {
                        JOptionPane.showMessageDialog(parent, "Unable to create directory.", "New File Error",
                                                      JOptionPane.ERROR_MESSAGE);
                        throw new IOException(e);
                    }
                }

                newFile = new File(projectPath, projectName);
                newFile = ((ExtensionFileFilter) newFileFilter).getCorrectFileName(newFile);

                if (!newFile.createNewFile())
                {
                    int confirm = JOptionPane.showConfirmDialog(parent,
                                                                "<HTML><h2>" + filetype
                                                                + " File already exists at this location.</h2>"
                                                                + "<p>Shall I overwrite it?</p></html>",
                                                                "Overwrite " + filetype + " File",
                                                                JOptionPane.OK_CANCEL_OPTION);

                    if (confirm != JOptionPane.OK_OPTION)
                    {
                        throw new IOException("Will not overwrite file " + newFile.getName());
                    }
                }
               
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(parent, "Unable to create " + filetype + " file.");
            newFile = null;
        }

        return newFile;
    }

    /****
     * Remove commas for printing strings to csv files
     * @param s string to format
     * @return formatted string
     */
    public static String formatForCSV(String s)
    {
    	if(s == null) {
    		return null;
    	}
    	return s.replaceAll("[^a-zA-Z_\\-0-9]+", "_");
    }

    /**
     *
     *
     * @param filename param
     *
     * @return returns
     *
     * @throws IOException throws
     * @throws FileNotFoundException throws
     */
    public static String readFile(File f)
                       throws IOException, FileNotFoundException
    {
        long       n    = f.length();
        char[]     cbuf = new char[(int) n];
        FileReader fr   = new FileReader(f);
        fr.read(cbuf);
        fr.close();

        return (new String(cbuf));
    }
}
