package edu.stanford.arcspread.mypackage.extraction;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;
import edu.stanford.arcspread.mypackage.dataStructures.NoScriptTag;
import edu.stanford.arcspread.mypackage.utils.Counter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class SummaryModules extends JPanel{

	private static final int NUMBEROFFRAMES = 4;
	private static final int NUMBEROFPAGES = 4;
	private static final int NUMBEROFTIMESERIESSITES = 2;
	private static JFrame frame;
	private JSplitPane splitPane1;
	private Checkbox check;
	private Checkbox check1;
	private Checkbox check2;
	private JTextField tfield;
	private JButton sButton;
	
	private static List<String> impText;
    private static HashMap<String, BufferedImage> images;
    private static ArrayList<BufferedImage> img1;
    private static HashMap<String,List<String>> impTextCollage;
    private static HashMap<String,ArrayList<String>> imagesCollage;
    private Counter<String> totalScore = new Counter<String>();
    
    private Counter<String> cPagePosition = new Counter<String>();
    private Counter<String> cHeight = new Counter<String>();
    private Counter<String> cStart = new Counter<String>();
	
	
	private HashSet<String> names = new HashSet<String>();
	private String prevName = "";
	
	static HashMap<Integer, Double> startingPos = new HashMap<Integer, Double>();
	String pathFile = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
	String preFile = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/";

	
	//Gets the important text and images for a single webpage
	public void webPageSummary(String file)
	{
		HashMap<String,BufferedImage> images = new HashMap<String,BufferedImage>();
		startingPos.clear();
		try {
			CollageSummary.summarise(file);
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		HashMap<String, Double> heightScore = new HashMap<String,Double>();
		HashMap<String, Double> pagePositionScore = new HashMap<String,Double>();
		HashMap<String, Double> startScore = new HashMap<String,Double>();
		ArrayList<String> s1 = new ArrayList<String>();
		impText = CollageSummary.cScore.topK(CollageSummary.cScore
				.size());
		try {
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.put("NOSCRIPT", new NoScriptTag());
			Parser parser = new Parser(file);
			parser.setNodeFactory(factory);
			NodeList list = parser.parse(new TagNameFilter("IMG"));
			@SuppressWarnings("unused")
			int total = 0;
			@SuppressWarnings("unused")
			int count = 0;
            for (SimpleNodeIterator iterator = list.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				if (s != null) {
					if (!s.contains("doubleclick") && !s.contains("specificclick")) // adsense and other domains
					{
						total+=tag.getStartPosition();
						count++;
						double tmpsCount = 0;
						double startPos = tag.getStartPosition();
						for(int j = 0; j < 4; j++)
						{
							tmpsCount += (startingPos.get(j) - startPos);
						}
						if (s.startsWith("http://"))
							s = s.replace("http://", pathFile + "/");
						else if (s.startsWith("/"))
							s = pathFile + s;
						else if (s.startsWith(".."))
							s = pathFile + s.substring(2);
						else if (s.startsWith("."))
							s = pathFile + s.substring(1);
						else if (!s.startsWith(preFile))
							s = preFile + s;
						cPagePosition.setCount(s, tag.getStartPosition());
						cStart.setCount(s, tmpsCount);
						try {
						    InputStream is = new BufferedInputStream(
									new FileInputStream(s));
							BufferedImage tempImage = ImageIO.read(is);
							images.put(s, tempImage);
							s1.add(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
            HashMap<String,BufferedImage> imageTemp = new HashMap<String,BufferedImage>();
            for (String sTemp:images.keySet()) {
				if (images.get(sTemp) != null) {
					if((images.get(sTemp).getWidth() < 20)  && (images.get(sTemp).getHeight() < 20))
					{
						imageTemp.put(sTemp, images.get(sTemp));
						cPagePosition.remove(sTemp);
						s1.remove(sTemp);
					}
					else
					{
						Double dim = ((double)images.get(sTemp).getWidth())*((double)images.get(sTemp).getHeight());
						cHeight.setCount(sTemp, dim);
					}
				}
			}
            for(String sTmp:imageTemp.keySet())
			{
				images.remove(sTmp);
			}          
            double cnt = 0, cnt1 = 0, cnt2 = 0;
			if(cPagePosition.size() > 0)
			{
			for(String tempS:cPagePosition.topK(cPagePosition.size()))
			{
				cnt++;
				Double tempScore = (double)cnt/cPagePosition.size();
				pagePositionScore.put(tempS, tempScore);
			}
			}
			if(cHeight.size() > 0)
			{
			for(String tempS1:cHeight.topK(cHeight.size()))
			{
				cnt1++;
				Double tempScore = 1-((double)cnt1/cHeight.size());
				heightScore.put(tempS1, tempScore);
			}
			}
			if(cStart.size() > 0)
			{
			for(String tempS3:cStart.topK(cStart.size()))
			{
				cnt2++;
				Double tempScore = (double)cnt2/cStart.size();
				startScore.put(tempS3, tempScore);
			}
			}
			for(String tempS2:s1)
			{
			Double tScore = new Double(0);
			if (startScore == null && heightScore == null && pagePositionScore == null) 
			{
				tScore = (double) 0;
			} 
			else 
			{
				if (heightScore != null && startScore != null && pagePositionScore != null) 
				{
					if(pagePositionScore.get(tempS2) != null)
						tScore += (0.55) * pagePositionScore.get(tempS2);
					if(startScore.get(tempS2) != null)
						tScore += (0.15) * startScore.get(tempS2);
					if(heightScore.get(tempS2) != null)
						tScore += (0.3) * heightScore.get(tempS2);
				} 
				else if (heightScore != null && pagePositionScore != null) 
				{
					tScore = (0.35) * heightScore.get(tempS2) + (0.65)
							* pagePositionScore.get(tempS2);
				} 
				else if (startScore != null && pagePositionScore != null) 
				{
					tScore = (0.65) * pagePositionScore.get(tempS2) + (0.35)
							* startScore.get(tempS2);
				} 
				else if (heightScore != null && startScore != null) 
				{
					tScore = (0.65) * heightScore.get(tempS2) + (0.35)
							* startScore.get(tempS2);
				} 
				else if (heightScore != null) 
				{
					tScore = heightScore.get(tempS2);
				} 
				else if (pagePositionScore != null) 
				{
					tScore = pagePositionScore.get(tempS2);
				} 
				else if (startScore != null) 
				{
					tScore = startScore.get(tempS2);
				} 
				else 
				{
					tScore = (double) 0;
				}
			}
			totalScore.setCount(tempS2, tScore);
			}
			img1.clear();
			for(String tempS3:totalScore.topK(totalScore.size()))
			{
				img1.add(images.get(tempS3));
			}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	}

	//Gets the visual collage summary for the important text and images for a single webpage
	public void visualWebPageSummary(String file)
	{
		webPageSummary(file);
		String[] tokens = file.split(".html");
	    String[] toks = tokens[0].split("/");
	    String frameName = toks[toks.length - 1] + ".html";
	    try{
	    	JLabel []imgTxtLabel = new JLabel[10];
	    	JScrollPane []textScrollPane = new JScrollPane[10];
	    	int i = 0;
	    	
	    	for(int j = 0; j < NUMBEROFFRAMES; j++)
	    	{
 			Pattern p = Pattern.compile("[a-zA-Z0-9 ,&.;:-]*$");
 			while (!p.matcher(impText.get(i).trim()).matches()) {
 				i++;
 			}
 			System.out.println(i);
 			imgTxtLabel[j] = new JLabel(String.format(
 					"<html><div WIDTH=%d>%s</div><html>", 500, impText.get(i)
 							.trim()), new ImageIcon(img1.get(j)),
 					JLabel.CENTER);
 			i++;
 			imgTxtLabel[j]
 					.setFont(imgTxtLabel[j].getFont().deriveFont(Font.ITALIC));
 			imgTxtLabel[j].setHorizontalAlignment(JLabel.CENTER);
 			textScrollPane[j] = new JScrollPane(imgTxtLabel[j]);
 			textScrollPane[j].addMouseListener(mouse);
 			textScrollPane[j].setName(file);
	    	}
 			
	    	JSplitPane []tsplitPane = new JSplitPane[10];
	    	
	    	tsplitPane[0] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
 					textScrollPane[0], textScrollPane[1]);
 			tsplitPane[0].setOneTouchExpandable(true);
 			tsplitPane[0].setDividerLocation((400)/(NUMBEROFFRAMES));
 			tsplitPane[0].setPreferredSize(new Dimension(1200,800/NUMBEROFFRAMES));
 			tsplitPane[0].setResizeWeight(0.5d);
	    	
	    	for(int k = 1; k <= NUMBEROFFRAMES - 1; k++)
	    	{
	    		
	 			tsplitPane[k] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
	 					tsplitPane[k-1], textScrollPane[k+1]);
	 			tsplitPane[k].setOneTouchExpandable(true);
	 			tsplitPane[k].setDividerLocation(((k)*800)/3);
	 			tsplitPane[k].setPreferredSize(new Dimension(1200, 800*k/3));
	 			tsplitPane[k].setResizeWeight(0.5d);
	    	}
 			
 			frame.setName(frameName);
 			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 			frame.setContentPane(tsplitPane[NUMBEROFFRAMES - 1]);
 			frame.pack();
 			frame.setVisible(true);

	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	
	//Gets the important text and images for a list of webpages
	public void webPageCollageSummary(String[] fileList)
	{
		for (String file:fileList)
		{
		try {
			CollageSummary.summarise(file);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		totalScore.clear();
		
		HashMap<String, Double> heightScore = new HashMap<String,Double>();
		HashMap<String, Double> pagePositionScore = new HashMap<String,Double>();
		HashMap<String, Double> startScore = new HashMap<String,Double>();
		List<String> impText1 = CollageSummary.cScore.topK(CollageSummary.cScore
				.size());
		impTextCollage.put(file, impText1);
		try {
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.put("NOSCRIPT", new NoScriptTag());
			Parser parser = new Parser(file);
			parser.setNodeFactory(factory);
			NodeList list = parser.parse(new TagNameFilter("IMG"));
		    ArrayList<String> img1 = new ArrayList<String>();
		    ArrayList<String> s1 = new ArrayList<String>();
		
			for (SimpleNodeIterator iterator = list.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				@SuppressWarnings("unused")
				int total = 0;
				@SuppressWarnings("unused")
				int count = 0;
				if (s != null) {
					if (!s.contains("doubleclick") && !s.contains("specificclick")) // adsense and other domains
					{
						total+=tag.getStartPosition();
						count++;
						double tmpsCount = 0;
						double startPos = tag.getStartPosition();
						for(int j = 0; j < 4; j++)
						{
							tmpsCount += (startingPos.get(j) - startPos);
						}	
						if (s.startsWith("http://"))
							s = s.replace("http://", pathFile + "/");
						else if (s.startsWith("/"))
							s = pathFile + s;
						else if (s.startsWith(".."))
							s = pathFile + s.substring(2);
						else if (s.startsWith("."))
							s = pathFile + s.substring(1);
						else if (!s.startsWith(preFile))
							s = preFile + s;
						
						cPagePosition.setCount(s, tag.getStartPosition());
						cStart.setCount(s, tmpsCount);
						
						try {
						    InputStream is = new BufferedInputStream(
									new FileInputStream(s));
							BufferedImage tempImage = ImageIO.read(is);
							images.put(s, tempImage);
							s1.add(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			HashMap<String, BufferedImage> imageTemp = new HashMap<String,BufferedImage>();
			for(String sTemp:images.keySet())
			{
				if (images.get(sTemp) != null) {
					if((images.get(sTemp).getWidth() < 20)  && (images.get(sTemp).getHeight() < 20))
					{
						imageTemp.put(sTemp, images.get(sTemp));
						cPagePosition.remove(sTemp);
						s1.remove(sTemp);
					}
					else
					{
						Double dim = ((double)images.get(sTemp).getWidth())*((double)images.get(sTemp).getHeight());
						cHeight.setCount(sTemp, dim);
					}
				}
			}
			for(String sTmp:imageTemp.keySet())
			{
				images.remove(sTmp);
			}          
            double cnt = 0, cnt1 = 0, cnt2 = 0;
			if(cPagePosition.size() > 0)
			{
			for(String tempS:cPagePosition.topK(cPagePosition.size()))
			{
				cnt++;
				Double tempScore = (double)cnt/cPagePosition.size();
				pagePositionScore.put(tempS, tempScore);
			}
			}
			if(cHeight.size() > 0)
			{
			for(String tempS1:cHeight.topK(cHeight.size()))
			{
				cnt1++;
				Double tempScore = 1-((double)cnt1/cHeight.size());
				heightScore.put(tempS1, tempScore);
			}
			}
			if(cStart.size() > 0)
			{
			for(String tempS3:cStart.topK(cStart.size()))
			{
				cnt2++;
				Double tempScore = (double)cnt2/cStart.size();
				startScore.put(tempS3, tempScore);
			}
			}
			for(String tempS2:s1)
			{
			Double tScore = new Double(0);
			if (startScore == null && heightScore == null && pagePositionScore == null) 
			{
				tScore = (double) 0;
			} 
			else 
			{
				if (heightScore != null && startScore != null && pagePositionScore != null) 
				{
					if(pagePositionScore.get(tempS2) != null)
						tScore += (0.55) * pagePositionScore.get(tempS2);
					if(startScore.get(tempS2) != null)
						tScore += (0.15) * startScore.get(tempS2);
					if(heightScore.get(tempS2) != null)
						tScore += (0.3) * heightScore.get(tempS2);
				} 
				else if (heightScore != null && pagePositionScore != null) 
				{
					tScore = (0.35) * heightScore.get(tempS2) + (0.65)
							* pagePositionScore.get(tempS2);
				} 
				else if (startScore != null && pagePositionScore != null) 
				{
					tScore = (0.65) * pagePositionScore.get(tempS2) + (0.35)
							* startScore.get(tempS2);
				} 
				else if (heightScore != null && startScore != null) 
				{
					tScore = (0.65) * heightScore.get(tempS2) + (0.35)
							* startScore.get(tempS2);
				} 
				else if (heightScore != null) 
				{
					tScore = heightScore.get(tempS2);
				} 
				else if (pagePositionScore != null) 
				{
					tScore = pagePositionScore.get(tempS2);
				} 
				else if (startScore != null) 
				{
					tScore = startScore.get(tempS2);
				} 
				else 
				{
					tScore = (double) 0;
				}
			}
			totalScore.setCount(tempS2, tScore);
			}	
			for(String tempS3:totalScore.topK(totalScore.size()))
			{
				img1.add(tempS3);
			}		
			imagesCollage.put(file, img1);
			s1.clear();
			CollageSummary.cScore.clear();
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	}
	}
	
	//Gets the visual collage summary for the important text and images for a list of webpages
	public void visualWebPageCollageSummary(String[] fileList)
	{
		webPageCollageSummary(fileList);
		String frameName = "Collage Summary"; 
	    try{
	    	
	    	JLabel []imgTxtLabel = new JLabel[10];
	    	JScrollPane []textScrollPane = new JScrollPane[10];	
	    	int i = 0;
	    	for(int j = 0; j < NUMBEROFPAGES; j++)
	    	{
 			Pattern p = Pattern.compile("[a-zA-Z0-9 ,&.;:-]*$");
 			while (!p.matcher(impTextCollage.get(fileList[j]).get(i).trim()).matches()) {
				i++;
			}
 			imgTxtLabel[j] = new JLabel(String.format(
 					"<html><div WIDTH=%d>%s</div><html>", 500, impTextCollage.get(fileList[j]).get(i).trim()), new ImageIcon(images.get(imagesCollage.get(fileList[j]).get(0))),
 					JLabel.CENTER);
 			i++;
 			imgTxtLabel[j]
 					.setFont(imgTxtLabel[j].getFont().deriveFont(Font.ITALIC));
 			imgTxtLabel[j].setHorizontalAlignment(JLabel.CENTER);
 			textScrollPane[j] = new JScrollPane(imgTxtLabel[j]);
 			textScrollPane[j].addMouseListener(mouse);
 			textScrollPane[j].setName(fileList[j]);
	    	}
 			
	    	JSplitPane []tsplitPane = new JSplitPane[10];
	    	tsplitPane[0] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
 					textScrollPane[0], textScrollPane[1]);
 			tsplitPane[0].setOneTouchExpandable(true);
 			tsplitPane[0].setDividerLocation((400)/(NUMBEROFPAGES));
 			tsplitPane[0].setPreferredSize(new Dimension(1200,800/NUMBEROFPAGES));
 			tsplitPane[0].setResizeWeight(0.5d);
	    	
	    	for(int k = 1; k <= NUMBEROFPAGES - 1; k++)
	    	{
	    		
	 			tsplitPane[k] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
	 					tsplitPane[k-1], textScrollPane[k+1]);
	 			tsplitPane[k].setOneTouchExpandable(true);
	 			tsplitPane[k].setDividerLocation(((k)*800)/3);
	 			tsplitPane[k].setPreferredSize(new Dimension(1200, 800*k/3));
	 			tsplitPane[k].setResizeWeight(0.5d);
	    	}
 			frame.setName(frameName);
 			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 			frame.setContentPane(tsplitPane[NUMBEROFPAGES - 1]);
 			frame.pack();
 			frame.setVisible(true);
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}

	public void visualTimeSeriesSummary(String[] fileList)
	{
		webPageCollageSummary(fileList);
		String frameName = "Collage Summary";
	    try{		
	    	JLabel []imgTxtLabel = new JLabel[10];
	    	JScrollPane []textScrollPane = new JScrollPane[10];
	    	int i = 0;
	    	for(int j = 0; j < NUMBEROFPAGES; j++)
	    	{
	 			Pattern p = Pattern.compile("[a-zA-Z0-9 ,&.;:-]*$");
	 			while (!p.matcher(impTextCollage.get(fileList[j]).get(i).trim()).matches()) {
					i++;
				}
 			imgTxtLabel[j] = new JLabel(String.format(
 					"<html><div WIDTH=%d>%s</div><html>", 500, impTextCollage.get(fileList[j]).get(i).trim()), new ImageIcon(images.get(imagesCollage.get(fileList[j]).get(0))),
 					JLabel.CENTER);
 			i++;
 			imgTxtLabel[j]
 					.setFont(imgTxtLabel[j].getFont().deriveFont(Font.ITALIC));
 			imgTxtLabel[j].setHorizontalAlignment(JLabel.CENTER);
 			textScrollPane[j] = new JScrollPane(imgTxtLabel[j]);
 			textScrollPane[j].addMouseListener(mouse);
 			textScrollPane[j].setName(fileList[j]);
 			
	    	}
 			JSplitPane []tsplitPane = new JSplitPane[20];
	    	int l = 0, r = 0;
	    	tsplitPane[0] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
 					textScrollPane[0], textScrollPane[1]);
 			tsplitPane[0].setOneTouchExpandable(true);
 			tsplitPane[0].setDividerLocation((400)/(NUMBEROFTIMESERIESSITES));
 			tsplitPane[0].setPreferredSize(new Dimension(1200,800/NUMBEROFTIMESERIESSITES));
 			tsplitPane[0].setResizeWeight(0.5d);
	    	
	    	for(int k = 1; k < NUMBEROFTIMESERIESSITES - 1; k++)
	    	{
	    		
	 			tsplitPane[k] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
	 					tsplitPane[k-1], textScrollPane[k+1]);
	 			tsplitPane[k].setOneTouchExpandable(true);
	 			tsplitPane[k].setDividerLocation(((k)*1600)/3);
	 			tsplitPane[k].setPreferredSize(new Dimension(1200, 1600*k/3));
	 			tsplitPane[k].setResizeWeight(0.5d);
	 			l = k;
	    	}	
	    	tsplitPane[l+1] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
 					textScrollPane[l+2], textScrollPane[l+3]);
 			tsplitPane[l+1].setOneTouchExpandable(true);
 			tsplitPane[l+1].setDividerLocation((400)/(NUMBEROFTIMESERIESSITES));
 			tsplitPane[l+1].setPreferredSize(new Dimension(1200,800/NUMBEROFTIMESERIESSITES));
 			tsplitPane[l+1].setResizeWeight(0.5d);
 			r = l+1;
	    	
	    	for(int k = r+1 ; k < (2*NUMBEROFTIMESERIESSITES) - 2; k++)
	    	{
	 			tsplitPane[k] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
	 					tsplitPane[k-1], textScrollPane[k+1]);
	 			tsplitPane[k].setOneTouchExpandable(true);
	 			tsplitPane[k].setDividerLocation(((k)*1600)/3);
	 			tsplitPane[k].setPreferredSize(new Dimension(1200, 1600*k/3));
	 			tsplitPane[k].setResizeWeight(0.5d);
	 			r = k;
	    	}
	    	JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tsplitPane[l],
 					tsplitPane[r]);
	    	splitPane1.setPreferredSize(new Dimension(1200, 800));
	    	splitPane1.setResizeWeight(0.5d);
	    	frame.setName(frameName);
 			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 			frame.setContentPane(splitPane1);
 			frame.pack();
 			frame.setVisible(true);
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}

	public SummaryModules() 
	{
		frame.setLayout(new FlowLayout());
		check = new Checkbox("Single Page Summary");
		check1 = new Checkbox("Collage Summary");
		check2 = new Checkbox("Time Series Summary");
		frame.add(check);
		frame.add(check1);
		frame.add(check2);
		Button button = new Button("Choose"); 
		button.addActionListener(action);
		frame.add(button);
	}
	
	private ActionListener action = new ActionListener() {
		
		public void actionPerformed(ActionEvent a) {
			String cmd = a.getActionCommand(); 
			if(cmd.equals("Choose"))
			{
				tfield = new JTextField();
				tfield.setSize(1000, 300);
				sButton = new JButton("Submit");
				sButton.addActionListener(action);
				frame.setContentPane(tfield);
				frame.getContentPane().add(sButton);
				frame.setLayout(new FlowLayout());
				frame.setSize(1100,800);
				images = new HashMap<String, BufferedImage>();
				img1 = new ArrayList<BufferedImage>();
				impText = new ArrayList<String>();
				imagesCollage = new HashMap<String, ArrayList<String>>();
				impTextCollage = new HashMap<String,List<String>>();
			}
			if(cmd.equals("Submit"))
			{
				if(check.getState())
				{
					visualWebPageSummary(tfield.getText());
				}
				else if(check1.getState())
				{
					String[] fileList = tfield.getText().split(",");
					for(String s:fileList)
						names.add(s);
					visualWebPageCollageSummary(fileList);
				}
				else if(check2.getState())
				{
					String[] fileList = new String[4];
					String[] fileList1 = tfield.getText().split(";");
					String[] fileList2 = fileList1[0].split(",");
					String[] fileList3 = fileList1[1].split(",");
					fileList[0] = fileList2[0];
					fileList[1] = fileList2[1];
					fileList[2] = fileList3[0];
					fileList[3] = fileList3[1];
					for(String s:fileList)
						names.add(s);
					visualTimeSeriesSummary(fileList);
				}
			}
		}
	};
	
	private MouseListener mouse = new MouseListener() {
		
       public void mouseClicked(MouseEvent e) {
    	  String name =  e.getComponent().getName();
    	  System.out.println(name);
    	   if(name.equals(prevName) || names.isEmpty())
    	   {
    		   try {
   				Desktop.getDesktop().open( new File(name));
   			} catch (IOException e1) {
   				e1.printStackTrace();
   			}
    	   }
    	   else if (names.contains(name))
    	   {  
    	    {
    	    HashMap<String, BufferedImage> images5 = new HashMap<String, BufferedImage>();
    		ArrayList<String> s5 = new ArrayList<String>();
    	    prevName = name;
    	    String[] tokens = name.split(".html");
    	    String[] toks = tokens[0].split("/");
    	    String frameName = toks[toks.length - 1] + ".html";
          try
            {
            	CollageSummary.cScore.clear();
    			String pathFile5 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
    			String preFile5 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/";
    			String file5 = name;

    			try {
    				CollageSummary.summarise(file5);
    			} catch (Exception e1) {
    				e1.printStackTrace();
    			}

    			List<String> impText3 = CollageSummary.cScore
    					.topK(CollageSummary.cScore.size());
    			PrototypicalNodeFactory factory3 = new PrototypicalNodeFactory();
    			factory3.put("NOSCRIPT", new NoScriptTag());
    			Parser parser3 = new Parser(file5);
    			parser3.setNodeFactory(factory3);
    			NodeList list3 = parser3.parse(new TagNameFilter("IMG"));
    			for (SimpleNodeIterator iterator = list3.elements(); iterator
    					.hasMoreNodes();) {
    				Tag tag = (Tag) iterator.nextNode();
    				String s = tag.getAttribute("src");
    				if (s != null) {
    					if (!s.contains("doubleclick") && !s.contains("specificclick")) // adsense and other domains
    					{
    						if (s.startsWith("http://"))
    							s = s.replace("http://", pathFile5 + "/");
    						else if (s.startsWith("/"))
    							s = pathFile5 + s;
    						else if (s.startsWith(".."))
    							s = pathFile5 + s.substring(2);
    						else if (s.startsWith("."))
    							s = pathFile5 + s.substring(1);
    						else if (!s.startsWith(preFile5))
    							s = preFile5 + s;
    						{
    							    InputStream is = new BufferedInputStream(
    										new FileInputStream(s));
    								BufferedImage tempImage = ImageIO.read(is);
    								images5.put(s, tempImage);
    								s5.add(s);				
    						}
    					}
    				}
    			}	
    			
    			int i = 0;
    			Pattern p = Pattern.compile("[a-zA-Z0-9 ,&.;:-]*$");
    			while (!p.matcher(impText3.get(i).trim()).matches()) {
    				i++;
    			}
    			JLabel imgTxtLabel1 = new JLabel(String.format(
    					"<html><div WIDTH=%d>%s</div><html>", 500, impText3.get(i++)
    							.trim()), new ImageIcon(images5.get(s5.get(0))),
    					JLabel.CENTER);
    			imgTxtLabel1
    					.setFont(imgTxtLabel1.getFont().deriveFont(Font.ITALIC));
    			imgTxtLabel1.setHorizontalAlignment(JLabel.CENTER);
    			JScrollPane textScrollPane1 = new JScrollPane(imgTxtLabel1);
    			textScrollPane1.addMouseListener(mouse);
    			textScrollPane1.setName(file5);

    			while (!p.matcher(impText3.get(i).trim()).matches()) {
    				i++;
    			}
    			JLabel imgTxtLabel2 = new JLabel(String.format(
    					"<html><div WIDTH=%d>%s</div><html>", 500, impText3
    							.get(i++).trim()), new ImageIcon(images5.get(s5
    					.get(1))), JLabel.CENTER);
    			imgTxtLabel2
    					.setFont(imgTxtLabel2.getFont().deriveFont(Font.ITALIC));
    			imgTxtLabel2.setHorizontalAlignment(JLabel.CENTER);
    			JScrollPane textScrollPane2 = new JScrollPane(imgTxtLabel2);
    			textScrollPane2.addMouseListener(mouse);
    			textScrollPane2.setName(file5);

    			while (!p.matcher(impText3.get(i).trim()).matches()) {
    				i++;
    			}
    			JLabel imgTxtLabel3 = new JLabel(String.format(
    					"<html><div WIDTH=%d>%s</div><html>", 500, impText3
    							.get(i++).trim()), new ImageIcon(images5.get(s5
    					.get(2))), JLabel.CENTER);
    			imgTxtLabel3
    					.setFont(imgTxtLabel3.getFont().deriveFont(Font.ITALIC));
    			imgTxtLabel3.setHorizontalAlignment(JLabel.CENTER);
    			JScrollPane textScrollPane3 = new JScrollPane(imgTxtLabel3);
    			textScrollPane3.addMouseListener(mouse);
    			textScrollPane3.setName(file5);

    			while (!p.matcher(impText3.get(i).trim()).matches()) {
    				i++;
    			}
    			JLabel imgTxtLabel4 = new JLabel(String.format(
    					"<html><div WIDTH=%d>%s</div><html>", 500, impText3
    							.get(i++).trim()), new ImageIcon(images5.get(s5
    					.get(3))), JLabel.CENTER);
    			imgTxtLabel4
    					.setFont(imgTxtLabel4.getFont().deriveFont(Font.ITALIC));
    			imgTxtLabel4.setHorizontalAlignment(JLabel.CENTER);
    			JScrollPane textScrollPane4 = new JScrollPane(imgTxtLabel4);
    			textScrollPane4.addMouseListener(mouse);
    			textScrollPane4.setName(file5);

    			JSplitPane tsplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    					textScrollPane1, textScrollPane2);
    			tsplitPane1.setOneTouchExpandable(true);
    			tsplitPane1.setDividerLocation(100);
    			
    			JSplitPane tsplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    					tsplitPane1, textScrollPane3);
    			tsplitPane2.setOneTouchExpandable(true);
    			tsplitPane2.setDividerLocation(250);
    			
    			splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tsplitPane2, textScrollPane4);
    			splitPane1.setOneTouchExpandable(true);
    			splitPane1.setDividerLocation(500);
    			splitPane1.setVisible(true);
    			splitPane1.setPreferredSize(new Dimension(1200, 800));
    			tsplitPane1.setPreferredSize(new Dimension(1200, 200));
    			tsplitPane2.setPreferredSize(new Dimension(1200, 600));
    			
    			JFrame frame1 = new JFrame(frameName);
    			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    			frame1.getContentPane().add(splitPane1);
    			splitPane1.setResizeWeight(0.5d);
    			tsplitPane1.setResizeWeight(0.5d);
    			tsplitPane2.setResizeWeight(0.5d);
    			frame1.pack();
    			frame1.setVisible(true);
            }catch(Exception e1)
            {
            	e1.printStackTrace();
            }
    	    }
    	   }
        }

	public void mouseEntered(MouseEvent arg0) {
		//TODO
	}

	public void mouseExited(MouseEvent arg0) {
		//TODO
	}

	public void mousePressed(MouseEvent arg0) {
		//TODO
	}

	public void mouseReleased(MouseEvent arg0) {
		//TODO
	}
	};


	private static void createAndShowGUI() {
		frame = new JFrame("SplitPaneDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		@SuppressWarnings("unused")
		SummaryModules splitPaneDemo = new SummaryModules();
		frame.pack();
		frame.setSize(1100,800);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}