package org.otika.honeybee.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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
        // TODO move this to init method to avoid deployment exception, 
        // however doing so would corrupt the hitCount value
        try {
            hitCount = repository.findAllConfigurationItems().get(0)
                    .getHitcounts();
        } catch (Exception ex) {
            System.err.println(getClass().getName()
                    + " : Exception fetching repository hit counts");
            Logger.getLogger(getClass().getName()).log(
                    Level.SEVERE, "Exception Hit counts: {0}", ex.getMessage());
        }
    }

    @PostConstruct
    public void init() {
    }

    /**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        hitCount++;
        System.out.println("Site visits count :" + hitCount);
    }

    /**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {

        try {
            System.out.println("Session destroyed: " + hitCount);
            Configuration c = repository.findAllConfigurationItems().get(0);
            System.out.println("Licence: " + c.getLicence());
            c.setHitcounts(hitCount + c.getHitcounts());
            configurationBean.update(c);
            System.out.println("new hit counts " + c.getHitcounts());
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).severe(ex.getMessage());
            System.err.println("Exception in Session Destroyed: " + ex);
        }
    }
}
