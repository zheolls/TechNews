package hit.TechNews.Mine.UserInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import hit.TechNews.R;

public class UserInfoAdapter extends ArrayAdapter<UserInfoItem> {
    private int resourceID;
    public UserInfoAdapter(@NonNull Context context, int resource, List<UserInfoItem> objects) {
        super(context, resource,objects);
        resourceID=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserInfoItem userInfoItem=getItem(position);
        @SuppressLint("ViewHolder") View view= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        TextView textView1=view.findViewById(R.id.usercentertv1);
        EditText editText=view.findViewById(R.id.user_center_et);
        assert userInfoItem != null;
        switch (position){
            case 0:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            case 1:
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            case 2:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            case 3:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        textView1.setText(userInfoItem.getKey());
        editText.setText(userInfoItem.getValue());
        editText.setEnabled(userInfoItem.isEnable());
        return view;
    }
}
