package xray2.crawler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import xray2.court.Lord;
import xray2.crawler.downloader.HTMLGetter;
import xray2.entity.URLEntity;
import xray2.scheduler.AddURLs;
import xray2.scheduler.GetNewURL;
import xray2.util.Util;



public class SimpleCrawler implements Runnable{
	private AddURLs adder;
	private GetNewURL getter;
	private Lord lord;
	
	public SimpleCrawler(AddURLs adder,GetNewURL getter,Lord lord){
		this.adder = adder;
		this.getter = getter;
		this.lord = lord;
	}
	@Override
	public void run() {
		while(true){
			
			
			URLEntity urlEntity = getter.getNewURL();
			
			System.out.println(urlEntity.getUrl());
			String html = HTMLGetter.getHtmlString(urlEntity.getUrl());
			
			if(html == null || html.equals("")){
				continue;
			}
			
			String charset = HTMLGetter.findHtmlCharset(html);
			
			Map[] maps = lord.getQualifiedURL(html, urlEntity.getUrl());
			maps[0] = HTMLGetter.completionUrl(urlEntity.getUrl(), maps[0]);
			maps[1] = HTMLGetter.completionUrlDoubleValue(urlEntity.getUrl(), maps[1]);
			if(maps != null){
				double nowURLScore = (Double) maps[1].get(urlEntity.getUrl());
				maps[1].remove(urlEntity.getUrl());
				maps[0].remove(urlEntity.getUrl());
				if(nowURLScore > 1.0){
					urlEntity.setCash(nowURLScore);
					urlEntity.setContent(html);
					urlEntity.setTitle(HTMLGetter.getTitle(html));
					urlEntity.setHost(Util.getHostByUrl(urlEntity.getUrl()));
					urlEntity.setInfo("");
					urlEntity.setPriority(Util.cashToPriority(urlEntity.getCash()));
					adder.addFetchedURL(urlEntity);
				}
				
				Iterator iter = maps[0].entrySet().iterator();
				while(iter.hasNext()){
					Entry e = (Entry) iter.next();
					String url = (String) e.getKey();
					String anchor = (String) e.getValue();
					double cash = (Double) maps[1].get(url);
					
					url = HTMLGetter.filterUrlParam(url, charset);
					URLEntity temp = new URLEntity();
					temp.setUrl(url);
					temp.setCash(cash);
					temp.setAnchorText(anchor);
					temp.setHost(Util.getHostByUrl(temp.getUrl()));
					temp.setInfo("");
					temp.setPriority(Util.cashToPriority(temp.getCash()));
					adder.addUnfetchedURL(temp);
				}
			}
			
			
			
		}
		
	}
	public static void main(String[] args){
		AddURLs adder = new AddURLs();
		GetNewURL getter = new GetNewURL();
		Lord lord = new Lord("军事");
		for(int i = 0; i < 20;i ++){
			SimpleCrawler c = new SimpleCrawler(adder,getter,lord);
			Thread t = new Thread(c);
			t.start();
		}
	}
}
