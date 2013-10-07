package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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

import org.otika.honeybee.model.Codetable;
import org.otika.honeybee.model.Content;
import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.util.Repository;

/**
 * Backing bean for Content entities.
 * <p>
 * This class provides CRUD functionality for all Content entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ContentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Repository repository;
	private Long id;
	private Content content;
	private List<Codetable> contentTypeItems;
	private List<String> ctypes = new ArrayList<>();

	@Inject
	private Conversation conversation;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	@PostConstruct
	public void init() {
		contentTypeItems = new ArrayList<>();
		contentTypeItems.addAll(repository.findCodeValueByType("ctype"));
		for (Codetable codetable : contentTypeItems) {
			ctypes.add(codetable.getCodetype());
		}
	}

	/*
	 * Support creating and retrieving Content entities
	 */
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
			this.content = this.example;
		} else {
			this.content = findById(getId());
		}
	}

	public Content findById(Long id) {

		return this.entityManager.find(Content.class, id);
	}

	/*
	 * Support updating and deleting Content entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.content.setEnduser(repository.findByEmail(FacesContext
						.getCurrentInstance().getExternalContext()
						.getRemoteUser()));
				this.entityManager.persist(this.content);
				return null; // TODO refactor
			} else {
				this.entityManager.merge(this.content);
				return "/content/view?faces-redirect=true&id="
						+ this.content.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/**
	 * Faces Method to delete a Content item
	 * 
	 * @return
	 */
	public String delete() {
		this.conversation.end();

		try {
			// TODO Would lists persist ? :(
			String nav = "/index?faces-redirect=true";

			for (String type : ctypes) {
				if (findById(getId()).getCtype().equalsIgnoreCase(type)) {
					nav = "/misc/" + type + "?faces-redirect=true";
				} else {
					nav = "/index?faces-redirect=true";
				}
			}

			this.entityManager.remove(findById(getId()));
			this.entityManager.flush();
			FacesMessage fmsg = new FacesMessage("Content Deleted");
			FacesContext.getCurrentInstance().addMessage(null, fmsg);
			return nav;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).severe(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/**
	 * Returns the database localized value of content type from Codetable
	 * Entity
	 * 
	 * @return
	 */
	public String localizedCtype(Codetable codetable) {
		try {
			String lang = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale().getLanguage();
			if (lang.equalsIgnoreCase("fr")) {
				return codetable.getCodevaluefr();
			} else if (lang.equalsIgnoreCase("ar")) {
				return codetable.getCodevaluear();
			} else {
				return codetable.getCodevalue();
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).severe(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Content entities with pagination
	 */

	private int page;
	private long count;
	private List<Content> pageItems;

	private Content example = new Content();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Content getExample() {
		return this.example;
	}

	public void setExample(Content example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Content> root = countCriteria.from(Content.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Content> criteria = builder.createQuery(Content.class);
		root = criteria.from(Content.class);
		TypedQuery<Content> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Content> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Enduser enduser = this.example.getEnduser();
		if (enduser != null) {
			predicatesList.add(builder.equal(root.get("enduser"), enduser));
		}

		String content = this.example.getContent();
		if (content != null && !"".equals(content)) {
			predicatesList.add(builder.like(root.<String> get("content"),
					'%' + content + '%'));
		}

		String contentar = this.example.getContentar();
		if (contentar != null && !"".equals(contentar)) {
			predicatesList.add(builder.like(root.<String> get("contentar"),
					'%' + contentar + '%'));
		}

		String contentfr = this.example.getContentfr();
		if (contentfr != null && !"".equals(contentfr)) {
			predicatesList.add(builder.like(root.<String> get("contentfr"),
					'%' + contentfr + '%'));
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

		Date datecreated = this.example.getDatecreated();
		if (datecreated != null) {
			predicatesList.add(builder.equal(root.get("datecreated"),
					datecreated));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Content> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Content entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Content> getAll() {

		CriteriaQuery<Content> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Content.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Content.class))).getResultList();
	}

	/**
	 * List of Content items by Content type
	 * 
	 * @param ctype
	 * @return list
	 */
	public List<Content> contentListByCtype(String ctype) {
		return repository.findContentByCtype(ctype);
	}

	/**
	 * List of Content items by language and Content type
	 * 
	 * @param ctype
	 * @return list
	 */
	public List<Content> contentListByCtypeAndLanguage(String ctype) {
		String lang = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().getLanguage();
		return repository.findContentByCtypeAndLanguage(ctype, lang);
	}

	/**
	 * Listener for Accordion Panel
	 * 
	 * @param content
	 */
	public void onTabChange(Content item) {
		// retrieve();
		System.out.println("Setting content id to " + item.getId());
		setId(item.getId());
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final ContentBean ejbProxy = this.sessionContext
				.getBusinessObject(ContentBean.class);

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

				return String.valueOf(((Content) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Content add = new Content();

	public Content getAdd() {
		return this.add;
	}

	public Content getAdded() {
		Content added = this.add;
		this.add = new Content();
		return added;
	}

	/* Getters & Setters */
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Content getContent() {
		return this.content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(Content content) {
		this.content = content;
	}

	/**
	 * @return the contentTypeItems
	 */
	public List<Codetable> getContentTypeItems() {
		return contentTypeItems;
	}

	/**
	 * @param contentTypeItems
	 *            the contentTypeItems to set
	 */
	public void setContentTypeItems(List<Codetable> contentTypeItems) {
		this.contentTypeItems = contentTypeItems;
	}

	/**
	 * @return the ctypes
	 */
	public List<String> getCtypes() {
		return ctypes;
	}

	/**
	 * @param ctypes
	 *            the ctypes to set
	 */
	public void setCtypes(List<String> ctypes) {
		this.ctypes = ctypes;
	}

} // END OF CLASS