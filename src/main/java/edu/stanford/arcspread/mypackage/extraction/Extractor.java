package edu.stanford.arcspread.mypackage.extraction;

import java.util.ArrayList;

import edu.stanford.arcspread.mypackage.dataStructures.TextUnit;

public interface Extractor {

	public String process() throws Exception;

	public String getExtractedText();

	public ArrayList<TextUnit> getExtractedTextUnits();

}
