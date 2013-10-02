package org.otika.honeybee.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.logging.Logger;
import org.otika.honeybee.model.Content;
import org.otika.honeybee.util.Repository;

@Model
public class ContentController {

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	@Inject
	private Repository repository;
	@Named
	@Produces
	@RequestScoped
	private Content newContent = new Content();
	private Content item;
	@Inject
	private PublisherBean publisherBean;
	private Long id;

	public ContentController() {

	}

	@PostConstruct
	public void init() {
		item = new Content();
	}

	/**
	 * Faces method to create a Content item
	 * 
	 * @return
	 */
	public String createContent() {
		try {
			if (newContent.getId() == null) {
				newContent.setEnduser(repository.findByEmail(FacesContext
						.getCurrentInstance().getExternalContext()
						.getRemoteUser()));
				publisherBean.createContent(newContent);
			} else {
				// TODO maybe use primefaces inplace edition feature instead of
				// calling the dialog
				newContent = repository.findContentById(id);
				publisherBean.updateContent(newContent);
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).debug(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
		return null;
	}

	/**
	 * Deletes a Content
	 * 
	 * @param content
	 * @return
	 */
	public String deleteContent(Content content) {
		try {
			// TODO attach content instance before deleting
			publisherBean.deleteContent(content);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
		return null;
	}

	/**
	 * Returns DB localized value of properties Content and Title
	 * @param content
	 * @param property
	 * @return ""
	 */
	public String localizedItemString(Content content, String property) {
		String lang = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().getLanguage();
		if (property.equalsIgnoreCase("title")) {
			if (lang.equalsIgnoreCase("fr")) {
				return content.getTitlefr();
			} else if (lang.equalsIgnoreCase("ar")) {
				return content.getTitlear();
			} else {
				return content.getTitle();
			}
		}

		if (property.equalsIgnoreCase("content")) {
			if (lang.equalsIgnoreCase("fr")) {
				return content.getContentfr();
			} else if (lang.equalsIgnoreCase("ar")) {
				return content.getContentar();
			} else {
				return content.getContent();
			}
		}
		return "";
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the item
	 */
	public Content getItem() {
		return item;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(Content item) {
		this.item = item;
	}

}
