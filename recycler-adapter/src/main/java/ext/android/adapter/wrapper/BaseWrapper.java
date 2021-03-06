package ext.android.adapter.wrapper;

import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16.
 */

public abstract class BaseWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecyclerView.Adapter<? super RecyclerView.ViewHolder> mInnerAdapter;
    private final List<Integer> mWrapperViewTypeList;
    RecyclerView mAttachedRecyclerView;

    BaseWrapper(@NonNull RecyclerView.Adapter<? super RecyclerView.ViewHolder> adapter) {
        mInnerAdapter = adapter;
        mWrapperViewTypeList = new ArrayList<>();
        RecyclerView.AdapterDataObserver dataObserver = new WrapperAdapterDataObserver();
        adapter.registerAdapterDataObserver(dataObserver);
    }

    @Override
    public final int getItemCount() {
        return getRealItemCount() + getWrapperItemCount();
    }

    @Override
    public final int getItemViewType(int position) {
        if (isWrapperViewPosition(position)) {
            int viewType = getWrapperItemType(position);
            mWrapperViewTypeList.add(viewType);
            return viewType;
        }
        return mInnerAdapter.getItemViewType(position - getWrapperTopCount());
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isWrapperViewType(viewType)) {
            return onCreateWrapperViewHolder(parent, viewType);
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isWrapperViewPosition(position)) {
            onBindWrapperViewHolder(holder, position);
        } else {
            mInnerAdapter.onBindViewHolder(holder, position - getWrapperTopCount());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (isWrapperViewPosition(position)) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            mInnerAdapter.onBindViewHolder(holder, position - getWrapperTopCount(), payloads);
        }
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mAttachedRecyclerView = recyclerView;
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isWrapperViewPosition(position)) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null) {
                        return spanSizeLookup.getSpanSize(position - getWrapperTopCount());
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mAttachedRecyclerView = null;
    }

    @CallSuper
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isWrapperViewPosition(position)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }

    private boolean isWrapperViewType(int viewType) {
        return mWrapperViewTypeList.size() != 0 && mWrapperViewTypeList.contains(viewType);
    }

    abstract int getWrapperTopCount();

    abstract boolean isWrapperViewPosition(int position);

    abstract int getWrapperItemCount();

    abstract void onBindWrapperViewHolder(RecyclerView.ViewHolder holder, int position);

    abstract int getWrapperItemType(int position);

    abstract RecyclerView.ViewHolder onCreateWrapperViewHolder(ViewGroup parent, int viewType);

    private class WrapperAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart + getWrapperTopCount(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(positionStart + getWrapperTopCount(), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart + getWrapperTopCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart + getWrapperTopCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition + getWrapperTopCount(), toPosition + getWrapperTopCount());
        }
    }
}
