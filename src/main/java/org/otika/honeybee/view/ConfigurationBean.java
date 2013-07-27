package org.otika.honeybee.view;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.otika.honeybee.model.Configuration;

@Named("configurationbean")
@ConversationScoped
@Stateful
public class ConfigurationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@Inject
	private Conversation conversation;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	/*
	 * Support creating and retrieving Defect entities
	 */

	private Long id;
	private Configuration configuration;
	private Configuration example = new Configuration();

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConfigurationBean() {

	}

	public String create() {
		if (this.conversation.isTransient()) {
			this.conversation.begin();
		} else {
			this.conversation.end();
		}
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
		}

		if (this.id == null) {
			this.configuration = this.example;
		} else {
			this.configuration = findById(getId());
		}
	}

	public Configuration findById(Long id) {

		return this.entityManager.find(Configuration.class, id);
	}

	public String update() {
		if (this.id == null) {
			entityManager.persist(this.configuration);
		} else {
			entityManager.merge(this.configuration);
		}
		return null;
	}

	public String update(Configuration c) {
		entityManager.merge(c);
		return null;
	}

	public String delete() {
		this.conversation.end();

		try {
			this.entityManager.remove(findById(getId()));
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
