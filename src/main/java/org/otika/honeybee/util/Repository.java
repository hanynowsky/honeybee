package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.model.Ingredient;
import org.otika.honeybee.model.Plant;
import org.otika.honeybee.model.Prescription;
import org.otika.honeybee.model.Store;
import org.otika.honeybee.model.Witness;

@Stateful
@SessionScoped
@LocalBean
public class Repository implements Serializable {

	private static final long serialVersionUID = 7658055782181380875L;
	@PersistenceContext
	// (type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public Repository() {

	}

	@PostConstruct
	public void init() {

	}

	public Plant findPlantById(Long id) {
		if (id != null) {
			try {
				TypedQuery<Plant> query = em.createNamedQuery("Plant.findById",
						Plant.class);
				query.setParameter("id", id);
				return query.getSingleResult();
			} catch (Exception ex) {
				// No need for Finally for EM is closed by container
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
			}
		}
		return null;
	}

	public Enduser findByEmail(String email) {
		if (email != null && !email.equals("")) {
			try {
				TypedQuery<Enduser> query = em.createNamedQuery(
						"Enduser.findByEmail", Enduser.class);
				query.setParameter("email", email);
				return query.getSingleResult();
			} catch (Exception ex) {
				// No need for Finally for EM is closed by container
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
			}
		}
		return null;
	}

	/**
	 * 
	 * @param prescription
	 * @param enduser
	 * @return witness
	 */
	public Witness findByPrescriptionAndEnduser(Prescription p, Enduser e) {
		if (p != null && e != null) {
			try {
				TypedQuery<Witness> query = em.createNamedQuery(
						"Witness.findByPrescriptionAndEnduser", Witness.class);
				query.setParameter("prescription", p);
				query.setParameter("enduser", e);
				return query.getSingleResult();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param store_id
	 * @return
	 */
	public Store findStoreById(Long id) {
		if (id != null) {
			try {
				TypedQuery<Store> query = em.createNamedQuery("Store.findById",
						Store.class);
				query.setParameter("id", id);
				return query.getSingleResult();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println("No entity Found for Store Exception");
				return null;
			}
		}
		return null;
	}

	/**
	 * List of Store object ordered by ID, descending order.
	 * 
	 * @return
	 */
	public List<Store> findAllStoreItemsOrderedByIdDESC() {

		try {
			TypedQuery<Store> query = em.createQuery(
					"SELECT s from Store s ORDER BY s.id DESC", Store.class);
			return query.getResultList();
		} catch (Exception ex) {
			Logger.getLogger(Repository.class.getName()).log(Level.ALL,
					ex.getMessage());
			System.out.println("No Store entity found");
			return null;
		}
	}

	/**
	 * LIst of Store objects in database
	 * 
	 * @return List<Store>
	 */
	public List<Store> findAllStoreItems() {

		try {
			TypedQuery<Store> query = em.createQuery("SELECT s from Store s",
					Store.class);
			return query.getResultList();
		} catch (Exception ex) {
			Logger.getLogger(Repository.class.getName()).log(Level.ALL,
					ex.getMessage());
			System.out.println("No Store entity found");
			return null;
		}
	} // END F METHOD

	/**
	 * 
	 * @param <T>
	 * @param o
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public <T> List<T> findAll(Object type) {
		CriteriaQuery<T> cq = (CriteriaQuery<T>) em.getCriteriaBuilder()
				.createQuery();
		cq.multiselect(cq.from(type.getClass()));
		return em.createQuery(cq).getResultList();
	} // END OF METHOD

	/**
	 * 
	 * @param <T>
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T findById(Object id, Object o) {
		if (id != null) {
			try {
				return (T) em.find(o.getClass(), id);
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println("No entity Found for "
						+ o.getClass().getName() + " :Exception " + ex);
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param form
	 * @return list
	 */
	public List<Ingredient> findIngredientItemsByForm(String form) {
		if (form != null && !"".equals(form) && !form.equals("all")) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByForm", Ingredient.class);
				query.setParameter("form", form);
				return query.getResultList();
			} catch (Exception ex) {
				// No need for Finally. EM is closed by container
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
			}
		} else {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findAll", Ingredient.class);				
				return query.getResultList();
			} catch (Exception ex) {
				// No need for Finally. EM is closed by container
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param label
	 * @param form
	 * @return ingredient
	 */
	public Ingredient findByLabelAndForm(String label, String form) {
		if (label != null && form != null && !label.equals("") && !form.equals("")) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByLabelAndForm", Ingredient.class);
				query.setParameter("label", label);
				query.setParameter("form", form);
				return query.getSingleResult();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(ex.getMessage());
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param label
	 * @return list_of_ingredients
	 */
	public List<Ingredient> findByLabel(String label) {
		if (label != null && !label.equals("")) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByLabel", Ingredient.class);
				query.setParameter("label", label);				
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(ex.getMessage());
				return null;
			}
		}
		return null;
	}

} // END OF CLASS
