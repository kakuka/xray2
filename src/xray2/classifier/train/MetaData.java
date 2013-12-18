package xray2.classifier.train;

import java.util.HashMap;
import java.util.Map;

public class MetaData {
	protected static String sourceDataPath = "C:\\Users\\asus\\Desktop\\SogouC\\ClassFile";
	protected static String targetDataDir = "D:\\MasterPaperexPeriment";
	
	public static int xxTermSize = 2000;
	
	protected static Map<String,String> fileDirCategoryMap = new HashMap<String,String>();
	
	public static int dataSetSize = 4000;
	protected static String generalDir = targetDataDir + "\\General" + MetaData.dataSetSize;;
	
	protected static String categoryBaseDir = targetDataDir;
	
	protected static void initfileDirCategoryMap(){
		if(fileDirCategoryMap.size() > 0) {return;}
		fileDirCategoryMap.put("汽车", "C000007");
		fileDirCategoryMap.put("财经", "C000008");
		fileDirCategoryMap.put("IT", "C000010");
		fileDirCategoryMap.put("健康", "C000013");
		fileDirCategoryMap.put("体育", "C000014");
		fileDirCategoryMap.put("旅游", "C000016");
		fileDirCategoryMap.put("教育", "C000020");
		fileDirCategoryMap.put("招聘", "C000022");
		fileDirCategoryMap.put("文化", "C000023");
		fileDirCategoryMap.put("军事", "C000024");
	}
	
	
	public static String getTargetFileDirByCategory(String category){
		
		initfileDirCategoryMap();
		String dir = fileDirCategoryMap.get(category);
		String fileDir = MetaData.targetDataDir + "//" + dir + "_" + MetaData.dataSetSize;
		
		return fileDir;
	}
	
	public static String getXXFilePathByCategory(String category){
		String destFileName = MetaData.getTargetFileDirByCategory(category) + "//xxData.txt" ;
		return destFileName;
	}
	
	public static Map<String,String> getFileDirCategoryMap(){
		initfileDirCategoryMap();
		return fileDirCategoryMap;
	}
	
	public static String getGeneralPath(){
		return generalDir;
	}
	
	public static String getPart1PathByCategory(String category){
		/*if(fileDirCategoryMap == null){
			initfileDirCategoryMap();
		}
		String dir = fileDirCategoryMap.get(category);
		String fileDir = MetaData.targetDataDir + "//" + dir + "_" + MetaData.dataSetSize;*/
		return getTargetFileDirByCategory(category)  + "//part1";
	}
	
	public static String getPart2PathByCategory(String category){
		/*if(fileDirCategoryMap == null){
			initfileDirCategoryMap();
		}
		String dir = fileDirCategoryMap.get(category);
		String fileDir = MetaData.targetDataDir + "//" + dir + "_" + MetaData.dataSetSize;
		String path = fileDir + "//part2";*/
		return getTargetFileDirByCategory(category)  + "//part2";
	}
	
	public static String getSourceDirByCategory(String category){
		String dir = fileDirCategoryMap.get(category);
		String fileDir = MetaData.sourceDataPath + "//" + dir;
		
		return fileDir;
	}
}

