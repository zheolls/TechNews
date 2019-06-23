package hit.TechNews.Views;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndLessOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager manager;

    public EndLessOnScrollListener(LinearLayoutManager manager){
        this.manager = manager;
    }

    public EndLessOnScrollListener(GridLayoutManager manager){
        this.manager = manager;
    }
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(newState == RecyclerView.SCROLL_STATE_IDLE){
            int lastVisiblePosition = manager.findLastVisibleItemPosition();
            if(lastVisiblePosition >= manager.getItemCount() - 1){
                onLoadMore();
            }
        }
    }

    /**
     * 提供一个抽象方法，在Activity中监听到这个EndLessOnScrollListener
     * 并且实现这个方法
     * */
    public abstract void onLoadMore();
}
