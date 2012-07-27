package edu.stanford.arcspread.mypackage.extraction;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.tags.StyleTag;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.Translate;

import edu.stanford.arcspread.mypackage.App;
import edu.stanford.arcspread.mypackage.dataStructures.NoScriptTag;
import edu.stanford.arcspread.mypackage.dataStructures.TextUnit;

import edu.stanford.arcspread.mypackage.testing.GoldAnswer;
import edu.stanford.arcspread.mypackage.utils.Counter;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.arcspread.mypackage.extraction.scoring.NormalizedSF;
import edu.stanford.arcspread.mypackage.extraction.scoring.ScoringFunction;

import websoc_utils.Pair;

public class ProcessPage implements Extractor {

	Parser parser;

	String fileName;


	static HashMap<Node, Pair<Integer, Integer>> counts;

	static Counter<String> termCount = new Counter<String>();;
	static Counter<String> docCount = new Counter<String>();;
	static Counter<Integer> docTermCount = new Counter<Integer>();;

	static double total_Docs = 0;

	Node maxNode;

	ArrayList<TextUnit> extractedTextUnits;

	String extractedTextAsString;

	/** Maximum text any node holds. Need for the scoring function. */
	int maxText = Integer.MIN_VALUE;

	/** Scoring function to use */
	public static ScoringFunction SCORER;

	/** Flag to indicate whether or not to include form tags. */
	public static boolean ignoreFormTags = true;

	int cnt;

	Map<Double, Node> sortedMap = new TreeMap<Double, Node>(
			Collections.reverseOrder());

	Map<Node, ArrayList<String>> sortedImageMap = new TreeMap<Node, ArrayList<String>>(
			Collections.reverseOrder());

	ArrayList<String> nodeText;

	ArrayList<String> imageUrl;

	HashSet<String> hNodeText;

	/*
	 * JPanel Pane1 = new JPanel();
	 * 
	 * JPanel Pane2 = new JPanel();
	 * 
	 * JPanel panel = new JPanel();
	 * 
	 * JFrame frame = new JFrame("Summary");
	 * 
	 * JButton button = new JButton("Next"); JButton fButton = new
	 * JButton("Fade");
	 * 
	 * JSlider slider = new JSlider();
	 * 
	 * BufferedImage[] img;
	 * 
	 * static int k;
	 * 
	 * int cnt2 = 1;
	 * 
	 * JLabel l1 = new JLabel(); JLabel l2 = new JLabel(); JLabel l3 = new
	 * JLabel(); JLabel l4 = new JLabel(); JLabel l5 = new JLabel(); JLabel l6 =
	 * new JLabel();
	 * 
	 * JTextArea t1 = new JTextArea("Summary"); JTextArea t2 = new
	 * JTextArea("Summary"); JTextArea t3 = new JTextArea("Summary"); JTextArea
	 * t4 = new JTextArea("Summary"); JTextArea t5 = new JTextArea("Summary");
	 * JTextArea t6 = new JTextArea("Summary");
	 * 
	 * JScrollPane s1 = new JScrollPane(t1); JScrollPane s2 = new
	 * JScrollPane(t2); JScrollPane s3 = new JScrollPane(t3); JScrollPane s4 =
	 * new JScrollPane(t4); JScrollPane s5 = new JScrollPane(t5); JScrollPane s6
	 * = new JScrollPane(t6);
	 * 
	 * JScrollPane ps1 = new JScrollPane(panel);
	 */

	public ProcessPage(String fileName) {
		super();
		this.fileName = fileName;
		counts = new HashMap<Node, Pair<Integer, Integer>>();
		// lp = new
		// LexicalizedParser("/Users/siddhisoman/Documents/workspace/OriginalStanford Parser/grammar/englishPCFG.ser.gz");

		/*
		 * Pane1.setLayout(new BoxLayout(Pane1, BoxLayout.LINE_AXIS));
		 * Pane2.setLayout(new BoxLayout(Pane2, BoxLayout.PAGE_AXIS));
		 * panel.setLayout ( new GridLayout (6, 1) );
		 * 
		 * ps1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * ps1.setVisible(true);
		 * 
		 * button.addActionListener(this); fButton.addActionListener(this);
		 * 
		 * button.setSize(30, 30); fButton.setSize(30, 30);
		 * 
		 * l1.setSize(200, 200); l2.setSize(200, 200); l3.setSize(200, 200);
		 * l4.setSize(200, 200); l5.setSize(200, 200); l6.setSize(200, 200);
		 * 
		 * s1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * t1.setBorder(BorderFactory.createLineBorder(Color.black));
		 * panel.add(t1);
		 * 
		 * Pane2.add(l1);
		 * 
		 * s2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * s2.setPreferredSize(new Dimension(250, 250));
		 * t2.setBorder(BorderFactory.createLineBorder(Color.black));
		 * panel.add(t2);
		 * 
		 * Pane2.add(l2);
		 * 
		 * s3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * t3.setBorder(BorderFactory.createLineBorder(Color.black));
		 * panel.add(t3);
		 * 
		 * Pane2.add(l3);
		 * 
		 * s4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * t4.setBorder(BorderFactory.createLineBorder(Color.black));
		 * panel.add(t4);
		 * 
		 * Pane2.add(l4);
		 * 
		 * s5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * t5.setBorder(BorderFactory.createLineBorder(Color.black));
		 * panel.add(t5);
		 * 
		 * Pane2.add(l5);
		 * 
		 * s6.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 * t6.setBorder(BorderFactory.createLineBorder(Color.black));
		 * panel.add(t6);
		 * 
		 * Pane2.add(l6);
		 * 
		 * Pane1.add(button); Pane1.add(fButton);
		 * 
		 * button.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		 * fButton.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		 * slider.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		 * 
		 * frame.setSize(1200,1200); Color c = new Color(0,255,255);
		 * 
		 * panel.setBackground(c); Pane1.setBackground(c);
		 * 
		 * frame.add(panel, BorderLayout.CENTER); frame.add(Pane1,
		 * BorderLayout.PAGE_END); frame.add(Pane2, BorderLayout.EAST);
		 * frame.setBackground(c); frame.setVisible(true);
		 */
	}

	public Image TransformColorToTransparency(BufferedImage image, Color c1,
			Color c2) {
		// Primitive test, just an example
		final int r1 = c1.getRed();
		final int g1 = c1.getGreen();
		final int b1 = c1.getBlue();
		final int r2 = c2.getRed();
		final int g2 = c2.getGreen();
		final int b2 = c2.getBlue();
		RGBImageFilter filter = new RGBImageFilter() {
			public final int filterRGB(int x, int y, int rgb) {
				int r = (rgb & 0xFF0000) >> 16;
				int g = (rgb & 0xFF00) >> 8;
				int b = rgb & 0xFF;
				if (r >= r1 && r <= r2 && g >= g1 && g <= g2 && b >= b1
						&& b <= b2) {
					// Set fully transparent but keep color
					return rgb & 0xFFFFFF;
				}
				return rgb;
			}
		};

		if (image != null) {
			ImageProducer ip = new FilteredImageSource(image.getSource(),
					filter);
			return Toolkit.getDefaultToolkit().createImage(ip);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private Image TransformGrayToTransparency(BufferedImage image) {
		if (image != null) {
			Image img1 = image.getScaledInstance(600, 100,
					java.awt.Image.SCALE_SMOOTH);
			BufferedImage image1 = new BufferedImage(600, 100,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D bufImageGraphics = image1.createGraphics();
			bufImageGraphics.drawImage(img1, 0, 0, null);
			bufImageGraphics.dispose();
			RGBImageFilter filter = new RGBImageFilter() {
				public final int filterRGB(int x, int y, int rgb) {
					return (rgb << 8) & 0x0F000000;
				}
			};

			if (image1 != null) {
				ImageProducer ip = new FilteredImageSource(image1.getSource(),
						filter);
				return Toolkit.getDefaultToolkit().createImage(ip);
			}
		}
		return null;
	}

	public static BufferedImage getScaledInstance(BufferedImage img,
			int targetWidth, int targetHeight, Object hint,
			boolean higherQuality) {
		final int type = img.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w;
		int h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			final BufferedImage tmp = new BufferedImage(w, h, type);
			final Graphics2D g2 = tmp.createGraphics();
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	/***************************************************************************
	 * Function to set the heuristic used by this class. This must be called
	 * before any call to process
	 * 
	 * @param function
	 */
	public static void setScoringFunction(ScoringFunction function) {

		SCORER = function;
	}

	/***************************************************************************
	 * Function to process the page. This function parses the html, and
	 * recursively calculates the heuristic. It also calls calculateMainNode,
	 * which calculates the node with the highest heuristic value.
	 * 
	 * @throws Exception
	 */
	public String process() throws Exception {

		if (SCORER == null)
			throw new Exception("Scoring Function not specified");

		PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
		factory.put("NOSCRIPT", new NoScriptTag());
		parser = new Parser(fileName);
		parser.setNodeFactory(factory);
		Node node;

		for (NodeIterator itr = parser.elements(); itr.hasMoreNodes();) {
			node = itr.nextNode();

			process(node);

		}

		String s = calculateMainNode();
		return s;
	}
	
	public void processTotal() throws Exception {

		if (SCORER == null)
			throw new Exception("Scoring Function not specified");

		PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
		factory.put("NOSCRIPT", new NoScriptTag());
		parser = new Parser(fileName);
		parser.setNodeFactory(factory);
		Node node;

		for (NodeIterator itr = parser.elements(); itr.hasMoreNodes();) {
			node = itr.nextNode();

			processTotal(node);

		}
	}

	/***************************************************************************
	 * Recursive function to process a node. Determines if a node is a TextNode,
	 * RemarkNode or TagNode and calls the appropriate function. This function
	 * also calculates maxText.
	 * 
	 * @param node
	 * @return Pair holding the textCount and linkCount
	 * @throws Exception
	 */

	Pair<Integer, Integer> process(Node node) throws Exception {

		Pair<Integer, Integer> nodeCounts = null;

		if (node instanceof RemarkNode) {

			nodeCounts = new Pair<Integer, Integer>(0, 0);
		}

		else if (node instanceof TextNode) {

			TextNode textNode = (TextNode) node;
			nodeCounts = processTextNode(textNode);

		}

		else if (node instanceof TagNode) {

			TagNode tagNode = (TagNode) node;
			nodeCounts = processTagNode(tagNode);

		}

		int textCount = nodeCounts.getFirst();
		int linkCount = nodeCounts.getSecond();

		if (textCount > maxText)
			maxText = textCount;

		counts.put(node, new Pair<Integer, Integer>(textCount, linkCount));
        GoldAnswer.totalCounts.put(node, new Pair<Integer, Integer>(textCount, linkCount));
		return new Pair<Integer, Integer>(textCount, linkCount);

	}
	
	Pair<Integer, Integer> processTotal(Node node) throws Exception {

		Pair<Integer, Integer> nodeCounts = null;

		if (node instanceof RemarkNode) {

			nodeCounts = new Pair<Integer, Integer>(0, 0);
		}

		else if (node instanceof TextNode) {

			TextNode textNode = (TextNode) node;
			nodeCounts = processTextNode(textNode);

		}

		else if (node instanceof TagNode) {

			TagNode tagNode = (TagNode) node;
			nodeCounts = processTagNode(tagNode);

		}

		int textCount = nodeCounts.getFirst();
		int linkCount = nodeCounts.getSecond();

		if (textCount > maxText)
			maxText = textCount;

		GoldAnswer.totalCounts.put(node, new Pair<Integer, Integer>(textCount, linkCount));
		return new Pair<Integer, Integer>(textCount, linkCount);

	}

	/***************************************************************************
	 * Function to process a textNode. Returns the length of the text from the
	 * node as textCount and 0 as linkCount. If the text contains only nonword
	 * characters, textCount is 0.
	 * 
	 * @param textNode
	 * @return Pair holding textCount and linkCount
	 * @throws Exception
	 */
	Pair<Integer, Integer> processTextNode(TextNode textNode) throws Exception {
		int textCount = 0;
		int linkCount = 0;

		String textFromNode = Translate.decode(textNode.getText());

		if (!textFromNode.matches("[\\W]+")) {
			textCount = textFromNode.length();

		}
		return new Pair<Integer, Integer>(textCount, linkCount);
	}

	/***************************************************************************
	 * Function to process a TagNode. The textCount and linkCount of a TagNode
	 * are sum of the textCounts and linkCounts of its children.
	 * 
	 * @param tagNode
	 * @return Pair holding textCount and linkCount
	 * @throws Exception
	 */

	Pair<Integer, Integer> processTagNode(TagNode tagNode) throws Exception {

		int textCount = 0;
		int linkCount = 0;

		// Ignore all form tags, if ignoreFormTags is set.

		if (ignoreFormTags) {
			if ((tagNode instanceof SelectTag) || (tagNode instanceof FormTag)
					|| (tagNode instanceof InputTag)
					|| (tagNode instanceof TextareaTag)
					|| (tagNode instanceof OptionTag)) {
				return new Pair<Integer, Integer>(textCount, linkCount);
			}
		}

		// Ignore script and style nodes
		if ((tagNode instanceof NoScriptTag) || (tagNode instanceof StyleTag)) {

			return new Pair<Integer, Integer>(textCount, linkCount);

		}

		if (tagNode instanceof CompositeTag) {

			CompositeTag ctag = (CompositeTag) tagNode;

			// For a LinkTag, textCount is set as 1

			if (ctag instanceof LinkTag) {
				linkCount = 1;
				textCount = 1;

			} else {
				NodeList children = ctag.getChildren();

				if (null != children) {

					for (NodeIterator itr = children.elements(); itr
							.hasMoreNodes();) {
						Node child = itr.nextNode();

						Pair<Integer, Integer> counts = process(child);
						textCount += counts.getFirst();
						linkCount += counts.getSecond();

					}

				}
			}
		}

		return new Pair<Integer, Integer>(textCount, linkCount);
	}

	double weightedScore(Node node) throws Exception {

		//double maxScore = Double.MIN_VALUE;

		//for (Node node : ProcessPage.counts.keySet()) 
		//{
			Pair<Integer, Integer> count = ProcessPage.counts.get(node);

			int numWords = count.getFirst();
			int numLinks = count.getSecond();

			double score = ProcessPage.SCORER.getScore(numLinks, numWords,
					maxText);
			// System.out.println(score);
			// return score;

			/*if (score > maxScore) {
				maxScore = score;
				maxNode = node;
			}*/

		//}

		/*extractedTextUnits.addAll(HTMLUtils.getTextUnits(maxNode));

		StringBuffer buffer = new StringBuffer();

		for (TextUnit textUnit : extractedTextUnits) {
			buffer.append(textUnit + "\n");
		}

		extractedTextAsString = buffer.toString();
		System.out.println("maxScore: " + maxScore);
		// System.out.println("maxNode: " + maxNode.toPlainTextString());
		System.out.println("extractedTextAsString: " + extractedTextAsString);*/
		return score;

	}

	double tfIdfScore(Node node) throws Exception {
		double score = Double.MIN_VALUE;
		//double maxScore = Double.MIN_VALUE;

		total_Docs = counts.size();
		Integer cnt = new Integer(0);

		//for (Node node : ProcessPage.counts.keySet()) 
		{
			String nodeText = node.toString();
			double tfidfVal = 0;
			String nodeWords[] = nodeText.split(" ");
			if (nodeText.length() > 0) {
				for (String s : nodeWords) {
					int cnt2 = cnt.intValue();
					cnt2++;
					Integer cnt1 = new Integer(cnt2);
					s = s.trim();
					String temp = s + "\t" + cnt1.toString();
				    if(!s.equals(""))
					{
				    	//System.out.println("2: " + temp);
						tfidfVal += getValue(s, temp, cnt1);
						//System.out.println("term: " + s + "tf-idf: " + tfidfVal);
					}
				}
				score = tfidfVal;

				/*if (score > maxScore) {
					maxScore = score;
					maxNode = node;
				}*/

			}
		}
		// System.out.println(score);
		// return score;

		/*System.out.println("maxScore: " + maxScore);
		// System.out.println("maxNode: " + maxNode.toPlainTextString());

		extractedTextUnits.addAll(HTMLUtils.getTextUnits(maxNode));

		StringBuffer buffer = new StringBuffer();

		for (TextUnit textUnit : extractedTextUnits) {
			buffer.append(textUnit + "\n");
		}

		extractedTextAsString = buffer.toString();
		System.out.println("extractedTextAsString: " + extractedTextAsString);*/
		return score;

	}

	double POSScore(Node node) {
		double score = Double.MIN_VALUE;
		//double maxScore = Double.MIN_VALUE;
		try {

			//for (Node node : ProcessPage.counts.keySet()) 
			{
				double posVal = 0;
				String nodeText = node.toString();
				if (nodeText != null) {
					App.posTag(nodeText);
				}
				//if (App.wordToTagsMap.size() > 50) 
				{
					// System.out.println(App.wordToTagsMap.size());
					Iterator<?> entries = App.wordToTagsMap.entrySet()
							.iterator();
					while (entries.hasNext()) {
						Map.Entry entry = (Map.Entry) entries.next();
						String key = (String) entry.getKey();
						Set<String> value = (Set<String>) entry.getValue();
						Iterator<String> it = value.iterator();

						while (it.hasNext()) {
							String sTag = it.next();
							if (sTag.contains("NN")) {
								posVal = posVal + 1;
								break;
							}
						}
					}

					score = (double) posVal / (double) App.wordToTagsMap.size();
					// System.out.println(score);
					/*if (score > maxScore) {
						maxScore = score;
						maxNode = node;
					}*/
				}
			}
			/*System.out.println("maxScore: " + maxScore);
			// System.out.println("maxNode: " + maxNode.toPlainTextString());

			extractedTextUnits.addAll(HTMLUtils.getTextUnits(maxNode));

			StringBuffer buffer = new StringBuffer();

			for (TextUnit textUnit : extractedTextUnits) {
				buffer.append(textUnit + "\n");
			}

			extractedTextAsString = buffer.toString();
			System.out.println("extractedTextAsString: "
					+ extractedTextAsString);*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(score);
		// return score;
		return score;

	}

	/***************************************************************************
	 * This function calculates node which has the highest value of the scoring
	 * function and sets maxNode. It also extracts the text from maxNode and
	 * sets extractedTextUnits and extractedSetAsString
	 * 
	 * @throws Exception
	 */
	String calculateMainNode() throws Exception {

		double maxScore = Double.MIN_VALUE;
		extractedTextUnits = new ArrayList<TextUnit>();
		//fillTfIdf();
		Double minusOne = new Double(-1);
		Double plusOne = new Double(1);

		 for (Node node : ProcessPage.counts.keySet())
		{
			// if(node != null)
			{
				 Double maxScorePOS = POSScore(node);
				 //Double maxScorePOS = new Double(0);
				 Double maxScoreWeighted = weightedScore(node);
				 Double maxScoreTfIdf = tfIdfScore(node);
			    // if(!maxScoreWeighted.isNaN()/*&& maxScorePOS > 0*/)
				{
					ArrayList<TextUnit> tempTextUnit = new ArrayList<TextUnit>();
					tempTextUnit.addAll(HTMLUtils.getTextUnits(node));
					StringBuffer bufferTemp = new StringBuffer();
					for (TextUnit textUnit : tempTextUnit) {
						bufferTemp.append(textUnit + "\n");
					}
					String nodeString = bufferTemp.toString();
					
					if(!maxScoreWeighted.isNaN() && !maxScoreTfIdf.isNaN() && !maxScorePOS.isNaN())
					{
						//GoldAnswer.xVal.write(maxScorePOS.toString() + " " + maxScoreWeighted.toString() + " " + maxScoreTfIdf.toString() + "\n");
						GoldAnswer.wScorer.put(GoldAnswer.tempCounter, maxScoreWeighted);
						GoldAnswer.tfScorer.put(GoldAnswer.tempCounter, maxScoreTfIdf);
						GoldAnswer.posScorer.put(GoldAnswer.tempCounter, maxScorePOS);
						if(nodeString!= null && GoldAnswer.goldString!= null)
						{
							if(nodeString.equals(GoldAnswer.goldString))
							{
								GoldAnswer.flag = true;
								GoldAnswer.yVal.write(plusOne.toString() + "\n");
								GoldAnswer.xVal.write(maxScorePOS.toString() + " " + maxScoreWeighted.toString() + " " + maxScoreTfIdf.toString() + "\n");
								GoldAnswer.yScorer.put(GoldAnswer.tempCounter, 1);
							}
							else
							{
								//GoldAnswer.yVal.write(minusOne.toString() + "\n");
								GoldAnswer.yScorer.put(GoldAnswer.tempCounter, -1);
							}
						}
						else
						{
							//GoldAnswer.yVal.write(minusOne.toString() + "\n");
							GoldAnswer.yScorer.put(GoldAnswer.tempCounter, -1);
						}
						GoldAnswer.tempCounter++;
					}
					Double score = (double)0.8*maxScoreWeighted + (double)0.15*maxScoreTfIdf /*+ (double) 0.05*maxScorePOS*/;
					// if(!score.isNaN())
					{
						// System.out.println(score);
						 if (score > maxScore) {
						 maxScore = score;
						 maxNode = node;
						 }
						// }
						// }
					}
				}
			}
		}
			System.out.println("maxScore: " + maxScore);
			// System.out.println(maxNode.toString());
			extractedTextUnits.addAll(HTMLUtils.getTextUnits(maxNode));

			StringBuffer buffer = new StringBuffer();
			for (TextUnit textUnit : extractedTextUnits) {
				buffer.append(textUnit + "\n");
			}

			extractedTextAsString = buffer.toString();
			System.out.println("extractedTextAsString: "
					+ extractedTextAsString);
			WebCrawlExtractor.webPageRank.setCount(fileName, maxScore);
			
		return extractedTextAsString;

	}

	/*public static void demoAPI(LexicalizedParser lp, String sentence) {
		// String sentence = "My name is Anthony.";
		String[] sent = sentence.split(" ");
		List<CoreLabel> rawWords = new ArrayList<CoreLabel>();
		for (String word : sent) {
			CoreLabel l = new CoreLabel();
			l.setWord(word);
			rawWords.add(l);
		}
		Tree parse = lp.apply(rawWords);
		System.out.println();
		// Tree parse = null;

		// TokenizerFactory<CoreLabel> tokenizerFactory =
		// PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		// TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		// GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		// List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println();

		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		String filename = "fileName.txt";

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(filename, false));
		} catch (Exception e) {
			e.printStackTrace();
		}
		tp.printTree(parse, pw);
		pw.close();
	}
*/
	public static void fillTfIdf() {
		Integer cnt = new Integer(0);
		for (Node node : GoldAnswer.totalCounts.keySet()) {

			String nodeText = node.toPlainTextString();
			String nodeWords[] = nodeText.split(" ");
			// System.out.println(node.toPlainTextString());
			// System.out.println("\nnext\n\n");
			// System.out.println(nodeWords.length);
			for (String s : nodeWords) {
				int cnt5 = cnt.intValue();
				cnt5++;
				Integer cnt6 = new Integer(cnt5);
				s = s.trim();
				if(!s.equals(""))
				{
				String temp = s + "\t" + cnt6.toString();
			    //System.out.println("1: " + temp);
				termCount.incrementCount(temp);
				//docCount.setCount(s, 1);
				docCount.incrementCount(s);
				docTermCount.incrementCount(cnt);
				}
			}
			for (String s : nodeWords) {
				int cnt7 = cnt.intValue();
				cnt7++;
				Integer cnt8 = new Integer(cnt7);
				String temp = s + "\t" + cnt8.toString();
			    //System.out.println(termCount.getCount(temp));
			}
		}
	}

	public double getValue(String s, String temp, Integer cnt) {
		// System.out.println(termCount.size());
		double documentCount = docCount.getCount(s);
		double minValue = Double.MIN_VALUE;
		double numOfOccurrences = termCount.getCount(temp);
		double tf = numOfOccurrences / (minValue + total_Docs);
		double idf = 0;
		if(documentCount > 0)
		 idf = (double) Math.log10(total_Docs / (minValue + documentCount));
		//if(documentCount > 1)
		//	System.out.println("docCount" + documentCount);
        //if(numOfOccurrences != 0)
        //	System.out.println("numOfOccurrences: " + numOfOccurrences);
		//System.out.println("tf: " + tf);
		//System.out.println("idf: " + idf);
	    //System.out.println("val" + (minValue));
		return (tf * idf);
	}

	public String getExtractedText() {
		return extractedTextAsString;
	}

	public ArrayList<TextUnit> getExtractedTextUnits() {
		return extractedTextUnits;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String file = "/Users/siddhisoman/Desktop/RAwork/usnews" + ".html";
		ProcessPage test = new ProcessPage(file);

		ScoringFunction sf = new NormalizedSF(0.95, 0.05);
		ProcessPage.setScoringFunction(sf);
		test.process();

		// //System.out.println("ARTICLE");

		/*
		 * ArrayList<TextUnit> textUnits = test.getTextUnits();
		 * 
		 * for (TextUnit textUnit: textUnits) {
		 * System.out.println("["+textUnit+"]"+ textUnit.getSize());
		 * System.out.println("--------------"); }
		 */
	}

	public static String summarize(String file) {
		ProcessPage test = new ProcessPage(file);
		ScoringFunction sf = new NormalizedSF(0.95, 0.05);
		ProcessPage.setScoringFunction(sf);
		String s = "";
		try {
			s = test.process();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	public static void collageSummarize(String file) {
		ProcessPage test = new ProcessPage(file);
		ScoringFunction sf = new NormalizedSF(0.95, 0.05);
		ProcessPage.setScoringFunction(sf);

		try {
			test.process();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * public void actionPerformed(ActionEvent e) {
	 * 
	 * String cmd = e.getActionCommand(); if(cmd.equals("Next")) { if(nodeText
	 * != null) { final String slTemp1 = nodeText.get(1 + cnt) + "\n";
	 * t1.setText(slTemp1);
	 * 
	 * final String slTemp2 = nodeText.get(2 + cnt) + "\n"; t2.setText(slTemp2);
	 * 
	 * final String slTemp3 = nodeText.get(3 + cnt) + "\n"; t3.setText(slTemp3);
	 * 
	 * final String slTemp4 = nodeText.get(4 + cnt) + "\n"; t4.setText(slTemp4);
	 * 
	 * final String slTemp5 = nodeText.get(5 + cnt) + "\n"; t5.setText(slTemp5);
	 * 
	 * final String slTemp6 = nodeText.get(6 + cnt) + "\n"; t6.setText(slTemp6);
	 * } k = 1; img = new BufferedImage[100]; Parser parser = null; try {
	 * //if(imageUrl != null) { String s1 = imageUrl.get(cnt2);
	 * System.out.println(s1); parser = new Parser(s1); } } catch
	 * (ParserException e2) { // TODO Auto-generated catch block
	 * e2.printStackTrace(); } NodeList list = null; try { list =
	 * parser.parse(new TagNameFilter("IMG")); } catch (ParserException e2) { //
	 * TODO Auto-generated catch block e2.printStackTrace(); } for
	 * (SimpleNodeIterator iterator = list.elements(); iterator
	 * .hasMoreNodes();) { try { Tag tag = (Tag) iterator.nextNode(); URL url =
	 * new URL(tag.getAttribute("src")); try { BufferedImage imgTemp =
	 * ImageIO.read(url); if(imgTemp!=null) { img[k++] = imgTemp; } } catch
	 * (Exception e1) { System.out.println("The image was not loaded."); } }
	 * catch (Exception e1) { System.out.println("Improper url"); } }
	 * 
	 * final String sUrl = parser.getURL();
	 * 
	 * /* l1.addMouseListener(new java.awt.event.MouseAdapter() { Frame
	 * imgSummary = new JFrame("Image Summary");
	 * 
	 * public void mouseEntered(java.awt.event.MouseEvent evt) {
	 * System.out.println("in");
	 * 
	 * try{ JPanel Pane3 = new JPanel(); JLabel jlt = new JLabel();
	 * BufferedImage ioTemp = ImageIO.read(new File("out.jpg"));
	 * 
	 * if(ioTemp !=null) jlt.setIcon(new ImageIcon(ioTemp)); Pane3.add(jlt);
	 * imgSummary.setSize(1200,1200); imgSummary.add(Pane3);
	 * imgSummary.setVisible(true);
	 * 
	 * }catch(Exception e) { e.printStackTrace(); } }
	 * 
	 * });
	 * 
	 * if(img[1] != null) { Image newimg1 = img[1].getScaledInstance(600, 100,
	 * java.awt.Image.SCALE_SMOOTH); ImageIcon nwImg1 = new ImageIcon(newimg1);
	 * l1.setIcon(nwImg1); } if(img[2] != null) { Image newimg2 =
	 * img[2].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
	 * ImageIcon nwImg2 = new ImageIcon(newimg2); l2.setIcon(nwImg2); }
	 * if(img[3] != null) { Image newimg3 = img[3].getScaledInstance(600, 100,
	 * java.awt.Image.SCALE_SMOOTH); ImageIcon nwImg3 = new ImageIcon(newimg3);
	 * l3.setIcon(nwImg3); } if(img[4] != null) { Image newimg4 =
	 * img[4].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
	 * ImageIcon nwImg4 = new ImageIcon(newimg4); l4.setIcon(nwImg4); }
	 * if(img[5] != null) { Image newimg5 = img[5].getScaledInstance(600, 100,
	 * java.awt.Image.SCALE_SMOOTH); ImageIcon nwImg5 = new ImageIcon(newimg5);
	 * l5.setIcon(nwImg5); } if(img[6] != null) { Image newimg6 =
	 * img[6].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
	 * ImageIcon nwImg6 = new ImageIcon(newimg6); l6.setIcon(nwImg6); } cnt2++;
	 * cnt = cnt + 6;
	 * 
	 * } if(cmd.equals("Text")) { l1.setText(nodeText.get(1 + cnt) + "\n");
	 * l2.setText(nodeText.get(2 + cnt) + "\n"); l3.setText(nodeText.get(3 +
	 * cnt) + "\n"); l4.setText(nodeText.get(4 + cnt) + "\n");
	 * l5.setText(nodeText.get(5 + cnt) + "\n"); l6.setText(nodeText.get(6 +
	 * cnt) + "\n"); cnt = cnt + 6; } if(cmd.equals("Fade")) { Image transpImg1
	 * = TransformGrayToTransparency(img[1]); if(transpImg1 != null)
	 * l1.setIcon(new ImageIcon(transpImg1));
	 * 
	 * Image transpImg2 = TransformGrayToTransparency(img[2]); if(transpImg2 !=
	 * null) l2.setIcon(new ImageIcon(transpImg2));
	 * 
	 * Image transpImg3 = TransformGrayToTransparency(img[3]); if(transpImg3 !=
	 * null) l3.setIcon(new ImageIcon(transpImg3));
	 * 
	 * Image transpImg4 = TransformGrayToTransparency(img[4]); if(transpImg4 !=
	 * null) l4.setIcon(new ImageIcon(transpImg4));
	 * 
	 * Image transpImg5 = TransformGrayToTransparency(img[5]); if(transpImg5 !=
	 * null) l5.setIcon(new ImageIcon(transpImg5));
	 * 
	 * Image transpImg6 = TransformGrayToTransparency(img[6]); if(transpImg6 !=
	 * null) l6.setIcon(new ImageIcon(transpImg6)); }
	 * 
	 * }
	 */

}
