package edu.stanford.arcspread.mypackage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.stanford.arcspread.mypackage.dataStructures.TextUnit;
import edu.stanford.arcspread.mypackage.extraction.WebCrawlExtractor;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;


public class testClass {
	
	
	/*
	 * String calculateMainNode() throws Exception {

		//double maxScore = weightedScore();
		//double maxScore = tfIdfScore();
		double maxScore = POSScore();
		extractedTextUnits = new ArrayList<TextUnit>();
		fillTfIdf();
	   
	    
	    nodeText = new ArrayList<String>();
		hNodeText = new HashSet<String>();
		imageUrl = new ArrayList<String>();
		
		//String sample = "This is a sample text";
		//String tagged = tagger.tagString(sample);
		//System.out.println(tagged);
	    Integer cnt = new Integer(0);
	    double fCount = 0;
		/*for (Node node1 : counts.keySet()) {

			int tempCount = 0;
			String nodeText = node1.toPlainTextString();
			double count = 0;
			if(nodeText.length() > 0)
			{
			String nodeWords[] = nodeText.split("\n");
			//if(nodeWords.length > 10)
			{
			for( String s:nodeWords)
			{
				//if(tempCount > 12)
				//	break;
				if(s.length() > 10)
				{
				demoAPI(lp, s);
				FileInputStream fstream = new FileInputStream("fileName.txt");
			    DataInputStream in = new DataInputStream(fstream);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String strLine;
			    while ((strLine = br.readLine()) != null)   {
			    	if(strLine.startsWith("n"))
			    	{
			    		String[] temp = strLine.split(",");
			    		count += temp.length;
			    	}
			    }
			    fCount = count;
			    in.close();
			    fstream.close();
				}
				tempCount++;
			}
			
			}
			/*
			if(node1.toPlainTextString().length() > 11)
			{
			Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
			Matcher m = p.matcher(node1.toPlainTextString().substring(0, 10));
			if(m.find())
			{
			double score = (double)fCount/(double)(node1.toPlainTextString().length());
			if (score > maxScore) {
				maxScore = score;
				maxNode = node1;
			}
			}
			}
			else
			{
				double score = (double)fCount/(double)(node1.toPlainTextString().length());
				if (score > maxScore) {
					maxScore = score;
					maxNode = node1;
				}
			}*/
		//	}
	//	}
			//System.out.println("fCount: " + fCount);
			//System.out.println("nodeText.length(): " + nodeText.length());
		/*	String nodeWords[] = nodeText.split(" ");
			//System.out.println(node.toPlainTextString());
			//System.out.println("\nnext\n\n");
			double tfidfVal = 0;
			for( String s:nodeWords)
			{
				int cnt2 = cnt.intValue();
				cnt2++;
				Integer cnt1 = new Integer(cnt2);
				String temp = s + "\t" + cnt1.toString();
				//System.out.println("2: " + temp);
				tfidfVal += getValue(s, temp, cnt1);
				//System.out.println("term: " + s + "tf-idf: " + tfidfVal);
			}
			double score = tfidfVal;
			if (score > maxScore) {
				maxScore = score;
				maxNode = node1;
			}*/
			//System.out.println("tf-idf: " + tfidfVal);
		/*sortedMap.put(fScore, node);
			//System.out.println(node.toString());
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
			*/
             
			/*Set<Map.Entry<Double, Node>> set = sortedMap.entrySet();
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
				String sbufTmp = null;
				Iterator<String> itr = hNodeText.iterator();
				/*if(sbufTemp.length() > 20)
				{
					sbufTmp = sbufTemp.substring(0,19);
					int flag = 0;
					while(itr.hasNext())
					{
						if(itr.next().indexOf(sbufTmp) != -1)
						{
							flag = 1;
							break;
						}
					}
					if(flag == 0)
					{
						nodeText.add(sbufTemp);
					}
				}
				else
					nodeText.add(sbufTemp);
				hNodeText.add(sbufTemp);
				//System.out.println(me.getKey());
				//System.out.println(sbufTemp);
			}
			
		//	double score = tfidfVal;
			

		//	}
		//System.out.println("maxScore: " + maxScore);
		//System.out.println("maxNode: " + maxNode.toPlainTextString());
		//System.out.println("extractedTextAsString: " + extractedTextAsString);
		WebCrawlExtractor.webPageRank.setCount(fileName, maxScore);	
        return extractedTextAsString;
		
	}
	 */
	
	
	
	public static double POSScore(String sample)
	{
		String[] s = sample.split(" ");
		double count = 0;
		double score = 0;
		try{
		FileInputStream fstream = new FileInputStream("fileName.txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    while ((strLine = br.readLine()) != null)   {
	    	//System.out.println(strLine);
	    	if(strLine.startsWith("nn"))
	    	{
	    		String[] temp = strLine.split(",");
	    		count += temp.length;
	    	}
	    }
	    in.close();
	    fstream.close();
	    score = (double)count/(double)(s.length);
	    System.out.println(score);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return score;
	}

	public static void main(String[] args)
	{
		/*try {
			//MaxentTagger tagger = new MaxentTagger("/Users/siddhisoman/Desktop/RAWork/stanford-postagger-2012-01-06/models/english-left3words-distsim.tagger");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			/*LexicalizedParser lp = new LexicalizedParser("/Users/siddhisoman/Documents/workspace/OriginalStanford Parser/grammar/englishPCFG.ser.gz");
			String sample = "This is a sample text to be read with caution";
			String[] s = sample.split(" ");
			double count = 0;
			demoAPI(lp, sample);
			try{
			FileInputStream fstream = new FileInputStream("fileName.txt");
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null)   {
		    	//System.out.println(strLine);
		    	if(strLine.startsWith("nn"))
		    	{
		    		String[] temp = strLine.split(",");
		    		count += temp.length;
		    	}
		    }
		    System.out.println(count);
		    System.out.println(s.length);
		    in.close();
		    fstream.close();
		    double score = (double)count/(double)(s.length);
		    System.out.println(score);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			}
			*/
		    //String tagged = tagger.tagString(sample);
			//System.out.println(tagged);
	}
	
	/*public static void demoAPI(LexicalizedParser lp, String sentence) {
	   String[] sent = sentence.split(" ");
	    List<CoreLabel> rawWords = new ArrayList<CoreLabel>();
	    for (String word : sent) {
	      CoreLabel l = new CoreLabel();
	      l.setWord(word);
	      rawWords.add(l);
	    }
	    Tree parse = lp.apply(rawWords);
	    System.out.println();
	    System.out.println();

	    TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	    String filename = "fileName.txt";
	    
	    PrintWriter pw = null;
	    try{
	    pw = new PrintWriter(new FileWriter(filename, false));
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    tp.printTree(parse, pw);
	    pw.close();
	  }*/
	

}
