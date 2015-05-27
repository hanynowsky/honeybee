package org.otika.honeybee.controller;

import javax.ejb.EJB;
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
import org.otika.honeybee.model.Comment;
import org.otika.honeybee.model.Content;
import org.otika.honeybee.util.UtilityBean;

/**
 * Controller that calls CRUD operations for Comment entities
 * @author Hanine .H ALMADANI <hanynowsky@gmail.com>
 *
 */

@Model
public class CommentController implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6898264918027605051L;
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	@Named
	@Produces
	@RequestScoped
	private Comment newComment = new Comment();
	@EJB
	private PublisherBean publisherBean;
	private Long id;
	private Content content;
	@Inject
	private UtilityBean utilitybean;

	public CommentController() {

	}

	/**
	 * Calls a stateless controller for CRUD operations
	 * 
	 * @return null
	 */
	public String createComment() {
		try {
			publisherBean.createComment(newComment);
			utilitybean.showMessage("info", "New comment created by "
					+ newComment.getCfname(), "");
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).debug(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
		/*
		 * TODO On the View figure out how to properly use AJAX to append a
		 * newly created comment with navigation
		 */
		return "/misc/blog?faces-redirect=true";
	}

	/**
	 * Deletes a comment item
	 * 
	 * @param comment
	 * @return
	 */
	public String deleteComment(Comment comment) {
		try {
			publisherBean.deleteComment(comment);
			utilitybean.showMessage("info", "Comment deleted: "
					+ comment.getId(), "");
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
		return null;
	}

	/**
	 * Listener for Accordion Panel that sets a content instance
	 * 
	 * @param content
	 */
	public void onTabChange(Content item) {
		if (item != null) {
			setContent(item);
		}
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
	 * @return the content
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(Content content) {
		this.content = content;
	}

}
