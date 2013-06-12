package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

import org.otika.honeybee.model.Prescription;
import org.otika.honeybee.model.Author;

/**
 * Backing bean for Prescription entities.
 * <p>
 * This class provides CRUD functionality for all Prescription entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class PrescriptionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Prescription entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Prescription prescription;

	public Prescription getPrescription() {
		return this.prescription;
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
			this.prescription = this.example;
		} else {
			this.prescription = findById(getId());
		}
	}

	public Prescription findById(Long id) {

		return this.entityManager.find(Prescription.class, id);
	}

	/*
	 * Support updating and deleting Prescription entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.prescription);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.prescription);
				return "view?faces-redirect=true&id="
						+ this.prescription.getId();
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
			this.entityManager.remove(findById(getId()));
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Prescription entities with pagination
	 */

	private int page;
	private long count;
	private List<Prescription> pageItems;

	private Prescription example = new Prescription();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Prescription getExample() {
		return this.example;
	}

	public void setExample(Prescription example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Prescription> root = countCriteria.from(Prescription.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Prescription> criteria = builder
				.createQuery(Prescription.class);
		root = criteria.from(Prescription.class);
		TypedQuery<Prescription> query = this.entityManager
				.createQuery(criteria.select(root).where(
						getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Prescription> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Author author = this.example.getAuthor();
		if (author != null) {
			predicatesList.add(builder.equal(root.get("author"), author));
		}
		String title = this.example.getTitle();
		if (title != null && !"".equals(title)) {
			predicatesList.add(builder.like(root.<String> get("title"),
					'%' + title + '%'));
		}
		String titlear = this.example.getTitlear();
		if (titlear != null && !"".equals(titlear)) {
			predicatesList.add(builder.like(root.<String> get("titlear"),
					'%' + titlear + '%'));
		}
		String titlefr = this.example.getTitlefr();
		if (titlefr != null && !"".equals(titlefr)) {
			predicatesList.add(builder.like(root.<String> get("titlefr"),
					'%' + titlefr + '%'));
		}
		String preparation = this.example.getPreparation();
		if (preparation != null && !"".equals(preparation)) {
			predicatesList.add(builder.like(root.<String> get("preparation"),
					'%' + preparation + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Prescription> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Prescription entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<Prescription> getAll() {

		CriteriaQuery<Prescription> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Prescription.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Prescription.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final PrescriptionBean ejbProxy = this.sessionContext
				.getBusinessObject(PrescriptionBean.class);

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

				return String.valueOf(((Prescription) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Prescription add = new Prescription();

	public Prescription getAdd() {
		return this.add;
	}

	public Prescription getAdded() {
		Prescription added = this.add;
		this.add = new Prescription();
		return added;
	}
}