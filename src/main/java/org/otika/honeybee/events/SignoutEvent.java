package org.otika.honeybee.events;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Event for User Sign out in action
 * 
 * @author hanine
 * 
 */

public class SignoutEvent {

	private String email;

	public SignoutEvent(String email) {
		this.email = email;
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Signed out!"));
	}

	public String getEmail() {
		return email;
	}

}
