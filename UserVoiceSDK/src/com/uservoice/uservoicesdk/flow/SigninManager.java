package com.uservoice.uservoicesdk.flow;

import android.support.v4.app.FragmentActivity;

import android.widget.Toast;
import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.dialog.PasswordDialogFragment;
import com.uservoice.uservoicesdk.dialog.SigninDialogFragment;
import com.uservoice.uservoicesdk.flow.SigninCallback;
import com.uservoice.uservoicesdk.model.AccessTokenResult;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

import java.util.regex.Pattern;

public class SigninManager {

    private final SigninCallback callback;
    private String email;
    private String name;
    private final FragmentActivity activity;
    private boolean passwordOnly;
    private Pattern emailFormat = Pattern.compile("\\A(\\w[-+.\\w!\\#\\$%&'\\*\\+\\-/=\\?\\^_`\\{\\|\\}~]*@([-\\w]*\\.)+[a-zA-Z]{2,9})\\z");

    public static void signIn(FragmentActivity activity, SigninCallback callback) {
        new SigninManager(activity, null, null, callback).signIn();
    }

    public static void signIn(FragmentActivity activity, String email, String name, SigninCallback callback) {
        new SigninManager(activity, email, name, callback).signIn();
    }

    private SigninManager(FragmentActivity activity, String email, String name, SigninCallback callback) {
        this.activity = activity;
        this.email = email == null || email.trim().length() == 0 ? null : email;
        this.name = name == null || name.trim().length() == 0 ? null : name;
        this.callback = callback;
    }

    private void signIn() {
        User currentUser = Session.getInstance().getUser();
        if (currentUser != null && (email == null || email.equals(currentUser.getEmail()))) {
            callback.onSuccess();
        } else if (Session.getInstance().getAccessToken() != null) {
            // If we have an access token but no user, they have signed in in this session. Don't prompt again.
            callback.onSuccess();
        } else if (email != null && !emailFormat.matcher(email).matches()) {
            Toast.makeText(activity, R.string.uv_msg_bad_email_format, Toast.LENGTH_SHORT).show();
            callback.onFailure();
        } else {
            email = email == null ? Session.getInstance().getEmail() : email;
            name = name == null ? Session.getInstance().getName() : name;
            if (email != null) {
                User.discover(email, new Callback<User>() {
                    @Override
                    public void onModel(User model) {
                        promptToSignIn();
                    }

                    @Override
                    public void onError(RestResult error) {
                        createUser();
                    }
                });
            } else {
                promptToSignIn();
            }
        }
    }

    private void createUser() {
        RequestToken.getRequestToken(new DefaultCallback<RequestToken>(activity) {
            @Override
            public void onModel(RequestToken model) {
                Session.getInstance().setRequestToken(model);
                User.findOrCreate(email, name, new DefaultCallback<AccessTokenResult<User>>(activity) {
                    @Override
                    public void onModel(AccessTokenResult<User> model) {
                        Session.getInstance().setUser(model.getModel());
                        Session.getInstance().setAccessToken(activity, model.getAccessToken());
                        Babayaga.track(Babayaga.Event.IDENTIFY);
                        callback.onSuccess();
                    }
                });
            }
        });
    }

    private void promptToSignIn() {
        if (passwordOnly) {
            PasswordDialogFragment dialog = new PasswordDialogFragment(callback);
            dialog.show(activity.getSupportFragmentManager(), "PasswordDialogFragment");
        } else {
            SigninDialogFragment dialog = new SigninDialogFragment(email, name, callback);
            dialog.show(activity.getSupportFragmentManager(), "SigninDialogFragment");
        }
    }

    public void setPasswordOnly(boolean passwordOnly) {
        this.passwordOnly = passwordOnly;
    }

    public static void signinForSubscribe(FragmentActivity activity, String email, SigninCallback callback) {
        SigninManager manager = new SigninManager(activity, email, Session.getInstance().getName(), callback);
        manager.setPasswordOnly(true);
        manager.signIn();
    }

}
