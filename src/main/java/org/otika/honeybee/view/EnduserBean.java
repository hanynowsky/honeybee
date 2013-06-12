package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.model.Language;
import org.otika.honeybee.model.Usergroup;
import org.otika.honeybee.util.Repository;

/**
 * Backing bean for Enduser entities.
 * <p>
 * This class provides CRUD functionality for all Enduser entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
@DeclareRoles(value = "ADMINISTRATOR")
public class EnduserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Enduser entities
	 */

	@Inject
	private Repository repository;

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Enduser enduser;

	public Enduser getEnduser() {
		return this.enduser;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

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
			this.enduser = this.example;
		} else {
			this.enduser = findById(getId());
		}
	}

	public Enduser findById(Long id) {

		return this.entityManager.find(Enduser.class, id);
	}

	/*
	 * Support updating and deleting Enduser entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {

				/**
				 * We set some values for new object creation and create it
				 */
				this.enduser.setDatejoined(new Date());
				this.enduser.setIsactive(true);
				this.entityManager.persist(this.enduser);
				return "search?faces-redirect=true";

				/**
				 * Updating end user object
				 */
			} else {
				this.entityManager.merge(this.enduser);
				return "view?faces-redirect=true&id=" + this.enduser.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			String user = FacesContext.getCurrentInstance()
					.getExternalContext().getRemoteUser();
			Enduser enduser = repository.findByEmail(user);
			if (enduser == findById(getId())
					|| sessionContext.isCallerInRole("ADMINISTRATOR") == true) {
				this.entityManager.remove(findById(getId()));
				this.entityManager.flush();
				if ((FacesContext.getCurrentInstance().getExternalContext()
						.getRemoteUser() != null || !FacesContext
						.getCurrentInstance().getExternalContext()
						.getRemoteUser().equals(""))
						&& sessionContext.isCallerInRole("ADMINISTRATOR") == false) {
					FacesContext.getCurrentInstance().getExternalContext()
							.invalidateSession();
					return "/index?faces-redirect=true";
				}
				return "search?faces-redirect=true";
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
		return null;
	}

	/*
	 * Support searching Enduser entities with pagination
	 */

	private int page;
	private long count;
	private List<Enduser> pageItems;

	private Enduser example = new Enduser();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Enduser getExample() {
		return this.example;
	}

	public void setExample(Enduser example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	@RolesAllowed("ADMINISTRATOR")
	public void paginate(AjaxBehaviorEvent evt) {
		try {
			if (sessionContext.isCallerInRole("ADMINISTRATOR")) {
				CriteriaBuilder builder = this.entityManager
						.getCriteriaBuilder();

				// Populate this.count

				CriteriaQuery<Long> countCriteria = builder
						.createQuery(Long.class);
				Root<Enduser> root = countCriteria.from(Enduser.class);
				countCriteria = countCriteria.select(builder.count(root))
						.where(getSearchPredicates(root));
				this.count = this.entityManager.createQuery(countCriteria)
						.getSingleResult();

				// Populate this.pageItems

				CriteriaQuery<Enduser> criteria = builder
						.createQuery(Enduser.class);
				root = criteria.from(Enduser.class);
				TypedQuery<Enduser> query = this.entityManager
						.createQuery(criteria.select(root).where(
								getSearchPredicates(root)));
				query.setFirstResult(this.page * getPageSize()).setMaxResults(
						getPageSize());
				this.pageItems = query.getResultList();
			} else {
				FacesMessage msg = new FacesMessage("Permission denied!");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	private Predicate[] getSearchPredicates(Root<Enduser> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Usergroup usergroup = this.example.getUsergroup();
		if (usergroup != null) {
			predicatesList.add(builder.equal(root.get("usergroup"), usergroup));
		}
		Language language = this.example.getLanguage();
		if (language != null) {
			predicatesList.add(builder.equal(root.get("language"), language));
		}
		String name = this.example.getName();
		if (name != null && !"".equals(name)) {
			predicatesList.add(builder.like(root.<String> get("name"),
					'%' + name + '%'));
		}
		String surname = this.example.getSurname();
		if (surname != null && !"".equals(surname)) {
			predicatesList.add(builder.like(root.<String> get("surname"),
					'%' + surname + '%'));
		}
		String email = this.example.getEmail();
		if (email != null && !"".equals(email)) {
			predicatesList.add(builder.like(root.<String> get("email"),
					'%' + email + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Enduser> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Enduser entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Enduser> getAll() {

		CriteriaQuery<Enduser> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Enduser.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Enduser.class))).getResultList();
	}

	public List<Enduser> getDummyList() {
		try {
			Enduser user = repository.findByEmail(FacesContext
					.getCurrentInstance().getExternalContext().getRemoteUser());
			List<Enduser> dummyList = new ArrayList<Enduser>();
			dummyList.add(user);
			if (user == null) {
				return null;
			}
			return dummyList;
		} catch (Exception ex) {
			return null;
		}
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final EnduserBean ejbProxy = this.sessionContext
				.getBusinessObject(EnduserBean.class);

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

				return String.valueOf(((Enduser) value).getId());
			}
		};
	}

	/**
	 * Custom Validator for validating password and password confirmation
	 */
	public void validatePassconf(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		String p = String.valueOf(value);
		// String pc = this.enduser.getPassword(); // returns null :(

		/*
		 * if (!p.equals(pc)) { FacesMessage message = new
		 * FacesMessage("Passwords do not match!");
		 * Logger.getLogger(getClass().getName()).log(Level.ALL,
		 * message.getSummary(), new ValidatorException(message)); throw new
		 * ValidatorException(message); }
		 */

		if (p.length() < 5 || p.length() > 12) {
			FacesMessage message = new FacesMessage(
					"Password length must be between 5 and 12!");
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					message.getSummary(), new ValidatorException(message));
			throw new ValidatorException(message);
		}
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Enduser add = new Enduser();

	public Enduser getAdd() {
		return this.add;
	}

	public Enduser getAdded() {
		Enduser added = this.add;
		this.add = new Enduser();
		return added;
	}
}