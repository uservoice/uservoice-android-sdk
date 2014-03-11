package com.uservoice.uservoicesdk.flow;

import android.content.Context;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.AccessTokenResult;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

public class InitManager {

    private final Context context;
    private final Runnable callback;
    private boolean canceled;

    public InitManager(Context context, Runnable callback) {
        this.context = context;
        this.callback = callback;
    }

    public void init() {
        if (Session.getInstance().getClientConfig() == null) {
            ClientConfig.loadClientConfig(new DefaultCallback<ClientConfig>(context) {
                @Override
                public void onModel(ClientConfig model) {
                    Session.getInstance().setClientConfig(model);
                    loadUser();
                }
            });
        } else {
            loadUser();
        }
    }

    private void loadUser() {
        if (Session.getInstance().getUser() == null) {
            if (shouldSignIn()) {
                RequestToken.getRequestToken(new DefaultCallback<RequestToken>(context) {
                    @Override
                    public void onModel(RequestToken model) {
                        if (canceled)
                            return;
                        Session.getInstance().setRequestToken(model);
                        Config config = Session.getInstance().getConfig();
                        User.findOrCreate(config.getEmail(), config.getName(), config.getGuid(), new DefaultCallback<AccessTokenResult<User>>(context) {
                            public void onModel(AccessTokenResult<User> model) {
                                if (canceled)
                                    return;
                                Session.getInstance().setAccessToken(context, model.getAccessToken());
                                Session.getInstance().setUser(model.getModel());
                                done();
                            }

                            ;
                        });
                    }
                });
            } else {
                AccessToken accessToken = BaseModel.load(Session.getInstance().getSharedPreferences(), "access_token", "access_token", AccessToken.class);
                if (accessToken != null) {
                    Session.getInstance().setAccessToken(accessToken);
                    User.loadCurrentUser(new DefaultCallback<User>(context) {
                        @Override
                        public void onModel(User model) {
                            Session.getInstance().setUser(model);
                            done();
                        }
                    });
                } else {
                    done();
                }
            }
        } else {
            done();
        }
    }

    private boolean shouldSignIn() {
        Config config = Session.getInstance().getConfig();
        return config.getEmail() != null;
    }

    public void cancel() {
        canceled = true;
    }

    private void done() {
        callback.run();
    }
}
