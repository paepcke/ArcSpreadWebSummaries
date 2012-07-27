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
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.stanford.arcspread.mypackage.dataStructures.NoScriptTag;

import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

//SplitPaneDemo itself is not a visible component.
public class CollageWebPages extends JPanel implements ListSelectionListener {

	private static JFrame frame;
	private JSplitPane splitPane;
	private JSplitPane splitPane1;
	private JSplitPane tsplitPane1;
	private JSplitPane tsplitPane2;
	private JPanel panel;
	private JButton reset;
	private JLabel imgTxtLabel1;
	private JLabel imgTxtLabel2;
	private JLabel imgTxtLabel3;
	private JLabel imgTxtLabel4;
	
	private JScrollPane textScrollPane1;
	private JScrollPane textScrollPane2;
	private JScrollPane textScrollPane3;
	private JScrollPane textScrollPane4;

	private HashSet<String> names = new HashSet<String>();
	private String prevName = "";
	static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	static ArrayList<String> s1 = new ArrayList<String>();

	static HashMap<String, BufferedImage> images1 = new HashMap<String, BufferedImage>();
	static ArrayList<String> s2 = new ArrayList<String>();

	static HashMap<String, BufferedImage> images2 = new HashMap<String, BufferedImage>();
	static ArrayList<String> s3 = new ArrayList<String>();

	static HashMap<String, BufferedImage> images3 = new HashMap<String, BufferedImage>();
	static ArrayList<String> s4 = new ArrayList<String>();

	static HashMap<Integer, Double> startingPos = new HashMap<Integer, Double>();

	public CollageWebPages() {

		String pathFile = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
		String preFile = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/";
		String file = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/coloradomines.html";

		try {
			// file = RemovingNoScript.removeNoScript(file);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			CollageSummary.summarise(file);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<String> impText = CollageSummary.cScore.topK(CollageSummary.cScore
				.size());
		try {

			String[] strings = {"a", "b", "c"};

			String result = Arrays.asList(strings).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
			System.out.println(result);
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.put("NOSCRIPT", new NoScriptTag());
			Parser parser = new Parser(file);
			parser.setNodeFactory(factory);
			NodeList list = parser.parse(new TagNameFilter("IMG"));

			for (SimpleNodeIterator iterator = list.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				if (s != null) {
					if (!s.contains("doubleclick")
							&& !s.contains("specificclick")) // adsense and
																// other domains
					{
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
						{
							try {
								// System.out.println(s);
								// URL url = new URL(s);
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

			}
			CollageSummary.cScore.clear();

			String pathFile1 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
			String preFile1 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/";
			String file1 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/civilwar.html";

			try {
				// file = RemovingNoScript.removeNoScript(file);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				CollageSummary.summarise(file1);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			List<String> impText1 = CollageSummary.cScore
					.topK(CollageSummary.cScore.size());
			// System.out.println(impText1);
			PrototypicalNodeFactory factory1 = new PrototypicalNodeFactory();
			factory1.put("NOSCRIPT", new NoScriptTag());
			Parser parser1 = new Parser(file1);
			parser1.setNodeFactory(factory1);
			NodeList list1 = parser1.parse(new TagNameFilter("IMG"));

			for (SimpleNodeIterator iterator = list1.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				if (s != null) {
					if (!s.contains("doubleclick")
							&& !s.contains("specificclick")) // adsense and
																// other domains
					{
						if (s.startsWith("http://"))
							s = s.replace("http://", pathFile1 + "/");
						else if (s.startsWith("/"))
							s = pathFile1 + s;
						else if (s.startsWith(".."))
							s = pathFile1 + s.substring(2);
						else if (s.startsWith("."))
							s = pathFile1 + s.substring(1);
						else if (!s.startsWith(preFile1))
							s = preFile1 + s;
						{
							try {
								// System.out.println(s);
								// URL url = new URL(s);
								InputStream is = new BufferedInputStream(
										new FileInputStream(s));
								BufferedImage tempImage = ImageIO.read(is);
								images1.put(s, tempImage);
								s2.add(s);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
				}

			}

			CollageSummary.cScore.clear();

			String pathFile2 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
			String preFile2 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/";
			String file2 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/louvre.html";

			try {
				// file = RemovingNoScript.removeNoScript(file);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				CollageSummary.summarise(file2);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			List<String> impText2 = CollageSummary.cScore
					.topK(CollageSummary.cScore.size());
			// System.out.println(impText1);
			PrototypicalNodeFactory factory2 = new PrototypicalNodeFactory();
			factory2.put("NOSCRIPT", new NoScriptTag());
			Parser parser2 = new Parser(file2);
			parser2.setNodeFactory(factory2);
			NodeList list2 = parser2.parse(new TagNameFilter("IMG"));

			for (SimpleNodeIterator iterator = list2.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				if (s != null) {
					if (!s.contains("doubleclick")
							&& !s.contains("specificclick")) // adsense and
																// other domains
					{
						if (s.startsWith("http://"))
							s = s.replace("http://", pathFile2 + "/");
						else if (s.startsWith("/"))
							s = pathFile2 + s;
						else if (s.startsWith(".."))
							s = pathFile2 + s.substring(2);
						else if (s.startsWith("."))
							s = pathFile2 + s.substring(1);
						else if (!s.startsWith(preFile2))
							s = preFile2 + s;
						{
							try {
								//System.out.println(s);
								// URL url = new URL(s);
								InputStream is = new BufferedInputStream(
										new FileInputStream(s));
								BufferedImage tempImage = ImageIO.read(is);
								images2.put(s, tempImage);
								s3.add(s);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
				}

			}

			CollageSummary.cScore.clear();

			String pathFile3 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
			String preFile3 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/";
			String file3 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/smithsonian.html";

			try {
				// file = RemovingNoScript.removeNoScript(file);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				CollageSummary.summarise(file3);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			List<String> impText3 = CollageSummary.cScore
					.topK(CollageSummary.cScore.size());
			// System.out.println(impText1);
			PrototypicalNodeFactory factory3 = new PrototypicalNodeFactory();
			factory3.put("NOSCRIPT", new NoScriptTag());
			Parser parser3 = new Parser(file3);
			parser3.setNodeFactory(factory3);
			NodeList list3 = parser3.parse(new TagNameFilter("IMG"));

			for (SimpleNodeIterator iterator = list3.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				if (s != null) {
					if (!s.contains("doubleclick")
							&& !s.contains("specificclick")) // adsense and
																// other domains
					{
						if (s.startsWith("http://"))
							s = s.replace("http://", pathFile3 + "/");
						else if (s.startsWith("/"))
							s = pathFile3 + s;
						else if (s.startsWith(".."))
							s = pathFile3 + s.substring(2);
						else if (s.startsWith("."))
							s = pathFile3 + s.substring(1);
						else if (!s.startsWith(preFile3))
							s = preFile3 + s;
						{
							try {
								// System.out.println(s);
								// URL url = new URL(s);
								InputStream is = new BufferedInputStream(
										new FileInputStream(s));
								BufferedImage tempImage = ImageIO.read(is);
								images3.put(s, tempImage);
								s4.add(s);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
				}

			}

			int i = 0;
			Pattern p = Pattern.compile("[a-zA-Z0-9 ,&.;:-]*$");
			while (!p.matcher(impText.get(i).trim()).matches()) {
				i++;
			}
			imgTxtLabel1 = new JLabel(String.format(
					"<html><div WIDTH=%d>%s</div><html>", 500, impText.get(i++)
							.trim()), new ImageIcon(images.get(s1.get(0))),
					JLabel.CENTER);
			imgTxtLabel1
					.setFont(imgTxtLabel1.getFont().deriveFont(Font.ITALIC));
			imgTxtLabel1.setHorizontalAlignment(JLabel.CENTER);
			textScrollPane1 = new JScrollPane(imgTxtLabel1);
			textScrollPane1.addMouseListener(mouse);
			textScrollPane1.setName(file);

			i = 0;
			while (!p.matcher(impText1.get(i).trim()).matches()) {
				i++;
			}
			imgTxtLabel2 = new JLabel(String.format(
					"<html><div WIDTH=%d>%s</div><html>", 500, impText1
							.get(i++).trim()), new ImageIcon(images1.get(s2
					.get(0))), JLabel.CENTER);
			imgTxtLabel2
					.setFont(imgTxtLabel2.getFont().deriveFont(Font.ITALIC));
			imgTxtLabel2.setHorizontalAlignment(JLabel.CENTER);
			textScrollPane2 = new JScrollPane(imgTxtLabel2);
			textScrollPane2.addMouseListener(mouse);
			textScrollPane2.setName(file1);

			i = 0;
			while (!p.matcher(impText2.get(i).trim()).matches()) {
				i++;
			}
			imgTxtLabel3 = new JLabel(String.format(
					"<html><div WIDTH=%d>%s</div><html>", 500, impText2
							.get(i++).trim()), new ImageIcon(images2.get(s3
					.get(0))), JLabel.CENTER);
			imgTxtLabel3
					.setFont(imgTxtLabel3.getFont().deriveFont(Font.ITALIC));
			imgTxtLabel3.setHorizontalAlignment(JLabel.CENTER);
			textScrollPane3 = new JScrollPane(imgTxtLabel3);
			textScrollPane3.addMouseListener(mouse);
			textScrollPane3.setName(file2);

			i = 0;
			while (!p.matcher(impText3.get(i).trim()).matches()) {
				i++;
			}
			imgTxtLabel4 = new JLabel(String.format(
					"<html><div WIDTH=%d>%s</div><html>", 500, impText3
							.get(i++).trim()), new ImageIcon(images3.get(s4
					.get(0))), JLabel.CENTER);
			imgTxtLabel4
					.setFont(imgTxtLabel4.getFont().deriveFont(Font.ITALIC));
			imgTxtLabel4.setHorizontalAlignment(JLabel.CENTER);
			textScrollPane4 = new JScrollPane(imgTxtLabel4);
			textScrollPane4.addMouseListener(mouse);
			textScrollPane4.setName(file3);

			tsplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					textScrollPane1, textScrollPane2);
			tsplitPane1.setOneTouchExpandable(true);
			tsplitPane1.setResizeWeight(0.5);
			tsplitPane1.setDividerLocation(100);

			tsplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					tsplitPane1, textScrollPane3);
			tsplitPane2.setOneTouchExpandable(true);
			tsplitPane2.setResizeWeight(0.5);
			tsplitPane2.setDividerLocation(300);

			// Create a split pane with the two scroll panes in it.
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tsplitPane2,
					textScrollPane4);

			splitPane.setResizeWeight(0.5);
			splitPane.setDividerLocation(500);
		    splitPane.setOneTouchExpandable(true);
		    splitPane.setContinuousLayout(true);
		    
		    add(splitPane, BorderLayout.CENTER);
	        add(createControlPanel(), BorderLayout.PAGE_END);
	 
			/*iButton = new JButton("Image");
			iButton.setSize(30, 30);
			iButton.addActionListener(action);
			iButton.setAlignmentX(Component.BOTTOM_ALIGNMENT);
			splitPane.add(iButton);*/

			// Provide a preferred size for the split pane.
			splitPane.setPreferredSize(new Dimension(1100, 800));
			tsplitPane1.setPreferredSize(new Dimension(1100, 200));
			tsplitPane2.setPreferredSize(new Dimension(1100, 600));
			
			names.add(file);
			names.add(file1);
			names.add(file2);
			names.add(file3);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Create the list of images and put it in a scroll pane.

	}
	
	 private JComponent createControlPanel() {
	        panel = new JPanel();
	        reset = new JButton("Image");
	        reset.addActionListener(action);
	        panel.add(reset);
	        return panel;
	    }
			
	
	private ActionListener action = new ActionListener() {

		public void actionPerformed(ActionEvent a) {
			String cmd = a.getActionCommand(); 
			if(cmd.equals("Image"))
			{
				System.out.println("Image button pressed!!");
				
				String file = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/coloradomines.html";
				String file1 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/civilwar.html";
				String file2 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/louvre.html";
				String file3 = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/smithsonian.html";
				
				try {
					CollageSummary.summarise(file);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				List<String> impText = CollageSummary.cScore.topK(CollageSummary.cScore
						.size());
				CollageSummary.cScore.clear();
				
				try {
					CollageSummary.summarise(file1);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				List<String> impText1 = CollageSummary.cScore.topK(CollageSummary.cScore
						.size());
				CollageSummary.cScore.clear();
				
				try {
					CollageSummary.summarise(file2);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				List<String> impText2 = CollageSummary.cScore.topK(CollageSummary.cScore
						.size());
				CollageSummary.cScore.clear();
				
				try {
					CollageSummary.summarise(file3);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				List<String> impText3 = CollageSummary.cScore.topK(CollageSummary.cScore
						.size());
				CollageSummary.cScore.clear();
				
				
				int i = 0;
				Pattern p = Pattern.compile("[a-zA-Z0-9 ,&.;:-]*$");
				while (!p.matcher(impText.get(i).trim()).matches()) {
					i++;
				}
				imgTxtLabel1 = new JLabel(String.format(
						"<html><div WIDTH=%d>%s</div><html>", 1000, impText.get(i++)
								.trim()), new ImageIcon(TransformGrayToTransparency(images.get(s1
										.get(0)).getScaledInstance(images.get(s1.get(0)).getWidth()/3, images.get(s1.get(0)).getHeight()/3, java.awt.Image.SCALE_SMOOTH))),
						JLabel.CENTER);
				imgTxtLabel1
						.setFont(imgTxtLabel1.getFont().deriveFont(Font.ITALIC));
				imgTxtLabel1.setHorizontalAlignment(JLabel.CENTER);
				textScrollPane1 = new JScrollPane(imgTxtLabel1);
				textScrollPane1.addMouseListener(mouse);
				textScrollPane1.setName(file);

				i = 0;
				while (!p.matcher(impText1.get(i).trim()).matches()) {
					i++;
				}
				imgTxtLabel2 = new JLabel(String.format(
						"<html><div WIDTH=%d>%s</div><html>", 1000, impText1
								.get(i++).trim()), new ImageIcon(TransformGrayToTransparency(images1.get(s2
										.get(0)).getScaledInstance(images1.get(s2.get(0)).getWidth()/3, images1.get(s2.get(0)).getHeight()/3, java.awt.Image.SCALE_SMOOTH))), JLabel.CENTER);
				imgTxtLabel2
						.setFont(imgTxtLabel2.getFont().deriveFont(Font.ITALIC));
				imgTxtLabel2.setHorizontalAlignment(JLabel.CENTER);
				textScrollPane2 = new JScrollPane(imgTxtLabel2);
				textScrollPane2.addMouseListener(mouse);
				textScrollPane2.setName(file1);

				i = 0;
				while (!p.matcher(impText2.get(i).trim()).matches()) {
					i++;
				}
				imgTxtLabel3 = new JLabel(String.format(
						"<html><div WIDTH=%d>%s</div><html>", 1000, impText2
								.get(i++).trim()), new ImageIcon(TransformGrayToTransparency(images2.get(s3
										.get(0)).getScaledInstance(images2.get(s3.get(0)).getWidth()/3, images2.get(s3.get(0)).getHeight()/3, java.awt.Image.SCALE_SMOOTH))), JLabel.CENTER);
				imgTxtLabel3
						.setFont(imgTxtLabel3.getFont().deriveFont(Font.ITALIC));
				imgTxtLabel3.setHorizontalAlignment(JLabel.CENTER);
				textScrollPane3 = new JScrollPane(imgTxtLabel3);
				textScrollPane3.addMouseListener(mouse);
				textScrollPane3.setName(file2);

				i = 0;
				while (!p.matcher(impText3.get(i).trim()).matches()) {
					i++;
				}
				imgTxtLabel4 = new JLabel(String.format(
						"<html><div WIDTH=%d>%s</div><html>", 1000, impText3
								.get(i++).trim()), new ImageIcon(TransformGrayToTransparency(images3.get(s4
						.get(0)).getScaledInstance(images3.get(s4.get(0)).getWidth()/3, images3.get(s4.get(0)).getHeight()/3, java.awt.Image.SCALE_SMOOTH))), JLabel.CENTER);
				imgTxtLabel4
						.setFont(imgTxtLabel4.getFont().deriveFont(Font.ITALIC));
				imgTxtLabel4.setHorizontalAlignment(JLabel.CENTER);
				textScrollPane4 = new JScrollPane(imgTxtLabel4);
				textScrollPane4.addMouseListener(mouse);
				textScrollPane4.setName(file3);

				tsplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
						textScrollPane1, textScrollPane2);
				tsplitPane1.setOneTouchExpandable(true);
				tsplitPane1.setResizeWeight(0.5);
				tsplitPane1.setDividerLocation(200);

				tsplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
						tsplitPane1, textScrollPane3);
				tsplitPane2.setOneTouchExpandable(true);
				tsplitPane2.setResizeWeight(0.5);
				tsplitPane2.setDividerLocation(500);

				// Create a split pane with the two scroll panes in it.
				splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tsplitPane2,
						textScrollPane4);

				splitPane.setResizeWeight(0.5);
			    splitPane.setOneTouchExpandable(true);
			    splitPane.setContinuousLayout(true);
			    
			    splitPane.setPreferredSize(new Dimension(1200, 800));
    			tsplitPane1.setPreferredSize(new Dimension(1200, 200));
    			tsplitPane2.setPreferredSize(new Dimension(1200, 600));
			    
			    //add(splitPane, BorderLayout.CENTER);
		        //add(createControlPanel(), BorderLayout.PAGE_END);
		        frame.setContentPane(splitPane);
		        //frame.setSize(1200, 1000);
		        frame.pack();
				frame.setVisible(true);
			}
		}
	};
	
	
	private Image TransformGrayToTransparency(Image image)
	  {
		if(image != null)
		{
			BufferedImage image1 = new BufferedImage(image.getHeight(this), image.getWidth(this), BufferedImage.TYPE_INT_ARGB);
			Graphics2D bufImageGraphics = image1.createGraphics();
			bufImageGraphics.drawImage(image, 0, 0, null);
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

	
	private MouseListener mouse = new MouseListener() {
		
       public void mouseClicked(MouseEvent e) {
    	  Frame[] act = Frame.getFrames();
    	  for(int k=0; k< act.length; k++)
    	  {
    		  System.out.println(act[k].getTitle());
    	  }
    	   String name =  e.getComponent().getName();
    	   if(name.equals(prevName))
    	   {
    		   try {
   				Desktop.getDesktop().open( new File(name));
   			} catch (IOException e1) {
   				// TODO Auto-generated catch block
   				e1.printStackTrace();
   			}
    	   }
    	   else if (names.contains(name))
    	   {
    	  
    	  /* if (e.getClickCount() == 2 && !e.isConsumed()) {
    		   e.consume();
    		   flag = 1;
    		   //handle double click.
    		   }
    	    try {
				Thread.sleep(4000);
			} catch (InterruptedException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
    	    if(flag == 1)
    	    {
    	    	System.out.println("Mouse event, clickCount = " + e.getClickCount());
    	    }
    	    else*/
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
    				// file = RemovingNoScript.removeNoScript(file);
    			} catch (Exception e2) {
    				// TODO Auto-generated catch block
    				e2.printStackTrace();
    			}
    			try {
    				CollageSummary.summarise(file5);
    			} catch (Exception e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}

    			List<String> impText3 = CollageSummary.cScore
    					.topK(CollageSummary.cScore.size());
    			// System.out.println(impText1);
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
    					if (!s.contains("doubleclick")
    							&& !s.contains("specificclick")) // adsense and
    																// other domains
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
    							// System.out.println(s);
    								// URL url = new URL(s);
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

    			i = 0;
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
    			
    			// Create a split pane with the two scroll panes in it.
    			splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tsplitPane2,
    					textScrollPane4);

    			splitPane1.setOneTouchExpandable(true);
    			splitPane1.setDividerLocation(500);
    			splitPane1.setVisible(true);

    			// Provide a preferred size for the split pane.
    			splitPane1.setPreferredSize(new Dimension(1200, 800));
    			tsplitPane1.setPreferredSize(new Dimension(1200, 200));
    			tsplitPane2.setPreferredSize(new Dimension(1200, 600));
    			
    			
    			// Create and set up the window.
    			JFrame frame1 = new JFrame(frameName);
    			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    			frame1.getContentPane().add(splitPane1);

    			// Display the window.
    			frame1.pack();
    			frame1.setVisible(true);
    			tsplitPane1.setResizeWeight(0.5d);
    			tsplitPane2.setResizeWeight(0.5d);
    			splitPane1.setResizeWeight(0.5d);
    			

            }catch(Exception e1)
            {
            	e1.printStackTrace();
            }
    	    }
    	   }
        }

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	};

	// Listens to the list
	public void valueChanged(ListSelectionEvent e) {
		JList list = (JList) e.getSource();
		// updateLabel(s1.get(3));
	}

	// Renders the selected image
	protected void updateLabel(JLabel picture, String name) {
		ImageIcon icon = new ImageIcon(images.get(name));
		picture.setIcon(icon);

		if (icon != null) {
			picture.setText(null);
		}
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}
	

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = CollageWebPages.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		// Create and set up the window.
		frame = new JFrame("SplitPaneDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CollageWebPages splitPaneDemo = new CollageWebPages();
		frame.setContentPane(splitPaneDemo);
		//frame.setSize(1200, 1000);
		//frame.getContentPane().add(splitPaneDemo.getSplitPane());
		/*JSplitPane tempPane = splitPaneDemo.getSplitPane();
		frame.add(tempPane);
		tempPane.setVisible(true);
		frame.add(splitPaneDemo.getImageButton());*/
		
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}