package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.otika.honeybee.model.Codetable;
import org.otika.honeybee.model.Comment;
import org.otika.honeybee.model.Configuration;
import org.otika.honeybee.model.Content;
import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.model.Honey;
import org.otika.honeybee.model.Ingredient;
import org.otika.honeybee.model.Language;
import org.otika.honeybee.model.Pcategory;
import org.otika.honeybee.model.Plant;
import org.otika.honeybee.model.Prescription;
import org.otika.honeybee.model.Ptype;
import org.otika.honeybee.model.Registery;
import org.otika.honeybee.model.Store;
import org.otika.honeybee.model.Substance;
import org.otika.honeybee.model.Witness;
import org.otika.honeybee.model.Bodypart;

/**
 * Repository to fetch data from database Should be stateless. We don't need to
 * keep state when fetching data from database. The state is kept by the view
 * beans instead.
 * 
 * @author hanine
 * 
 */
/*
 * @LocalBean
 */
@Stateless
@Named(value = "repository")
public class Repository implements Serializable {

	private static final long serialVersionUID = 7658055782181380875L;
	@PersistenceContext
	// (type=PersistenceContextType.EXTENDED)
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public Repository() {
	}

	@PostConstruct
	public void init() {
		// TODO
	}

	/**
	 * Find a Plant object by its ID
	 * 
	 * @param id
	 *            Plant id
	 * @return plant PLant
	 */
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

	/**
	 * Finds a Bodypart item by its label
	 * 
	 * @param label
	 * @return
	 */
	public Bodypart findBodypartByLabel(String label) {
		if (label != null && !label.equals("")) {
			try {
				TypedQuery<Bodypart> query = em.createNamedQuery(
						"Bodypart.findByLabel", Bodypart.class);
				query.setParameter("label", label);
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
	 * Finds a Bodypart item by label (Native query using Like operator)
	 * @param likeLabel
	 * @return
	 */
	public Bodypart findBodypartByLikeLabel(String likeLabel) {
		if (likeLabel != null && !likeLabel.equals("")) {
			try {
				Query query = em.createNativeQuery(
						"SELECT * FROM bodypart WHERE label LIKE '" + likeLabel
								+ "%'", Bodypart.class);				
				Bodypart bodypart = (Bodypart) query.getResultList().get(0);
				System.out.println("Like Bodypart fetched: " + bodypart);
				return bodypart;
			} catch (Exception ex) {				
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
			}
		}
		return null;
	}

	/**
	 * Finds an end user by his/her email address
	 * 
	 * @param email
	 *            End user email
	 * @return end-user End user
	 */
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
	 * Finds a Witness object by its prescription and end user
	 * 
	 * @param p
	 *            Prescription
	 * @param e
	 *            End-user
	 * @return witness Witness object
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
	 * @param id
	 *            store_id
	 * @return Store otherwise null
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
	 * @param type
	 *            Object
	 * @return List of type object
	 */
	@SuppressWarnings({ "unchecked" })
	public <T> List<T> findAll(Object type) {
		CriteriaQuery<T> cq = (CriteriaQuery<T>) em.getCriteriaBuilder()
				.createQuery();
		cq.multiselect(cq.from(type.getClass()));
		return em.createQuery(cq).getResultList();
	} // END OF METHOD

	/**
	 * Finds an object by its id and type
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
	 * Returns a list of all ingredient items if <em>all</em> is specified as a
	 * value, otherwise, ingredient items by form (raw, oil, juice, mixture..)
	 * 
	 * @param form
	 *            Ingredient form (specifying 'all' as a value returns all items
	 * @return list Ingredient items by form
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
	 * Finds an ingredient by its label and form
	 * 
	 * @param label
	 * @param form
	 * @return ingredient
	 */
	public Ingredient findByLabelAndForm(String label, String form) {
		if (label != null && form != null && !label.equals("")
				&& !form.equals("")) {
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

	/**
	 * List of Ingredients by plant
	 * 
	 * @param plant
	 * @return list<Ingredient>
	 */
	public List<Ingredient> findByPlant(Plant plant) {
		if (plant != null) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByPlant", Ingredient.class);
				query.setParameter("plant", plant);
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(Repository.class.getName() + " : "
						+ ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * List of Ingredients by Substance
	 * 
	 * @param substance
	 * @return list<Ingredient>
	 */
	public List<Ingredient> findBySubstance(Substance substance) {
		if (substance != null) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findBySubstance", Ingredient.class);
				query.setParameter("substance", substance);
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(Repository.class.getName() + " : "
						+ ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * List of Ingredients by Honey
	 * 
	 * @param honey
	 * @return list<Ingredient>
	 */
	public List<Ingredient> findByHoney(Honey honey) {
		if (honey != null) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByPlant", Ingredient.class);
				query.setParameter("honey", honey);
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(Repository.class.getName() + " : "
						+ ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param plant
	 * @param form
	 * @return
	 */
	public List<Ingredient> findByPlantAndForm(Plant plant, String form) {
		if (plant != null) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByPlantAndForm", Ingredient.class);
				query.setParameter("plant", plant);
				query.setParameter("form", form);
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(Repository.class.getName() + " : "
						+ ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param honey
	 * @param form
	 * @return
	 */
	public List<Ingredient> findByHoneyAndForm(Honey honey, String form) {
		if (honey != null) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findByPlantAndForm", Ingredient.class);
				query.setParameter("honey", honey);
				query.setParameter("form", form);
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(Repository.class.getName() + " : "
						+ ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * List of Ingredients by substance and form
	 * 
	 * @param substance
	 * @param form
	 * @return
	 */
	public List<Ingredient> findBySubstanceAndForm(Substance substance,
			String form) {
		if (substance != null) {
			try {
				TypedQuery<Ingredient> query = em.createNamedQuery(
						"Ingredient.findBySubstanceAndForm", Ingredient.class);
				query.setParameter("substance", substance);
				query.setParameter("form", form);
				return query.getResultList();
			} catch (Exception ex) {
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				System.out.println(Repository.class.getName() + " : "
						+ ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param code
	 *            language_code
	 * @return Language
	 */
	public Language findByCode(String code) {
		try {
			TypedQuery<Language> query = em.createNamedQuery(
					"Language.findByCode", Language.class);
			query.setParameter("code", code);
			return query.getSingleResult();
		} catch (Exception ex) {
			System.err.println(ex);
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "{0} {1}",
					new Object[] { ex.getMessage(), ex.getCause() });
		}
		return null;
	}

	/**
	 * Fetches an end user by user key
	 * 
	 * @param key
	 *            User key
	 * @return end-user
	 */
	public Enduser findByUserkey(String key) {
		try {
			TypedQuery<Enduser> query = em.createNamedQuery(
					"Enduser.findByUserkey", Enduser.class);
			query.setParameter("userkey", key);
			return query.getSingleResult();
		} catch (Exception ex) {
			System.out.println("Exception in Repository: FindByUserkey "
					+ getClass() + " " + ex.getMessage() + " | "
					+ ex.getClass());
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					"{0} - Cause: {1}",
					new Object[] { ex.getMessage(), ex.getCause() });
		}
		return null;
	}

	/**
	 * Fetches an end user by email and password
	 * 
	 * @param email
	 *            <a>End user email</a>
	 * @param password
	 * @return end-user
	 */
	public Enduser findByEmailAndPassword(String email, String password) {
		try {
			TypedQuery<Enduser> query = em.createNamedQuery(
					"Enduser.findByEmailAndPassword", Enduser.class);
			query.setParameter("email", email);
			query.setParameter("password", password);
			return query.getSingleResult();
		} catch (Exception ex) {
			System.out
					.println("Exception in Repository: FindByEmailAndPassword "
							+ getClass() + " " + ex.getMessage() + " | "
							+ ex.getClass());
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					"{0} - Cause: {1}",
					new Object[] { ex.getMessage(), ex.getCause() });
		}
		return null;
	}

	/**
	 * Fetches Configuration object by ID
	 * 
	 * @param id
	 *            <a>PK</a>
	 */
	public Configuration findById(Long id) {
		try {
			if (id != null && id >= 0) {
				return em.find(Configuration.class, id);
			}
		} catch (Exception ex) {
			System.err.println("Exception finding Configuration object "
					+ ex.getMessage());
			String msg = ex.getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
		return null;
	}

	/**
	 * List of Configuration items
	 * 
	 * @return configuration_list otherwise null
	 */
	public List<Configuration> findAllConfigurationItems() {
		try {
			TypedQuery<Configuration> query = em.createNamedQuery(
					"Configuration.findAll", Configuration.class);
			return query.getResultList();
		} catch (Exception ex) {
			System.err.println("Exception finding Configuration list "
					+ ex.getMessage());
			String msg = ex.getMessage() + "" + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<Registery> allRegisteryItems() {
		try {
			CriteriaQuery<Registery> cq = em.getCriteriaBuilder().createQuery(
					Registery.class);
			cq.select(cq.from(Registery.class));
			return em.createQuery(cq).getResultList();
		} catch (Exception ex) {
			System.err.println(ex);
			return null;
		}
	}

	/**
	 * Find Plant by season
	 * 
	 * @return
	 */
	public List<Plant> findPlantItemsBySeason() {
		try {
			TypedQuery<Plant> query = em.createNamedQuery("Plant.findBySeason",
					Plant.class);
			return query.getResultList();
		} catch (Exception ex) {
			System.err.println("Exception finding Plant by Season list "
					+ ex.getMessage());
			String msg = ex.getMessage() + "" + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * All Plant types
	 * 
	 * @return plant types
	 */
	public List<Ptype> allPtypeItems() {
		try {
			CriteriaQuery<Ptype> cq = em.getCriteriaBuilder().createQuery(
					Ptype.class);
			cq.select(cq.from(Ptype.class));
			return em.createQuery(cq).getResultList();
		} catch (Exception ex) {
			System.err.println(ex);
			return null;
		}
	}

	/**
	 * All Plant categories
	 * 
	 * @return plant categories
	 */
	public List<Pcategory> allPcategoryItems() {
		try {
			CriteriaQuery<Pcategory> cq = em.getCriteriaBuilder().createQuery(
					Pcategory.class);
			cq.select(cq.from(Pcategory.class));
			return em.createQuery(cq).getResultList();
		} catch (Exception ex) {
			System.err.println(ex);
			return null;
		}
	}

	/**
	 * Find a Pcategory by label
	 * 
	 * @param label
	 * @return Pcategory by label
	 */
	public Pcategory findPcategoryByLabel(String label) {
		try {
			TypedQuery<Pcategory> query = em.createNamedQuery(
					"Pcategory.findByLabel", Pcategory.class);
			query.setParameter("label", label);
			return query.getSingleResult();
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getClass();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * Find a Plant type by its label
	 * 
	 * @param label
	 *            Label
	 * @return Ptype by label
	 */
	public Ptype findPtypeByLabel(String label) {
		try {
			TypedQuery<Ptype> query = em.createNamedQuery("Ptype.findByLabel",
					Ptype.class);
			query.setParameter("label", label);
			return query.getSingleResult();
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * List of Content items by content type (blog, nutrition ...)
	 * 
	 * @param ctype
	 * @return list
	 */
	public List<Content> findContentByCtype(String ctype) {
		try {
			TypedQuery<Content> query = em.createNamedQuery(
					"Content.findByCtype", Content.class);
			query.setParameter("ctype", ctype);
			return query.getResultList();
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * Finds a Content object by its ID
	 * 
	 * @param id
	 * @return
	 */
	public Content findContentById(long id) {
		try {
			return em.find(Content.class, id);
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * <p>
	 * Returns a list of Content items by Content type and View language
	 * </p>
	 * <p>
	 * This is almost specific for Blog entries, as A blog entry is not
	 * multilingual. So if column <em>contentfr</em> is not null and the content
	 * type is <em>blog</em>, that means the Content item is a French Blog entry
	 * </p>
	 * <p>
	 * This differs from other Content types like nutrition or cupping which are
	 * multilingual, thus we use a simple non discriminatory query in order to
	 * retrieve items.
	 * </p>
	 * 
	 * @param ctype
	 * @param lang
	 * @return
	 */
	public List<Content> findContentByCtypeAndLanguage(String ctype, String lang) {
		try {
			TypedQuery<Content> query = em
					.createQuery(
							"SELECT c FROM Content c WHERE c.content != null ORDER BY datecreated DESC",
							Content.class);
			if (lang.equalsIgnoreCase("fr")) {
				query = em
						.createQuery(
								"SELECT c FROM Content c WHERE c.contentfr != null ORDER BY datecreated DESC",
								Content.class);
			} else if (lang.equalsIgnoreCase("ar")) {
				query = em
						.createQuery(
								"SELECT c FROM Content c WHERE c.contentar != null ORDER BY datecreated DESC",
								Content.class);
			} else {
				query = em
						.createQuery(
								"SELECT c FROM Content c WHERE c.content != null ORDER BY datecreated DESC",
								Content.class);
			}
			return query.getResultList();
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * Returns a list of Comment items for a parent Content
	 * 
	 * @param content
	 * @return
	 */
	public List<Comment> findCommentByContent(Content content) {
		try {
			TypedQuery<Comment> query = em.createNamedQuery(
					"Comment.findByContent", Comment.class);
			query.setParameter("content", content);
			return query.getResultList();
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

	/**
	 * Returns a list of Codetable items by type
	 * 
	 * @param codetype
	 * @return
	 */
	public List<Codetable> findCodeValueByType(String codetype) {
		try {
			TypedQuery<Codetable> query = em
					.createQuery(
							"SELECT c FROM Codetable c where c.codetype = :codetype ORDER BY c.codevalue",
							Codetable.class);
			query.setParameter("codetype", codetype);
			return query.getResultList();
		} catch (Exception ex) {
			String msg = ex.getMessage() + " " + ex.getCause().getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
	}

} // END OF CLASS
