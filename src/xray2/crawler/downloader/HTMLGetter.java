package xray2.crawler.downloader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xray2.util.Util;




public class HTMLGetter {
	
	/**
	 * 读取url所指示的html，如果获得的话，返回utf-8编码的String，否则返回null 过滤掉html中的换行符
	 * @param url
	 * @return
	 */
	
	public static String getHtmlString(String urlStr) {
		
		Pattern httpPattern = Pattern.compile("(https://|http://)?[^:@]+[.][^:@]+",Pattern.CASE_INSENSITIVE);
		Matcher httpMatcher = httpPattern.matcher(urlStr);
		if(httpMatcher.matches()){
			if(httpMatcher.group(1) == null){
				urlStr = "http://" + urlStr;
			}
		}else{
			return null;
		}
		String returnStr = "";
		try {
			//System.out.println(urlStr);
			URL url = new URL(urlStr);// 创建URL
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection(); // 获得URLConnection
			httpConnection.setReadTimeout(5000);
			HttpURLConnection.setFollowRedirects(true);
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (compatible; MSIE 6.0; Windows 2000)");
			httpConnection.setRequestProperty("ContentType", "text/html");

			httpConnection.connect();
			String contentType = httpConnection.getHeaderField("Content-Type");
			//System.out.println(contentType);
			String charset = null;
			if(contentType != null){
				Pattern pattern = Pattern.compile(".*?charset\\s*=\\s*(.+)",Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(contentType);
				
				if(matcher.matches()){
					charset = matcher.group(1);
				}
			}
			
			//System.out.println(charset);
			if (httpConnection.getResponseCode() == 200) {
				// 读取URL连接的网络资源
				try {
					InputStream input = httpConnection.getInputStream();
					byte[] htmlBytes;
					htmlBytes = readHtmlBytes(input);
					String htmlString = new String(htmlBytes.clone());
					
					/*
					 * <meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
						<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
						<meta http-equiv="Content-Type" content="text/html; charset=big5" />
						<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
						<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
						<meta http-equiv="Content-Type" content="text/html; charset=GB18030" />
					 */
					List codeList = new ArrayList();
					codeList.add("utf-8");
					codeList.add("gb2312");
					codeList.add("gbk");
					codeList.add("big5");
					codeList.add("iso-8859-1");
					codeList.add("GB18030");

					
					List thisCodeList = new ArrayList();
					String charset1 = charset;
					String charset2 = findHtmlCharset(htmlString);
					if(charset1 == null){
						charset1 = charset2;
					}else{
						thisCodeList.add(charset1);
					}
					thisCodeList.add(charset2);
					for(int i = 0;i < codeList.size();i++){
						if(thisCodeList.indexOf(codeList.get(i)) == -1){
							thisCodeList.add(codeList.get(i));
						}
					}
					//System.out.println(charset);
					boolean decoded = false;
					for(int i =0 ;i < thisCodeList.size();i++){
						if(decoded){
							break;
						}
						String charsetTemp = (String) thisCodeList.get(i);
						try{
							returnStr = new String(htmlBytes, charsetTemp);
							decoded = true;
						}catch(Exception e){
							returnStr = null;
						}
					}
					
					
					//returnStr = java.net.URLDecoder.decode(returnStr, charset);
					//returnStr = java.net.URLEncoder.encode(returnStr, "utf-8");
					if(returnStr != null){
						returnStr = new String(returnStr.getBytes("utf-8"),"utf-8");
					}
					
					{
						//returnStr = putInOneLine(returnStr);
					}
					
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("not 200");
				return null;
			}

			httpConnection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return returnStr;
	}
	
	public static String getTitle(String html){
		String titleReg = "<title>(.*?)</title>";
		Pattern pattern = Pattern.compile(titleReg);
		Matcher matcher = pattern.matcher(html);
		String title = "";
		if (matcher.find()) {
			title = matcher.group(1);
		}
		return title;
	}
	
	public static String getKeywords(String html){
		String keywordsReg = "<meta name=\\s*\"?keyword[s]?\"?\\s*content\\s*=\"?(.*?)\"?/?>";
		Pattern pattern = Pattern.compile(keywordsReg);
		Matcher matcher = pattern.matcher(html);
		String keywords = "";
		if (matcher.find()) {
			keywords = matcher.group(1);
		}
		return keywords;
	}
	
	public static String findHtmlCharset(String str){
		Pattern pattern = Pattern.compile("<\\s*meta.+?charset\\s*=[ \"]*([-a-zA-Z0-9]*).*>",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str) ;
		if(matcher.find()){
			return matcher.group(1);
		}
		return "";
	}
	
	protected static byte[] readHtmlBytes(InputStream input) throws IOException{
		
		byte[][] buf = new byte[10][];
		int byteArrayCount = 1;
		int totalCount = 0;
		for(int i = 0;i < buf.length;i++){
			buf[i] = new byte[1024 * 1024];
			byteArrayCount = i + 1;
			int count = 0;
			int start = 0;
			while((count = input.read(buf[i],start,buf[i].length - start)) != -1){
				start += count;
				if(start >= buf[i].length){
					break;
				}
			}
			if(start == buf[i].length){
			}else{
				totalCount += start;
				break;
			}
		}
		byte[] htmlBytes = new byte[totalCount];
		int copyed = 0;
		for(int i = 0;i < byteArrayCount;i++){
			for(int j = 0;j < buf[i].length;j++){
				htmlBytes[i * 1024 * 1024 + j] = buf[i][j];
				copyed ++;
				if(copyed == totalCount)
					break;
			}
			if(copyed == totalCount)
				break;
		}
		return htmlBytes;
	}
	
	protected static String putInOneLine(String str){
		try {
			InputStream myIn = new ByteArrayInputStream(str.getBytes("utf-8"));
			// 将System.in转化为面向字符的流
			InputStreamReader ir = new InputStreamReader(myIn,"utf-8");
			BufferedReader in = new BufferedReader(ir);// 为输入流提供缓冲区
			String s;
			StringBuilder sBHtml = new StringBuilder("");
			while ((s = in.readLine()) != null) {
				sBHtml.append(s);
			}
			return sBHtml.toString();
		}catch(Exception e){
			e.printStackTrace();
			return str;
		}
	}
	
	public static Map<String,String> getUrls(String nowUrl,String htmlString){
		//System.out.println("f : getUrl");
		Map<String,String> urlMap = getInsideUrls(htmlString);
		//String charset = findHtmlCharset(htmlString);
		urlMap = completionUrl(nowUrl, urlMap);
		return urlMap;
	}
	
	public static String getRobotTxt(String robotsUrl){
		System.out.println("f : getRobotTxt");
		Pattern httpPattern = Pattern.compile("(https://|http://)?[^:@]+[.][^:@]+",Pattern.CASE_INSENSITIVE);
		Matcher httpMatcher = httpPattern.matcher(robotsUrl);
		if(httpMatcher.matches()){
			if(httpMatcher.group(1) == null){
				robotsUrl = "http://" + robotsUrl;
			}
		}else{
			return null;
		}
		String returnStr = "";
		try {
			//System.out.println(robotsUrl);
			URL url = new URL(robotsUrl);// 创建URL
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection(); // 获得URLConnection
			httpConnection.setReadTimeout(10000);
			HttpURLConnection.setFollowRedirects(true);
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (compatible; MSIE 6.0; Windows 2000)");
			httpConnection.setRequestProperty("ContentType", "text/html");

			httpConnection.connect();
			
			
			//System.out.println(charset);
			if (httpConnection.getResponseCode() == 200) {
				// 读取URL连接的网络资源
				try {
					InputStream input = httpConnection.getInputStream();
					byte[] htmlBytes;
					htmlBytes = readHtmlBytes(input);
					String htmlString = new String(htmlBytes,Util.CHARSET);
					returnStr = htmlString;
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("not 200");
				return null;
			}

			httpConnection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return returnStr;
	}

	
	protected static Map<String,String> getInsideUrls(String htmlString){
		List<Map<String,String>> urlMapList = new ArrayList<Map<String,String>>();
		//<a href="http://storage.ctocio.com.cn/">存储</a>
		//<a href=list.asp?boardid=2><font color=#000066>教师答疑</font></a>
		
		Pattern pattern = Pattern.compile("<a.*?href\\s*=\\s*\"?([^#\"\n\\{\\}\\[\\]\\\\]*?)(>|[\"\\s].*?>)(.*?)</a>",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(htmlString);
		Map<String,String> urlMap = new HashMap<String,String>();
		String charset = findHtmlCharset(htmlString);
		while(matcher.find()){
			String url = matcher.group(1);
			String urlText = "";
			if(url == null || "".equals(url)|| url.contains("@") ){
			}else{
				pattern = Pattern.compile("javascript:.+",Pattern.CASE_INSENSITIVE);//javascript的一种
				Matcher matcherTemp = pattern.matcher(url);
				if(matcherTemp.find()){
				}else{
					if(url.indexOf('\'') >= 0){
						//javascript的一种
						//'+arrWeb1[0].url+'
					}else{
						urlText = matcher.group(3);
						Pattern patternText = Pattern.compile("<.*?alt=\"(.*?)\".*?>");
						Matcher matcherText = patternText.matcher(urlText);
						if(matcherText.matches()){
							urlText = matcherText.group(1);
						}
						urlText = filterUrlTextHtml(urlText);
						/*Map<String,String> map = new HashMap<String,String>();
						map.put("url", url);
						map.put("urlText", urlText);
						urlMapList.add(map);*/
						url = filterUrlParam(url,charset);
						urlMap.put(url,urlText);
						//System.out.println(map);
					}
				}
				
			}	
		}
		return urlMap;
	}
	
	protected static String refineUrlPath(String url){
		String endStr = "";
		if(url == null || url.equals("")){
			return "";
		}
		String headReg = "(http://|https://)(.*)";
		String headStr = "";
		Pattern p = Pattern.compile(headReg);
		Matcher m = p.matcher(url);
		if(m.matches()){
			headStr = m.group(1);
			url = m.group(2);
		}
		if(url.charAt(url.length() - 1) == '/'){
			endStr = "/";
		}
		String[] dirs = url.split("/");
		int cIndex = 0;
		List<String> pathDirList = new ArrayList<String>();
		
		while(cIndex < dirs.length){
			if(dirs[cIndex].equals("..")){
				pathDirList.remove(pathDirList.size() - 1);
				//System.out.println(url);
			}else{
				if(dirs[cIndex].equals(".")){
				}else{
					pathDirList.add(dirs[cIndex]);
				}
			}
			cIndex++;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(headStr);
		boolean first = true;
		for(String cDir : pathDirList){
			if(!first){
				sb.append("/");
			}else{
				first = false;
			}
			sb.append(cDir);
		}
		sb.append(endStr);
		return sb.toString();
	}
	
	public static String filterUrlParam(String url,String charset){
		
		String reg = "([^?]+)[?]([^=]+)=([^&]+)(&.*)";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(url);
		List<String> paramList = new ArrayList<String>();
		Map<String,String> paramMap = new HashMap<String,String>();
		Map<String,String> preAndType = new HashMap<String,String>();
		if(matcher.matches()){
			String p1 = matcher.group(2);
			String v1 = matcher.group(3);
			try {
				v1 = URLEncoder.encode(v1, charset);
			} catch (Exception e) {
			}
			paramMap.put(p1, v1);
			//paramList.add(p1);
			String params = matcher.group(4);
			String mUrl = matcher.group(1);
			if(params != null && params.length() > 0){
				params = params.replaceAll("&(amp;)+", "&amp;");
				reg = "(&(amp;)?)([^=]+)=([^&]+)";
				pattern = Pattern.compile(reg);
				matcher = pattern.matcher(params);
				while(matcher.find()){
					String p = matcher.group(3);
					String v = matcher.group(4);
					String andType = matcher.group(1);
					if(paramMap.get(p) == null){
						try {
							v = URLEncoder.encode(v, charset);
						} catch (Exception e) {
						}
						paramMap.put(p, v);
						preAndType.put(p, andType);
						paramList.add(p);
					}
				}
				String urlTemp = mUrl + "?" + p1 + "=" + v1;
				for(String paramTemp : paramList){
					urlTemp = urlTemp + preAndType.get(paramTemp) + paramTemp + "=" + paramMap.get(paramTemp);
				}
				return urlTemp;
			}else{
				return url;
			}
		}else{
			return url;
		}
		
	}
	

	public static void main(String[] args){
		String url = "http://www.zy91.com/zxzx.aspx?type=4&amp;amp;amp;amp;amp;amp;page=5&amp;amp;amp;amp;amp;page=2&amp;amp;amp;amp;page=3&amp;amp;amp;page=2&amp;amp;page=4&amp;page=5";
		String robots = Util.getRobotDisallowPath(getRobotTxt("http://www.17u.cn/robots.txt"), "kakuka");
		System.out.println(robots);
		System.out.println(Util.canVisit(robots,"http://www.17u.cn/FlightQuery.aspx?refid"));
		//System.out.println(filterUrlParam(url));
	}
	public static Map<String,String> completionUrl(String nowUrl,Map<String,String> urlMap){
		if(nowUrl == null || urlMap == null){
			return null;
		}
		Map<String,String> returnMap = new HashMap<String,String>();
		
		Set<String> keySet = urlMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		
		String reg = "((http://|https://)?.+[.].+[/])[^/]*";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(nowUrl);
		if(matcher.matches()){
			nowUrl = matcher.group(1);
		}else{
			nowUrl = nowUrl + "/";
		}
		
		while(iterator.hasNext()){
			String url = iterator.next();
			String urlText = urlMap.get(url);
			try {
				url = getFullUrl(nowUrl, url);
				url = refineUrlPath(url);
				url = Util.unifyUrl(url);
				if(url.endsWith(".pdf") || url.endsWith(".doc")||url.endsWith(".PDF")||url.endsWith(".docx")||url.endsWith(".DOC")||url.endsWith(".DOCX")) {
					url = null;
				}
			} catch (Exception e) {

			}
			/*try {
				url = URLEncoder.encode(url, charset);
			} catch (Exception e) {
			}*/
			//Map<String,String> mapTemp = new HashMap<String,String>();
			if(url != null){
				returnMap.put(url, urlText);
			}
		}
		return returnMap;
	}
	
	public static Map<String,Double> completionUrlDoubleValue(String nowUrl,Map<String,Double> urlMap){
		if(nowUrl == null || urlMap == null){
			return null;
		}
		Map<String,Double> returnMap = new HashMap<String,Double>();
		
		Set<String> keySet = urlMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		
		String reg = "((http://|https://)?.+[.].+[/])[^/]*";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(nowUrl);
		if(matcher.matches()){
			nowUrl = matcher.group(1);
		}else{
			nowUrl = nowUrl + "/";
		}
		
		while(iterator.hasNext()){
			String url = iterator.next();
			Double urlText = urlMap.get(url);
			try {
				url = getFullUrl(nowUrl, url);
				url = refineUrlPath(url);
				url = Util.unifyUrl(url);
				if(url.endsWith(".pdf") || url.endsWith(".doc")||url.endsWith(".PDF")||url.endsWith(".docx")||url.endsWith(".DOC")||url.endsWith(".DOCX")) {
					url = null;
				}
			} catch (Exception e) {

			}
			/*try {
				url = URLEncoder.encode(url, charset);
			} catch (Exception e) {
			}*/
			//Map<String,String> mapTemp = new HashMap<String,String>();
			if(url != null){
				returnMap.put(url, urlText);
			}
		}
		return returnMap;
	}
	
	/**
	 * 要确保nowUrl是带有斜杠的，因为如果每次都判断的话，太浪费时间
	 * www.baidu.com 要转换成www.baidu.com/ 这样的形式
	 * @param now
	 * @param url
	 * @return
	 */
	protected static String getFullUrl(String now, String url) {
		
		//下面这段能去掉url中的莫名其妙的东西
		Pattern patternString = Pattern.compile("(.+)");
		Matcher matcherString = patternString.matcher(url);
		String urlString = "";
		while(matcherString.find()){
			urlString += matcherString.group(1);
		}
		url = urlString;
		
		String urlReg1 = "(http://|https://).+";
		//String urlReg2 = ".+[.].+[.].+";
		// 这两种都是合法的url表达式,如果上述两者都不符合，那就是站内相对链接
		Pattern pattern = Pattern.compile(urlReg1);
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			//System.out.println(url);
			return url;
		} else {
			if(url.indexOf("/") != 0){
				return now + url;
			}else{
				return Util.getHostByUrl(now) + url.substring(1);
			}
		}
	}
	
	/**
	 * 过滤html的标签
	 * @param str
	 * @return
	 */
	protected static String filterUrlTextHtml(String str){
		String[] contents = str.split("<.*?>");
		StringBuilder sB = new StringBuilder();
		for(String line : contents){
			sB.append(line);
		}
		String rs = sB.toString();
		rs.replaceAll("&nbsp;", " ");
		return sB.toString();
	}
}
