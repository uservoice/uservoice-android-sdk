package com.uservoice.uservoicesdk.flow;

import android.content.Context;
import android.content.SharedPreferences;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.AccessTokenResult;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.rest.RestResult;
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
            ClientConfig.loadClientConfig(context, new DefaultCallback<ClientConfig>(context) {
                @Override
                public void onModel(ClientConfig model) {
                    Session.getInstance().setClientConfig(model);
                    // if we are getting the client config, they are launching the ui
                    // do this here so that we have the subdomain id, so that the channel event works for now
                    // once babayaga actually supports recording events using the subdomain key, this could be moved back to UserVoice.java
                    Babayaga.track(context, Babayaga.Event.VIEW_CHANNEL);
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
                RequestToken.getRequestToken(context, new DefaultCallback<RequestToken>(context) {
                    @Override
                    public void onModel(RequestToken model) {
                        if (canceled)
                            return;
                        Session.getInstance().setRequestToken(model);
                        Config config = Session.getInstance().getConfig(context);
                        User.findOrCreate(context, config.getEmail(), config.getName(), config.getGuid(), new DefaultCallback<AccessTokenResult<User>>(context) {
                            public void onModel(AccessTokenResult<User> model) {
                                if (canceled)
                                    return;
                                Session.getInstance().setAccessToken(context, model.getAccessToken());
                                Session.getInstance().setUser(context, model.getModel());
                                done();
                            }

                            @Override
                            public void onError(RestResult error) {
                                if (error.getType().equals("unauthorized")) {
                                    done();
                                } else {
                                    super.onError(error);
                                }
                            }
                        });
                    }
                });
            } else {
                AccessToken accessToken = BaseModel.load(Session.getInstance().getSharedPreferences(context), "access_token", "access_token", AccessToken.class);
                if (accessToken != null) {
                    Session.getInstance().setAccessToken(accessToken);
                    User.loadCurrentUser(context, new DefaultCallback<User>(context) {
                        @Override
                        public void onModel(User model) {
                            Session.getInstance().setUser(context, model);
                            done();
                        }

                        @Override
                        public void onError(RestResult error) {
                            Session.getInstance().setAccessToken(null);
                            SharedPreferences.Editor edit = Session.getInstance().getSharedPreferences(context).edit();
                            edit.remove("access_token");
                            edit.commit();
                            loadUser();
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
        Config config = Session.getInstance().getConfig(context);
        return config.getEmail() != null;
    }

    public void cancel() {
        canceled = true;
    }

    private void done() {
        callback.run();
    }
}
