package xray2.classifier.train;

import java.io.*;
import java.util.ArrayList;

public class TestFile2 {
	public static void createDirAndFile(String path){
		File f = new File(path);
		if(f.exists()) return;
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void writeTofile(String filePath,String data) throws IOException{
		if(data == null || data.length() == 0) return;
		File f = new File(filePath);
		if(f.exists()) return;
		if(filePath.endsWith(File.separator)){
		}else{
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdir();
			}
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			bw.write(data);
			bw.flush();
			bw.close();
		}
	}
	
	 public static void copyFile(File srcFileName, File destFileName, String srcCoding, String destCoding) throws IOException {// 把文件转换为GBK文件
	        BufferedReader br = null;
	        BufferedWriter bw = null;
	        try {
	            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), srcCoding));
	            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), destCoding));
	            char[] cbuf = new char[1024 * 5];
	            int len = cbuf.length;
	            int off = 0;
	            int ret = 0;
	            while ((ret = br.read(cbuf, off, len)) > 0) {
	                off += ret;
	                len -= ret;
	            }
	            bw.write(cbuf, 0, off);
	            bw.flush();
	        } finally {
	            if (br != null)
	                br.close();
	            if (bw != null)
	                bw.close();
	        }
	    }
	
	/*public static void copyFile(File sourceFile, File targetFile ,String targetEncoding)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}*/

	public static void main(String[] args) throws IOException {

		System.out.println("a" + "\\t" + "b");
		System.out.println("a" + "\t" + "b");
		
	}
}