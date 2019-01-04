package cherry.android.recycler.sample.linear;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.sample.RecyclerActivity;
import cherry.android.recycler.sample.adapter.StringSimpleAdapter;
import cherry.android.recycler.sample.loadmore.SimpleLoadMoreView;
import ext.android.adapter.wrapper.LoadMoreWrapper;

public class LinearLoadingMoreActivity extends RecyclerActivity {
    private static final String TAG = "Recycler";
    private StringSimpleAdapter mAdapter;
    private List<String> mItems;
    private LoadMoreWrapper mWrapper;
    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StringSimpleAdapter();
        mWrapper = new LoadMoreWrapper(mAdapter, new SimpleLoadMoreView());
        mWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore");
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count > 5) {
                            mWrapper.setState(LoadMoreWrapper.STATE_NO_MORE);
                        }
                        mItems.add("load More item. " + count);
                        mAdapter.notifyItemChanged(mItems.size() - 1);
                        count++;
                    }
                }, 1500);
            }
        });
        mRecyclerView.setAdapter(mWrapper);
        for (int i = 0; i < 15; i++) {
            mItems.add("it is item " + i);
        }
        mAdapter.setItems(mItems);
    }
}
