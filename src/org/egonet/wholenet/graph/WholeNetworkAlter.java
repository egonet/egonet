package org.egonet.wholenet.graph;

import java.util.*;

import org.egonet.wholenet.gui.NameMapperFrame.NameMapping;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;

public class WholeNetworkAlter implements Comparable<WholeNetworkAlter> {

	private final Integer id;
	private final List<NameMapping> occurences;
	public WholeNetworkAlter(Integer id) {
		super();
		this.id = id;
		this.occurences = new ArrayList<NameMapping>();
	}
	
	public void addOccurence(NameMapping mapping) {
		occurences.add(mapping);
	}

	public Integer getId() {
		return id;
	}

	public List<NameMapping> getOccurences() {
		return occurences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WholeNetworkAlter))
			return false;
		WholeNetworkAlter other = (WholeNetworkAlter) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public int compareTo(WholeNetworkAlter o) {
		return id.compareTo(o.id);
	}
	
	public String toString() {
		if(occurences.size() <= 0)
			return id.toString();
		return occurences.get(0).toString() + " (" + id + ")";
	}
	
	private Map<String,HashMultiset<String>> rawAttributes = Maps.newTreeMap();
	
	public void addAttributes(Map<String,String> attributes) {
		for(String key : attributes.keySet()) {
			HashMultiset<String> attribute = rawAttributes.get(key);
			if(attribute == null) {
				attribute = HashMultiset.create();
				rawAttributes.put(key, attribute);
			}
			attribute.add(attributes.get(key));
		}
	}
	
	public Map<String,String> getAttributes() {
		Map<String,String> results = Maps.newTreeMap();
		for(String key : rawAttributes.keySet()) {
			String bestValue = "";
			Integer bestCount = 0;
			HashMultiset<String> valueCounts = rawAttributes.get(key);
			for(String value : valueCounts.elementSet()) {
				Integer count = valueCounts.count(value);
				if(count > bestCount || 
						(count.equals(bestCount) && value.length() > bestValue.length()))
				{
					bestValue = value;
					bestCount = count;
				}
			}
			results.put(key, bestValue);
		}
		return results;
	}
}
