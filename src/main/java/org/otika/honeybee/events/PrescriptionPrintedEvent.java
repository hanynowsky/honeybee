package org.otika.honeybee.events;

/**
 * Class that serves as an event for a printed prescription
 * @author hanine
 *
 */
public class PrescriptionPrintedEvent {
	private Long id;
	
	public PrescriptionPrintedEvent(Long id){
		this.id = id;		
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

}
