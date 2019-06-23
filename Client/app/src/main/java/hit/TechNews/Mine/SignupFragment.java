package hit.TechNews.Mine;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import hit.TechNews.API.api;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Mine.UserInfo.ResultCode;
import hit.TechNews.R;
import hit.TechNews.Utils.HttpUtil;
import hit.TechNews.Utils.JsonUtil;
import hit.TechNews.Utils.ToastUtil;

public class SignupFragment extends BaseFragment {
    @BindView(R.id.signupemail)
    EditText mEmail;
    @BindView(R.id.signuppassword)
    EditText mPassword;
    @BindView(R.id.signupphone)
    EditText mPhone;
    @BindView(R.id.signupname)
    EditText mNickname;
    @BindView(R.id.signuphead)
    ImageView headview;
    @BindView(R.id.signupbutton)
    Button signupbuttom;
    UserEntry userEntry;
    String email,password,phone,nickname;
    View focusView = null;
    boolean cancel = false;
    public static SignupFragment getInstance(){
        return new SignupFragment();
    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void returnResult(String result) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signup;
    }

    @Override
    protected void initView(View view) {
        userEntry=DB.getInstance(App.instance).getUser();
        signupbuttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });
    }

    @Override
    protected void lazyFetchData() {

    }

    private void attemptSignup() {
        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);
        // Store values at the time of the login attempt.
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        phone=mPhone.getText().toString();
        nickname=mNickname.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if(TextUtils.isEmpty(phone)){
            mPhone.setError("需要手机号");
            focusView=mPhone;
            cancel=true;
        }
        if(!isPhoneValid(phone)){
            mPhone.setError("手机号错误");
            focusView=mPhone;
            cancel=true;
        }
        if(TextUtils.isEmpty(nickname)){
            mNickname.setError("需要昵称");
            focusView=mNickname;
            cancel=true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Signup();
        }
    }
    private void Signup(){


        String URL= api.IPADDRESS+"/api/signup";
        List<HttpUtil.Param> params = new ArrayList<>();
        params.add(new HttpUtil.Param("email",email));
        params.add(new HttpUtil.Param("password",password));
        params.add(new HttpUtil.Param("phone",phone));
        params.add(new HttpUtil.Param("nickname",nickname));
        HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                ResultCode resultCode= JsonUtil.deserialize(response,ResultCode.class);
                if (resultCode.getCode()==0){
                    userEntry.setUserid(resultCode.getUserEntry().getUserid());
                    userEntry.setNickname(nickname);
                    userEntry.setPhone(phone);
                    userEntry.setEmail(email);
                    userEntry.setToken(resultCode.getUserEntry().getToken());
                    DB.getInstance(App.instance).Login(userEntry);
                    Objects.requireNonNull(getActivity()).finish();
                }
                else if (resultCode.getCode()==1){
                    mPhone.setError("手机号已存在");
                    mPhone.requestFocus();
                }
                else if (resultCode.getCode()==2){
                    mEmail.setError("邮箱已存在");
                    mEmail.requestFocus();
                }
                else if (resultCode.getCode()==3){
                    new ToastUtil().shortDuration("服务器错误").show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                new ToastUtil().shortDuration("网络错误").show();

            }
        },params);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        String regex=".*[a-zA-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(phone);
        return !m.matches();
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


}
