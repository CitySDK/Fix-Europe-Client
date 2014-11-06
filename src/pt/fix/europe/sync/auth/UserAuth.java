package pt.fix.europe.sync.auth;

import com.google.gson.annotations.SerializedName;

public class UserAuth {
	@SerializedName("user_id")
	private String userId;
	@SerializedName("user_token")
	private AccessToken userToken;
	// TODO: should include more fields...
	
	public UserAuth() {
		userId = "";
		userToken = null;
	}

	public String getUserId() {
		return userId;
	}

	public AccessToken getUserToken() {
		return userToken;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserToken(AccessToken userToken) {
		this.userToken = userToken;
	}
}
