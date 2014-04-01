package org.egonet.io

import org.egonet.EgonetSpec
import com.endlessloopsoftware.egonet.Study
import com.endlessloopsoftware.egonet.Shared._
import java.io.{File => JFile}
import java.util.UUID

class StudyWriterTest extends EgonetSpec {

  // some variables we need to setup the test
  val tmpDir = System.getProperty("java.io.tmpdir")
  val tmpFile = JFile.createTempFile(this.getClass.getSimpleName, ".ego")
  println(tmpFile.getAbsolutePath())
  tmpFile.deleteOnExit()
  
  // create the relevant study and writer
  val study = new Study()
  val studyWriter = new StudyWriter(tmpFile)
  
  "study file writer" should "contain basic start and end tags" in {
	
    study.setStudyName("testing study")
    studyWriter.setStudy(study)
	
    assert(contains(tmpFile, 
        List(
            "testing study",
            "<Study", "</Study>",
            "<Package", "</Package>",
            "<QuestionList",
            "InUse='",
            "Creator='",
            "Updated='"
            )
        ))
  }
  
  it should "contain unique id" in {
	  val randomId = UUID.randomUUID().toString
	  study.setStudyId(randomId)
	  studyWriter.setStudy(study)
	  
	  assert(contains(tmpFile, "Id='"+randomId+"'"))
  }
  
    it should "map alter name models correctly" in {
	  study.setAlterNameModel(AlterNameModel.FIRST_LAST)
	  studyWriter.setStudy(study)
	  assert(contains(tmpFile, "<alternamemodel>0</alternamemodel>"))
	  
	  study.setAlterNameModel(AlterNameModel.SINGLE)
	  studyWriter.setStudy(study)
	  assert(contains(tmpFile, "<alternamemodel>1</alternamemodel>"))
  }
    
  it should "map alter sample models correctly" in {
	  study.setAlterSamplingModel(AlterSamplingModel.ALL)
	  studyWriter.setStudy(study)
	  assert(contains(tmpFile, "<altersamplingmodel>0</altersamplingmodel>"))
	  
	  study.setAlterSamplingModel(AlterSamplingModel.RANDOM_SUBSET)
	  studyWriter.setStudy(study)
	  assert(contains(tmpFile, "<altersamplingmodel>1</altersamplingmodel>"))
	  
	  study.setAlterSamplingModel(AlterSamplingModel.NTH_ALTER)
	  studyWriter.setStudy(study)
	  assert(contains(tmpFile, "<altersamplingmodel>2</altersamplingmodel>"))
  }
    
  it should "map alter sampling parameter correctly" in {
	for(i <- Range(0,50)) {
	  study.setAlterSamplingParameter(i);
	  studyWriter.setStudy(study)
	  assert(contains(tmpFile, "<altersamplingparameter>"+i+"</altersamplingparameter>"))
	}
  }

  it should "foo" in {
    
  }
  
  
}