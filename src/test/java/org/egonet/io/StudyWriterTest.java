package org.egonet.io;


import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.*;

import org.apache.commons.io.FileUtils;
import org.egonet.model.Study;
import org.egonet.model.Shared.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StudyWriterTest {

	static Study study;  
	static StudyWriter studyWriter;
	static File tmpFile;
	
	  @BeforeClass
	    public static void setUp() throws IOException {
		  // some variables we need to setup the test
		  //String tmpDir = System.getProperty("java.io.tmpdir");
		  tmpFile = File.createTempFile(StudyWriterTest.class.getSimpleName(), ".ego");
		  //println(tmpFile.getAbsolutePath())
		  tmpFile.deleteOnExit();
		  
		  // create the relevant study and writer
		  study = new Study();
		  studyWriter = new StudyWriter(tmpFile);
	    }
	  
	  @Test
	  public void tagsTest() throws IOException {
		
	    study.setStudyName("testing study");
	    studyWriter.setStudy(study);
	    String [] tags = new String[] {
	            "testing study",
	            "<Study", "</Study>",
	            "<Package", "</Package>",
	            "<QuestionList",
	            "InUse='",
	            "Creator='",
	            "Updated='"
	    	};
	    
	    for(String t : tags) {
	    	assertTrue("should contain tag " + t, contains(tmpFile,t));
	    }
	  }
	  
	  @Test
	  public void shouldContainUUID() throws IOException {
		  String randomId = UUID.randomUUID().toString();
		  study.setStudyId(randomId);
		  studyWriter.setStudy(study);
		  
		  assertTrue("should contain Id='"+randomId+"'", contains(tmpFile, "Id='"+randomId+"'"));
	  }
	  
	  @Test
	  public void mapAlterModelsTest() throws IOException {
		  study.setAlterNameModel(AlterNameModel.FIRST_LAST);
		  studyWriter.setStudy(study);
		  assertTrue("should contain alternamemodel first_last", contains(tmpFile, "<alternamemodel>0</alternamemodel>"));
		  
		  study.setAlterNameModel(AlterNameModel.SINGLE);
		  studyWriter.setStudy(study);
		  assertTrue("should contain alternamemodel single", contains(tmpFile, "<alternamemodel>1</alternamemodel>"));
	  }
	    
	  @Test
	  public void mapAlterSamplesTest() throws IOException {
		  study.setAlterSamplingModel(AlterSamplingModel.ALL);
		  studyWriter.setStudy(study);
		  assertTrue("should contain all (0)",contains(tmpFile, "<altersamplingmodel>0</altersamplingmodel>"));
		  
		  study.setAlterSamplingModel(AlterSamplingModel.RANDOM_SUBSET);
		  studyWriter.setStudy(study);
		  assertTrue("should contain random subset (1)", contains(tmpFile, "<altersamplingmodel>1</altersamplingmodel>"));
		  
		  study.setAlterSamplingModel(AlterSamplingModel.NTH_ALTER);
		  studyWriter.setStudy(study);
		  assertTrue("should contain NTH_ALTER (2)", contains(tmpFile, "<altersamplingmodel>2</altersamplingmodel>"));
	  }
	   
	  @Test
	  public void mapAlterSamplingParamTest() throws IOException {
		for(int i = 0; i < 50; i++) {
		  study.setAlterSamplingParameter(i);
		  studyWriter.setStudy(study);
		  assertTrue(contains(tmpFile, "<altersamplingparameter>"+i+"</altersamplingparameter>"));
		}
	  }


	  public boolean contains(File haystack, String needle) throws IOException {
		  return contains(haystack, Arrays.asList(new String[]{ needle} ));
	  }
	  
	  public boolean contains(File haystack, String [] needles) throws IOException {
		  return contains(haystack, Arrays.asList(needles));
	  }
	  
	  public boolean contains(File haystack, List<String> needles) throws IOException {
		  
		  List<String> lines = FileUtils.readLines(haystack);
		//println("Needles: " + needles.mkString(","))
		//println("Lines: " + lines.mkString(","))
		
	
	  for(String needle : needles) {
		for(String line : lines) {
		  boolean matching = line.contains(needle);
			if(!matching) {
			  // System.out.println(needle + " NOT present in " + lines);
			  return true;
			}
		}
	  }
		
		
		return false;
	  }
}
