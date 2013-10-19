package org.otika.honeybee.events;

import org.otika.honeybee.model.Enduser;

/**
 * <p>
 * Event class for a new user registration
 * </p>
 * 
 * @author Hanine .H ALMADANI <hanynowsky@gmail.com>
 * 
 */
public class RegistrationEvent {

	private Enduser enduser;

	public RegistrationEvent(Enduser user) {
		this.enduser = user;
	}

	/**
	 * @return the id
	 */
	public Enduser getEnduser() {
		return enduser;
	}

}
