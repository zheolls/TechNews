package hit.TechNews.Utils;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import hit.TechNews.R;

public class ImageLoadUtil {
    private static int placeholderId = R.drawable.ic_image_loading;
    private static int errorId = R.drawable.ic_empty_picture;
    /**
     * 加载图片
     * fallback imageUrl为null显示
     * priority 设置优先级
     * asGif 会检查是不是gif图片
     * */
    public static void loadingImg(Context context, ImageView iv, String picUrl){
        Glide.with(context)
                .load(picUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholderId)
                .error(errorId)
                .fallback(errorId)
                .priority(Priority.IMMEDIATE)
                .centerCrop()
                .into(iv);
    }



    public static void displayCircle(Context context,ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(errorId)
                .centerCrop()
                .dontAnimate()
                .into(imageView);
    }

    public static void clear(Context context){
        clearImageDiskCache(context);
        Glide.get(context).clearDiskCache();
    }

    /**
     * 清除图片磁盘缓存
     */
    private static void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
