package org.otika.honeybee.util;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/*import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder; */

/*
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;
*/
/**
 * Uses facebook4j API. See pom.xml
 */
@Named
@SessionScoped
public class FaceBooker implements Serializable {

	/**
	 * id name first_name last_name link gender locale age_range
	 */
	private static final long serialVersionUID = 1729007644653485526L;

	/**
	 * Default constructor.
	 */
	public FaceBooker() {
		// TODO --
	/*	ConfigurationBuilder cb = new ConfigurationBuilder();
		/*ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthAppId("*********************")
				.setOAuthAppSecret("******************************************")
				.setOAuthAccessToken(
						"**************************************************")
				.setOAuthPermissions(
						"email,publish_stream,publish_actions,public_profile,user_birthday,user_hometown,user_location,user_relationships");
		FacebookFactory ff = new FacebookFactory(cb.build());
		Facebook facebook = ff.getInstance(); */
	}

}
