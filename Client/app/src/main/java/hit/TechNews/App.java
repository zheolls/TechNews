package hit.TechNews;

import android.content.ComponentCallbacks2;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;

import com.bumptech.glide.Glide;

import hit.TechNews.BaseModel.BaseApplication;

public class App extends BaseApplication {
    public static int mAppStatus = -1;
    public static App instance;
    public App(){
        instance=this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 解决7.0无法使用file://格式的URI的第二种方法
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);

        //1.清理内存中的图片2.清理掉Activity只保留Root Activity
        switch (level) {
            case TRIM_MEMORY_COMPLETE:
                //表示 App 退出到后台，并且已经处于 LRU List 比较考靠前的位置
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
                //表示 App 正在正常运行，但是系统已经开始根据 LRU List 的缓存规则杀掉了一部分缓存的进程
                break;
            case TRIM_MEMORY_UI_HIDDEN:
                Glide.get(this).clearMemory();
                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
                break;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }
}
