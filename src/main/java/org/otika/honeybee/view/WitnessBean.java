package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
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

import org.otika.honeybee.model.Witness;
import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.model.Prescription;
import org.otika.honeybee.util.Repository;

/**
 * Backing bean for Witness entities.
 * <p>
 * This class provides CRUD functionality for all Witness entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class WitnessBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Witness entities
	 */
	Long wid = (long) -1;
	@Inject
	private Repository repository;
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Witness witness;

	public Witness getWitness() {
		return this.witness;
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
			this.witness = this.example;
		} else {
			this.witness = findById(getId());
		}
	}

	public Witness findById(Long id) {

		return this.entityManager.find(Witness.class, id);
	}

	/*
	 * Support updating and deleting Witness entities
	 */

	public String update() {
		this.conversation.end();

		/**
		 * Ensures that the user has only one witness per prescription
		 */
		Witness w = null;
		Prescription p = this.witness.getPrescription();
		Enduser e = this.witness.getEnduser();
		try {
			w = repository.findByPrescriptionAndEnduser(p, e);
		} catch (Exception exp) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					exp.getMessage());
			wid = null;
		}

		try {
			if (this.id == null) {
				if (w == null || wid == null) {
					this.entityManager.persist(this.witness);
					return "search?faces-redirect=true";
				} else {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage("You already wrote a witness for "
									+ p.getTitle(), null));
				}
			} else {
				if (this.witness.getEnduser().getEmail()
						.equals(getRemoteUserCredential())
						|| sessionContext.isCallerInRole("ADMINISTRATOR")) {
					this.entityManager.merge(this.witness);
					return "view?faces-redirect=true&id="
							+ this.witness.getId();
				}
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(ex.getMessage()));
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage(), ex);
			return null;
		}
		return null;
	}

	public String delete() {
		this.conversation.end();

		try {
			if (findById(getId()).getEnduser().getEmail()
					.equals(getRemoteUserCredential())
					|| sessionContext.isCallerInRole("ADMINISTRATOR")) {
				this.entityManager.remove(findById(getId()));
				this.entityManager.flush();
			} else {
				String msg = "This Witness is owned by someone else!";
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(msg, null));
			}
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/**
	 * 
	 * @return Remote user credential as string
	 */
	private String getRemoteUserCredential() {
		try {
			return FacesContext.getCurrentInstance().getExternalContext()
					.getRemoteUser();
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage());
			return null;
		}
	}

	/**
	 * Check if the witness being edited has the same end user as the one
	 * connected
	 */
	public boolean isCheckWitnessEnduser() {
		try {
			String email = this.witness.getEnduser().getEmail();
			if (email != null && getRemoteUserCredential() != null
					&& email.equals(getRemoteUserCredential())) {
				return true;
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								"Witness might belong to another user", null));
				return false;
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage(), ex);
			return false;
		}
	}

	/*
	 * Support searching Witness entities with pagination
	 */

	private int page;
	private long count;
	private List<Witness> pageItems;

	private Witness example = new Witness();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Witness getExample() {
		return this.example;
	}

	public void setExample(Witness example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Witness> root = countCriteria.from(Witness.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Witness> criteria = builder.createQuery(Witness.class);
		root = criteria.from(Witness.class);
		TypedQuery<Witness> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Witness> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Enduser enduser = this.example.getEnduser();
		if (enduser != null) {
			predicatesList.add(builder.equal(root.get("enduser"), enduser));
		}
		Prescription prescription = this.example.getPrescription();
		if (prescription != null) {
			predicatesList.add(builder.equal(root.get("prescription"),
					prescription));
		}
		String comment = this.example.getComment();
		if (comment != null && !"".equals(comment)) {
			predicatesList.add(builder.like(root.<String> get("comment"),
					'%' + comment + '%'));
		}
		String subject = this.example.getSubject();
		if (subject != null && !"".equals(subject)) {
			predicatesList.add(builder.like(root.<String> get("subject"),
					'%' + subject + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Witness> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Witness entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Witness> getAll() {

		CriteriaQuery<Witness> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Witness.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Witness.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final WitnessBean ejbProxy = this.sessionContext
				.getBusinessObject(WitnessBean.class);

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

				return String.valueOf(((Witness) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Witness add = new Witness();

	public Witness getAdd() {
		return this.add;
	}

	public Witness getAdded() {
		Witness added = this.add;
		this.add = new Witness();
		return added;
	}
}