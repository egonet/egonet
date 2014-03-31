package org.egonet.tests.graph

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.util.Map
import java.util.Random
import java.util.Set
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import org.egonet.graph.KPlexes
import org.egonet.tests.EgonetSpec

class KPlexesTest extends EgonetSpec {

  protected var rand = new Random();
  private var kp = new KPlexes[Integer]();

  private def graphWithThreeCliques(): Map[Integer, Set[Integer]] = {

    val res: Map[Integer, Set[Integer]] = Maps.newHashMap();

    val one: Set[Integer] = Sets.newHashSet(2, 3, 8);
    val two: Set[Integer] = Sets.newHashSet(1, 3, 4);
    val three: Set[Integer] = Sets.newHashSet(1, 2, 4, 5);
    val four: Set[Integer] = Sets.newHashSet(2, 3);
    val five: Set[Integer] = Sets.newHashSet(3, 6, 7);
    val six: Set[Integer] = Sets.newHashSet(5, 7);
    val seven: Set[Integer] = Sets.newHashSet(5, 6);
    val eight: Set[Integer] = Sets.newHashSet(1, 9);
    val nine: Set[Integer] = Sets.newHashSet(8);

    res.put(1, one);
    res.put(2, two);
    res.put(3, three);
    res.put(4, four);
    res.put(5, five);
    res.put(6, six);
    res.put(7, seven);
    res.put(8, eight);
    res.put(9, nine);
    res;
  }

  private def intSet(members: Integer*): Set[Integer] = {
    Sets.newHashSet(members.asJava);
  }

  private def matchMapToList(answers: List[Int], calculations: Map[Integer, Integer]): Boolean = {
    answers.zipWithIndex.foreach {
      case (answer, i) => {
        var calculated = calculations.get(i + 1)
        if (!(answer == calculated)) { return false; }
      }
    }

    true
  }

  "testConnectionsByNode" should "answer equals calculated" in {
    var calculations: Map[Integer, Integer] = kp.connectionsByNode(graphWithThreeCliques());
    var answers = List(3, 3, 4, 2, 3, 2, 2, 2, 1);

    assert(matchMapToList(answers, calculations))
  }

  "testConnectednessByNode" should "answer has expected connectednes" in {
    var calculations: Map[Integer, Integer] = kp.connectednessByNode(graphWithThreeCliques());
    var answers = List(2, 2, 3, 2, 2, 2, 2, 1, 1);

    assert(matchMapToList(answers, calculations))
  }

  "testConnectionsWithinSubgroup" should "have expected subgroup values" in {
    var calculations: Map[Integer, Integer] = kp.connectionsWithinSubgroup(graphWithThreeCliques(), intSet(1, 2, 3, 4));
    var answers = List(2, 3, 3, 2, 1, 0, 0, 1, 0);

    assert(matchMapToList(answers, calculations))
  }

  "testCriticalNodes" should "be critical in a 1-plex" in {
    var calculations: Set[Integer] = kp.criticalNodesInKPlex(
      graphWithThreeCliques(),
      intSet(7),
      1)

    var answers = intSet(7);

    assert(answers == calculations)
  }

  "testSubgraphBoundingFinalKPlex" should "boundsKPlex" in {
    var finalKPlex = intSet(5, 6, 7);
    var subgraph =
      kp.subgraphBoundingFinalKPlex(graphWithThreeCliques(), intSet(7), 1, 3);
    var boundsKPlex: Set[Integer] = subgraph.keySet();
    assert(boundsKPlex.containsAll(finalKPlex));
  }

  "testGrowClique" should "Expect {7} to grow into 1-plex of {5,6,7}" in {
    var clique = kp.growKPlex(graphWithThreeCliques(), intSet(7), 1, 3);
    assert(intSet(5, 6, 7) == clique);
  }

  "testCliqueSearch" should "Find a clique of size three, because all three cliques have size three." in {
    var clique = kp.findLargeKPlex(graphWithThreeCliques(), 1);
    assert(3 == clique.size());
  }

  "testKPlexSearch" should "find a 2-plex of size 4" in {
    assert(intSet(1, 2, 3, 4) == kp.findLargeKPlex(graphWithThreeCliques(), 2));
  }

}