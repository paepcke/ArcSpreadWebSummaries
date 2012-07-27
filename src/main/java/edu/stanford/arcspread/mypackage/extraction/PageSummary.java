package edu.stanford.arcspread.mypackage.extraction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageSummary {
	
	List<String> impText;
	HashMap<String, BufferedImage> images;
	
	public PageSummary()
	{
		impText = new ArrayList<String>();
		images = new HashMap<String, BufferedImage> ();
	}
	
	public List<String> getImpText()
	{
		return impText;
	}
	
	public HashMap<String, BufferedImage> getImpImagest()
	{
		return images;
	}

};
