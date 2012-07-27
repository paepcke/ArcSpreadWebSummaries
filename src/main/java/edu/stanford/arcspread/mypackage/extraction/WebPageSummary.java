package edu.stanford.arcspread.mypackage.extraction;

import java.util.List;

public class WebPageSummary {
	
	String s_webPage;
	
	public WebPageSummary(String webPage)
	{
		s_webPage = webPage;
	}
	
	public String PageTextSummary()
	{
		String s = ProcessPage.summarize(s_webPage);
		return s;
	}
	
	public void PageImageSummary()
	{
		//ProcessPage.summarize(s_webPage);
		imageParser.imageSummarize(s_webPage);
	}
	
	public void PageCollageSummery()
	{
		try {
			CollageSummary.summarise(s_webPage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int RankOfPage(String page)
	{
		List<String> webStrings = WebCrawlExtractor.webPageRank.topK(WebCrawlExtractor.webPageRank.size());
		int count = 1;
		for(String s1:webStrings)
		{
			if(s1.equals(page))
				break;
			count++;
		}
		return count;
	}

}
