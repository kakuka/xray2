package xray2.classifier.train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

/**
 * 卡方检验的计算
 * @author asus
 *
 */
public class XXCalculate {

	public static Map<String,String> fileDirCategoryMap = MetaData.getFileDirCategoryMap();
	
/*	public static String xxfileName = "xxData.txt";*/
	
	public Map<String,TermData> calculataByCategory(String category) throws IOException{
		
		System.out.println("calculating " + category + " xx start...");
		String subDir = fileDirCategoryMap.get(category);
		if(subDir == null) return null;
		
		String generalDir = MetaData.getGeneralPath();
		//String categoryDir = MetaData.categoryBaseDir + "\\" + subDir + "_" + MetaData.dataSetSize + "\\part1" ; 
		
		String categoryDir = MetaData.getPart1PathByCategory(category);
		Map<String,TermData> rMap = this.calcu(this.fullFillMap(this.mergeMap(this.calculateFileDir(new File(generalDir), false), this.calculateFileDir(new File(categoryDir), true))));
		System.out.println("calculating " + category + " xx ok!");
		return rMap;
	}
	
	
	
	public static LinkedHashMap<String,Double> getTermMapByCategory(String category) throws IOException{
		String filePath =  MetaData.getXXFilePathByCategory(category);
		
		if(!(new File(filePath).exists())){
			PrepareData prepareData = new PrepareData();
			prepareData.createDataSetByCategory(category);
			XXCalculate xxCalculate = new XXCalculate();
			xxCalculate.saveByCategoryDir(xxCalculate.calculataByCategory(category),category , MetaData.xxTermSize);;
		}
		
		BufferedReader br = null;
		LinkedHashMap<String,Double> rMap = new LinkedHashMap<String,Double>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
            

            String line = br.readLine();
            while(line != null){
            	String[] datas = line.split("\t");
            	if(datas.length == 4){
            		String term = datas[1];
            		String dfStr = datas[3];
            		double df = Double.valueOf(dfStr);
            		double idf = Math.log( (MetaData.dataSetSize * 2 * 1.0) / df);
            		
            	/*	idf = 1;*/
            		
            		rMap.put(term, /*Math.log10(Double.valueOf(xx))*/idf);
            	}else{
            		//System.out.println(datas.length);
            	}
            	line = br.readLine();
            }
        } finally {
            if (br != null)
                br.close();
            
        }
        
        return rMap;
	}
	
	
	protected void saveByCategoryDir(Map<String,TermData> map,String category ,int saveCount) throws IOException{
		
		
		ArrayList<TermData> list = new ArrayList<TermData>();
		
		Iterator<Entry<String,TermData>> iter = map.entrySet().iterator(); 
		while(iter.hasNext()){
			Entry<String,TermData> e = iter.next();
			TermData t = e.getValue();
			t.term = e.getKey();
			list.add(t);
		}
		Collections.sort(list, new Comparator<TermData>(){
			@Override
			public int compare(TermData arg0, TermData arg1) {
				// TODO Auto-generated method stub
				return (int) (arg1.xx - arg0.xx);
			}
			
		});
		String destFileName = MetaData.getXXFilePathByCategory(category);
		/*MetaData.targetDataDir + "\\" + categoryDir + "_" + MetaData.dataSetSize + "\\" + "xxData.txt"*/ ;
		TestFile2.createDirAndFile(destFileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), "utf-8"));
		int count = 0;
		for(TermData t: list){
			/*if(t.xx < 10.83) break;*/
			if(count > saveCount) break;
			count++;
			String termString = count + "\t" + t.term + "\t"  + t.xx + "\t" + t.df; 
			bw.write(termString);
			
			bw.newLine();
		}
        bw.flush();
	}
	
	protected double calcuTerm(TermData t){
		if(t == null) return 0.0;
		
		double E00 = 0;
		double E01 = 0;
		double E10 = 0;
		double E11 = 0;
		double dataSetSize = t.N00 + t.N01 + t.N10 + t.N11;
		/*System.out.println(MetaData.dataSetSize);*/
		
		
		E00 = dataSetSize * (t.N00 + t.N01)/dataSetSize * (t.N00 + t.N10) / dataSetSize;
		E01 = dataSetSize * (t.N00 + t.N01)/dataSetSize * (t.N01 + t.N11) / dataSetSize;
		
		E10 = dataSetSize * (t.N10 + t.N11)/dataSetSize * (t.N00 + t.N10) / dataSetSize;
		E11 = dataSetSize * (t.N10 + t.N11)/dataSetSize * (t.N01 + t.N11) / dataSetSize;
		
		/*System.out.println(E00);
		System.out.println(E01);
		System.out.println(E10);
		System.out.println(E11);*/
		double factor1 = (t.N00 - E00) * (t.N00 - E00) /E00;
		double factor2 = (t.N01 - E01) * (t.N01 - E01) /E01;
		double factor3 = (t.N10 - E10) * (t.N10 - E10) /E10;
		double factor4 = (t.N11 - E11) * (t.N11 - E11) /E11;
		/*t.xx = ((t.N11 + t.N10 + t.N01 + t.N00) * ( t.N11 * t.N00 - t.N10 * t.N01) * ( t.N11 * t.N00 - t.N10 * t.N01) * 1.0)/
				((t.N11 + t.N01) * (t.N11 + t.N10) * (t.N10 + t.N00) * (t.N01 + t.N00));*/
		t.xx = factor1 + factor2 + factor3 + factor4;
		return t.xx;
	}
	
	protected double calcuTermMI(TermData t){
		if(t == null) return 0.0;
		double factor1 = ((t.N11 * 1.0)/MetaData.dataSetSize)* log((MetaData.dataSetSize * t.N11 + 0.5)/((t.N10 + t.N11)*( t.N01 + t.N11)),2);
		double factor2 = ((t.N01 * 1.0)/MetaData.dataSetSize)* log((MetaData.dataSetSize * t.N01 + 0.5)/((t.N00 + t.N01)*( t.N01 + t.N11)),2);
		double factor3 = ((t.N10 * 1.0)/MetaData.dataSetSize)* log((MetaData.dataSetSize * t.N10 + 0.5)/((t.N10 + t.N11)*( t.N00 + t.N10)),2);
		double factor4 = ((t.N00 * 1.0)/MetaData.dataSetSize)* log((MetaData.dataSetSize * t.N00 + 0.5)/((t.N00 + t.N01)*( t.N00 + t.N10)),2);
		
		t.xx = factor1 + factor2 + factor3 + factor4;
		
		return t.xx;
	}
	
	protected Map<String,TermData> calcu(Map<String,TermData> map){
		
		Iterator<Entry<String,TermData>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,TermData> e = iter.next();
			
			TermData t = e.getValue();
			t.xx = this.calcuTerm(t);
			
		}
		return map;
	}

	protected double log(double x, double base){
	    return Math.log(x) / Math.log(base);
	}
	
	protected Map<String,TermData> mergeMap(Map<String,TermData> map1,Map<String,TermData> map2){ 
		if(map1 == null){
			return map2; 
		}
		if(map2 == null) return map1;
		
		Map<String,TermData> rMap = map1;
		Iterator<Entry<String,TermData>> iter = map2.entrySet().iterator();
		while(iter.hasNext()){
			
			Entry<String,TermData> e = iter.next();
			TermData t = rMap.get(e.getKey());
			if(t == null){
				t = e.getValue();
				rMap.put(e.getKey(), t);
			}else{
				t = t.plus(e.getValue());
			}
		}
		return rMap;
	}
	

	public void calcuAllAndSave(){
		
		Iterator<Entry<String,String>> iter = this.fileDirCategoryMap.entrySet().iterator();
		
		while(iter.hasNext()){
			Entry<String,String> e = iter.next();
			
			/*String categoryDir = e.getValue();*/
			
			String category = e.getKey();
			try {
				this.saveByCategoryDir(this.calculataByCategory(category), category, MetaData.xxTermSize);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	
	
	protected Map<String,TermData> calculateFileDir(File dir,boolean isOne) throws IOException{
		
		if(dir == null || !dir.isDirectory()) {
			System.out.print(dir);
			System.out.println("is not dir or not exist!");
			return null;
		}
		Map<String,TermData> rMap = null;
		for(File f : dir.listFiles()){
			if(f.isDirectory()) continue;
			if(rMap == null){
				rMap = this.calculatePerFile(f, isOne);
			}else{
				rMap = this.mergeMap(rMap, this.calculatePerFile(f, isOne));
			}
			
		}
		return rMap;
	}
	
	
	
	protected Map<String,TermData> fullFillMap(Map<String,TermData> map){
		if(map == null) return null;
		
		Iterator<Entry<String,TermData>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,TermData> e = iter.next();
			TermData t = e.getValue();
			t.N00 = MetaData.dataSetSize - t.N10;
			t.N01 = MetaData.dataSetSize - t.N11;
		}
		
		return map;
	}
	
	protected Map<String, TermData> calculatePerFile(File docFile, boolean isOne)
			throws IOException {

		Map<String,TermData> rMap = new HashMap<String,TermData>();
		Set<String> readedSet = new HashSet<String>();
		if(!docFile.exists()){
			System.out.print(docFile);
			System.out.println("not exist!");
			return null;
		}
		BufferedReader br = new BufferedReader(new FileReader(docFile));
		IKSegmentation ik = new IKSegmentation(br, true);
		Lexeme l = ik.next();
		while (l != null) {
			
			String term = l.getLexemeText();
			
			l = ik.next();
			
			term = term.replaceAll("&nbsp", "");
			term = term.replaceAll("nbsp", "");
			term = term.replaceAll("sougou", "");
			term = term.replace(".sogou.", "");
			term = term.replaceAll("sogou", "");
			term = term.replaceAll("搜狗", "");
			term = term.replaceAll("www", "");
			term = term.replaceAll("com", "");
			term = term.replaceAll("www.sougou.com", "");
			if(term.length() == 0) continue;
			if(readedSet.contains(term)) 
				continue;
			
			TermData data = rMap.get(term);
			if(data == null){
				data = new TermData();
				rMap.put(term, data);
			}
			
			if(isOne){
				data.N11 += 1;
			}else{
				data.N10 += 1;
			}
			data.df += 1;
			readedSet.add(term);
		}
		return rMap;
	}
}
