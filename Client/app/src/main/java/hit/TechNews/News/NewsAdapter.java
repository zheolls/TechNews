package hit.TechNews.News;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import hit.TechNews.BaseModel.BaseViewHolder;
import hit.TechNews.Entry.NewsEntry;
import hit.TechNews.R;
import hit.TechNews.Utils.DateUtil;
import hit.TechNews.Utils.ImageLoadUtil;
import hit.TechNews.Utils.SettingUtil;

public class NewsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_FOOTER = 0;
    private static final int ITEM_IMAGE = 1;
    private String TAG="NewsAdapter";
    private List<NewsEntry> datas;
    private Context mContext;
    private int viewFooter;
    private View footerView;
    private OnItemClickListener mOnItemClickListener;
    private int itemWidth;

    public NewsAdapter(List<NewsEntry> datas, Context context) {
        this.datas = datas;
        this.mContext = context;
        itemWidth = (SettingUtil.getScreenWidth(context) - SettingUtil.dip2px(context, 32)) / 3;
    }

    public void replaceAll(List<NewsEntry> elements) {
        if (datas.size() > 0) {
            datas.clear();
        }
        datas.addAll(elements);
        notifyDataSetChanged();
    }

    public void addAll(List<NewsEntry> elements) {
        datas.addAll(elements);
        notifyDataSetChanged();
    }
    public int getArticleId(int position){
        return datas.get(position).getArticleid();
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ITEM_IMAGE) {
            return new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_has_image, viewGroup, false));
        }else {
            Log.d("FOOT",String.valueOf(viewFooter));
            footerView = LayoutInflater.from(mContext).inflate(viewFooter, viewGroup, false);
            return new BaseViewHolder(footerView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder baseViewHolder, @SuppressLint("RecyclerView") final int i) {

        if (!(viewFooter != 0 && i == getItemCount() - 1)) {
            int type = getItemViewType(i);
            if (mOnItemClickListener != null) {
                baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(i, baseViewHolder);
                    }
                });
            }

            NewsEntry item = datas.get(i);
            try {
                Log.d("NEWSADAPTER",String.valueOf(item.getArticleid()));

                ImageView imageView = baseViewHolder.getView(R.id.cover);
                baseViewHolder.setText(R.id.title, item.getTitle());
                baseViewHolder.setText(R.id.author,item.getSource());
                baseViewHolder.setText(R.id.date, DateUtil.fromToday(item.getDate()));
                baseViewHolder.setText(R.id.fever,String.valueOf(item.getFever()));
                ImageLoadUtil.loadingImg(mContext, imageView, item.getImgurl());
            } catch (Exception e) {
                Log.d("NEWSADAPTER",e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = (datas == null ? 0 : datas.size());
        if (viewFooter != 0) {
            count++;
        }
        return count;    }
    public void addFooterView(int footerView) {
        this.viewFooter = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setFooterVisible(int visible) {
        footerView.setVisibility(visible);
    }

    //设置点击事件
    public void setOnItemClickLitener(OnItemClickListener mLitener) {
        mOnItemClickListener = mLitener;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (viewFooter != 0 && position == getItemCount() - 1) {
            type = TYPE_FOOTER;
        } else {
            type = ITEM_IMAGE;
        }
        return type;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int i, BaseViewHolder baseViewHolder);
    }
}
