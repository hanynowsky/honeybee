package org.otika.honeybee.util;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.otika.honeybee.model.Configuration;
import org.otika.honeybee.view.ConfigurationBean;

/**
 * Application Lifecycle Listener implementation class SessionListener
 * 
 */
@WebListener
public class SessionListener implements HttpSessionListener {

	private int hitCount = 0;
	@EJB
	private Repository repository;
	@EJB
	private ConfigurationBean configurationBean;	

	/**
	 * Default constructor.
	 */
	public SessionListener() {
		try {
			hitCount = repository.findAllConfigurationItems().get(0)
					.getHitcounts();
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).severe(ex.getMessage());
		}
	}

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		hitCount++;
		System.out.println("Site visits count :" + hitCount);
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {

		try {
			System.out.println("Session destroyed: " + hitCount);
			Configuration c = new Configuration();
			c = repository.findAllConfigurationItems().get(0);
			System.out.println("Licence: " + c.getLicence());
			c.setHitcounts(hitCount);
			configurationBean.update(c);			
			System.out.println("new hit counts " + c.getHitcounts());
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).severe(ex.getMessage());
		}
	}

}