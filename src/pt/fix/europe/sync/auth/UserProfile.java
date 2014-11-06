package pt.fix.europe.sync.auth;

public class UserProfile {
	private String user;
	private String email;
	private String password;
	private String token;
	private String type;
	
	public UserProfile() {
		this.user = "";            
		this.password = "";     
		this.token = "";
		this.type = "";
		this.email = "";
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getToken() {
		return token;
	}

	public String getType() {
		return type;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String secret) {
		this.password = secret;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

