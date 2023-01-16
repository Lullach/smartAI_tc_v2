package com.helena.imageJTest;
import calc.Measurement;
import java.time.LocalTime;

import ij.*;
import ij.io.Opener;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.RankFilters;
import ij.process.AutoThresholder;
import ij.process.ImageProcessor;


import static ij.plugin.filter.RankFilters.MEDIAN;
import static ij.process.ImageProcessor.*;



// try with "./data/image2.png"
public class ImageJClass {
	
	
	// returns the respective Measurement object associated with the input picture
	// You still need to close all the windows 
	public Measurement analyze(String filepath,LocalTime time) {
		// Open picture as 8-bit
		Opener opener = new Opener();
		ImagePlus image = opener.openImage(filepath);
		ImageProcessor imageP = image.getProcessor();
		image.show();
		// Substract Background
		BackgroundSubtracter backgroundSubtracter = new BackgroundSubtracter();
		backgroundSubtracter.rollingBallBackground(imageP, 90, false, true, false,false, false);
		ImagePlus testdisplay = new ImagePlus("Result Substracted Background", imageP);
		testdisplay.show();
		// Apply Median Filter
		RankFilters rankFilters = new RankFilters();
		rankFilters.rank(imageP, 5, MEDIAN);
		testdisplay = new ImagePlus("Result Median Filter", imageP);
		testdisplay.show();
		// Apply Threshold
		imageP.setAutoThreshold(AutoThresholder.Method.valueOf("IsoData"), true, BLACK_AND_WHITE_LUT);
		testdisplay = new ImagePlus(" Result Threshold", imageP);
		testdisplay.show();
		// Analyze
		ImagePlus picAnalyze = new ImagePlus("Picture Analyze", imageP);
		ResultsTable resultsTable = new ResultsTable();
		Analyzer analyzer = new Analyzer(picAnalyze, Measurements.AREA_FRACTION, resultsTable);
		analyzer.measure();
		resultsTable.show("Result Analysis");
		
		
		Measurement measure= new Measurement(resultsTable.getColumn(resultsTable.getLastColumn())[0], time);
		return measure;

	}
	

}
