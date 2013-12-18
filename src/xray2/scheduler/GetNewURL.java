package xray2.scheduler;

import xray2.entity.URLEntity;
import xray2.storer.URLPool;

public class GetNewURL {
	int startPriority = 9;
	URLPool urlPool = new URLPool();
	public URLEntity getNewURL(){
		
		URLEntity urlEntity = urlPool.popUnfetchedURLByPriority(startPriority);
		
		
		if(urlEntity == null){
			startPriority--;
			if(startPriority == 0)
				startPriority = 9;
			return getNewURL();
		}else{
			if(startPriority != 9)startPriority++;
		}
		return urlEntity;
	}
}
