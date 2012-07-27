package edu.stanford.arcspread.mypackage.testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.Translate;

import edu.stanford.arcspread.mypackage.dataStructures.NoScriptTag;
import edu.stanford.arcspread.mypackage.dataStructures.TextUnit;


import websoc_utils.Pair;
import websoc_utils.StringUtils;

import edu.stanford.arcspread.mypackage.extraction.*;
import edu.stanford.arcspread.mypackage.extraction.preprocessor.RemovingNoScript;
import edu.stanford.arcspread.mypackage.extraction.preprocessor.XHTML;
import edu.stanford.arcspread.mypackage.extraction.scoring.NormalizedSF;
import edu.stanford.arcspread.mypackage.extraction.scoring.ScoringFunction;

/***
 * Class to extract the gold answer from the cedar tagged files.
 * @author jyotika
 *
 */


public class GoldAnswer {

	Parser parser;

	String rawFile;

	String taggedFile;

	public ArrayList<TextUnit> goldList = new ArrayList<TextUnit>();

	/** Cedar labels to be extracted */
	static ArrayList<String> cedarLabels = new ArrayList<String>();

	ArrayList<TextUnit> listFromGoldAns = new ArrayList<TextUnit>();

	/** Map from a node to the text extracted from it */
	HashMap<TextUnit, Node> rawNodesMap = new HashMap<TextUnit, Node>();

	HashMap<TextUnit,TextUnit> parentTUMap = new HashMap<TextUnit, TextUnit>();
	
	public static FileWriter fstreamX  = null;
    
	public static BufferedWriter xVal = null;
	
	public static FileWriter fstreamY  = null;
    
	public static BufferedWriter yVal = null;

    public static String goldString = null;
    
    public static HashMap<Integer, Double> wScorer = new HashMap<Integer, Double>();
    
    public static HashMap<Integer, Double> tfScorer = new HashMap<Integer, Double>();
    
    public static HashMap<Integer, Double> posScorer = new HashMap<Integer, Double>();
    
    public static HashMap<Integer, Integer> yScorer = new HashMap<Integer, Integer>();
    
    public static int tempCounter;
    
    public static boolean flag = false;
    
    public static HashMap<Node, Pair<Integer, Integer>> totalCounts = new HashMap<Node, Pair<Integer, Integer>>();
    
	public static void init() {
		cedarLabels.add("CedarContent");
		cedarLabels.add("CedarTitle");
		cedarLabels.add("CedarSubTitle");
		cedarLabels.add("CedarAuthor");
		cedarLabels.add("CedarLocation");
		cedarLabels.add("CedarCopyright");
		cedarLabels.add("CedarAPCopyright");
		cedarLabels.add("CedarSource");
		cedarLabels.add("CedarDate");
		try {
			fstreamX = new FileWriter("X.txt");
			xVal = new BufferedWriter(fstreamX);
			fstreamY = new FileWriter("Y.txt");
			yVal = new BufferedWriter(fstreamY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOexception");
		}
	}

	public GoldAnswer(String goldFile, String rawFile) {
		super();
		this.rawFile = rawFile;
		this.taggedFile = goldFile;

	}

	/***
	 * Wrapper function for processRaw(Node)
	 * @throws Exception
	 */
	public void processRaw() throws Exception {

		PrototypicalNodeFactory factory = new PrototypicalNodeFactory ();
		factory.put("noscript", new NoScriptTag());
		parser = new Parser(rawFile);
		parser.setNodeFactory(factory);
		Node node;

		for (NodeIterator itr = parser.elements();itr.hasMoreNodes();) {
			node = itr.nextNode();

			processRaw(node);
		}


	}


	/***
	 * Recursive function to process the raw file and map every node to the text extracted from it. 
	 * Fills up rawNodesMap.
	 *  
	 * @param node
	 * @return TextUnit
	 * @throws Exception
	 */
	public TextUnit processRaw(Node node) throws Exception {

		if (node instanceof NoScriptTag)
			return new TextUnit("");

		if (node instanceof TextNode) {

			TextNode textNode = (TextNode) node;
			String textFromNode = Translate.decode(textNode.getText());

			if (!textFromNode.matches("[\\W]+")) {
				return new TextUnit(textFromNode);
			}

			return new TextUnit("");
		}

		NodeList children = node.getChildren();

		if (children == null) {
			return new TextUnit("");
		}

		StringBuffer buffer = new StringBuffer();

		for (NodeIterator itr = children.elements();itr.hasMoreNodes();) {
			Node child = itr.nextNode();

			buffer.append(processRaw(child));

		}

		String text = buffer.toString();
		rawNodesMap.put(new TextUnit(text), node);
		return new TextUnit(text);

	}

	/***
	 * Wrapper function for processTagged(Node)
	 * 
	 * @throws Exception
	 */
	public void processTagged() throws Exception {


		PrototypicalNodeFactory factory = new PrototypicalNodeFactory ();
		factory.put("noscript", new NoScriptTag());


		parser = new Parser(taggedFile);
		parser.setNodeFactory(factory);
		Node node;

		for (NodeIterator itr = parser.elements();itr.hasMoreNodes();) {
			node = itr.nextNode();
			processTagged(node);
		}


	}

	/***
	 * Recursive function to go through the DOM tree of the goldAnswer, and pull out all the text
	 * marked with any on the labels in 'cedarLabels'. It also maps every bit of text to the text 
	 * contained in its parent node, to handle cases where a span has been split by different labels.
	 * 
	 * @param node
	 * @throws Exception
	 */

	public void processTagged(Node node) throws Exception {

		if (node instanceof Span) {

			Span span = (Span) node;
			String className = span.getAttribute("class");

			if (className !=null && cedarLabels.contains(className)) {

				String text = getTextFromSpan(span);
				TextUnit unit = new TextUnit(text);

				if (null != span.getParent()) {

					TextUnit parentText = HTMLUtils.getTextFromNodeAsTU(span.getParent());
					parentTUMap.put(unit, parentText);
				}
				listFromGoldAns.add(unit);
			}
		}

		NodeList children = node.getChildren();

		if (children == null) {
			return;
		}

		for (NodeIterator itr = children.elements();itr.hasMoreNodes();) {
			Node child = itr.nextNode();

			processTagged(child);

		}

	}

	/***
	 * Function to extract text from a span
	 * 
	 * @param span
	 * @return
	 * @throws Exception
	 */
	public String getTextFromSpan(Span span) throws Exception {

		String textFromNode = Translate.decode(span.toPlainTextString());

		if (!textFromNode.matches("[\\W]+")) 
			return textFromNode;

		else 
			return null;
	}

	/***
	 * This function goes through each TextUnit in 'listFromGoldAns' and looks up the node that corresponds
	 * to it from 'rawNodesMap'. If a node is found, it adds all the TextUnits from that node to 'goldList'. 
	 * Else, it looks for the parent from 'parentTUMap' and tries to find a node that corresponds to
	 * the parent.
	 * 
	 * @throws Exception
	 */
	void generateTextUnits() throws Exception {

		for (TextUnit unit : listFromGoldAns) {

			Node node = rawNodesMap.get(unit);

			if (node != null) {
				ArrayList<TextUnit> units = HTMLUtils.getTextUnits(node);
				for (TextUnit newUnit : units)
					if (!goldList.contains(newUnit))
						goldList.add(newUnit);
			}

			else {
				TextUnit parentTextUnit = parentTUMap.get(unit);
				Node parentNode = rawNodesMap.get(parentTextUnit);

				if (null != parentNode) {

					ArrayList<TextUnit> parentUnits = HTMLUtils
							.getTextUnits(parentNode);

					for (TextUnit newUnit : parentUnits)
						if (!goldList.contains(newUnit))
							goldList.add(newUnit);
				} else {
					System.err.println("ERROR: " + taggedFile + " " + unit);
					goldList.add(unit);

				}
			}

		}

	}

	/***
	 * Top level function to call all the necessary functions.
	 * 
	 * @return The gold list of TextUnits
	 * @throws Exception
	 */
	public ArrayList<TextUnit> process() throws Exception {

		processRaw();
		processTagged();
		generateTextUnits();

		return goldList;
	}

	public static void main(String[] args) throws Exception{


		/*Lexer.STRICT_REMARKS = false;

		GoldAnswer.init();

		String raw = StringUtils.readEntire("/Users/siddhisoman/Desktop/RAwork/cedarExport/taggedDocuments/document_1313.raw.html");
		raw = RemovingNoScript.removeNoScript(raw);
		raw = XHTML.convertToXHTML(raw);

		GoldAnswer test = new GoldAnswer("/Users/siddhisoman/Desktop/RAwork/cedarExport/taggedDocuments/document_1313.tagged.html",
				raw);

		ArrayList<TextUnit> goldList = test.process();

		//System.out.println("-----------GOLD ANSWER 1313-----------");

		for (TextUnit unit: goldList) {
			//System.out.println(unit);
		}
		 */
		
		GoldAnswer.init();
		
		File pathRaw = new File("/Users/siddhisoman/Desktop/RAwork/cedarExport/taggedDocuments/rDocs/");
		File pathTagged = new File("/Users/siddhisoman/Desktop/RAwork/cedarExport/taggedDocuments/tDocs/");

		File [] filesRaw = pathRaw.listFiles();
		File [] filesTagged = pathTagged.listFiles();
		double extent = (double) ((0.67)*(double)filesRaw.length);
		//System.out.println((int)extent);
		//System.out.println(filesRaw.length);
		for (int p = 0; p < (int)extent; p++){
			try{
			String prfile = filesRaw[p].getAbsolutePath();
			ProcessPage prTest = new ProcessPage(prfile);

			ScoringFunction psf = new NormalizedSF(0.95, 0.05);
			ProcessPage.setScoringFunction(psf);
			prTest.processTotal();
			}catch(Exception e)
			{
				System.out.println("Problem!");
			}
		}
		
		ProcessPage.fillTfIdf();
		System.out.println("raw: " + filesRaw.length);
		System.out.println("tagged: " + filesTagged.length);
		
		for (int i = 0; i < (int)1; i++){
			if (".".equals(filesRaw[i].getName()) || "..".equals(filesRaw[i].getName()) || (filesRaw[i].getName().contains("index.htm") || (filesRaw[i].getName().contains("DS_Store")))) {
				continue;  // Ignore the self and parent aliases.
			}
			
			if (filesRaw[i].isFile())
			{
				int ind = filesRaw[i].getName().indexOf(".raw");
				//System.out.println(filesRaw[i].getName().substring(0,ind));
				//break;
				for (int j = 0; j < filesTagged.length; j++){
					if (".".equals(filesTagged[j].getName()) || "..".equals(filesTagged[j].getName()) || (filesTagged[j].getName().contains("index.htm") || (filesTagged[j].getName().contains("DS_Store")))) {
						continue;  // Ignore the self and parent aliases.
					}
					if (filesTagged[j].isFile())
					{
						if(ind >0)
						{
							if(filesTagged[j].getName().contains(filesRaw[i].getName().substring(0,ind)))
							{
								//System.out.println(filesRaw[i].getAbsolutePath());
								//System.out.println(filesTagged[j].getAbsolutePath());
								
								try{
									
								String raw = StringUtils.readEntire(filesRaw[i].getAbsolutePath());
								raw = RemovingNoScript.removeNoScript(raw);
								raw = XHTML.convertToXHTML(raw);

								GoldAnswer gtest = new GoldAnswer(filesTagged[j].getAbsolutePath(),
										raw);

								ArrayList<TextUnit> goldList = gtest.process();
								
								StringBuffer buffer = new StringBuffer();
								for (TextUnit textUnit : goldList) {
									buffer.append(textUnit + "\n");
								}

								goldString = buffer.toString();
								
								String pfile = filesRaw[i].getAbsolutePath();
								ProcessPage pTest = new ProcessPage(pfile);

								ScoringFunction psf = new NormalizedSF(0.95, 0.05);
								ProcessPage.setScoringFunction(psf);
								System.out.println("i:" + i + " length: " + filesRaw.length);
								pTest.process();
								//System.out.println("new");
								if(flag)
								{
								for(int l = 0; l < 10; l++)
								{
									Integer tempInt = (int)(tempCounter*(Math.random()));
									//System.out.println(tempInt);
									Double tmpWScore = wScorer.get(tempInt);
									Double tmpTfScore = tfScorer.get(tempInt);
									Double tmpPosScore = posScorer.get(tempInt);
									Integer tmpYScore = yScorer.get(tempInt);
									xVal.write(tmpWScore.toString() + " " + tmpTfScore.toString() + " " + tmpPosScore.toString() + "\n");
									yVal.write(tmpYScore.toString() + "\n");
								}
								}
								}
								catch(Exception e)
								{
									System.out.println("exception!!");
								}
								wScorer.clear();
								tfScorer.clear();
								posScorer.clear();
								tempCounter = 0;
								flag = false;
								break;
							}
						}
					}
				}
			}
			//if (filesRaw[i].isFile() && filesTagged[i].isFile())
			{ //this line weeds out other directories/folders
				//System.out.println(filesRaw[i]);
				// System.out.println(filesTagged[i]);
			}
		}
        xVal.close();
        yVal.close();
		/*String raw1 = StringUtils.readEntire("/Users/siddhisoman/Desktop/RAwork/cedarExport/taggedDocuments/document_1548.raw.html");
		raw1 = RemovingNoScript.removeNoScript(raw1);
		raw1 = XHTML.convertToXHTML(raw1);

		GoldAnswer test1 = new GoldAnswer("/Users/siddhisoman/Desktop/RAwork/cedarExport/taggedDocuments/document_1548.tagged.html",
				raw1);

		ArrayList<TextUnit> goldList1 = test1.process();

		System.out.println("-----------GOLD ANSWER 1548-----------");

		for (TextUnit unit: goldList1) {
			System.out.println(unit);
		}

		 */



	}


}
