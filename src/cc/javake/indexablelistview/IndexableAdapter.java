package cc.javake.indexablelistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

/**
 * 可索引的 Adapter for IndexableListView
 *
 * @param <T>
 */
public abstract class IndexableAdapter<T extends Indexable> extends BaseAdapter
		implements SectionIndexer {

	private static final String LETTER_SEC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String OTHER_SEC = "#";
	
	private static final String TOP_SEC = "↑";

	public static final String ALL_Sections = OTHER_SEC + LETTER_SEC; // #ABC...

	private Context context;
	private List<T> dataList;

	private int posiOffset;	// ListView 设置了 HeadView后的 位置偏移量   > 0 支持 ↑ 回到顶部
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	private String[] sections4Show = null;

	public IndexableAdapter(Context context, List<T> dataList) {
		this(context, dataList, 0);
	}
	
	public IndexableAdapter(Context context, List<T> dataList, int posiOffset) {
		super();
		this.context = context;
		this.dataList = dataList;
		this.posiOffset = posiOffset;
		initData4Sections();
	}

	private void initData4Sections() {
		this.sectionMap.clear();
		if (dataList == null) {
			sections4Show = new String[0];
			return;
		}
		for (int i = 0; i < dataList.size(); i++) {
			String indexChar = dataList.get(i).getIndexChar();
			indexChar = (indexChar == null) ? "":indexChar.toUpperCase(); // 转大写，去Null
			String section = LETTER_SEC.contains(indexChar) ? indexChar : OTHER_SEC;
			if (!sectionMap.containsKey(section)) { // 第一次 查询到 section
				sectionMap.put(section, i + posiOffset);
			}
		}
		if (posiOffset > 0) {
			sectionMap.put(TOP_SEC, 0);
		}
		List<String> sectList = new ArrayList<String>();
		sectList.addAll(sectionMap.keySet());
		Collections.sort(sectList, sectionCompiler); // 排序 [进入Map后 已乱序]
		sections4Show = new String[sectList.size()];
	    sectList.toArray(sections4Show);
	}

	public Context getContext() {
		return context;
	}

	public List<T> getDataList() {
		return dataList;
	}
	
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
		initData4Sections();
	}
	
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object[] getSections() {
		return sections4Show;
	}

	@Override
	public int getPositionForSection(int sectionPosi) {
	    return sectionMap.get(sections4Show[sectionPosi]);
	}

	/**
	 *  根据当前列表 位置  计算出 section的位置 [暂未用]
	 */
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Comparator<String> sectionCompiler = new Comparator<String>() {

		@Override
		public int compare(String lhs, String rhs) {
			if (TOP_SEC.equals(lhs)) {
				return -1;
			} else if (TOP_SEC.equals(rhs)) {
				return 1;
			}
			return lhs.compareTo(rhs);
		}
	};

}
