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
	private boolean configDone;
	private boolean userDone;

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
					configDone = true;

                    if (shouldSignIn()) {
                        RequestToken.getRequestToken(new DefaultCallback<RequestToken>(context) {
                            @Override
                            public void onModel(RequestToken model) {
                                if (canceled) return;
                                Session.getInstance().setRequestToken(model);
                                Config config = Session.getInstance().getConfig();
                                User.findOrCreate(config.getEmail(), config.getName(), config.getGuid(), new DefaultCallback<AccessTokenResult<User>>(context) {
                                    public void onModel(AccessTokenResult<User> model) {
                                        if (canceled) return;
                                        Session.getInstance().setAccessToken(context, model.getAccessToken());
                                        Session.getInstance().setUser(model.getModel());
                                        userDone = true;
                                        checkComplete();
                                    };
                                });
                            }
                        });
                    } else {
                        AccessToken accessToken = BaseModel.load(Session.getInstance().getSharedPreferences(), "access_token", AccessToken.class);
                        if (accessToken != null) {
                            Session.getInstance().setAccessToken(accessToken);
                            User.loadCurrentUser(new DefaultCallback<User>(context) {
                                @Override
                                public void onModel(User model) {
                                    Session.getInstance().setUser(model);
                                    userDone = true;
                                    checkComplete();
                                }
                            });
                        } else {
                            userDone = true;
                        }
                    }

					checkComplete();
				}
			});
		} else {
			configDone = true;
		}

		if (Session.getInstance().getUser() != null) {
			userDone = true;
		}

		checkComplete();
	}
	
	private boolean shouldSignIn() {
		Config config = Session.getInstance().getConfig();
		return config.getEmail() != null;
	}
	
	public void cancel() {
		canceled = true;
	}
	
	private void checkComplete() {
		if (configDone && userDone) {
			callback.run();
		}
	}
}
