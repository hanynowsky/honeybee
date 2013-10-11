package org.otika.honeybee.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.agorava.Facebook;
import org.agorava.FacebookBaseService;
import org.agorava.core.api.UserProfile;
import org.agorava.core.api.oauth.OAuthService;
import org.agorava.core.api.oauth.OAuthSession;
import org.agorava.core.cdi.Current;
import org.agorava.facebook.model.FacebookProfile;
import org.agorava.core.api.oauth.OAuthToken;

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
	private String userEmail;
	private String userImageUrl;
	private boolean userVerified;
	private String verifier;
	private String verifierName;
	private FacebookProfile facebookProfile;
	private FacebookBaseService facebookBaseService;

	@Inject
	@Facebook
	OAuthService service;

	public SocialServiceBean() {
	}

	@PostConstruct
	public void init() {

	}

	@Named
	public OAuthService getMyFacebookService() {
		return service;
	}

	/**
	 * OAuthSession
	 * 
	 * @param session
	 * @return
	 */
	@SessionScoped
	@Produces
	@Facebook
	@Current
	public OAuthSession produceOauthSession(@Facebook @Default OAuthSession session) {
		// TODO @produces: if we do not use @Default, we get a circular
		// reference
		// @Facebook @Default OAuthSession session
		return session;
	}

	/**
	 * Faces FaceBook Connect Method
	 * 
	 * @return
	 */
	public String connect() {
		try {
			// TODO facebookProfile

			authURL = service.getAuthorizationUrl();
			System.out.println("authURL: " + authURL);
			// OAuthSession session = getMyFacebookService().getSession();
			// TODO
			/*
			 * verifierName = service.getVerifierParamName(); verifier =
			 * service.getVerifier();
			 */

			// System.out.println("Session is connected?: " +
			// session.isConnected());
		} catch (Exception ex) {
			/*
			 * Logger.getLogger(getClass().getName()).severe( ex.getMessage());
			 */
			System.err.println(ex);
			Logger.getLogger(getClass().getName()).severe(
					"Cannot boot Social Service: ");
		}
		return "/misc/facebook?faces-redirect=true";
	}

	/**
	 * Reset Connection
	 */
	public void resetConnection() {
		//service.;
	}

	/**
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void redirectToAuthorizationURL() throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(getAuthURL());
	}

	/**
	 * Social Media Name
	 * 
	 * @return social media name as String
	 */
	public String getSocialMediaName() {
		return service.getSocialMediaName().toLowerCase();
	}
	
	/**
	 * Social Media Access Token
	 * @return
	 */
	public OAuthToken getAccessToken(){
		return service.getAccessToken();				
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

	/**
	 * @return the facebookProfile
	 */
	public FacebookProfile getFacebookProfile() {
		return facebookProfile;
	}

	/**
	 * @param facebookProfile
	 *            the facebookProfile to set
	 */
	public void setFacebookProfile(FacebookProfile facebookProfile) {
		this.facebookProfile = facebookProfile;
	}

	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail
	 *            the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return the userVerified
	 */
	public boolean isUserVerified() {
		return userVerified;
	}

	/**
	 * @param userVerified
	 *            the userVerified to set
	 */
	public void setUserVerified(boolean userVerified) {
		this.userVerified = userVerified;
	}

	/**
	 * @return the verifier
	 */
	public String getVerifier() {
		return verifier;
	}

	/**
	 * @param verifier
	 *            the verifier to set
	 */
	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	/**
	 * @return the verifierName
	 */
	public String getVerifierName() {
		return verifierName;
	}

	/**
	 * @param verifierName
	 *            the verifierName to set
	 */
	public void setVerifierName(String verifierName) {
		this.verifierName = verifierName;
	}

	/**
	 * @return the facebookBaseService
	 */
	public FacebookBaseService getFacebookBaseService() {
		return facebookBaseService;
	}

	/**
	 * @param facebookBaseService
	 *            the facebookBaseService to set
	 */
	public void setFacebookBaseService(FacebookBaseService facebookBaseService) {
		this.facebookBaseService = facebookBaseService;
	}

}
