package cc.javake.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cc.javake.indexablelistview.IndexableAdapter;
import cc.javake.indexablelistview.IndexableListView;
import cc.javake.indexablelistview.R;

public class DemoActivity extends Activity {

	private IndexableListView mListView;
	private DemoAdapter adapter;
	private List<DemoItem> dataList;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mListView = (IndexableListView) findViewById(R.id.listview);

        dataList = buildData();
        Collections.sort(dataList);

        // head view
        LayoutInflater layInf = LayoutInflater.from(this);
        TextView tv = (TextView)layInf.inflate(android.R.layout.simple_list_item_1, null);
        tv.setText("Hello , I am search view.");
        tv.setBackgroundColor(Color.DKGRAY);
        tv.setTextColor(Color.CYAN);
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.indexablelistview_search_l, 0, 0, 0);
        tv.setCompoundDrawablePadding(15);
        mListView.addHeaderView(tv);
        
        tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				mListView.setIndexBarWarpHeight(!mListView.isIndexBarWarpHeight());
//				mListView.setIndexBarDrawTopSec(!mListView.isIndexBarDrawTopSec());
				dataList.add(new DemoItem("AA test"));
				Collections.sort(dataList);
				adapter.notifyDataSetChanged();
				mListView.withDataSetChanged();
			}
		});
        
        mListView.setFastScrollEnabled(true);
        
        new Handler() { }.postDelayed(new Thread() {
        	@Override
        	public void run() {
        		super.run();
                adapter = new DemoAdapter(DemoActivity.this, dataList, 1);
                mListView.setAdapter(adapter);
                mListView.withDataSetChanged();
        	}
        }, 9000);
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				startActivity(new Intent(DemoActivity.this, TestActivity.class));
			}
		});
    }
    
    class DemoAdapter extends IndexableAdapter<DemoItem> {

		public DemoAdapter(Context context, List<DemoItem> dataList, int posiOffset) {
			super(context, dataList, posiOffset);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layInf = LayoutInflater.from(getContext());
			TextView tv = (TextView)layInf.inflate(android.R.layout.simple_list_item_1, null);
			tv.setText(getDataList().get(position).getVal());
			return tv;
		}
    }
    
    private List<DemoItem> buildData() {
    	List<String> mItems = new ArrayList<String>();
        mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
        mItems.add("Steve Jobs");
        mItems.add("Inheritance (The Inheritance Cycle)");
        mItems.add("11/22/63: A Novel");
        mItems.add("The Hunger Games");
        mItems.add("The LEGO Ideas Book");
        mItems.add("Explosive Eighteen: A Stephanie Plum Novel");
        mItems.add("Catching Fire (The Second Book of the Hunger Games)");
        mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
        mItems.add("Death Comes to Pemberley");
        mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
        mItems.add("Steve Jobs");
        mItems.add("Inheritance (The Inheritance Cycle)");
        mItems.add("11/22/63: A Novel");
        mItems.add("The Hunger Games");
        mItems.add("The LEGO Ideas Book");
        mItems.add("Explosive Eighteen: A Stephanie Plum Novel");
        mItems.add("Catching Fire (The Second Book of the Hunger Games)");
        mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
        mItems.add("Death Comes to Pemberley");
        
        List<DemoItem> dataList = new ArrayList<DemoItem>(mItems.size());
        for (int i = 0; i < mItems.size(); i++) {
			dataList.add(new DemoItem(mItems.get(i)));
		}
        return dataList;
    }
    
}