/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.endlessloopsoftware.ego.client.graph;

import org.apache.commons.collections15.Transformer;
import org.egonet.util.listbuilder.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

import java.util.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.geom.Point2D;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import javax.imageio.ImageIO;

public class GraphData {

	final private static Logger logger = LoggerFactory.getLogger(GraphData.class);
	
	private String[] completeAlterNameList;

	private Interview interview;

	private EgoClient egoClient;
	
	public int[][] adjacencyMatrix;

	/**
	 * List to contain list of all alter Questions in interview
	 */
	private static List<Question> interviewAlterQuestionList;

	/**
	 * List to contain list of all alter pair Questions in interview
	 */
	private List<Question> interviewAlterPairQuestionList;

	

	public GraphData(EgoClient egoClient) {
		this.egoClient=egoClient;
		
		int x = egoClient.getInterview().getStudy().getNumAlters();
		adjacencyMatrix = new int[x][x];
		
		interview = egoClient.getInterview();
		completeAlterNameList = egoClient.getInterview().getStats().alterList;
		adjacencyMatrix = interview.getStats().adjacencyMatrix;
		interviewAlterQuestionList = new ArrayList<Question>();
		interviewAlterPairQuestionList = new ArrayList<Question>();
	}

	/**
	 * Generates list of alter pairs about whom the graph question is.
	 * 
	 * @param graphQuestion
	 * @return Pair AlterPairList
	 */
	public List<Pair<Integer>> getAlterPairs(GraphQuestionSelectionPair graphQuestion) {
		List<Pair<Integer>> alterPairList = new ArrayList<Pair<Integer>>();
		List<Integer> alterNumbers = new ArrayList<Integer>();
		alterNumbers = getAlterNumbers(graphQuestion);
		for (int i = 0; i < alterNumbers.size(); i = i + 2) {
			Pair<Integer> alterPair = new Pair<Integer>(alterNumbers.get(i), alterNumbers.get(i + 1));
			alterPairList.add(alterPair);
			// logger.info(alterPair);
		}
		return alterPairList;
	}

	/**
	 * Populates alter-question list and alter-pair question list for the chosen
	 * interview
	 */
	private void populateQuestionLists() {
		Question question;

		interview.rewind();
		interviewAlterQuestionList.removeAll(interviewAlterQuestionList);
		interviewAlterPairQuestionList
				.removeAll(interviewAlterPairQuestionList);
		while (interview.hasNext()) {
			question = interview.next();
			if (question.questionType == Shared.QuestionType.ALTER) {
				interviewAlterQuestionList.add(question);
			} else if (question.questionType == Shared.QuestionType.ALTER_PAIR) {
				interviewAlterPairQuestionList.add(question);
			}
		}
	}

	/**
	 * Given a graph question, this method returns the list of alters for whom
	 * the question answer pair was provided.
	 * 
	 * @param graphQuestion
	 * @param category:
	 *            ALTER_QUESTION or ALTER_PAIR_QUESTION
	 * @return
	 */
	public List<Integer> getAlterNumbers(GraphQuestionSelectionPair graphQuestion) {

		List<Integer> alterNumbers = new ArrayList<Integer>();
		Long QID = graphQuestion.getQuestion().UniqueId;
		Iterator questionIterator;
		Question interviewQuestion;

		populateQuestionLists();

		if (graphQuestion.getCategory() == Shared.QuestionType.ALTER) {
			questionIterator = interviewAlterQuestionList.iterator();
		} else { // if category is ALTER_PAIR_QUESTION
			questionIterator = interviewAlterPairQuestionList.iterator();
		}

		while (questionIterator.hasNext()) {

			interviewQuestion = (Question) questionIterator.next();
			if (interviewQuestion.answer.answered == false) {
				logger.info(interviewQuestion.UniqueId + " | "
						+ interviewQuestion.text + " | Unanswered");
				continue;
			}

			if (interviewQuestion.UniqueId == QID) {
				if (interviewQuestion.answer.getValue() == graphQuestion.getSelection().getValue()) {
				    alterNumbers.addAll(interviewQuestion.answer.getAlters());
				}
			}
		}
		return alterNumbers;
	}

	public List<Integer> getAlterNumbers(Question selectedQuestion, Selection selection) {
		List<Integer> alterNumbers = new ArrayList<Integer>();
		Long QID = selectedQuestion.UniqueId;
		Iterator questionIterator;
		Question interviewQuestion;
		populateQuestionLists();
		questionIterator = interviewAlterQuestionList.iterator();
		while (questionIterator.hasNext()) {
			interviewQuestion = (Question) questionIterator.next();
			if (interviewQuestion.answer.answered == false) {
				logger.info(interviewQuestion.UniqueId + " | "
						+ interviewQuestion.text + " | Unanswered");
				continue;
			}
			if (interviewQuestion.UniqueId == QID) {
				if (interviewQuestion.answer.getValue() == selection.getValue()) {
				    alterNumbers.addAll(interviewQuestion.answer.getAlters());
				}
			}
		}
		return alterNumbers;
	}

	/**
	 * Given a graph question, this method returns the list of alters for whom
	 * the question answer pair was provided.
	 * 
	 * @param graphQuestion
	 * @param category:
	 *            ALTER_QUESTION or ALTER_PAIR_QUESTION
	 * @return
	 */
	public List<String> getAlterNames(GraphQuestionSelectionPair graphQuestion) {

		List<String> alterNames = new ArrayList<String>();
		Long QID = graphQuestion.getQuestion().UniqueId;
		Iterator questionIterator;
		Question interviewQuestion;

		populateQuestionLists();

		if (graphQuestion.getCategory() == Shared.QuestionType.ALTER) {
			questionIterator = interviewAlterQuestionList.iterator();
		} else { // if category is ALTER_PAIR_QUESTION
			questionIterator = interviewAlterPairQuestionList.iterator();
		}
		while (questionIterator.hasNext()) {

			interviewQuestion = (Question) questionIterator.next();
			if (interviewQuestion.answer.answered == false) {
				logger.info(interviewQuestion.UniqueId + " | "
						+ interviewQuestion.text + " | Unanswered");
				continue;
			}
			if (interviewQuestion.UniqueId == QID) {
			    if (interviewQuestion.answer.getValue() == graphQuestion.getSelection().getValue()) {
					
					for (int alterNum : interviewQuestion.answer.getAlters()) {
						alterNames.add(completeAlterNameList[alterNum]);
					}
				}
			}
		}

		return alterNames;
	}

	public void generateAdjacencyMatrix(Question question,
			Selection selection, boolean weighted) {
		Study study = egoClient.getInterview().getStudy();
		if (study.getUIType().equals(Shared.TRADITIONAL_QUESTIONS)) {
			for (Iterator it = egoClient.getInterview().getAnswerSubset(
					question.UniqueId).iterator(); it.hasNext();) {
				Answer a = (Answer) it.next();
				if (weighted) {
					if ((adjacencyMatrix[a.firstAlter()][a.secondAlter()] == 0)
							&& (adjacencyMatrix[a.secondAlter()][a.firstAlter()] == 0)
							&& (a.getValue() == selection.getValue())) {
						adjacencyMatrix[a.firstAlter()][a.secondAlter()] = (a.adjacent) ? selection
								.getValue()
								: 0;
						adjacencyMatrix[a.secondAlter()][a.firstAlter()] = (a.adjacent) ? selection
								.getValue()
								: 0;
						//logger.info("Updating weighted adjacency matrix");
					}
				} else {
					if ((adjacencyMatrix[a.firstAlter()][a.secondAlter()] == 0)
							&& (adjacencyMatrix[a.secondAlter()][a.firstAlter()] == 0)
							&& (a.getValue() == selection.getValue())) {
						adjacencyMatrix[a.firstAlter()][a.secondAlter()] = (a.adjacent) ? 1
								: 0;
						adjacencyMatrix[a.secondAlter()][a.firstAlter()] = (a.adjacent) ? 1
								: 0;
						logger.info("Updating weighted adjacency matrix");
					}

				}
			}
		}
	}

	public static void writeImage(File imageFile, String format) {
		int width = GraphRenderer.getVv().getWidth();
		int height = GraphRenderer.getVv().getHeight();

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		GraphRenderer.getVv().paint(graphics);

		graphics.dispose();

		try {
			ImageIO.write(bi, format, imageFile);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
	
	
	public static void writeCoordinates(File dataFile) throws IOException {
		VisualizationViewer<Vertex,Edge> vv = GraphRenderer.getVv();
		Layout<Vertex,Edge> layout = vv.getGraphLayout();
		Graph g = layout.getGraph();

		FileWriter fw = new FileWriter(dataFile);
		
		@SuppressWarnings("unchecked")
		Collection<Vertex> verts = g.getVertices();
		for(Vertex v : verts)
		{
			
			String nodeLabel = GraphRenderer.getGraphSettings().getNodeLabel(v);
			
			Point2D pt = layout.transform(v);
			String line = ("\""+nodeLabel + "\"," + pt.getX() + "," + pt.getY() + "\n");
			System.out.print(line);
			fw.write(line);
		}
		
		fw.close();
	}

	public static Transformer<Vertex,String> getStringLabeller(Graph graph) {
		return new ToStringLabeller<Vertex>();
	}

	/*public static Rectangle calculateGraphRect() {
		double x;
		double y;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;

		double labeloffset = OFFSET * 2.0d;

		Point2D location;

		Layout layout = GraphRenderer.getVv().getGraphLayout();
		MutableTransformer layoutTransformer = GraphRenderer.getVv()
				.getLayoutTransformer();
		Graph graph = layout.getGraph();
		StringLabeller labeller = getStringLabeller(GraphRenderer.getGraph());
		
		@SuppressWarnings({"unchecked"})
		Set<Vertex> vertices = graph.getVertices();
		Vertex mostRightVertex = vertices.iterator().next();

		// Find the upper most left in lower most right vertices
		for (Vertex v : vertices) {
			// Transform from graph layout coordinates to graphics2d coordinates
			location = layoutTransformer.transform(layout.getLocation(v));
			x = location.getX();
			y = location.getY();
			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
				mostRightVertex = v;
			}
			if (y < minY) {
				minY = y;
			}
			if (y > maxY) {
				maxY = y;
			}
		}

		// Calculate the width of the right most vetrex label
		String label = labeller.getLabel(mostRightVertex);
		if (label == null) {
			label = "";
		}
		if (GraphRenderer.getVv().getRenderer() instanceof PluggableRenderer) {
			VertexFontFunction vertexFontFunction = ((PluggableRenderer) GraphRenderer
					.getVv().getRenderer()).getVertexFontFunction();
			Font font = vertexFontFunction.getFont(mostRightVertex);
			Rectangle2D labelBounds = font.getStringBounds(label,
					((Graphics2D) GraphRenderer.getVv().getGraphics())
							.getFontRenderContext());
			labeloffset += labelBounds.getWidth();
		} else {
			Font font = GraphRenderer.getVv().getFont();
			Rectangle2D labelBounds = font.getStringBounds(label,
					((Graphics2D) GraphRenderer.getVv().getGraphics())
							.getFontRenderContext());
			labeloffset += labelBounds.getWidth();
		}

		final Dimension actual = new Dimension((int) (maxX - minX)
				+ (int) labeloffset, (int) (maxY - minY) + (int) OFFSET * 2);
		return new Rectangle(new Point((int) minX, (int) minY), actual);
	}*/

	public String[] getCompleteAlterNameList() {
		return completeAlterNameList;
	}

	public Interview getInterview() {
		return interview;
	}

	public List<Question> getInterviewAlterPairQuestionList() {
		return interviewAlterPairQuestionList;
	}

	public List<Question> getInterviewAlterQuestionList() {
		return interviewAlterQuestionList;
	}

	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public void setAdjacencyMatrix(int[][] adj) {
		adjacencyMatrix = adj;
	}

}
