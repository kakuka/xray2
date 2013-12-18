package xray2.util;

import xray2.entity.URLEntity;
import xray2.scheduler.AddURLs;

public class Seed {
	private AddURLs adder = new AddURLs();
	public void add(String url){
		URLEntity temp = new URLEntity();
		temp.setUrl(url);
		temp.setCash(9);
		temp.setHost(Util.getHostByUrl(temp.getUrl()));
		temp.setInfo("");
		temp.setPriority(Util.cashToPriority(temp.getCash()));
		adder.addUnfetchedURL(temp);
	}
	
	public static void main(String[] args){
		Seed seed = new Seed();
		seed.add("http://www.sina.com.cn/");
		seed.add("http://www.sohu.com/");
		seed.add("http://www.qq.com/");
		seed.add("http://www.163.com/");
	}
}
