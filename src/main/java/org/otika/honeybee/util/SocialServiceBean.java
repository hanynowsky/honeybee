package org.otika.honeybee.util;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.agorava.Facebook;
import org.agorava.core.api.UserProfile;
import org.agorava.core.api.oauth.OAuthService;
import org.agorava.core.api.oauth.OAuthSession;

@Named
@SessionScoped
public class SocialServiceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7875755648192164905L;
	private String authURL;
	private UserProfile userProfile;
	private String userFullName;
	private String userImageUrl;
	@EJB
	// using @Inject : No bean is eligible for declaration
	@Facebook
	OAuthService service;

	public SocialServiceBean() {
	}

	@PostConstruct
	public void init() {
		authURL = service.getAuthorizationUrl();
		OAuthSession session = getMyFacebookService().getSession();
		userProfile = session.getUserProfile();
		userFullName = userProfile.getFullName();
		userImageUrl = userProfile.getProfileImageUrl();
	}

	@Named
	public OAuthService getMyFacebookService() {
		return service;
	}

	/**
	 * @return the authURL
	 */
	public String getAuthURL() {
		return authURL;
	}

	/**
	 * @param authURL
	 *            the authURL to set
	 */
	public void setAuthURL(String authURL) {
		this.authURL = authURL;
	}

	/**
	 * @return the userProfile
	 */
	public UserProfile getUserProfile() {
		return userProfile;
	}

	/**
	 * @param userProfile
	 *            the userProfile to set
	 */
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/**
	 * @param userFullName
	 *            the userFullName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	/**
	 * @return the userImageUrl
	 */
	public String getUserImageUrl() {
		return userImageUrl;
	}

	/**
	 * @param userImageUrl
	 *            the userImageUrl to set
	 */
	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

}
