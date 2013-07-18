package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.otika.honeybee.model.Registery;
import org.otika.honeybee.util.Repository;

@Named("registeryBean")
@ConversationScoped
@Stateful
@RolesAllowed({ "ADMINISTRATOR" })
public class RegisteryBean implements Serializable {

	private static final long serialVersionUID = -5287325805225149678L;
	@Inject
	private Conversation conversation;
	@EJB
	private Repository repository;
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;
	private List<Registery> allRegisteryItems;
	@Resource
	private SessionContext sessionContext;
	private static Logger log = Logger.getLogger(RegisteryBean.class.getName());
	private Registery item;

	public RegisteryBean() {

	}

	/**
	 * PostConstruct
	 */
	@PostConstruct
	public void init() {
		if (this.conversation.isTransient()) {
			this.conversation.begin();
		}
		try {
			allRegisteryItems = repository.allRegisteryItems();
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
	}

	/**
	 * 
	 * @param registery
	 * @return
	 */
	public String edit(Registery registery) {
		try {
			em.merge(registery);
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
		return "/admin/dashboard";
	}

	/**
	 * 
	 * @param registery
	 * @return
	 */
	public String create(Registery registery) {
		if (!this.conversation.isTransient()) {
			this.conversation.end();
		}
		try {
			em.persist(registery);
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
		return "/admin/dashboard";
	}

	/**
	 * 
	 * @param registery
	 * @return
	 */
	public String delete(Registery registery) {
		if (!this.conversation.isTransient()) {
			this.conversation.end();
		}
		try {
			em.remove(registery);
			em.flush();
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
		return "/admin/dashboard";
	}

	public Converter getConverter() {

		final RegisteryBean ejbProxy = this.sessionContext
				.getBusinessObject(RegisteryBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Registery) value).getId());
			}
		};
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	protected Registery findById(Long id) {
		try {
			em.find(Registery.class, id);
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
		return null;
	}

	/*
	 * Getters & Setters
	 */

	/**
	 * @return the allRegisteryItems
	 */
	public List<Registery> getAllRegisteryItems() {
		return allRegisteryItems;
	}

	/**
	 * @param allRegisteryItems
	 *            the allRegisteryItems to set
	 */
	public void setAllRegisteryItems(List<Registery> allRegisteryItems) {
		this.allRegisteryItems = allRegisteryItems;
	}

	public Registery getItem() {
		return item;
	}

	public void setItem(Registery item) {
		this.item = item;
	}

} // END OF CLASS
