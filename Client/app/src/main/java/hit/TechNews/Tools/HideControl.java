package hit.TechNews.Tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public  class HideControl {
    private View obj_view;
    private final static int MSG_HIDE = 0x01;
    private boolean is_have=false;
    private HideHandler mHideHandler;

    public HideControl() {
        mHideHandler = new HideHandler();
    }
    public HideControl setView(View view){
        obj_view=view;
        return this;
    }
    @SuppressLint("HandlerLeak")
    public class HideHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDE) {
                obj_view.setVisibility(View.INVISIBLE);
            }

        }
    }
    public HideControl set_is_have(boolean have){
        is_have=have;
        return this;
    }


    private Runnable hideRunable = new Runnable() {

        @Override
        public void run() {
            is_have=false;
            mHideHandler.obtainMessage(MSG_HIDE).sendToTarget();
        }
    };

    public void startHideTimer() {//开始计时,三秒后执行runable
        if (is_have){
            return;
        }
        is_have=true;
        mHideHandler.removeCallbacks(hideRunable);
        if(obj_view.getVisibility() == View.INVISIBLE){
            obj_view.setVisibility(View.VISIBLE);
        }
        mHideHandler.postDelayed(hideRunable, 3000);
    }

    public void endHideTimer() {//移除runable,将不再计时
        mHideHandler.removeCallbacks(hideRunable);
    }

    public void resetHideTimer() {//重置计时
        mHideHandler.removeCallbacks(hideRunable);
        mHideHandler.postDelayed(hideRunable, 3000);
    }

}

