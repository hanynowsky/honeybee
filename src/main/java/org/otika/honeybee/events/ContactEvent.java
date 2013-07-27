package org.otika.honeybee.events;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Event for User Sign-in action
 *
 * @author hanine
 *
 */
public class ContactEvent {
    
    public ContactEvent() {
      
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Event: Contact Email Triggered"));
    }
    
}
