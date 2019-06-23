package hit.TechNews.Mine.UserInfo;

import java.io.Serializable;

import hit.TechNews.Entry.UserEntry;

public class ResultCode implements Serializable {
    private int code;
    private String error;
    private UserEntry user;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public UserEntry getUserEntry() {
        return user;
    }

    public void setUserEntry(UserEntry userEntry) {
        this.user = userEntry;
    }
}
