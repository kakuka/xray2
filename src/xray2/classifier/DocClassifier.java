package xray2.classifier;

import xray2.classifier.train.RocchioClassifier;




public class DocClassifier {
	RocchioClassifier bc;
	public DocClassifier(){
		bc = new RocchioClassifier();
	}
	public double classify(String doc,String category){
		return bc.classify(doc, category);
	}
}
