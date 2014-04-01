package org.egonet

import scala.io.Source
import org.scalatest.FlatSpec
import java.io.{File=>JFile}

class EgonetSpec extends FlatSpec {

  
  def contains(haystack: JFile, needle: String) : Boolean = contains(haystack, List(needle))
  
  def contains(haystack: JFile, needles: List[String]) : Boolean = {
	val src = Source.fromFile(haystack)
	var lines = src.getLines.toList
	
	//println("Needles: " + needles.mkString(","))
	//println("Lines: " + lines.mkString(","))
	
	needles.foreach(needle => {
		var matching = lines.exists(_.contains(needle))
		if(!matching) {
		  //println(needle + " NOT present in " + lines.mkString(","))
		  return false
		}
	})
	
	
	true
  }
}