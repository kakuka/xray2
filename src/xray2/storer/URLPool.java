package xray2.storer;

import xray2.entity.URLEntity;
import xray2.storer.database.FetchedURLTable;
import xray2.storer.database.UnfetchedURLTable;

public class URLPool {
	
	private FetchedURLTable ft = new FetchedURLTable();
	private UnfetchedURLTable uft = new UnfetchedURLTable();
	
	public boolean checkExist(String url){
		if(url == null || url.length() == 0){return false;}
		return (ft.checkExist(url)|| uft.checkExist(url));
	}
	
	public boolean checkExistInFetched(String url){
		if(url == null || url.length() == 0){return false;}
		return (ft.checkExist(url));
	}
	
	public URLEntity popUnfetchedURLByPriority(int priority){
		return uft.popNextUrl(priority);
	}
	
	public void addUnfetchedURL(URLEntity urlEntity){
		uft.addUrl(urlEntity);
	}
	
	public void addFetchedURL(URLEntity urlEntity){
		ft.addUrl(urlEntity);
	}
}
