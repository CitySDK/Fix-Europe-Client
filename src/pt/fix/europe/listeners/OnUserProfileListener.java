package pt.fix.europe.listeners;

import pt.fix.europe.auth.GoogleProfile;

public interface OnUserProfileListener {
	void onUserProfileFetched(GoogleProfile result);
}
