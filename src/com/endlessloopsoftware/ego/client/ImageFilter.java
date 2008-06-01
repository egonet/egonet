package com.endlessloopsoftware.ego.client;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;

public class ImageFilter extends FileFilter {
    
    Set<String> extensionNames;
    public ImageFilter()
    {
        String [] formats = ImageIO.getWriterFormatNames();
        extensionNames = new HashSet<String>(formats.length);
        
        for(String fmt : formats)
            extensionNames.add(fmt.toLowerCase());
    }

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        return (extension != null && extensionNames.contains(extension.toLowerCase()));
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    //The description of this filter
    public String getDescription() {
        return "Image formats " + extensionNames.toString();
    }
}

