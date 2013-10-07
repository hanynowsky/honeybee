package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
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

import org.otika.honeybee.controller.CommentController;
import org.otika.honeybee.model.Comment;
import org.otika.honeybee.model.Content;
import org.otika.honeybee.util.Repository;

/**
 * Backing bean for Comment entities.
 * <p>
 * This class provides CRUD functionality for all Comment entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CommentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Repository repository;
	@Inject
	private CommentController commentController;

	/*
	 * Support creating and retrieving Comment entities
	 */

	private String idValue;
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Comment comment;

	public Comment getComment() {
		return this.comment;
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
			this.comment = this.example;
		} else {
			this.comment = findById(getId());
		}
	}

	public Comment findById(Long id) {

		return this.entityManager.find(Comment.class, id);
	}

	/*
	 * Support updating and deleting Comment entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.comment);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.comment);
				return "view?faces-redirect=true&id=" + this.comment.getId();
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
			this.idValue = String.valueOf(findById(getId()).getId());
			this.entityManager.remove(findById(getId()));
			this.entityManager.flush();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Deleted Comment: " + idValue));
			return "/misc/blog?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Comment entities with pagination
	 */

	private int page;
	private long count;
	private List<Comment> pageItems;

	private Comment example = new Comment();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Comment getExample() {
		return this.example;
	}

	public void setExample(Comment example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Comment> root = countCriteria.from(Comment.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Comment> criteria = builder.createQuery(Comment.class);
		root = criteria.from(Comment.class);
		TypedQuery<Comment> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Comment> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Content content = this.example.getContent();
		if (content != null) {
			predicatesList.add(builder.equal(root.get("content"), content));
		}

		Date datecreated = this.example.getDatecreated();
		if (datecreated != null) {
			predicatesList.add(builder.equal(root.get("datecreated"),
					datecreated));
		}

		Date dateupdated = this.example.getDateupdated();
		if (dateupdated != null) {
			predicatesList.add(builder.equal(root.get("dateupdated"),
					dateupdated));
		}

		String comcontent = this.example.getComcontent();
		if (comcontent != null && !"".equals(comcontent)) {
			predicatesList.add(builder.like(root.<String> get("content"),
					'%' + comcontent + '%'));
		}

		String cfname = this.example.getCfname();
		if (cfname != null && !"".equals(cfname)) {
			predicatesList.add(builder.like(root.<String> get("cfname"),
					'%' + cfname + '%'));
		}

		String email = this.example.getEmail();
		if (email != null && !"".equals(email)) {
			predicatesList.add(builder.like(root.<String> get("email"),
					'%' + email + '%'));
		}
		String url = this.example.getUrl();
		if (url != null && !"".equals(url)) {
			predicatesList.add(builder.like(root.<String> get("url"),
					'%' + url + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Comment> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Comment entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Comment> getAll() {

		CriteriaQuery<Comment> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Comment.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Comment.class))).getResultList();
	}

	/**
	 * Returns a list of Comment items for a parent Content
	 * 
	 * @param content
	 * @return
	 */
	public List<Comment> fetchListOfCommentsForContent(Content content) {
		// TODO Beware: Content instance must not be null
		Content contentItem = null;
		if (content != null) {
			contentItem = content;
		} else {
			contentItem = commentController.getContent();
		}
		return repository.findCommentByContent(contentItem);
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final CommentBean ejbProxy = this.sessionContext
				.getBusinessObject(CommentBean.class);

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

				return String.valueOf(((Comment) value).getId());
			}
		};
	}

	/**
	 * Faces validation for Comment's content & author
	 * 
	 * @param context
	 * @param component
	 * @param value
	 * @throws ValidatorException
	 */
	public void validateComment(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		List<String> prohibitedWords = new ArrayList<>();
		String[] keywords = { "fuck", "merde", "putain", "chier" };
		boolean valid = true;
		for (int i = 0; i < keywords.length; i++) {
			prohibitedWords.add(keywords[i]);
		}
		for (String taboo : prohibitedWords) {
			if (String.valueOf(value).toLowerCase().contains(taboo)) {
				valid = false;
			} else {
				valid = true;
			}
		}

		if (!valid) {
			FacesMessage message = new FacesMessage(
					"Please watch your language! Try again!");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(null, message);
			((UIInput) component).setValid(false);
		}
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Comment add = new Comment();

	public Comment getAdd() {
		return this.add;
	}

	public Comment getAdded() {
		Comment added = this.add;
		this.add = new Comment();
		return added;
	}

	/**
	 * @return the idValue
	 */
	public String getIdValue() {
		return idValue;
	}

	/**
	 * @param idValue
	 *            the idValue to set
	 */
	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

}