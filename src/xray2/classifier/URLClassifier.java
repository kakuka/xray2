package xray2.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import xray2.util.Util;





public class URLClassifier {
	public class Block {
		public ArrayList<Node> nodes;
		public int level;
	}
	ArrayList<Block> blocks ;
	Block cB = null ;
	DocClassifier dc;
	String category = "";
	double simDoc = 0;
	/*public static final String scoreSpliter = "#";*/
	double factor = 0.5;
	
	public void setFactor(double factor){
		this.factor = factor;
	}
	
	public URLClassifier(String category,DocClassifier dc){
		this.category = category;
		this.dc = dc;
	}
	protected void buildBlocks(String html){
		blocks = new ArrayList<Block>();
		Document doc = Jsoup.parse(html);
		simDoc = dc.classify(doc.text(), category);	
		travel(doc,0);
	}
	
	public Map[] getURLs(String html,String url){
		this.buildBlocks(html);
		return classify(url);
	}
	
	
	protected String filterURL(String url) {
		if (url == null)
			return null;
		if (url.contains("@")) {
			return null;
		} else {
			if (url.startsWith("javascript:") || url.contains("\'")) {
				return null;
			}
			if (url.contains("#")) {
				int index = url.indexOf('#');
				url = url.substring(0, index);
			}
			return url;
		}
	}

	/*public static void main(String[] args){
		System.out.println(URLClassifier.filterURL("javascript:"));
		System.out.println(URLClassifier.filterURL("sdfasdf'dsf"));
		System.out.println(URLClassifier.filterURL("sdf@asdfdsf"));
		System.out.println(URLClassifier.filterURL("sdf.html#asdfdsf"));
		System.out.println(URLClassifier.filterURL("sdf.htmasdfdsf"));
	}*/
	
	protected Map[] classify(String url){
		Map<String,String> rMapT = new HashMap<String,String>();
		Map<String,Double> rScoreMapT = new HashMap<String,Double>();
		rMapT.put(url, "");
		rScoreMapT.put(url, simDoc);
		for(int i = 0;i < blocks.size();i++){
			ArrayList<Node> a = blocks.get(i).nodes;
			String context = "";
			if(i >= 1){
				ArrayList<Node> b = blocks.get(i - 1).nodes;
				StringBuilder sb = new StringBuilder();
				if(b.size() <= 3){
					for(Node n:b){
						Document doc = Jsoup.parse(n.outerHtml());
						sb.append(doc.text() + " ");
					}
					if(sb.toString().length() < 6 && blocks.get(i).level != blocks.get(i-1).level){
						context = sb.toString();
					}
				}
			}
			for(Node n:a){
				String urlTemp = this.filterURL(n.attr("href"));
				if(urlTemp == null)
					continue;
				Document doc = Jsoup.parse(n.outerHtml());
				if(n.attr("rel").equals("nofollow")/*||n.attr("rel").equals("Nofollow")||n.attr("rel").equals("NOFOLLOW")*/){
				}else{
					double score = factor * dc.classify(context + doc.text() , category)  + (1-factor)*simDoc;
					if(score > 1){					
						rMapT.put(urlTemp,doc.text());
						rScoreMapT.put(urlTemp, score);
					}else{
					}
				}
			}
		}
		Map[] r = new HashMap[2];
		r[0] = rMapT;
		r[1] = rScoreMapT;
		return r;
	}
	
	protected void travel(Node n,int depth){
		if(n.nodeName().equals("a")){
			if(cB == null){
				cB = new Block();
				cB.nodes = new ArrayList<Node>();
				cB.nodes.add(n);
				cB.level = depth;
				blocks.add(cB);
			}else{
				if(cB.level == depth){
					cB.nodes.add(n);
				}else{
					cB = new Block();
					cB.nodes = new ArrayList<Node>();
					cB.nodes.add(n);
					cB.level = depth;
					blocks.add(cB);
				}
			}
		}
		for(Node child : n.childNodes()){
			travel(child,depth + 1);
		}
	}
}
