package org.otika.honeybee.events;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Event for User Sign-in action
 * 
 * @author hanine
 * 
 */

public class SigninEvent {

	private String email;

	public SigninEvent(String email) {
		this.email = email;
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Signed in!"));
	}

	public String getEmail() {
		return email;
	}

}
