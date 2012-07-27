package edu.stanford.arcspread.mypackage.extraction;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.stanford.arcspread.mypackage.dataStructures.NoScriptTag;
import edu.stanford.arcspread.mypackage.utils.Counter;


public class Test {
	
	static ArrayList <BufferedImage> img1 = new ArrayList<BufferedImage>();
	static HashMap <String,BufferedImage> images = new HashMap<String, BufferedImage>();
	static ArrayList <String> s1 = new ArrayList<String>();
	static double total;
	static double count;
	static Counter<String> cPagePosition = new Counter<String>();
	static Counter<String> cHeight = new Counter<String>();
	static Counter<String> cStart = new Counter<String>();
	
	static Counter<String> totalScore = new Counter<String>();
	static HashMap<Integer, Double> startingPos = new HashMap<Integer, Double>();
	
	static HashMap<String,String> labelUrl = new HashMap<String,String>();
	static HashMap<String,Integer> urlRank = new HashMap<String,Integer>();
	static HashMap<String,ArrayList<String>> labelRankings = new HashMap<String,ArrayList<String>>();
	
	public static void main(String[] args)
	{
		try{
			
		String pathFile = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages";
		String preFile = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/www.eonline.com/";
		String file = "/Users/siddhisoman/Downloads/imgRank/public/sample_pages/www.eonline.com/index.html";
		
		String slabelRankings = "/Users/siddhisoman/Desktop/arcspread/trainingdatafromrankingexperiment/allRankersCodedAnalyzed.csv";
		String slabelUrl = "/Users/siddhisoman/Desktop/arcspread/trainingdatafromrankingexperiment/urlToPicIDMap.csv";
		boolean flag = false;
		
		try{
		  FileInputStream fstream = new FileInputStream(slabelUrl);
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  while ((strLine = br.readLine()) != null)   
		  {
			  String[] tokens = strLine.split(",");
			  tokens[2] = tokens[2].toLowerCase();
			  labelUrl.put(tokens[1], tokens[2]);
		  }
		  
		  FileInputStream fstream1 = new FileInputStream(slabelRankings);
		  DataInputStream in1 = new DataInputStream(fstream1);
		  BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
		  String strLine1;
		  while ((strLine1 = br1.readLine()) != null)   
		  {
			  ArrayList<String> tempList = new ArrayList<String>();
			  String[] tokens = strLine1.split(",");
			  if(tokens[38].equals("1") && tokens[34].equals("1"))
			  {
				  flag = true;
				  for (int i = 1; i < 32; i++ )
				  {
					  if(tokens[i].equals(" "))
					  {
						  tempList.add("-1");
					  }
					  else
					  {
						  tempList.add(tokens[i]);
					  }
				  }
				  //System.out.println(tokens[0]);
				  labelRankings.put(tokens[0], tempList);
			  }
		  }
		  
		  System.out.println(labelUrl.size());
		  System.out.println(labelRankings.size());
		  in.close();
		  in1.close();
		  br.close();
		  br1.close();
		  }catch (Exception e)
		  {
			  System.err.println("Error: " + e.getMessage());
		  }
		
		//File path = new File(pathFile);
		//Integer rank = new Integer(0);
		//FileWriter fstream2 = new FileWriter("rankOutput.txt");
		//BufferedWriter out = new BufferedWriter(fstream2);
		
		
		FileWriter fstreamX = new FileWriter("X.txt");
		BufferedWriter xVal = new BufferedWriter(fstreamX);
		
		
		FileWriter fstreamY = new FileWriter("Y.txt");
		BufferedWriter yVal = new BufferedWriter(fstreamY);
		//String prefix = "http://veggies.stanford.edu:4567/sample_pages/apple_files/";
		
		
		//File [] files = path.listFiles();
		//for (int p = 0; p < path.length(); p++)
		{
			//try
			{
			//String prfile = files[p].getAbsolutePath();
			//if(prfile.contains("."))
			{
				//System.out.println(prfile);
				//file = prfile;
		//String file = "/Users/siddhisoman/Desktop/sample_pages/nasa" + ".html";
		try {
			//file = RemovingNoScript.removeNoScript(file);
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
		HashMap<String, Double> heightScore = new HashMap<String,Double>();
		HashMap<String, Double> pagePositionScore = new HashMap<String,Double>();
		HashMap<String, Double> startScore = new HashMap<String,Double>();
		
		try{
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.put("NOSCRIPT", new NoScriptTag());
			Parser parser = new Parser(file);
			parser.setNodeFactory(factory);
			NodeList list = parser.parse(new TagNameFilter("IMG"));
            
			for (SimpleNodeIterator iterator = list.elements(); iterator
					.hasMoreNodes();) {
				Tag tag = (Tag) iterator.nextNode();
				String s = tag.getAttribute("src");
				if(s != null)
				{
				if(!s.contains("doubleclick")  && !s.contains("specificclick")) //adsense and other domains
					{
					//System.out.println(tag.getStartPosition());
					total+=tag.getStartPosition();
					count++;
					double tmpsCount = 0;
					double startPos = tag.getStartPosition();
					for(int j = 0; j < 4; j++)
					{
						tmpsCount += (startingPos.get(j) - startPos);
					}
					//System.out.println(s);
					if(s.startsWith("http://"))
						s = s.replace("http://", pathFile + "/");
					else if(s.startsWith("/"))
						s = pathFile + s;
					else if(s.startsWith(".."))
						s = pathFile + s.substring(2);
					else if(s.startsWith("."))
						s = pathFile + s.substring(1);
					else if(!s.startsWith(preFile))
						s = preFile + s;
					//
					cPagePosition.setCount(s, tag.getStartPosition());
					cStart.setCount(s, tmpsCount);
					
					//String tempAttr = tag.getAttribute("src");
						//if(s.substring(0, 6).equals("http:/"))
						{
							try{
							//System.out.println(s);
							//URL url = new URL(s);
							InputStream is = new BufferedInputStream(new FileInputStream(s));
							BufferedImage tempImage = ImageIO.read(is);
							img1.add(tempImage);
							images.put(s, tempImage);
							s1.add(s);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							
						}
					}
			}
			}
			for(String temp:s1)
			{
				//System.out.println(temp);
			}
			
			//total = total/count;
			for (int i = 0; i < img1.size(); i++) {
				if (img1.get(i) != null) {
					if((img1.get(i).getWidth() < 20)  && (img1.get(i).getHeight() < 20))
					{
						img1.remove(i);
						//System.out.println(s1.get(i));
						cPagePosition.remove(s1.get(i));
						s1.remove(i);
					}
					else
					{
						Double dim = ((double)img1.get(i).getWidth())*((double)img1.get(i).getHeight());
						cHeight.setCount(s1.get(i), dim);
					}
				}
			}
			System.out.println("remove");
			for(String temp:s1)
			{
				//System.out.println(temp);
			}
			System.out.println("String");
			double cnt = 0, cnt1 = 0, cnt2 = 0;
			if(cPagePosition.size() > 0)
			{
			for(String tempS:cPagePosition.topK(cPagePosition.size()))
			{
				cnt++;
				Double tempScore = (double)cnt/cPagePosition.size();
				System.out.println(tempS);
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
			System.out.println(heightScore.size() + " " + pagePositionScore.size() + " " + startScore.size());
			/*System.out.println(cnt1);
			System.out.println(s1.size());*/
			
			int cnt3 = 0;
			for(String tempS2:s1)
			{
				cnt3++;
				if(cnt3 > cnt - 1 || cnt3 > cnt1 -1 || cnt3 > cnt2 -1)
				{
					break;
				}
				if (tempS2 != null) {
					if(flag)
					{
						String tempS3 = tempS2;
					if(tempS2.contains("sample_pages"))
						tempS3 = tempS3.replace("/Users/siddhisoman/Downloads/imgRank/public/sample_pages", "http://veggies.stanford.edu:4567/sample_pages");
					
					//System.out.println(tempS3);
					String id = labelUrl.get(tempS3);
					//System.out.println(id);
					
					ArrayList<String> rankList = new ArrayList<String>();
					if(labelRankings.containsKey(id))
					{
					rankList = labelRankings.get(id);
					//System.out.println(rankList.size());
					for(int m = 0; m < rankList.size(); m++)
					{
						if(rankList.get(m) != null)
						{
						Integer tempRank = Integer.parseInt(rankList.get(m));
						//System.out.println(tempRank);
						
						if(tempRank != -1)
						{
							System.out.println(tempS2);
							//if(pagePositionScore.get(tempS2) != null && startScore.get(tempS2) != null && heightScore.get(tempS2) != null)
							{
							xVal.write(pagePositionScore.get(tempS2) + " " + startScore.get(tempS2) + " " + heightScore.get(tempS2) + "\n");
							if(tempRank < 4)
							{
								yVal.write("1" + "\n");
							}
							else
							{
								yVal.write("-1" + "\n");
							}
							}
						}
						}
					}
					}
					}
					
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
			}
			xVal.close();
			yVal.close();
			
			//rank = 0;
			//out.write(webPageName + "\n");
			for(String tempS3:totalScore.topK(totalScore.size()))
			{
				//System.out.println(tempS3 + " : " + totalScore.getCount(tempS3));
				//String[] tempS4 = tempS3.split("_files/");
				//System.out.println(tempS4[0] + " " + tempS4[1]);
				//String temp = tempS4[0] + ".html/" + tempS4[1];
				//String temp = prefix + tempS4[1];
				//out.write(temp + ",");
				if(tempS3.contains("sample_pages"))
					tempS3 = tempS3.replace("/Users/siddhisoman/Downloads/imgRank/public/sample_pages", "http://veggies.stanford.edu:4567/sample_pages");
				//out.write(tempS3 + ",");
				//rank++;
			}
			//out.close();
			
			//original image order
			
			/*JFrame frame1 = new JFrame("Image summary");
			JPanel Panel1 = new JPanel();
			int num_images = img1.size();
			LayoutManager Layout1 = new GridLayout (num_images/4, 1);
			Panel1.setLayout ( Layout1 );
	        for(int i = 0; i < num_images; i++)
	        {
	        	//if(imgP != null)
	        	{
	        		if(img1.get(i) != null)
	        		{
	        			{
	        			Image newimg = img1.get(i).getScaledInstance((int)(600), (int)(2400/img1.size()),  java.awt.Image.SCALE_SMOOTH); 
	        			JLabel picLabel = new JLabel(new ImageIcon( newimg ));
	        			Panel1.add (picLabel);
	        			}
	        		}
	        	}
	        }
	        frame1.setSize(1200,1200);
			frame1.getContentPane().add(Panel1);
			frame1.setVisible(true);
			*/
			
			//sorted image order
			
			/*JFrame frame2 = new JFrame("Sorted Image summary");
			JPanel Panel2 = new JPanel();
			int num_images1 = totalScore.size();
			LayoutManager Layout2 = new GridLayout (num_images1/4, 1);
			Panel2.setLayout ( Layout2 );
	        for(String tempString:totalScore.topK(totalScore.size()))
	        {
	        	//if(imgP != null)
	        	{
	        		BufferedImage tmpImage = images.get(tempString);
	        		if(tmpImage != null)
	        		{
	        			{
	        			Image newimg = tmpImage.getScaledInstance((int)(600), (int)(2400/num_images1),  java.awt.Image.SCALE_SMOOTH); 
	        			JLabel picLabel = new JLabel(new ImageIcon( newimg ));
	        			Panel2.add (picLabel);
	        			}
	        		}
	        	}
	        }
	        frame2.setSize(1200,1200);
			frame2.getContentPane().add(Panel2);
			/*System.out.println("avg_image_width: " + avg_image_width);
			System.out.println("avg_image_height: " + avg_image_height);
			System.out.println("num_images: " + num_images);*/
			//frame2.setVisible(true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			}
			img1.clear();
			images.clear();
			s1.clear();
			total = 0;
			count = 0;
			cPagePosition.clear();
			cHeight.clear();
			cStart.clear();
			
			totalScore.clear();
			startingPos.clear();
			}
			/*catch(Exception e)
			{
				System.out.println("for loop exception");
			}*/
			}
		}catch(Exception e)
		{
			System.out.println("File exception!!");
		}
		
	}
};
