package org.otika.honeybee.events;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * <h1>Event for User Sign-in action</h1>
 * 
 * @author Hanine .H ALMADANI <hanynowsky@gmail.com>
 * 
 */
public class SigninEvent {

	private String email;

	public SigninEvent(String email) {
		this.email = email;
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Signed in"));
	}

	public String getEmail() {
		return email;
	}
}
