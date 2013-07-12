package org.otika.honeybee.util;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.otika.honeybee.model.Plant;
import org.otika.honeybee.view.PlantBean;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Managed bean to retrieve an image from database
 * 
 * @author hanine <hanynowsky@gmail.com>
 * 
 */
@ManagedBean
@ApplicationScoped
public class ImagesBean {

	@EJB
	private Repository service;
	@EJB
	private PlantBean plantBean;

	/**
	 * 
	 * @return Prime_Faces_streamed_content
	 */
	public StreamedContent getImage() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();

			if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
				/*
				 * So, we're rendering the view. Return a stub StreamedContent
				 * so that it will generate right URL.
				 */
				return new DefaultStreamedContent();
			} else {
				/*
				 * So, browser is requesting the image. Return a real
				 * StreamedContent with the image bytes.
				 */
				String id = context.getExternalContext()
						.getRequestParameterMap().get("plant_id");
				Plant plant;
				if (id != null) {
					plant = service.findPlantById(Long.valueOf(id));
				} else {
					plant = plantBean.getAll().get(0);
				}
				StreamedContent content = new DefaultStreamedContent(
						new ByteArrayInputStream(plant.getGraphic()));                
				return content;
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("No Graphic found!"));
			Logger.getLogger(ImagesBean.class.getName()).severe(
					ex.getMessage()+" | cause: "+ex.getCause());
		}
		return null;
	} // END OF METHOD

}