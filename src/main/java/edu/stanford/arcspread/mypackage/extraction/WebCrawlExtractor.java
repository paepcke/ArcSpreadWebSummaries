package edu.stanford.arcspread.mypackage.extraction;

import edu.stanford.arcspread.mypackage.utils.Counter;


public class WebCrawlExtractor{
	
	//flag saveToDatabase..function to batch save into the disc..
	static String filePath = "/Users/siddhisoman/Desktop/RAwork/";
	String s_crawlName;
	int m_noOfPages;
	boolean f_sampling;
	static  Counter<String> webPageRank = new Counter<String>();
	//change to arraylist
	String[] listOfWebPages = {"/Users/siddhisoman/Desktop/RAwork/usnews1.html","/Users/siddhisoman/Desktop/RAwork/usnews.html","/Users/siddhisoman/Desktop/RAwork/yahoo.html", "/Users/siddhisoman/Desktop/RAwork/cnn.html"};
	
	public WebCrawlExtractor(String crawlName, int noOfPages, boolean sampling)
	{
		s_crawlName = crawlName;
		m_noOfPages = noOfPages;
		f_sampling = sampling;
	}
	
	public WebCrawlExtractor() {
		// TODO Auto-generated constructor stub
	}

	public String[] topX(int x)
	{
		String[] list = new String[x];
		int cnt = 0;
		for(String s:webPageRank.topK(x))
		{
			String[] temp = s.split("/");
			list[cnt++] = temp[temp.length-1];
		}
		return list;
	}
	
	public String Summary()
	{
		String summary = null;
		//change to iterator
		for (int i = 0; i < listOfWebPages.length; i++) 
		{
			WebPageSummary ps = new WebPageSummary(listOfWebPages[i]);
			summary += ps.PageTextSummary();
			//ps.PageImageSummary();
			ps.PageCollageSummery();
		}		
		return summary;
	}
	
	public static void main(String args[])
	{
		WebCrawlExtractor wce = new WebCrawlExtractor();
		String sum = wce.Summary();
		//System.out.println(sum);
		//WebPageSummary ps1 = new WebPageSummary("/Users/siddhisoman/Desktop/RAwork/usnews.html");
		//ps1.PageCollageSummery();
		
		String[] listWebPages = wce.topX(2);
		for(String s1:listWebPages)
		{
			System.out.println(s1);
			WebPageSummary ps2 = new WebPageSummary(filePath + s1);
			int rank = ps2.RankOfPage(filePath + s1);
			System.out.println(s1 + "'s rank is "+ rank);
		}
	}

}
