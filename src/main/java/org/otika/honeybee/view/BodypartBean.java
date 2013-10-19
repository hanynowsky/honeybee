package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

import org.otika.honeybee.model.Bodypart;
import org.otika.honeybee.util.Repository;
import org.otika.honeybee.util.UtilityBean;

/**
 * Backing bean for Bodypart entities.
 * <p>
 * This class provides CRUD functionality for all Bodypart entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 * 
 */

@Named
@Stateful
@ConversationScoped
public class BodypartBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Bodypart entities
	 */

	private Long id;

	private String pathLabel;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Bodypart bodypart;

	public Bodypart getBodypart() {
		return this.bodypart;
	}

	@Inject
	private Conversation conversation;
	@Inject
	private Repository repository;
	@Inject
	private UtilityBean utilityBean;

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
			this.bodypart = this.example;
		} else {
			this.bodypart = findById(getId());			
		}
	}

	public Bodypart findById(Long id) {
		return this.entityManager.find(Bodypart.class, id);
	}

	/**
	 * Finds a Bodypart item by its label and initiates the bean
	 * 
	 * @param label
	 * @return bodypart
	 */
	public Bodypart findByLabel(String label) {
		try {
			if (!label.isEmpty()) {
				Bodypart bodypart = repository.findBodypartByLikeLabel(label);
				this.id = bodypart.getId();
				retrieve();
				System.out.println("Fetched Bodypart ID is: " + this.id);
				return bodypart;
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).severe(
					"Argument Label is NULL " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Action Listener that sets a new value for Bodypart ID
	 * 
	 * @param label
	 */
	public void revalueId() {
		String pathLabelValue = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("pathLabel");
		System.out.println("pathLabelvalue is: " + pathLabelValue);
		if (pathLabelValue != null) {			
			this.id = findByLabel(pathLabelValue).getId();
			this.bodypart = findById(id);						
			utilityBean.redirectToFacesURL("/bodypart/view?id="+this.id);
		} else {
			System.out.println("pathLabel is null");
		}
	}

	/*
	 * Support updating and deleting Bodypart entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.bodypart);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.bodypart);
				return "view?faces-redirect=true&id=" + this.bodypart.getId();
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
	 * Support searching Bodypart entities with pagination
	 */

	private int page;
	private long count;
	private List<Bodypart> pageItems;

	private Bodypart example = new Bodypart();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Bodypart getExample() {
		return this.example;
	}

	public void setExample(Bodypart example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Bodypart> root = countCriteria.from(Bodypart.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Bodypart> criteria = builder.createQuery(Bodypart.class);
		root = criteria.from(Bodypart.class);
		TypedQuery<Bodypart> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Bodypart> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String label = this.example.getLabel();
		if (label != null && !"".equals(label)) {
			predicatesList.add(builder.like(root.<String> get("label"),
					'%' + label + '%'));
		}
		String labelfr = this.example.getLabelfr();
		if (labelfr != null && !"".equals(labelfr)) {
			predicatesList.add(builder.like(root.<String> get("labelfr"),
					'%' + labelfr + '%'));
		}
		String labelar = this.example.getLabelar();
		if (labelar != null && !"".equals(labelar)) {
			predicatesList.add(builder.like(root.<String> get("labelar"),
					'%' + labelar + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Bodypart> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Bodypart entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Bodypart> getAll() {

		CriteriaQuery<Bodypart> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Bodypart.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Bodypart.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final BodypartBean ejbProxy = this.sessionContext
				.getBusinessObject(BodypartBean.class);

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

				return String.valueOf(((Bodypart) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Bodypart add = new Bodypart();

	public Bodypart getAdd() {
		return this.add;
	}

	public Bodypart getAdded() {
		Bodypart added = this.add;
		this.add = new Bodypart();
		return added;
	}

	public String getPathLabel() {
		return pathLabel;
	}

	public void setPathLabel(String pathLabel) {
		this.pathLabel = pathLabel;
	}
}