package org.egonet.io.wholenet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egonet.gui.wholenet.NameMapperFrame.NameMapping;

import net.sf.functionalj.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import au.com.bytecode.opencsv.CSVReader;

public class NameMappingReader {
	
	public static class Mapping {
		
		// "ego_first","ego_last","alter_number","alter_name","group"
		
		public String egoName,alterName;
		public Integer alterNumber, group;
		
		public Mapping(String[] csvColumns) {
			this(csvColumns[0],csvColumns[1],csvColumns[2],csvColumns[3]);
		}
		
		public Mapping(String egoName, String alterNumber, String alterName, String group) 
		{
			this.egoName = egoName;
			this.alterNumber = Integer.parseInt(alterNumber);
			this.alterName = alterName;
			this.group = Integer.parseInt(group);
		}
		
		public Mapping(NameMapping mapping) {
			String name = mapping.getInterview().getIntName();
			this.egoName = name;
			
			this.alterNumber = mapping.getAlterNumber();
			this.alterName = mapping.toString();
			this.group = mapping.getGroup();
		}
		
		public Key key() {
			return new Key(egoName,alterNumber);
		}
	}
	
	public static class Key extends Pair<String,Integer> {
		public Key(String name, Integer alterNumber) {
			super(name,alterNumber);
		}
		public String toString() {
			return getFirst()+" "+getSecond();
		}
	}
	
	public static class Mappings {
		private List<Mapping> mappings;
		private Map<Key,Mapping> keyToMapping;
		private Multimap<Integer,Mapping> groupToMappings;
		
		public Mappings() {
			mappings = Lists.newArrayList();
			keyToMapping = Maps.newHashMap();
			groupToMappings = ArrayListMultimap.create();
		}
		
		public void add(Mapping mapping) {
			mappings.add(mapping);
			keyToMapping.put(mapping.key(), mapping);
			groupToMappings.put(mapping.group, mapping);
		}
		
		private void reIndex() {
			keyToMapping = Maps.newHashMap();
			groupToMappings = ArrayListMultimap.create();
			for(Mapping mapping : mappings) {
				keyToMapping.put(mapping.key(), mapping);
				groupToMappings.put(mapping.group, mapping);
			}
		}
		
		public Set<Integer> groups() {
			return groupToMappings.keySet();
		}
		
		public Collection<Mapping> group(Integer groupIndex) {
			return groupToMappings.get(groupIndex);
		}
		
		public void breakGroup(Integer groupIndex) {
			Collection<Mapping> group = group(groupIndex);
			
			List<Integer> unusedIndices = Lists.newArrayList();
			for(Integer i = 1; unusedIndices.size() < group.size(); i++) {
				if(i.equals(groupIndex) || ! groups().contains(i)) {
					unusedIndices.add(i);
				}
			}
			
			for(Mapping mapping : group) {
				mapping.group = unusedIndices.remove(0);
			}
			
			reIndex();
		}
		
		public void combineGroups(Integer... groupIndexes) {
			if(groupIndexes.length > 1) {
				for(Integer groupIndex : groupIndexes) {
					for(Mapping mapping : group(groupIndex)) {
						mapping.group = groupIndexes[0];
					}
				}
				reIndex();
			}
		}
		
		public Set<Key> keys() {
			return keyToMapping.keySet();
		}
		
		public Mapping get(Key key) {
			return keyToMapping.get(key);
		}
	}
	
	private Mappings mappings;
	
	public NameMappingReader(File mappingFile) throws IOException {
		FileReader fileReader = new FileReader(mappingFile);
		CSVReader csv = new CSVReader(fileReader);

		csv.readNext(); // skip header
		
		mappings = new Mappings();
		
		String[] nextLine;
		while((nextLine = csv.readNext()) != null) {
			mappings.add(new Mapping(nextLine));
		}
		
		fileReader.close();
	}
	
	public void applyTo(List<NameMapping> nameMappings) {
		Mappings oldMappings = new Mappings();
		for(NameMapping nameMapping : nameMappings) {
			oldMappings.add(new Mapping(nameMapping));
		}
		
		// Remove incorrect links
		for(Integer oldGroup : new ArrayList<Integer>(oldMappings.groups())) {
			Boolean groupShownToBeInvalid = false;
			for(Mapping oldMapping1 : oldMappings.group(oldGroup)) {
				Mapping mapping1 = mappings.get(oldMapping1.key());
				for(Mapping oldMapping2 : oldMappings.group(oldGroup)) {
					Mapping mapping2 = mappings.get(oldMapping2.key());
					if(mapping1 != null && mapping2 != null && 
							! mapping1.group.equals(mapping2.group))
					{
						groupShownToBeInvalid = true;
					}
				}
			}
			if(groupShownToBeInvalid) {
				oldMappings.breakGroup(oldGroup);
			}
		}
		
		// Add new links
		for(Integer group : mappings.groups()) {
			Set<Integer> matchingOldGroups = Sets.newTreeSet();
			for(Mapping mapping : mappings.group(group)) {
				Mapping oldMapping = oldMappings.get(mapping.key());
				if(oldMapping != null) {
					matchingOldGroups.add(oldMapping.group);
				}
			}
			oldMappings.combineGroups(matchingOldGroups.toArray(new Integer[]{}));
		}
		
		// Apply calculated groupings to original name mappings.
		for(NameMapping nameMapping : nameMappings) {
			nameMapping.setGroup(
					oldMappings.get(
							new Mapping(nameMapping).key())
					.group);
		}
	}
}
