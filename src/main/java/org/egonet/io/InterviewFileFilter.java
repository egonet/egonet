package org.egonet.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ProgressMonitor;

import org.egonet.util.ExtensionFileFilter;

import com.endlessloopsoftware.egonet.Study;

/**
 * File filter to filter the interview files based on selected study The
 * file chooser displays only the interview files compatible with the
 * currently chosen study
 * 
 * @author sonam
 * 
 */
public class InterviewFileFilter extends ExtensionFileFilter {
	private final Map<File, Boolean> cacheResults = new HashMap<File, Boolean>();
	private final Study study;
	
	public InterviewFileFilter(Study study, String description, String extension) {
		super(description, extension);
		this.study = study;
	}

	public void cacheList(File currentDirectory, final ProgressMonitor progress) {
		int ct = 0;

		for (File ptr : currentDirectory.listFiles()) {
			final Integer tct = ++ct;
			cacheResults.put(ptr, ptr.canRead() && ptr.isFile()
					&& cacheAccept(ptr));
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progress.setProgress(tct);
				}
			});

		}
	}

	public boolean accept(File ptr) {
		if (cacheResults.containsKey(ptr)) {
			// cache hit
			return cacheResults.get(ptr);
		} else {
			// cache miss
			boolean accept = ptr.canRead() && cacheAccept(ptr);
			cacheResults.put(ptr, accept);
			return accept;
		}
	}

	public boolean cacheAccept(java.io.File f) {

		if (f.isDirectory())
			return true;

		boolean cantread = (!f.isFile()) || (!f.canRead());
		if (cantread)
			return !cantread;

		boolean accept = true;
		try {
			// compare study id of interview file with id of currently selected study
			InterviewReader sr = new InterviewReader(study, f);
			try { 
				sr.getInterview();
				accept = true;
			} catch (Throwable ex)
			{
				accept = false;
			}
		} catch (Throwable t) {
			accept = false;
		}
		return accept;
	}

	public String getDescription() {
		String str = "Interview files";
		return str;
	}
}