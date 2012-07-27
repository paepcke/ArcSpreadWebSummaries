package edu.stanford.arcspread.mypackage.extraction;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
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
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.util.Translate;

import edu.stanford.arcspread.mypackage.dataStructures.NoScriptTag;
import edu.stanford.arcspread.mypackage.dataStructures.TextUnit;

import edu.stanford.arcspread.mypackage.extraction.scoring.NormalizedSF;
import edu.stanford.arcspread.mypackage.extraction.scoring.ScoringFunction;

import edu.stanford.arcspread.mypackage.utils.Counter;
import websoc_utils.Pair;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class CollageSummary extends JApplet implements Extractor,ActionListener
	{
	
	public static Counter<String> cScore = new Counter<String>();
	
	HashMap<String, Double> start = new HashMap<String,Double>();
	
	Parser parser;

	int cnt;
	
	String fileName;
	
	Map<Double, Node> sortedMap = new TreeMap<Double, Node>(Collections.reverseOrder());
	
	Map<Node, ArrayList<String>> sortedImageMap = new TreeMap<Node, ArrayList<String>>(Collections.reverseOrder());

	static HashMap<Node, Pair<Integer, Integer>> counts;

	Node maxNode;

	ArrayList<TextUnit> extractedTextUnits;
	
	ArrayList<String> nodeText;
	
	ArrayList<String> imageUrl;
	
	HashSet<String> hNodeText;

	String extractedTextAsString;
	
	JPanel Pane1 = new JPanel();
	
	JPanel Pane2 = new JPanel();
	
	JPanel panel = new JPanel();
	
	//JFrame frame = new JFrame("Summary");
	
	JButton button = new JButton("Next"); 
	
	JButton iButton = new JButton("Image"); 
	
	JButton tButton = new JButton("Snapshot"); 
	
	JButton fButton = new JButton("Fade");
	
	JSlider slider = new JSlider();
	
	BufferedImage[] img;
	
	static int k;
	
	int cnt2 = 1;
	
	JLabel l1 = new JLabel();
	JLabel l2 = new JLabel();
	JLabel l3 = new JLabel();
	JLabel l4 = new JLabel();
	JLabel l5 = new JLabel();
	JLabel l6 = new JLabel();
	
	JTextArea t1 = new JTextArea("Summary");
	JTextArea t2 = new JTextArea("Summary");	
	JTextArea t3 = new JTextArea("Summary");
	JTextArea t4 = new JTextArea("Summary");
	JTextArea t5 = new JTextArea("Summary");
	JTextArea t6 = new JTextArea("Summary");
	
	JScrollPane s1 = new JScrollPane(t1);
	JScrollPane s2 = new JScrollPane(t2);
	JScrollPane s3 = new JScrollPane(t3);
	JScrollPane s4 = new JScrollPane(t4);
	JScrollPane s5 = new JScrollPane(t5);
	JScrollPane s6 = new JScrollPane(t6);
	
	JScrollPane ps1 = new JScrollPane(panel);

	/** Maximum text any node holds. Need for the scoring function.*/
	int maxText = Integer.MIN_VALUE;

	/** Scoring function to use */
	public static ScoringFunction SCORER;

	
	/** Flag to indicate whether or not to include form tags.*/
	public static boolean ignoreFormTags = true;

	public CollageSummary(String fileName) {
		super();
		this.fileName = fileName;
		counts = new HashMap<Node, Pair<Integer, Integer>>();
		
		panel.setPreferredSize(new Dimension(800, 1100));
		Pane2.setPreferredSize(new Dimension(400, 1100));
		Pane1.setLayout(new BoxLayout(Pane1, BoxLayout.LINE_AXIS));	
		Pane2.setLayout(new BoxLayout(Pane2, BoxLayout.PAGE_AXIS));	
		panel.setLayout ( new GridLayout (6, 1) );
		
		ps1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		ps1.setVisible(true);
		
		button.addActionListener(this);
		iButton.addActionListener(this);
		tButton.addActionListener(this);
		fButton.addActionListener(this);
		slider.addChangeListener(new ChangeListener() {
		    // This method is called whenever the slider's value is changed
		    public void stateChanged(ChangeEvent evt) {
		        JSlider slider = (JSlider)evt.getSource();

		        if (!slider.getValueIsAdjusting()) {
		            // Get new value
		        	
		        	/*Graphics2D g3 = img[1].createGraphics();
		        	
		        	g3.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		        	g3.dr*/
		        
		        	/*Image transpImg1 = TransformGrayToTransparency(img[1]);
		        	if(transpImg1 != null)
		        	l1.setIcon(new ImageIcon(transpImg1));
		        	
		        	Image transpImg2 = TransformGrayToTransparency(img[2]);
		        	if(transpImg2 != null)
		        	l2.setIcon(new ImageIcon(transpImg2));
		        	
		        	Image transpImg3 = TransformGrayToTransparency(img[3]);
		        	if(transpImg3 != null)
		        	l3.setIcon(new ImageIcon(transpImg3));
		        	
		        	Image transpImg4 = TransformGrayToTransparency(img[4]);
		        	if(transpImg4 != null)
		        	l4.setIcon(new ImageIcon(transpImg4));
		        	
		        	Image transpImg5 = TransformGrayToTransparency(img[5]);
		        	if(transpImg5 != null)
		        	l5.setIcon(new ImageIcon(transpImg5));
		        	
		        	Image transpImg6 = TransformGrayToTransparency(img[6]);
		        	if(transpImg6 != null)
		        	l6.setIcon(new ImageIcon(transpImg6));
		        	
		        	*/
		        }
		    }
		});
		
		button.setSize(30, 30);
		iButton.setSize(30, 30);
		tButton.setSize(30, 30);
		fButton.setSize(30, 30);
		
		l1.setSize(200, 200);
		l2.setSize(200, 200);
		l3.setSize(200, 200);
		l4.setSize(200, 200);
		l5.setSize(200, 200);
		l6.setSize(200, 200);
		
		s1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//panel.add(s1);
		t1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(t1);	
		
		Pane2.add(l1);
		
		s2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		s2.setPreferredSize(new Dimension(250, 250));
		t2.setBorder(BorderFactory.createLineBorder(Color.black));
		//panel.add(s2);
		panel.add(t2);	
		
		Pane2.add(l2);
		
		s3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		t3.setBorder(BorderFactory.createLineBorder(Color.black));
		//panel.add(s3);
		panel.add(t3);	
		
		Pane2.add(l3);
		
		s4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		t4.setBorder(BorderFactory.createLineBorder(Color.black));
		//panel.add(s4);
		panel.add(t4);	
		
		Pane2.add(l4);
		
		s5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		t5.setBorder(BorderFactory.createLineBorder(Color.black));
		//panel.add(s5);
		panel.add(t5);
		
		Pane2.add(l5);
		
		s6.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		t6.setBorder(BorderFactory.createLineBorder(Color.black));
		//panel.add(s6);
		panel.add(t6);	
		
		Pane2.add(l6);
		
	/*	panel.add(t1);
		panel.add(t2);
		panel.add(t3);
		panel.add(t4);
		panel.add(t5);
		panel.add(t5);*/
		
		Pane1.add(button);
		Pane1.add(iButton);
		//Pane1.add(tButton);
		Pane1.add(fButton);
		
		button.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		iButton.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		tButton.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		fButton.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		slider.setAlignmentX(Component.BOTTOM_ALIGNMENT);

		/*for (TextUnit textUnit : extractedTextUnits) {
			buffer.append(textUnit + "\n");
			JLabel Label = new JLabel (textUnit.getText() + "\n");
			Panel.add (Label);
		}*/

		//System.out.println(maxNode.toPlainTextString());
		//System.out.println(maxScore);
		//frame.setSize(1200,1200);
		//Color c = new Color(0,255,255);
		//frame.setBackground(c);
		
		//panel.setBackground(c);
		//Pane1.setBackground(c);
	//	System.out.println(extractedTextAsString);
		
		//frame.add(panel, BorderLayout.CENTER);
		//frame.add(Pane1, BorderLayout.PAGE_END);
		//frame.add(Pane2, BorderLayout.EAST);
		//frame.setBackground(c);
		//frame.setVisible(true);
	}
	
	public Image TransformColorToTransparency(BufferedImage image, Color c1, Color c2)
	  {
	    // Primitive test, just an example
	    final int r1 = c1.getRed();
	    final int g1 = c1.getGreen();
	    final int b1 = c1.getBlue();
	    final int r2 = c2.getRed();
	    final int g2 = c2.getGreen();
	    final int b2 = c2.getBlue();
	    RGBImageFilter filter = new RGBImageFilter()
	    {
	      public final int filterRGB(int x, int y, int rgb)
	      {
	        int r = (rgb & 0xFF0000) >> 16;
	        int g = (rgb & 0xFF00) >> 8;
	        int b = rgb & 0xFF;
	        if (r >= r1 && r <= r2 &&
	            g >= g1 && g <= g2 &&
	            b >= b1 && b <= b2)
	        {
	          // Set fully transparent but keep color
	          return rgb & 0xFFFFFF;
	        }
	        return rgb;
	      }
	    };

	    if(image != null)
	    {
	    ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(ip);
	    }
	    return null;
	  }

	private Image TransformGrayToTransparency(BufferedImage image)
	  {
		if(image != null)
		{
			Image img1 = image.getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			BufferedImage image1 = new BufferedImage(600, 100, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bufImageGraphics = image1.createGraphics();
			bufImageGraphics.drawImage(img1, 0, 0, null);
			bufImageGraphics.dispose();
			RGBImageFilter filter = new RGBImageFilter()
	    {
	      public final int filterRGB(int x, int y, int rgb)
	      {
	        return (rgb << 8) & 0x0F000000;
	      }
	    };

	    if(image1 != null)
	    {
	    ImageProducer ip = new FilteredImageSource(image1.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(ip);
	    }
		}
	    return null;
	  }

	
	
	public static BufferedImage getScaledInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {
final int type = img.getTransparency() == Transparency.OPAQUE ?BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;BufferedImage ret = (BufferedImage) img;
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

	/***************************************************************************
	 * This function calculates node which has the highest
	 * value of the scoring function and sets maxNode. It also extracts the text
	 * from maxNode and sets extractedTextUnits and extractedSetAsString
	 * 
	 * @throws Exception
	 */
	String calculateMainNode() throws Exception {

		//TextScore.weightedScore();
		
		extractedTextUnits = new ArrayList<TextUnit>();
		nodeText = new ArrayList<String>();
		hNodeText = new HashSet<String>();
		imageUrl = new ArrayList<String>();
		
		double maxScore = Double.MIN_VALUE;
		int imgCount = 0;

		for (Node node : counts.keySet()) {
			
			Pair<Integer, Integer> count = counts.get(node);

			int numWords = count.getFirst();
			int numLinks = count.getSecond();

			Double score = SCORER.getScore(numLinks, numWords, maxText);
			if(!score.isNaN())
			{
			if(node.toPlainTextString().trim() != "")
			{
			cScore.setCount(node.toPlainTextString().trim(), score);
			start.put(node.toPlainTextString().trim(), (double) node.getStartPosition());
			Double fScore = new Double(score);
			sortedMap.put(fScore, node);
			if(node instanceof TagNode)
			{
				try{
				TagNode tagNode = (TagNode)node;
				String nodeName = tagNode.getTagName();
				if( nodeName.equalsIgnoreCase( "a" ) ){
					String s2 = tagNode.getAttribute( "href" );
					if(s2.startsWith("http://"))
					{
						imageUrl.add(s2);
					}
				}
				}catch(Exception e3)
				{
					System.out.println("Problem with url..");
				}
				
			}

			if (score > maxScore) {
				maxScore = score;
				maxNode = node;

			}
			}
			}
		}
		for(String s:cScore.topK(cScore.size()))
		{
			if(cScore.getCount(s) > 0)
			{
				SummaryModules.startingPos.put(imgCount, start.get(s));
				imgCount++;
			}
		}
		
		Set<Map.Entry<Double, Node>> set = sortedMap.entrySet();
		String[] bufferTemp = new String [1000];
		int tempCnt = 0;
		
		for (Map.Entry<Double, Node> me : set) {
			ArrayList<TextUnit> extractedTextUnitsTemp = new ArrayList<TextUnit>();
			extractedTextUnitsTemp.addAll(HTMLUtils.getTextUnits(me.getValue()));
			StringBuffer bufTemp = new StringBuffer();
			for (TextUnit textUnit : extractedTextUnitsTemp) {
				bufTemp.append(textUnit + "\n");
			}
			bufferTemp[tempCnt++] = bufTemp.toString();
			String sbufTemp = bufTemp.toString();
			nodeText.add(sbufTemp);
			hNodeText.add(sbufTemp);
		}
		
		
		
		return null;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		/*int tempCnt = cnt - 6;
		System.out.println("cnt" + cnt);

		/*for (int i = cnt; i < cnt + 5; i++) {
			ArrayList<TextUnit> extractedTextUnitsTemp = new ArrayList<TextUnit>();
			try {
				extractedTextUnitsTemp.addAll();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			StringBuffer bufTemp = new StringBuffer();
			for (TextUnit textUnit : extractedTextUnitsTemp) {
				bufTemp.append(textUnit + "\n");
			}
			bufferTemp[tempCnt] = bufTemp.toString();
			System.out.println(me.getKey());
			System.out.println(bufferTemp[tempCnt++]);
			cnt++;
		}
		
		try {
			extractedTextUnits.addAll(HTMLUtils.getTextUnits(maxNode));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		StringBuffer buffer = new StringBuffer();
		int numLabels = 6;
		System.out.println(numLabels);
		Container Panel;
		Panel = getContentPane ();
		LayoutManager Layout = new GridLayout (numLabels, 1);
		Panel.setLayout ( Layout );
		Button button = new Button("Submit"); 
		button.setVisible(true);
		
		//Panel.add (Label);
		Panel.add(button); 
		*/
		String cmd = e.getActionCommand();  
		if(cmd.equals("Next"))
		{
			final String slTemp1 = nodeText.get(1 + cnt) + "\n";
			t1.setText(slTemp1);
			
			final String slTemp2 = nodeText.get(2 + cnt) + "\n";
			t2.setText(slTemp2);
			
			final String slTemp3 = nodeText.get(3 + cnt) + "\n";
			t3.setText(slTemp3);
			
			final String slTemp4 = nodeText.get(4 + cnt) + "\n";
			t4.setText(slTemp4);
			
			final String slTemp5 = nodeText.get(5 + cnt) + "\n";
			t5.setText(slTemp5);
			
			final String slTemp6 = nodeText.get(6 + cnt) + "\n";
			t6.setText(slTemp6);
			
			/*l1.setText(slTemp1);
			l2.setText(slTemp2);
			l3.setText(slTemp3);
			l4.setText(slTemp4);
			l5.setText(slTemp5);
			l6.setText(slTemp6);
			*/
			
		/*	l1.setIcon(null);
			l2.setIcon(null);
			l3.setIcon(null);
			l4.setIcon(null);
			l5.setIcon(null);
			l6.setIcon(null);
			cnt = cnt + 6;*/
			
			k = 1;
			img = new BufferedImage[100];
			Parser parser = null;
			try {
				String s1 = imageUrl.get(cnt2);
				System.out.println(s1);
				parser = new Parser(s1);
			} catch (ParserException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			NodeList list = null;
			try {
				list = parser.parse(new TagNameFilter("IMG"));
			} catch (ParserException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			for (SimpleNodeIterator iterator = list.elements(); iterator
					.hasMoreNodes();) {
				try {
					Tag tag = (Tag) iterator.nextNode();
					URL url = new URL(tag.getAttribute("src"));
					try {
						BufferedImage imgTemp = ImageIO.read(url);
						if(imgTemp!=null)
						{
							img[k++] = imgTemp;
						}
					} catch (Exception e1) {
						System.out.println("The image was not loaded.");
					}
				} catch (Exception e1) {
					System.out.println("Improper url");
				}
			}
			
			final String sUrl = parser.getURL();
			
			l1.addMouseListener(new java.awt.event.MouseAdapter() {
				 //Frame imgSummary = new JFrame("Image Summary");
		    	  
			    public void mouseEntered(java.awt.event.MouseEvent evt) {
			    	System.out.println("in");
			    	
			    	try{
			    	/*  JFrame imgSummary = new JFrame("Image Summary");
			    	  JPanel Pane3 = new JPanel();
			    	  Pane3.setLayout(new GridLayout (6, 1));	
			    	  imgSummary.setSize(1200,1200);
			  		
			    	  imgSummary.add(Pane3);
			    	  Color c = new Color(0,255,255);
			    	  imgSummary.setBackground(c);
			    	  imgSummary.setVisible(true);
			    	 
			    	  JLabel jl1 = new JLabel();
			    	  JLabel jl2 = new JLabel();
			    	  JLabel jl3 = new JLabel();
			    	  JLabel jl4 = new JLabel();
			    	  JLabel jl5 = new JLabel();
			    	  JLabel jl6 = new JLabel();
			    	  
			    	  if(img[1] != null)
						{
						Image newimg1 = img[1].getScaledInstance(1200, 100, java.awt.Image.SCALE_SMOOTH);
						ImageIcon nwImg1 = new ImageIcon(newimg1);
						jl1.setIcon(nwImg1);
						}
						if(img[2] != null)
						{
						Image newimg2 = img[2].getScaledInstance(1200, 100, java.awt.Image.SCALE_SMOOTH);
						ImageIcon nwImg2 = new ImageIcon(newimg2);
						jl2.setIcon(nwImg2);
						}
						if(img[3] != null)
						{
						Image newimg3 = img[3].getScaledInstance(1200, 100, java.awt.Image.SCALE_SMOOTH);
						ImageIcon nwImg3 = new ImageIcon(newimg3);
						jl3.setIcon(nwImg3);
						}
						if(img[4] != null)
						{
						Image newimg4 = img[4].getScaledInstance(1200, 100, java.awt.Image.SCALE_SMOOTH);
						ImageIcon nwImg4 = new ImageIcon(newimg4);
						jl4.setIcon(nwImg4);
						}
						if(img[5] != null)
						{
						Image newimg5 = img[5].getScaledInstance(1200, 100, java.awt.Image.SCALE_SMOOTH);
						ImageIcon nwImg5 = new ImageIcon(newimg5);
						jl5.setIcon(nwImg5);
						}
						if(img[6] != null)
						{
						Image newimg6 = img[6].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
						ImageIcon nwImg6 = new ImageIcon(newimg6);
						jl6.setIcon(nwImg6);
						}
						
						Pane3.add(jl1);
						Pane3.add(jl2);
						Pane3.add(jl3);
						Pane3.add(jl4);
						Pane3.add(jl5);
						Pane3.add(jl6);
						
						imgSummary.add(Pane3);
						imgSummary.setVisible(true);
						*/
			    		
			    		 // FileWriter fstream = new FileWriter("out.jpg");
			    	  	 // BufferedWriter out1 = new BufferedWriter(fstream);
			    		JPanel Pane3 = new JPanel();
			    		JLabel jlt = new JLabel();
			    		/*String output = null;
			    	      System.out.println("url" + sUrl);
			    	      URL tUrl = new URL(sUrl);
			    	      output = getPageContent(tUrl);
			    	      out1.write(output);
			    	      
			    	      
			    	      //create a BufferedImage object
			    	       BufferedImage image = new BufferedImage(1200, 100,   BufferedImage.TYPE_INT_RGB);
			    	        
			    	        //calling createGraphics() to get the Graphics2D
			    	        Graphics2D g = image.createGraphics();
			    	        
			    	        //set color and other parameters
			    	        g.setColor(Color.WHITE);
			    	        g.fillRect(0, 0, 1200, 100);
			    	        g.setColor(Color.BLACK);
			    	             
			    	       g.drawString(output, 1200, 100);
			    	       
			    	      //releasing resources
			    	      g.dispose();
			    	       
			    	        //creating the file
			    	       ImageIO.write(image, "jpeg", new File("out.jpg"));*/
			    	       BufferedImage ioTemp = ImageIO.read(new File("out.jpg"));
			    	      
			    	      if(ioTemp !=null)
			    	      jlt.setIcon(new ImageIcon(ioTemp));
			    	      	Pane3.add(jlt);
			    	     // imgSummary.setSize(1200,1200);
			    	     // imgSummary.add(Pane3);
						 // imgSummary.setVisible(true);
						  
			    	}catch(Exception e)
			    	{
			    		e.printStackTrace();
			    	}
			        }
			 
			    public void mouseExited(java.awt.event.MouseEvent evt) {
			    	   //	imgSummary.dispose();
			    }
			});
			
		/*	l1.setIcon(null);
			l2.setIcon(null);
			l3.setIcon(null);
			l4.setIcon(null);
			l5.setIcon(null);
			l6.setIcon(null); */
			
			/*
			System.out.println(k);
			int i = 0;
			int cnt1 = 0;
			while(cnt1 != 6)
			{
			if(img[i++] != null)
				{
	        	Image newimg = img[i].getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH);
	        	l1.setIcon(new ImageIcon(newimg));
	        	cnt1++;
				}
			if(i > 20)
				break;
			}
			System.out.println(i);
			System.out.println(cnt1);
			*/
			if(img[1] != null)
			{
			Image newimg1 = img[1].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg1 = new ImageIcon(newimg1);
			l1.setIcon(nwImg1);
			}
			if(img[2] != null)
			{
			Image newimg2 = img[2].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg2 = new ImageIcon(newimg2);
			l2.setIcon(nwImg2);
			}
			if(img[3] != null)
			{
			Image newimg3 = img[3].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg3 = new ImageIcon(newimg3);
			l3.setIcon(nwImg3);
			}
			if(img[4] != null)
			{
			Image newimg4 = img[4].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg4 = new ImageIcon(newimg4);
			l4.setIcon(nwImg4);
			}
			if(img[5] != null)
			{
			Image newimg5 = img[5].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg5 = new ImageIcon(newimg5);
			l5.setIcon(nwImg5);
			}
			if(img[6] != null)
			{
			Image newimg6 = img[6].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg6 = new ImageIcon(newimg6);
			l6.setIcon(nwImg6);
			}
			cnt2++;
			cnt = cnt + 6;
			
		}
		if(cmd.equals("Text"))
		{
			l1.setText(nodeText.get(1 + cnt) + "\n");
			l2.setText(nodeText.get(2 + cnt) + "\n");
			l3.setText(nodeText.get(3 + cnt) + "\n");
			l4.setText(nodeText.get(4 + cnt) + "\n");
			l5.setText(nodeText.get(5 + cnt) + "\n");
			l6.setText(nodeText.get(6 + cnt) + "\n");
			cnt =  cnt + 6;
		}
		if(cmd.equals("Image"))
		{
			l1.setIcon(null);
			l2.setIcon(null);
			l3.setIcon(null);
			l4.setIcon(null);
			l5.setIcon(null);
			l6.setIcon(null);
			k = 1;
			img = new BufferedImage[100];
			Parser parser = null;
			try {
				String s1 = imageUrl.get(cnt2+2);
				System.out.println(s1);
				parser = new Parser(s1);
			} catch (ParserException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			NodeList list = null;
			try {
				list = parser.parse(new TagNameFilter("IMG"));
			} catch (ParserException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			for (SimpleNodeIterator iterator = list.elements(); iterator
					.hasMoreNodes();) {
				try {
					Tag tag = (Tag) iterator.nextNode();
					URL url = new URL(tag.getAttribute("src"));
					try {
						BufferedImage imgTemp = ImageIO.read(url);
						if(imgTemp!=null)
						{
							img[k++] = imgTemp;
						}
					} catch (Exception e1) {
						System.out.println("The image was not loaded.");
					}
				} catch (Exception e1) {
					System.out.println("Improper url");
				}
			}
			/*
			System.out.println(k);
			int i = 0;
			int cnt1 = 0;
			while(cnt1 != 6)
			{
			if(img[i++] != null)
				{
	        	Image newimg = img[i].getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH);
	        	l1.setIcon(new ImageIcon(newimg));
	        	cnt1++;
				}
			if(i > 20)
				break;
			}
			System.out.println(i);
			System.out.println(cnt1);
			*/
			if(img[1] != null)
			{
			Image newimg1 = img[1].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg1 = new ImageIcon(newimg1);
			l1.setIcon(nwImg1);
			}
			if(img[2] != null)
			{
			Image newimg2 = img[2].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg2 = new ImageIcon(newimg2);
			l2.setIcon(nwImg2);
			}
			if(img[3] != null)
			{
			Image newimg3 = img[3].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg3 = new ImageIcon(newimg3);
			l3.setIcon(nwImg3);
			}
			if(img[4] != null)
			{
			Image newimg4 = img[4].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg4 = new ImageIcon(newimg4);
			l4.setIcon(nwImg4);
			}
			if(img[5] != null)
			{
			Image newimg5 = img[5].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg5 = new ImageIcon(newimg5);
			l5.setIcon(nwImg5);
			}
			if(img[6] != null)
			{
			Image newimg6 = img[6].getScaledInstance(600, 100, java.awt.Image.SCALE_SMOOTH);
			ImageIcon nwImg6 = new ImageIcon(newimg6);
			l6.setIcon(nwImg6);
			}
			cnt2++;
		}
		if(cmd.equals("Fade"))
		{
			Image transpImg1 = TransformGrayToTransparency(img[1]);
        	if(transpImg1 != null)
        	l1.setIcon(new ImageIcon(transpImg1));
        	
        	Image transpImg2 = TransformGrayToTransparency(img[2]);
        	if(transpImg2 != null)
        	l2.setIcon(new ImageIcon(transpImg2));
        	
        	Image transpImg3 = TransformGrayToTransparency(img[3]);
        	if(transpImg3 != null)
        	l3.setIcon(new ImageIcon(transpImg3));
        	
        	Image transpImg4 = TransformGrayToTransparency(img[4]);
        	if(transpImg4 != null)
        	l4.setIcon(new ImageIcon(transpImg4));
        	
        	Image transpImg5 = TransformGrayToTransparency(img[5]);
        	if(transpImg5 != null)
        	l5.setIcon(new ImageIcon(transpImg5));
        	
        	Image transpImg6 = TransformGrayToTransparency(img[6]);
        	if(transpImg6 != null)
        	l6.setIcon(new ImageIcon(transpImg6));     	
		}
		if(cmd.equals("Snapshot"))
		{
			 try {
				Desktop.getDesktop().open( new File("/Users/siddhisoman/Desktop/news.html"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	         try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         Robot robot = null;
				try {
					robot = new Robot();
				} catch (AWTException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         Rectangle area = new Rectangle(0, 0, 700, 700);
	         Image image = robot.createScreenCapture(area);
	         JFrame f2 = new JFrame("Image");
	         Container panel1 = getContentPane ();
	         JLabel tempLabel =  new JLabel();
	         if(image != null)
	         {
	        	 tempLabel.setIcon(new ImageIcon(image));
	         	panel1.add(tempLabel);
	         	f2.add(panel1);
	         	f2.setSize(400,400);
	         	f2.setVisible(true);
	         }
	         
		}
	/*	int numLabels = 6;
		for (int j = 0; j < 6; j++)
		{
			JLabel Label = new JLabel (nodeText.get(j + cnt) + "\n");
			Panel.add (Label);
		}*/
/*
		for (TextUnit textUnit : extractedTextUnits) {
			buffer.append(textUnit + "\n");
			JLabel Label = new JLabel (textUnit.getText() + "\n");
			Panel.add (Label);
		}

		extractedTextAsString = buffer.toString();
		//System.out.println(maxNode.toPlainTextString());
		//System.out.println(maxScore);*/
	//	button.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		
	//	System.out.println(extractedTextAsString);
		
		
		//button.addActionListener(this);
		
}


	
	public String getExtractedText() {
		return extractedTextAsString;
	}

	public ArrayList<TextUnit> getExtractedTextUnits() {
		return extractedTextUnits;
	}
	
	public static void summarise (String file) throws Exception
	{
		//String file = "/Users/siddhisoman/Desktop/RAwork/usnews"
		//		+ ".html";
	    CollageSummary test = new CollageSummary(file);

	    ScoringFunction sf = new NormalizedSF(0.95, 0.05);
	    CollageSummary.setScoringFunction(sf);
	    test.process();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		//String file = "data/cedarExport/taggedDocuments/document_1590.raw"
			//	+ ".html";
		String file = "/Users/siddhisoman/Desktop/RAwork/uspapers-02-2008-text"
					+ ".html";
		//String file = "http://www.usnews.com/";
				//			+ ".html";
		CollageSummary test = new CollageSummary(file);
/*
		ScoringFunction sf = new NormalizedSF(0.95, 0.05);
		ProcessPage.setScoringFunction(sf);
		test.process();*/
		CollageSummary.summarise(file);

		//System.out.println("ARTICLE");

		/*
		  ArrayList<TextUnit> textUnits = test.getTextUnits();
		  
		  for (TextUnit textUnit: textUnits) {
		  System.out.println("["+textUnit+"]"+ textUnit.getSize());
		  System.out.println("--------------"); }
		 */
	}
	
	
	/** Fetch the HTML content of the page as simple text.   */
	  public String getPageContent(URL fURL) {
	    String result = null;
	    URLConnection connection = null;
	    try {
	      connection =  fURL.openConnection();
	      Scanner scanner = new Scanner(connection.getInputStream());
	      scanner.useDelimiter(END_OF_INPUT);
	      result = scanner.next();
	    }
	    catch ( IOException ex ) {
	      System.out.println("Cannot open connection to " + fURL.toString());
	    }
	    return result;
	  }

	  /** Fetch HTML headers as simple text.  */
	  public String getPageHeader(URL fURL){
	    StringBuilder result = new StringBuilder();

	    URLConnection connection = null;
	    try {
	      connection = fURL.openConnection();
	    }
	    catch (IOException ex) {
	    	System.out.println("Cannot open connection to URL: " + fURL);
	    }

	    int headerIdx = 0;
	    String headerKey = null;
	    String headerValue = null;
	    while ( (headerValue = connection.getHeaderField(headerIdx)) != null ) {
	      headerKey = connection.getHeaderFieldKey(headerIdx);
	      if ( headerKey != null && headerKey.length()>0 ) {
	    	  if(((headerKey.equals("Date")) || (headerKey.equals("Set-Cookie"))))
	    	  {
	    		  
	    	  }
	    	  else
	    	  {
	        result.append( headerKey );
	        result.append(" : ");
	    	  }
	      }
	      if((headerKey != null)&&((headerKey.equals("Date")) || (headerKey.equals("Set-Cookie"))))
	      {
	      }
	      else
	      {
	    	  result.append( headerValue );
	          result.append(NEWLINE);
	      }
	      headerIdx++;
	    }
	    System.out.println(result.toString());
	    return result.toString();
	  }
	  
	  private static final String HTTP = "http";
	  private static final String HEADER = "header";
	  private static final String CONTENT = "content";
	  private static final String END_OF_INPUT = "\\Z";
	  private static final String NEWLINE = System.getProperty("line.separator");

}
