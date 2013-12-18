package xray2.classifier.train;

/**
 * 计算卡方检验和tf-idf时需要用到的数据
 * @author asus
 *
 */
public class TermData {
	public String term;
	/*对应卡方检验的公式*/
	public int N11;
	public int N10;
	public int N01;
	public int N00;
	
	public double xx;
	/*所有类别中出现这个词的文档的数目*/
	public int df;
	public TermData(){
		this.N00 = 0;
		this.N01 = 0;
		this.N10 = 0;
		this.N11 = 0;
		this.xx = 0.0;
		this.df = 0;
	}
	
	public TermData plus(TermData t){
		
		if(t == null) return this.clone();
		
		this.df += t.df;
		this.N00 += t.N00;
		this.N01 += t.N01;
		this.N10 += t.N10;
		this.N11 += t.N11;
		
		return this.clone();
	}
	
	public TermData clone(){
		TermData rt = new TermData();
		rt.df = this.df;
		rt.N00 = this.N00;
		rt.N01 = this.N01;
		rt.N10 = this.N10;
		rt.N11 = this.N11;
		
		return rt;
	}
	
	@Override
	public String toString() {
		return this.term + "---" +"{xx:" + this.xx + ",N00:" + this.N00 + "," + "N01:" + this.N01 + "," + "N10:"
				+ this.N10 + "," + "N11:" + this.N11 + ",df:" + this.df +"}";
	}
}
