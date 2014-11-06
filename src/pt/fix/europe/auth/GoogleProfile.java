package pt.fix.europe.auth;

import com.google.gson.annotations.SerializedName;

public class GoogleProfile {
	private String id;
	private String email;
	@SerializedName("verified_email")
	private boolean verifiedEmail;
	private String name;
	@SerializedName("given_name")
	private String givenName;
	@SerializedName("family_name")
	private String familyName;
	private String link;
	private String picture;
	private String gender;
	
	public String getId() {
		return id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public boolean isVerifiedEmail() {
		return verifiedEmail;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGivenName() {
		return givenName;
	}
	
	public String getFamilyName() {
		return familyName;
	}
	
	public String getLink() {
		return link;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setVerifiedEmail(boolean verifiedEmail) {
		this.verifiedEmail = verifiedEmail;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
}
