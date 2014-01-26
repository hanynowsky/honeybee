package org.otika.honeybee.util;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * Message-Driven Bean implementation class for: GeneralMDB
 */
@MessageDriven(name = "GeneralMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/generalmdb") })
public class GeneralMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(GeneralMDB.class
			.getName());

	/**
	 * Default constructor.
	 */
	public GeneralMDB() {
		// TODO
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		// TODO Logic here
		LOGGER.info("Received message: " + message.toString());
		try {
			if (message instanceof TextMessage) {
				TextMessage msg = (TextMessage) message;
				String sm = msg.getText();
				LOGGER.info("Received msg text is: " + sm);
				// TODO continue here
			}
		} catch (JMSException ex) {
			LOGGER.severe("On Message failure! " + ex.getMessage());
			throw new RuntimeException(ex);
		}

	}

}
