package pt.fix.europe.sync.auth;

import com.google.gson.annotations.SerializedName;

public class AccessToken {
	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("expires_in")
	private String expiresIn;
	
	public AccessToken() {
		accessToken = "";
		expiresIn = "";
	}
	
	public String getExpiresIn() {
		return expiresIn;
	}
	
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
