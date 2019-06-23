package hit.TechNews.Entry;

import java.io.Serializable;

public class UserEntry implements Serializable {
    private int userid;
    private String email;
    private String phone;
    private String nickname;
    private String token;

    public UserEntry(int userid) {
        this.userid = userid;
        this.token="";
        this.nickname="未登录";
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
