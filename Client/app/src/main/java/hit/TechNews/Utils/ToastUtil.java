package hit.TechNews.Utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import hit.TechNews.BaseModel.BaseApplication;

public class ToastUtil {
    private Toast mToast;
    public static ToastUtil sToastUtil = new ToastUtil();

    public ToastUtil() {

    }

    /**
     * 短时间显示Toast
     */
    @SuppressLint("ShowToast")
    public ToastUtil shortDuration(CharSequence message) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApplication.instance, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     */
    @SuppressLint("ShowToast")
    public ToastUtil longDuration(CharSequence message) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApplication.instance, message, Toast.LENGTH_LONG);
        } else {
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     */
    public ToastUtil indefinite(int duration) {
        if (mToast != null) {
            mToast.setDuration(duration);
        }
        return this;
    }

    /**
     * 设置显示位置
     */
    public ToastUtil setGravity(int gravity) {
        if (mToast != null) {
            mToast.setGravity(gravity, 0, 0);
        }
        return this;
    }

    /**
     * 设置Toast字体及背景颜色
     */
    public ToastUtil setToastColor(int messageColor, int backgroundColor) {
        if (mToast != null) {
            View view = mToast.getView();
            TextView message = ((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundColor(backgroundColor);
            message.setTextColor(messageColor);
        }
        return this;
    }

    /**
     * 设置Toast字体及背景
     */
    public ToastUtil setToastBackground(int messageColor, int background) {
        if (mToast != null) {
            View view = mToast.getView();
            TextView message = ((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundResource(background);
            view.setBackgroundColor(Color.TRANSPARENT);
            message.setTextColor(messageColor);
        }
        return this;
    }


    /**
     * 显示Toast
     */
    public ToastUtil show() {
        mToast.show();
        return this;
    }

    /**
     * 完全自定义布局Toast
     */
    public ToastUtil(View view) {
        mToast = new Toast(BaseApplication.instance);
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
}
