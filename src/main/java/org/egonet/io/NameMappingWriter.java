package org.egonet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.egonet.gui.wholenet.NameMapperFrame;

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
				"ego_name","unused","alter_number","alter_name","group"});
		
		for(NameMapperFrame.NameMapping mapping : nameMappings) {
			String name = mapping.getInterview().getIntName();
			csv.writeNext(new String[]{
					name,
					"",
					mapping.getAlterNumber()+"",
					mapping.toString(), // alter name
					mapping.getGroup()+""
			});
		}
		
		csv.flush();
		fw.close();
	}
}
