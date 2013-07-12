package org.otika.honeybee.events;

import org.otika.honeybee.model.Enduser;

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
