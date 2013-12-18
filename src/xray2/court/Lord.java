package xray2.court;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import xray2.classifier.DocClassifier;
import xray2.classifier.URLClassifier;

public class Lord {
	
	private URLClassifier ucc;
	private DocClassifier docc;
	
	public Lord(String category){
		docc = new DocClassifier();
		docc.classify("", category);
		ucc = new URLClassifier(category,docc);
		ucc.setFactor(0.6);
	}
	
	public double getStrutureScore(String url){
		int count = url.split("/").length;
		return 1.0/(count + 1.0);
	}
	
	public double addTwoScore(double sScore,double simScore){
		double lambda = 0.2;
		double scoreD = lambda * sScore + (1 - lambda) * simScore;  
		return scoreD;
	}
	
	public Map[] getQualifiedURL(String html,String url){
		if(html == null || html.length() < 10) return null;
		
		Map[] urlMap = ucc.getURLs(html,url);
		
		Iterator iter = urlMap[1].entrySet().iterator();

		while(iter.hasNext()){
			Entry<String,Double> e = (Entry<String, Double>) iter.next();
			String u = e.getKey();
			double score = e.getValue();
			e.setValue(addTwoScore(this.getStrutureScore(u) , score));
		}
		
		
		return urlMap;
	}
}
