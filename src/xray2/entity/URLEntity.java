package xray2.entity;

public class URLEntity {
	private int id;
	private String url;
	private String host;
	private String anchorText;
	private String title;
	private String content;
	private double cash;
	private int priority;
	private String lastFetchTime;
	private String lastModifyTime;
	private int lastFetcheUpdated;
	private String info;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getAnchorText() {
		return anchorText;
	}
	public void setAnchorText(String anchorText) {
		this.anchorText = anchorText;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getLastFetchTime() {
		return lastFetchTime;
	}
	public void setLastFetchTime(String lastFetchTime) {
		this.lastFetchTime = lastFetchTime;
	}
	public String getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public int getLastFetcheUpdated() {
		return lastFetcheUpdated;
	}
	public void setLastFetcheUpdated(int lastFetcheUpdated) {
		this.lastFetcheUpdated = lastFetcheUpdated;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
