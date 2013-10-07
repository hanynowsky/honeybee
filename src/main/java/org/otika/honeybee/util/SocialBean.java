package org.otika.honeybee.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.agorava.Facebook;
import org.agorava.core.api.oauth.OAuthAppSettings;
import org.agorava.core.api.oauth.OAuthSession;
import org.agorava.core.cdi.Current;
import org.agorava.core.oauth.PropertyOAuthAppSettingsBuilder;

/**
 * 
 * @author Hanine .H ALMADANI <hanynowsky@gmail.com>
 * 
 */
public class SocialBean {

	public SocialBean() {

	}

	/**
	 * <p>
	 * use prefix (twitter, facebook or linkedin) to find the right key in
	 * agorava.properties file.
	 * </p>
	 * <p>
	 * Other modules like StackOverflow or GitHub can be downloaded
	 * </p>
	 */
	@ApplicationScoped
	@Produces
	@Facebook
	public OAuthAppSettings produceSetting() {
		PropertyOAuthAppSettingsBuilder builder = new PropertyOAuthAppSettingsBuilder();
		return builder.prefix("facebook").build();
	}
	
	/**
	 * OAuthSession
	 * @param session
	 * @return
	 */
	@SessionScoped
	@Produces
	@Facebook
	@Current
	public OAuthSession produceOauthSession(@Facebook @Default OAuthSession session) {
		// TODO if we do not use @Default, we get a circular reference
		// @Facebook @Default OAuthSession session
	    return session;
	}

}
