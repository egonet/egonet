package org.egonet.wholenet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.egonet.wholenet.gui.NameMapperFrame;

import au.com.bytecode.opencsv.CSVWriter;

public class NameMappingWriter {
	
	private final Iterable<NameMapperFrame.NameMapping> nameMappings;
	
	public NameMappingWriter(Iterable<NameMapperFrame.NameMapping> nameMappings) {
		this.nameMappings = nameMappings;
	}
	
	public void writeToFile(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		CSVWriter csv = new CSVWriter(fw);
		
		csv.writeNext(new String[]{
				"ego_first","ego_last","alter_number","alter_name","group"});
		
		for(NameMapperFrame.NameMapping mapping : nameMappings) {
			String[] name = mapping.getInterview().getName();
			String first = name.length > 0 ? name[0] : "";
			String last = name.length > 1 ? name[1] : "";
			csv.writeNext(new String[]{
					first,
					last,
					mapping.getAlterNumber()+"",
					mapping.toString(), // alter name
					mapping.getGroup()+""
			});
		}
		
		csv.flush();
		fw.close();
	}
}
