package xray2.classifier.train;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;



public class RocchioClassifier {
	
	/**
	 * 词汇表要保持顺序，因为svm仅仅使用了index来标识属性
	 */
	protected LinkedHashMap<String,Double> vocabulary = null;
	protected Map<String,Integer> vocabularyIndex = null;
	protected double[] weightVector = null;
	protected static Map<String,String> fileDirCategoryMap = MetaData.getFileDirCategoryMap();
	
	double[] positiveVector = null;
	double[] negativeVector = null;
	
	protected void build(String category){
		System.out.println("build Rocchio start");
		
		if (this.vocabularyIndex == null) {
			this.loadVocabularyByCategory(category);
		}
		String generalDirPath = MetaData.getGeneralPath();
		String filePath = MetaData.getPart1PathByCategory(category);
		
		positiveVector = this.loadDir(category, filePath);
		negativeVector = this.loadDir(category, generalDirPath);

		for(int i = 0;i < positiveVector.length;i++){
			positiveVector[i] = (positiveVector[i])/MetaData.dataSetSize;
			negativeVector[i] = (negativeVector[i])/MetaData.dataSetSize;
		}

		positiveVector = this.arrayProduct(positiveVector, weightVector);
		negativeVector = this.arrayProduct(negativeVector, weightVector);
		
		System.out.println("build Rocchio ok!");
	}
	
	public double arrayDotProduct(double[] r1,double[] r2){
		if(r1.length != r2.length) return 0;
		
		double sum = 0;
		for(int i = 0 ;i < r1.length;i++){
			sum += r1[i] * r2[i];
		}
		
		return sum;
		
	}
	
	public double[] arrayProduct(double[] r1,double[] r2){
		if(r1.length != r2.length) return r1;
		
		for(int i = 0 ;i < r1.length;i++){
			r1[i] *= r2[i];
		}
		
		return r1;
		
	}
	
	public double arrayEuclideanNorm(double[] r1){
		if(r1.length == 0) return 0;
		
		double squareSum = 0;
		for(int i = 0 ;i < r1.length;i++){
			squareSum += r1[i] * r1[i];
		}
		//Math.sqrt(squareSum);
		return Math.sqrt(squareSum);
	}
	
	public double classify(String doc,String category){
		if(vocabulary == null){
			this.build(category);
		}
		double[] fileVector;
		try {
			fileVector = this.docToRocchioVector(doc, category);
			return this.classifyByVector(fileVector);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
		
	}

	/**
	 * 1.0 和 -1.0 表示是否相关
	 */
	protected double classifyByVector(double[] fileVector){
		
		fileVector = this.arrayProduct(fileVector, weightVector);
		double modifyFactor = 0.00001;
		double simPosi = (this.arrayDotProduct(fileVector,this.positiveVector) + modifyFactor)/(this.arrayEuclideanNorm(fileVector) * this.arrayEuclideanNorm(positiveVector) + modifyFactor);
		double simNega = (this.arrayDotProduct(fileVector,this.negativeVector) + modifyFactor)/(this.arrayEuclideanNorm(fileVector) * this.arrayEuclideanNorm(negativeVector) + modifyFactor);
		

		double score = Math.abs(simPosi)/Math.abs(simNega);
		/*if(score == Double.NaN) score = 100;
		if(score > 100) score = 100;
		if(score < 0) score = 0;*/
		return score;
		/*if(goldenRatio * simPosi > simNega && simPosi >= 0.5){
			return 1.0;
		}
		return -1.0;*/
	}
	
	public double classify(File doc,String category){
		if(vocabulary == null){
			this.build(category);
		}
		double[] fileVector;
		try {
			fileVector = this.docToRocchioVector(doc, category);
			return this.classifyByVector(fileVector);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	protected double[] arrayAdd(double[] r1,double[] r2){
		
		if(r1.length != r2.length) return r1;
		
		for(int i = 0 ;i < r1.length;i++){
			r1[i] += r2[i];
		}
		
		return r1;
	}
	
	protected double[] loadDir(String category,String dirPath){
		
		File dir = new File(dirPath);
		double[] rValues = new double[vocabulary.size()];
		Arrays.fill(rValues,0);
		
		if(!dir.exists() || !dir.isDirectory()) {
			System.out.println(dirPath + ": is not a dir or not exist!");
			return rValues;
		}
		File[] files = dir.listFiles();
		for(int i = 0;i < files.length;i++){
			try {
				rValues = this.arrayAdd(rValues, this.docToRocchioVector(files[i], category));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return rValues;
	}
	
	protected void loadVocabularyByCategory(String category){
		try {
			vocabulary = XXCalculate.getTermMapByCategory(category);
			vocabularyIndex = new HashMap<String,Integer>();
			int index = 0;
			
			weightVector = new double[vocabulary.size()];
			for(Iterator<String> iter = vocabulary.keySet().iterator();iter.hasNext();){
				String key = iter.next();
				vocabularyIndex.put(key, index);
				
				weightVector[index] = vocabulary.get(key);
				index++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected double[] docToRocchioVector(File docPath, String category)
			throws IOException {

		return this.docToRocchioVector(new BufferedReader(new FileReader(docPath)), category);
	}
	
	protected double[] docToRocchioVector(BufferedReader br, String category)
			throws IOException {

		

		double[] rArray = new double[this.vocabulary.size()];

		Arrays.fill(rArray,0);

		IKSegmentation ik = new IKSegmentation(br, true);
		Lexeme l = ik.next();
		while (l != null) {
			String term = l.getLexemeText();
			l = ik.next();
			if (!vocabularyIndex.containsKey(term)) {
				continue;
			}
			
			int index = vocabularyIndex.get(term);
			rArray[index] += 1;
			
		}
		return rArray;
	}

	protected double[] docToRocchioVector(String docStr, String category)
			throws IOException {

		return this.docToRocchioVector(new BufferedReader(new StringReader(docStr)),
				category);

	}

	/*public double classify(String doc) {

		return 0.0;
	}*/
}
