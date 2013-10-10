package org.otika.honeybee.controller;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.logging.Logger;
import org.otika.honeybee.model.Comment;
import org.otika.honeybee.model.Content;

/**
 * <h>CRUD Bean</h>
 * <p>
 * No need to implement an interface or extend an abstract class since we only
 * have two or three objects
 * </p>
 * 
 * @author Hanine.H
 * 
 */
@Stateless
public class PublisherBean implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3929275547682463815L;
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public PublisherBean() {

	}

	/**
	 * Creates a new Content Item
	 * 
	 * @param content
	 */
	public void createContent(Content content) {
		entityManager.persist(content);
		Logger.getLogger(getClass().getName()).info(
				"Created Content with id " + content.getId());
	}

	/**
	 * Updates a Content item by id
	 * 
	 * @param id
	 */
	public void updateContent(long id) {
		entityManager.merge(entityManager.find(Content.class, id));
		Logger.getLogger(getClass().getName()).info(
				"Updated Content with id " + id);
	}

	/**
	 * Updates a Content Item
	 * 
	 * @param content
	 */
	public void updateContent(Content content) {
		entityManager.merge(content);
		Logger.getLogger(getClass().getName()).info(
				"Updated Content with id " + content.getId());
	}

	/**
	 * Deletes a Content object
	 * 
	 * @param content
	 */
	public void deleteContent(Content content) {
		Content c = entityManager.find(Content.class, content.getId());
		entityManager.remove(c);
		entityManager.flush();
		Logger.getLogger(getClass().getName()).info(
				"Deleted Content with id " + content.getId());
	}

	/**
	 * Creates a new comment
	 * 
	 * @param comment
	 */
	public void createComment(Comment comment) {
		entityManager.persist(comment);
		Logger.getLogger(getClass().getName()).info(
				"Created Comment with id " + comment.getId());

	}

	/**
	 * Update a Comment by id
	 * 
	 * @param id
	 */
	public void updateComment(long id) {
		entityManager.merge(entityManager.find(Comment.class, id));
		Logger.getLogger(getClass().getName()).info(
				"Updated Comment with id " + id);
	}

	/**
	 * Update a Comment Object
	 * 
	 * @param id
	 */
	public void updateComment(Comment comment) {
		entityManager.merge(comment);
		Logger.getLogger(getClass().getName()).info(
				"Updated Comment with id " + comment.getId());
	}

	/**
	 * Deletes a Comment object
	 * 
	 * @param content
	 */
	public void deleteComment(Comment comment) {
		entityManager.remove(comment);
		entityManager.flush();
		Logger.getLogger(getClass().getName()).info(
				"Deleted Content with id " + comment.getId());
	}

}
