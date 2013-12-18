package xray2.classifier.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class PrepareData {
	/*public static String sourceDataPath = "C:\\Users\\asus\\Desktop\\SogouC\\ClassFile";
	public static String targetDataDir = "D:\\MasterPaperexPeriment";
	
	
	
	public static int dataSetSize = 4000;*/
	public static Map<String,String> fileDirCategoryMap = MetaData.getFileDirCategoryMap();
	public PrepareData(){
	}
	
	
	protected Set<Integer> getRandomNum(int size,int includeStart,int notIncludeEnd){
		
		Set<Integer> rSet = new HashSet<Integer>();
		Random random = new Random();
		while(rSet.size() < size){
			int num = includeStart + random.nextInt(notIncludeEnd - includeStart);
			rSet.add(num);
		}
		return rSet;
	}
	
	protected boolean checkGeneralDataSetExist(String dirPath){
		File f = new File(dirPath);
		if(!f.exists()){
			return false;
		}
		if(f.isFile()) return true;
		if(f.listFiles().length > 0) return true;
		
		return false;
	}
	
	protected void createGeneralDataSet(){
		System.out.println("creating general data set start...");
		String fileDir = MetaData.getGeneralPath();
		/*String fileDir = "General" + MetaData.dataSetSize;*/
		if(this.checkGeneralDataSetExist(/*MetaData.targetDataDir + "//" + */fileDir)) {
			System.out.println("creating general data set ok!");
			return;
		}
		int categorySize = fileDirCategoryMap.size();
		int sizePerCategory = MetaData.dataSetSize/categorySize;
		
		Iterator<Entry<String,String>> iter = fileDirCategoryMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,String> e = iter.next();
			
			Set<Integer> nameSet = this.getRandomNum(sizePerCategory, 0, 8000);
			Iterator<Integer> iterForFileNameSet = nameSet.iterator();
			while(iterForFileNameSet.hasNext()){
				int num = iterForFileNameSet.next();
				File f = new File(MetaData.sourceDataPath + "//" + e.getValue() + "//" + num + ".txt");
				
				String filePathAndName = /*MetaData.targetDataDir + "//" + */fileDir + "//" + e.getValue() + "_" + num + ".txt";
				File targetFile = new File(filePathAndName);
				TestFile2.createDirAndFile(filePathAndName);
				try {
					TestFile2.copyFile(f, targetFile,"GB2312","utf-8");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		System.out.println("creating general data set ok!");
	}
	
	public void creatAllDataSet(){
		Iterator<Entry<String,String>> iter = fileDirCategoryMap.entrySet().iterator();
		while(iter.hasNext()){
			String category = iter.next().getKey();
			this.createDataSetByCategory(category);
		}
	}
	
	public static void main(String[] args){
		PrepareData pd = new PrepareData();
		pd.creatAllDataSet();
	}
	
	public void createDataSetByCategory(String category){
		
		/*System.out.println();*/
		this.createGeneralDataSet();
		System.out.println("creating " + category + " dir start...");
		String dir = fileDirCategoryMap.get(category);
		Set<Integer> part1nums = this.getRandomNum(MetaData.dataSetSize, 0, 8000);
		File tempFile = new File(MetaData.targetDataDir + "//" + dir + "_" + MetaData.dataSetSize);
		
		if(tempFile != null && tempFile.listFiles() != null &&  tempFile.listFiles().length > 0) return;
		
		Iterator<Integer> iter = part1nums.iterator();
		while(iter.hasNext()){
			int num = iter.next();
			//String fileDir = MetaData.targetDataDir + "//" + dir + "_" + MetaData.dataSetSize;
			String nameAndPath = MetaData.getPart1PathByCategory(category) + "//" + num +".txt";
			
			File f = new File(MetaData.getSourceDirByCategory(category) + "//" + num + ".txt");
			File targetFile = new File(nameAndPath);
			TestFile2.createDirAndFile(nameAndPath);
			try {
				TestFile2.copyFile(f, targetFile,"GB2312","utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		for(int i = 0;i < 8000;i++){
			if(!part1nums.contains(i)){
				//String fileDir = MetaData.targetDataDir + "//" + dir + "_" + MetaData.dataSetSize;
				String nameAndPath =MetaData.getPart2PathByCategory(category) + "//" /*fileDir + "//part2//"*/ + i +".txt";
				
				File f = new File(MetaData.getSourceDirByCategory(category) +  "//" + i + ".txt");
				File targetFile = new File(nameAndPath);
				TestFile2.createDirAndFile(nameAndPath);
				try {
					TestFile2.copyFile(f, targetFile,"GB2312","utf-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("creating " + category + " dir ok!");
	}
	
	

}

