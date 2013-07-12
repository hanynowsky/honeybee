package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
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

import org.otika.honeybee.model.Help;
import org.otika.honeybee.util.BundleBean;
import org.otika.honeybee.util.UtilityBean;

/**
 * Backing bean for Help entities.
 * <p>
 * This class provides CRUD functionality for all Help entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
@DeclareRoles(value = { "ADMINISTRATOR", "AUTHOR" })
public class HelpBean implements Serializable {

	private static final long serialVersionUID = 1L;
	@Inject
	private Conversation conversation;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	@Inject
	private UtilityBean utilityBean;
	private BundleBean bundle = new BundleBean();

	private Long id;
	private Help help;

	/*
	 * Support creating and retrieving Help entities
	 */

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Help getHelp() {
		return this.help;
	}

	/**
	 * 
	 * @return faces outcome
	 */
	@RolesAllowed(value = { "ADMINISTRATOR" })
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
			this.help = this.example;
		} else {
			this.help = findById(getId());
		}
	}

	public Help findById(Long id) {
		return this.entityManager.find(Help.class, id);
	}

	/*
	 * Support updating and deleting Help entities
	 */

	@RolesAllowed(value = { "ADMINISTRATOR" })
	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.help);
				utilityBean.showMessage("INFO",
						this.help.getTitle() + bundle.i18n("item_created")
								+ " : ", null);
				return "view.xhtml?id=" + this.help.getId();
				// return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.help);
				utilityBean.showMessage("INFO",
						this.help.getTitle() + bundle.i18n("item_updated")
								+ " : ", null);
				return "view.xhtml?id=" + this.help.getId();
				// return "view?faces-redirect=true&id=" + this.help.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	@RolesAllowed(value = { "ADMINISTRATOR" })
	public String delete() {
		this.conversation.end();
		boolean isAdmin = false;
		try {
			isAdmin = sessionContext.isCallerInRole("ADMINISTRATOR");
			if (!isAdmin) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage("No permission to delete!",
								"Only Administrators are allowed."));
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("No Principal"));
		}

		try {
			if (isAdmin) {
				this.entityManager.remove(findById(getId()));
				this.entityManager.flush();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(bundle.i18n("item_deleted") + " : "));
				// return "search?faces-redirect=false";
				return "/misc/redirect.xhtml?faces-redirect=false";
			} else {
				FacesMessage facesm = new FacesMessage(
						FacesMessage.SEVERITY_WARN, "No permission to delete!",
						"Sorry!");
				FacesContext.getCurrentInstance().addMessage(null, facesm);
				System.out.println("No permission to delete! Sorry!");
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
		return null;
	}

	/*
	 * Support searching Help entities with pagination
	 */

	private int page;
	private long count;
	private List<Help> pageItems;

	private Help example = new Help();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Help getExample() {
		return this.example;
	}

	public void setExample(Help example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Help> root = countCriteria.from(Help.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Help> criteria = builder.createQuery(Help.class);
		root = criteria.from(Help.class);
		TypedQuery<Help> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Help> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String title = this.example.getTitle();
		if (title != null && !"".equals(title)) {
			predicatesList.add(builder.like(root.<String> get("title"),
					'%' + title + '%'));
		}
		String content = this.example.getContent();
		if (content != null && !"".equals(content)) {
			predicatesList.add(builder.like(root.<String> get("content"),
					'%' + content + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Help> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Help entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Help> getAll() {

		CriteriaQuery<Help> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Help.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Help.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final HelpBean ejbProxy = this.sessionContext
				.getBusinessObject(HelpBean.class);

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

				return String.valueOf(((Help) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Help add = new Help();

	public Help getAdd() {
		return this.add;
	}

	public Help getAdded() {
		Help added = this.add;
		this.add = new Help();
		return added;
	}

	/*
	 * Utilities
	 */

	/**
	 * Validator method to be attached to an input component
	 * @param context FacesContext
	 * @param toValidate Component_toValidate
	 * @param value Component_Object_value
	 */
	public void validateSubject(FacesContext context, UIComponent toValidate,
			Object value) {
		if (String.valueOf(value).toLowerCase().contains("fuck")) {
			context.addMessage(toValidate.getClientId(context),
					new FacesMessage("Forbidden word"));
			((UIInput) toValidate).setValid(false);
        }
	}
}