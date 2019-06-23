package hit.TechNews.Mine.UserInfo;

import java.io.Serializable;

public class UserInfoItem implements Serializable {
    private String key;
    private String value;
    private boolean enable;
    public UserInfoItem(String key,String value){
        this.key=key;
        this.value=value;
        this.enable=false;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
