package org.otika.honeybee.util;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.otika.honeybee.model.Plant;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ApplicationScoped
public class ImagesBean {

	@EJB
	private Repository service;

	public StreamedContent getImage() {
		try {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so
			// that it will generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			String id = context.getExternalContext().getRequestParameterMap()
					.get("plant_id");
			Plant plant = service.findPlantById(Long.valueOf(id));
			StreamedContent content = new DefaultStreamedContent(
					new ByteArrayInputStream(plant.getGraphic()));
			return content;
		}
		}
		catch(Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Graphic found!"));
			Logger.getLogger(ImagesBean.class.getName()).log(Level.ALL, ex.getMessage(), ex);
		}
		return null;
	} // END OF METHOD

}