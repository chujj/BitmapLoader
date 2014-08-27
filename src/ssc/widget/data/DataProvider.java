package ssc.widget.data;

public interface DataProvider {
	public int getCount();
	public DataProvider getIdx(int idx);
	public String getDescription();
}
