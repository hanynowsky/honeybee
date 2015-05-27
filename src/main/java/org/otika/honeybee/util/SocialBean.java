package org.otika.honeybee.util;

/*import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.agorava.Facebook;
import org.agorava.core.api.oauth.OAuthAppSettings;
import org.agorava.core.oauth.PropertyOAuthAppSettingsBuilder;
*/
/**
 * <h1>Settings CDI producer for a Social Media using Agorava API.</h1>
 * <p>
 * Settings are read from agorava.properties in classpath
 * </p>
 * 
 * @author <a href="mailto:hanynowsky@gmail.com">Hanine .H ALMADANI</a>
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
	/*@ApplicationScoped
	@Produces
	@Facebook
	public OAuthAppSettings facebookProducer() {
		PropertyOAuthAppSettingsBuilder builder = new PropertyOAuthAppSettingsBuilder();
		builder.callback("/social/callback.xhtml");
		return builder.prefix("facebook").build();
	} */

}
