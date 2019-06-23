package hit.TechNews.BaseModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

@SuppressLint("Registered")
public class BaseApplication extends Application {
    public static BaseApplication instance;
    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.refWatcher;
    }
    private RefWatcher refWatcher;
    public BaseApplication(){}

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        refWatcher= LeakCanary.install(this);
    }

    @Override
    public void onLowMemory() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onLowMemory();
    }
}
