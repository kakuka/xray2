package xray2.scheduler;

import xray2.entity.URLEntity;
import xray2.storer.URLPool;

public class AddURLs {
	URLPool urlPool = new URLPool();
	public synchronized void addFetchedURL(URLEntity urlEntity){
		if(urlEntity == null) return;
		if(urlPool.checkExistInFetched(urlEntity.getUrl())) return;
		urlPool.addFetchedURL(urlEntity);
	}
	
	public synchronized void addUnfetchedURL(URLEntity urlEntity){
		if(urlEntity == null) return;
		if(urlPool.checkExist(urlEntity.getUrl())) return ;
		urlPool.addUnfetchedURL(urlEntity);
	}
}
