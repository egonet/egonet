package com.endlessloopsoftware.ego.client.graph;

import com.endlessloopsoftware.ego.*;
import org.egonet.util.listbuilder.Selection;
import com.endlessloopsoftware.ego.client.Interview;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.egonet.Shared;
import java.util.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.awt.Dimension;
import java.awt.Rectangle;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

import javax.imageio.ImageIO;

import edu.uci.ics.jung.utils.Pair;

public class GraphData {

	private static final double OFFSET = 25.0d;

	private String[] completeAlterNameList;

	private Interview interview;

	public static int[][] adjacencyMatrix = new int[EgoClient.interview
			.getStudy().getNumAlters()][EgoClient.interview.getStudy()
			.getNumAlters()];

	/**
	 * List to contain list of all alter Questions in interview
	 */
	private static List<Question> interviewAlterQuestionList;

	/**
	 * List to contain list of all alter pair Questions in interview
	 */
	private List<Question> interviewAlterPairQuestionList;

	public GraphData() {
		interview = EgoClient.interview;
		completeAlterNameList = EgoClient.interview.getStats().alterList;
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
	public List<Pair> getAlterPairs(GraphQuestion graphQuestion) {
		List<Pair> alterPairList = new ArrayList<Pair>();
		List<Integer> alterNumbers = new ArrayList<Integer>();
		alterNumbers = getAlterNumbers(graphQuestion);
		for (int i = 0; i < alterNumbers.size(); i = i + 2) {
			Pair alterPair = new Pair(alterNumbers.get(i), alterNumbers
					.get(i + 1));
			alterPairList.add(alterPair);
			// System.out.println(alterPair);
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
			if (question.questionType == Question.ALTER_QUESTION) {
				interviewAlterQuestionList.add(question);
			} else if (question.questionType == Question.ALTER_PAIR_QUESTION) {
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
	public List<Integer> getAlterNumbers(GraphQuestion graphQuestion) {

		List<Integer> alterNumbers = new ArrayList<Integer>();
		Long QID = graphQuestion.getQuestion().UniqueId;
		Iterator questionIterator;
		Question interviewQuestion;

		populateQuestionLists();

		if (graphQuestion.getCategory() == Question.ALTER_QUESTION) {
			questionIterator = interviewAlterQuestionList.iterator();
		} else { // if category is ALTER_PAIR_QUESTION
			questionIterator = interviewAlterPairQuestionList.iterator();
		}

		while (questionIterator.hasNext()) {

			interviewQuestion = (Question) questionIterator.next();
			if (interviewQuestion.answer.answered == false) {
				System.out.println(interviewQuestion.UniqueId + " | "
						+ interviewQuestion.text + " | Unanswered");
				continue;
			}

			if (interviewQuestion.UniqueId == QID) {
				if ((interviewQuestion.answer.string.trim())
						.equals(graphQuestion.getSelection().getString().trim())) {
					int[] alterNumArray = interviewQuestion.answer.getAlters();
					for (int alterNum : alterNumArray) {
						alterNumbers.add(alterNum);
						// System.out.println(alterNum);
					}
				}
			}
		}
		return alterNumbers;
	}

	public List<Integer> getAlterNumbers(Question selectedQuestion,
			Selection selection) {
		List<Integer> alterNumbers = new ArrayList<Integer>();
		Long QID = selectedQuestion.UniqueId;
		Iterator questionIterator;
		Question interviewQuestion;
		populateQuestionLists();
		questionIterator = interviewAlterQuestionList.iterator();
		while (questionIterator.hasNext()) {
			interviewQuestion = (Question) questionIterator.next();
			if (interviewQuestion.answer.answered == false) {
				System.out.println(interviewQuestion.UniqueId + " | "
						+ interviewQuestion.text + " | Unanswered");
				continue;
			}
			if (interviewQuestion.UniqueId == QID) {
				if ((interviewQuestion.answer.string.trim()).equals(selection
						.getString().trim())) {
					int[] alterNumArray = interviewQuestion.answer.getAlters();
					for (int alterNum : alterNumArray) {
						alterNumbers.add(alterNum);
						// System.out.println(alterNum);
					}
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
	public List<String> getAlterNames(GraphQuestion graphQuestion) {

		List<String> alterNames = new ArrayList<String>();
		Long QID = graphQuestion.getQuestion().UniqueId;
		Iterator questionIterator;
		Question interviewQuestion;

		populateQuestionLists();

		if (graphQuestion.getCategory() == Question.ALTER_QUESTION) {
			questionIterator = interviewAlterQuestionList.iterator();
		} else { // if category is ALTER_PAIR_QUESTION
			questionIterator = interviewAlterPairQuestionList.iterator();
		}
		while (questionIterator.hasNext()) {

			interviewQuestion = (Question) questionIterator.next();
			if (interviewQuestion.answer.answered == false) {
				System.out.println(interviewQuestion.UniqueId + " | "
						+ interviewQuestion.text + " | Unanswered");
				continue;
			}
			if (interviewQuestion.UniqueId == QID) {
				if (interviewQuestion.answer.string.trim().equals(
						graphQuestion.getSelection().getString().trim())) {
					int[] alterNumArray = interviewQuestion.answer.getAlters();
					for (int alterNum : alterNumArray) {
						alterNames.add(completeAlterNameList[alterNum]);
					}
				}
			}
		}

		return alterNames;
	}

	public static void generateAdjacencyMatrix(Question question,
			Selection selection, boolean weighted) {
		Study study = EgoClient.interview.getStudy();
		if (study.getUIType().equals(Shared.TRADITIONAL_QUESTIONS)) {
			for (Iterator it = EgoClient.interview.getAnswerSubset(
					question.UniqueId).iterator(); it.hasNext();) {
				Answer a = (Answer) it.next();
				if (weighted) {
					if ((adjacencyMatrix[a.getAlters()[0]][a.getAlters()[1]] == 0)
							&& (adjacencyMatrix[a.getAlters()[1]][a.getAlters()[0]] == 0)
							&& (a.getValue() == selection.getValue())) {
						adjacencyMatrix[a.getAlters()[0]][a.getAlters()[1]] = (a.adjacent) ? selection
								.getValue()
								: 0;
						adjacencyMatrix[a.getAlters()[1]][a.getAlters()[0]] = (a.adjacent) ? selection
								.getValue()
								: 0;
						System.out
								.println("Updating weighted adjacency matrix");
					}
				} else {
					if ((adjacencyMatrix[a.getAlters()[0]][a.getAlters()[1]] == 0)
							&& (adjacencyMatrix[a.getAlters()[1]][a.getAlters()[0]] == 0)
							&& (a.getValue() == selection.getValue())) {
						adjacencyMatrix[a.getAlters()[0]][a.getAlters()[1]] = (a.adjacent) ? 1
								: 0;
						adjacencyMatrix[a.getAlters()[1]][a.getAlters()[0]] = (a.adjacent) ? 1
								: 0;
						System.out
								.println("Updating weighted adjacency matrix");
					}

				}
			}
		}
	}

	public static void writeJPEGImage(File imageFile) {
		int width = GraphRenderer.getVv().getWidth();
		int height = GraphRenderer.getVv().getHeight();

		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		GraphRenderer.getVv().paint(graphics);

		graphics.dispose();

		try {
			ImageIO.write(bi, "jpeg", imageFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Color bg = GraphRenderer.getVv().getBackground(); Rectangle rect =
		 * calculateGraphRect(); Dimension size = rect.getSize();
		 * size.setSize(size.width + OFFSET * 2, size.height + OFFSET * 2);
		 * BufferedImage bi = new BufferedImage(size.width, size.height,
		 * BufferedImage.TYPE_INT_BGR); Graphics2D graphics =
		 * bi.createGraphics(); graphics.setColor(bg); graphics.fillRect(0, 0,
		 * size.width, size.height); Dimension visibleSize =
		 * GraphRenderer.getVv().getSize();
		 *  // Hide the visualization viewer, resize it to entire graph size and
		 * move the graph to // upper most left corner of the viewr.
		 * GraphRenderer.getVv().setVisible(false);
		 * GraphRenderer.getVv().setSize(size);
		 * GraphRenderer.getVv().getViewTransformer().translate(OFFSET -
		 * rect.getX(), OFFSET - rect.getY());
		 * 
		 * GraphRenderer.getVv().paint(graphics);
		 *  // Return the previous size and location and redisplay the graph
		 * GraphRenderer.getVv().getViewTransformer().translate(rect.getX() -
		 * OFFSET, rect.getY() - OFFSET);
		 * GraphRenderer.getVv().setSize(visibleSize);
		 * GraphRenderer.getVv().setVisible(true);
		 * 
		 * try { ImageIO.write(bi, "jpeg", imageFile); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
	}

	public static StringLabeller getStringLabeller(Graph graph) {
		return StringLabeller.getLabeller(graph);
	}

	public static Rectangle calculateGraphRect() {
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
	}

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
