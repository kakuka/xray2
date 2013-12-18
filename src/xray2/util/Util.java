package xray2.util;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Util {
	
	public static final String CHARSET = "UTF-8";
	public static final String REDIS_ADDRESS = "localhost";
	public static final String URLMAP_TEXT = "url_text";
	public static final String URLMAP_U_INTERVAL = "u_interval";
	public static final String URLMAP_FILE_ID = "fid";
	public static final String URLMAP_VERIFIED = "verified";
	public static final String URLMAP_UPDATE = "u_update";
	public static final String URLMAP_NEXT_TIME = "next_time";
	public static final String URLMAP_HOST_ID = "host_id";
	public static final String URLMAP_FETCHING = "fetching";
	public static final String URLMAP_URL = "url";
	public static final String CRAWLER_NAME = "601CRAWLER";
	public static final String ROBOTPATHSPILTER = "::";
	
	public static final String URL_SERVER_IP = "127.0.0.1";
	public static final int URL_SERVER_PORT = 1970;
	
	
	public static final String FILE_SERVER_IP = "127.0.0.1";
	public static final int FILE_SERVER_PORT = 1930;
	
	public static final String TITLE_SPLITTER = "$$$$";
	/**
	 * interval的为 > 0 的 int
	 * @param interval
	 * @return
	 */
	public static int computeInHostPriority(int interval){
		if(interval <= 0 ){
			return 5;
		}
		return (20/interval)/2 + 1;
	}
	
	public static int cashToPriority(double cash){
		cash *= 2;
		if(cash/10 >= 1) return 9;
		if(cash <= 1) return 1;
		return (int)(cash);
	}
	public static String doubleToString(double d){

		DecimalFormat df = new DecimalFormat("0.000000000000000"); 
		String num = df.format(d); 
		return num;
	}
	
	
	/**
	 * 过滤了http标签，但是要保留所有的https标签，url为合法的url格式
	 * @param url
	 * @return
	 */
	public static String filterHttp(String url){
		try {
			Pattern pattern = Pattern.compile("HTTP://(.*)",
					Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
			Matcher matcher = pattern.matcher(url);

			if (matcher.matches()) {
				// System.out.println(matcher.groupCount());
				return matcher.group(1);
			}else{
				//不能匹配 说明是 https
				return url;
			}
		} catch (Exception e) {
			
		}
		return null;
	
	}
	
	public static String getRobotsUrl(String url){
		String host = getHostByUrl(url);
		return host + "/robots.txt";
	}
	/**
	 * 输入的参数不能为空，“”，也不能为不符合规范的url形式,带最后的/
	 * @param url
	 * @return
	 */
	public static String getHostByUrl(String url){
		
		if(url == null){
			return null;
		}
		//String reg = "((https://|http://)[^/]+)[/].*";
		String reg = "((https://|http://)[^/]+)[/].*";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(url);
		if(matcher.matches()){
			url = matcher.group(1);
		}else{
			reg = "((https://|http://)[^?]+)[?].*";
			pattern = Pattern.compile(reg);
			matcher = pattern.matcher(url);
			if(matcher.matches()){
				url = matcher.group(1);
			}
		}
		url = url + "/";
		return url;
		/*reg = "(https://|http://)[^/]+";
		pattern = Pattern.compile(reg);
		matcher = pattern.matcher(url);
		if(matcher.matches()){
		}else{
			url = "http://" + url;
		}
		return url;*/
	}
	
	/**
	 * 过滤html的标签
	 * @param str
	 * @return
	 */
	public static String filterUrlTextHtml(String str){
		String[] contents = str.split("<.*?>");
		StringBuilder sB = new StringBuilder();
		for(String line : contents){
			sB.append(line);
		}
		String rs = sB.toString();
		rs.replaceAll("&nbsp;", " ");
		return sB.toString();
	}
	
	public static String getAllContent(String html){
		if(html == null || html.length() == 0) return "";
 		Document doc = Jsoup.parse(html);
 		
		/*Elements es = doc.getAllElements();
		Iterator<Element> iter = es.iterator();
		String str = "";
		while(iter.hasNext()){
			Element e = iter.next();
			if(e.hasText()){
				str = str + e.text();
			}
		}*/
		
		return doc.text();
	}
	
	public static String filterHtmlTags(String html) {

		String[] contentDscript = html.split("<script.*?</script>|<SCRIPT.*?</SCRIPT>");
		StringBuffer sB = new StringBuffer();
		try{
		for(String line : contentDscript){
			String[] contentDstyle = line.split("<style.*?</style>|<STYLE.*?</STYLE>");
			
			for(String line2 :contentDstyle){
				String[] contentsOutNotes = line2.split("<!--.*?-->");
				for (String lineOutNote : contentsOutNotes) {
					String[] contents = lineOutNote.split("<a [^<]+</a>|<A [^<]+</A>");
					/*if (contents.length < 3) {
						contents = line2.split("<A [^<]+</A>");
					}*/
					for (String line3 : contents) {
						
						line3 = filterUrlTextHtml(line3);
						line3 = line3.replace("&nbsp", "");
						line3 = line3.replaceAll("\\s*", "");
						sB.append(line3);
					}
				}
				
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return sB.toString();
	}
	
	public static void main(String[] args){
		//System.out.println(doubleToString(292312999));
		//System.out.println(getHostByUrl("http://www.baidu.ad????showid= 2"));
	}
	/**
	 * 从robots文件中提取出适合自己的串，如果文件中未指定自己的名字，则使用*用户
	 * robots不为空，“”
	 * crawlerName也不为空或空值
	 * @param robots
	 * @param crawlerName
	 * @return
	 */
	public static String getRobotDisallowPath(String robots, String crawlerName) {
		String[] lines = robots.split("\n");
		Pattern pattern = Pattern
				.compile("^((User-agent)|(Disallow)):\\s*(.*?)\\s*");
		Matcher matcher;
		String name = "";
		String pathes = "";
		boolean meanningPath = false;
		for (String line : lines) {
			matcher = pattern.matcher(line);
			try {
				if (matcher.matches()) {
					if (matcher.group(2) != null) {
						String tempName = matcher.group(4);
						tempName = tempName.trim();
						if (tempName == null)
							continue;
						if (tempName.equals("*") && !name.equals(crawlerName)) {
							name = tempName;
							pathes = "";
							meanningPath = true;
						} else {
							if (tempName.equals(crawlerName)) {
								name = tempName;
								pathes = "";
								meanningPath = true;
							} else {
								meanningPath = false;
							}
						}
					}
					if (matcher.group(3) != null) {
						if (meanningPath) {
							if ("".equals(pathes)) {
								pathes = pathes + matcher.group(4);
							} else {
								pathes = pathes + Util.ROBOTPATHSPILTER + matcher.group(4);
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return pathes;
	}
	
	/**
	 * pathesStr 表示经过处理的robot串 xxx::xxx::xxx::xxx形式，不为空，url为合法的url
	 * @param pathesStr
	 * @param url
	 * @return
	 */
	public static boolean canVisit(String pathesStr,String url){
		//System.out.println(pathesStr);
		//System.out.println(url);
		if(pathesStr == null){
			 return true;
		}
		String[] pathes = pathesStr.split(Util.ROBOTPATHSPILTER);
		for(String line : pathes){
			line = line.trim();
			//System.out.println(line);
			
			if("".equals(line))
				continue;
			if(line.contains("*")){
				line = line.replace("*", ".*?");
			}
			try {
				if (line.charAt(line.length() - 1) == '/') {
					// System.out.println(line);
					
					if(line.charAt(0) == '/'){
						line = line.substring(1);
					}
					/*System.out.println(Util.getHostByUrl(url)
							+ line + ".*");*/
					Pattern pattern = Pattern.compile(Util.getHostByUrl(url)
							+ line + ".*");
					Matcher matcher = pattern.matcher(url);
					if (matcher.matches())
						return false;
				} else {
					if (line.charAt(line.length() - 1) == '$') {
						Pattern pattern = Pattern.compile(".*?" + line);
						Matcher matcher = pattern.matcher(url);
						if (matcher.matches())
							return false;
					} else {
						Pattern pattern = Pattern.compile(".*?" + line);
						Matcher matcher = pattern.matcher(url);
						if (matcher.matches())
							return false;
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
		return true;
	}
	
	public static String fullUrl(String url){
		String reg = "(https://|http://).+";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(url);
		if(matcher.matches()){
		}else{
			url = "http://" + url;
		}
		return url;
	}
	public static String unifyUrl(String url){
		url = fullUrl(url);
		String reg = "(https://|http://)[^/]+";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(url);
		if(matcher.matches()){
			url = url + "/";
		}else{
		}
		return url;
	}
	/**
	 * host和url都为合法的格式
	 * @param host
	 * @param url
	 * @return
	 */
	public static boolean isInHost(String host, String url) {
		//System.out.println(host);
		//System.out.println(url);
		Pattern urlPattern = Pattern.compile("(http://|https://)?([^/]+).*");
		Matcher hostMatcher = urlPattern.matcher(host);
		if(hostMatcher.matches()){
			host = hostMatcher.group(2);
		}else{
			return false;
		}
		Matcher urlMatcher = urlPattern.matcher(url);
		if(urlMatcher.matches()){
			url = urlMatcher.group(2);
		}else{
			return false;
		}
		//System.out.println(host);
		//System.out.println(url);
		if (url.equalsIgnoreCase(host)) {
			//System.out.println("true");
			return true;
		} else {
			return false;
		}
	}
}
