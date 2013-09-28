package org.otika.honeybee.events;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Class that serves as an event for a printed prescription
 * 
 * @author hanine
 * 
 */
public class PrescriptionPrintedEvent {
	private Long id;

	public PrescriptionPrintedEvent(Long id) {
		this.id = id;
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Prescription exported."));
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

}
