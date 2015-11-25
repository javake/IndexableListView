package cc.javake.demo;

import cc.javake.indexablelistview.Indexable;

public class DemoItem implements Indexable, Comparable<DemoItem> {
	
	private String val;

	public DemoItem(String paramString) {
		this.val = paramString;
	}

	public int compareTo(DemoItem paramDemoItem) {
		return getVal().compareTo(paramDemoItem.getVal());
	}

	public String getIndexChar() {
		return String.valueOf(this.val.charAt(0));
	}

	public String getVal() {
		return this.val;
	}
	
}
