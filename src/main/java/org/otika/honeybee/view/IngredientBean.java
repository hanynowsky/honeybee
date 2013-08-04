package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
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

import org.otika.honeybee.model.Defect;
import org.otika.honeybee.model.Honey;
import org.otika.honeybee.model.Ingredient;
import org.otika.honeybee.model.Plant;
import org.otika.honeybee.model.Substance;
import org.otika.honeybee.model.Virtue;
import org.otika.honeybee.util.Repository;
import org.otika.honeybee.util.UtilityBean;

/**
 * Backing bean for Ingredient entities.
 * <p>
 * This class provides CRUD functionality for all Ingredient entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class IngredientBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<String> ingredientForms;
	@EJB
	private Repository repository;
	private List<Ingredient> selectedIngredients;
	private String form;
	@Inject
	private UtilityBean utilityBean;
	private List<String> units;

	/*
	 * Support creating and retrieving Ingredient entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Ingredient ingredient;

	public Ingredient getIngredient() {
		return this.ingredient;
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
			this.ingredient = this.example;
		} else {
			this.ingredient = findById(getId());
		}
	}

	public Ingredient findById(Long id) {
		return this.entityManager.find(Ingredient.class, id);
	}

	/*
	 * Support updating and deleting Ingredient entities
	 */

	/**
	 * Creating & Editing Ingredient objects
	 * 
	 * @return outcome
	 */
	public String update() {
		// ending conversation was originally here
		try {
			if (this.id == null) {
				if (!isIngredientPresent()
						&& !isOneParentIngredientPresent()) {
					isHoney();
					this.conversation.end();
					this.entityManager.persist(this.ingredient);
					appendIngredientVirtuesAndDefects();
					return "search?faces-redirect=true";
				} else {
					return null;
				}
			} else {
				if (this.id != null) { //!isOneParentIngredientPresent()
					isHoney();
					this.conversation.end();
					this.entityManager.merge(this.ingredient);
					appendIngredientVirtuesAndDefects();
					return "view?faces-redirect=true&id="
							+ this.ingredient.getId();
				} else {
					return null;
				}
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
	 * Support searching Ingredient entities with pagination
	 */

	private int page;
	private long count;
	private List<Ingredient> pageItems;

	private Ingredient example = new Ingredient();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Ingredient getExample() {
		return this.example;
	}

	public void setExample(Ingredient example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Ingredient> root = countCriteria.from(Ingredient.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Ingredient> criteria = builder
				.createQuery(Ingredient.class);
		root = criteria.from(Ingredient.class);
		TypedQuery<Ingredient> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Ingredient> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Plant plant = this.example.getPlant();
		if (plant != null) {
			predicatesList.add(builder.equal(root.get("plant"), plant));
		}
		Honey honey = this.example.getHoney();
		if (honey != null) {
			predicatesList.add(builder.equal(root.get("honey"), honey));
		}
		Substance substance = this.example.getSubstance();
		if (substance != null) {
			predicatesList.add(builder.equal(root.get("substance"), substance));
		}
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

		String labelmar = this.example.getLabelmar();
		if (labelmar != null && !"".equals(labelmar)) {
			predicatesList.add(builder.like(root.<String> get("labelmar"),
					'%' + labelmar + '%'));
		}

		String form = this.example.getForm();
		if (form != null && !"".equals(form)) {
			predicatesList.add(builder.like(root.<String> get("form"),
					'%' + form + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Ingredient> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Ingredient entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Ingredient> getAll() {

		CriteriaQuery<Ingredient> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Ingredient.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Ingredient.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final IngredientBean ejbProxy = this.sessionContext
				.getBusinessObject(IngredientBean.class);

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

				return String.valueOf(((Ingredient) value).getId());
			}
		};
	}

	/*
	 * Utility Methods
	 */

	/**
	 * Post construct method
	 */
	@PostConstruct
	public void init() {

		// this.conversation.begin();

		ingredientForms = new ArrayList<String>();
		ingredientForms.add("raw");
		ingredientForms.add("oil");
		ingredientForms.add("powder");
		ingredientForms.add("honey");
		ingredientForms.add("juice");
		ingredientForms.add("mixture");

		selectedIngredients = getAll();

		units = new ArrayList<String>();
		units.add("kg");
		units.add("liter");
		units.add("spoon");
		units.add("unit");
		units.add("bottle");
	}

	/*
	 * 
	 * @return List_of_ingredient_items
	 */
	public List<Ingredient> getIngredientItemsByForm() {
		if (getForm() != null && !"".equals(getForm())) {
			try {
				CriteriaQuery<Ingredient> criteria = this.entityManager
						.getCriteriaBuilder().createQuery(Ingredient.class);
				return this.entityManager
						.createQuery(
								criteria.select(criteria.from(Ingredient.class)))
						.setParameter("form", getForm()).getResultList();
			} catch (Exception ex) {
				// No need for Finally clause for EM is closed by container
				Logger.getLogger(Repository.class.getName()).log(Level.ALL,
						ex.getMessage());
				return null;
			}
		}
		return null;
	}

	/**
	 * Method that sets the same defects and virtues for all ingredients of same
	 * type
	 */
	private void appendIngredientVirtuesAndDefects() {
		try {
			Ingredient ilf;
			Ingredient ibylf = repository.findByLabelAndForm(
					this.ingredient.getLabel(), this.ingredient.getForm());
			if (ibylf == null) {
				ilf = this.ingredient;
			} else {
				ilf = ibylf;
			}
			if (ilf.getForm().equalsIgnoreCase("raw")
					&& ilf.getSubstance() == null) {
				/*
				 * Maybe we should exclude honey ingredient if it has different
				 * virtues and defects
				 */
				List<Ingredient> ingredients = repository
						.findByLabel(this.ingredient.getLabel());
				for (Ingredient i : ingredients) {
					if (!i.getForm().equalsIgnoreCase("raw")) {
						i.setDefects(new HashSet<Defect>(ilf.getDefects()));
						i.setVirtues(new HashSet<Virtue>(ilf.getVirtues()));
						// merge i here => hibernate shared exception
						this.entityManager.merge(i);
					}
				}
				/*
				 * Set<Virtue> virtues = new
				 * HashSet<Virtue>(this.ingredient.getVirtues()); Set<Defect>
				 * defects = new HashSet<Defect>(this.ingredient.getDefects());
				 * Ingredient o =
				 * repository.findByLabelAndForm(this.ingredient.getLabel(),
				 * "oil") ; Ingredient p =
				 * repository.findByLabelAndForm(this.ingredient.getLabel(),
				 * "powder"); Ingredient h =
				 * repository.findByLabelAndForm(this.ingredient.getLabel(),
				 * "honey");
				 * o.setVirtues(virtues);p.setVirtues(virtues);h.setVirtues
				 * (virtues);
				 * o.setDefects(defects);p.setDefects(defects);h.setDefects
				 * (defects); this.entityManager.merge(o);
				 * this.entityManager.merge(p); this.entityManager.merge(h);
				 */
			}
		} catch (Exception ex) {
			Logger.getLogger(IngredientBean.class.getName()).log(Level.ALL,
					ex.getMessage());
			System.out
					.println(ex.getMessage()
							+ " "
							+ ex
							+ " Setting defects & virtues for all ingredients of same type, failed!");
			ex.printStackTrace();
		}

	}

	/**
	 * Method that checks whether an Ingredient has a Honey object as the only
	 * parent and sets its form to honey
	 * 
	 * @return is_Honey?
	 */
	private boolean isHoney() {
		Ingredient i = this.ingredient;
		if (i.getHoney() != null && i.getSubstance() == null
				&& i.getPlant() == null) {
			i.setForm("honey");
			return true;
		}
		return false;
	}

	/**
	 * Listener for the ingredient form select one menu
	 */
	public void updateIngredients() {
		if (form == null || form.equals("")) {
			System.out.println(form + " : form is null");
			utilityBean
					.showMessage("WARN", "Please select ingredient form", "");
		} else {
			if (getIngredientItemsByForm() != null) {
				selectedIngredients = getIngredientItemsByForm();
			} else {
				selectedIngredients = repository
						.findIngredientItemsByForm(form);
				// System.out.println("IngByForm list was null");
			}
		}
	}

	/**
	 * 
	 * @param evt Value Change event
	 */
	public void FormValueChangeListener(ValueChangeEvent evt) {
		if (evt != null) {
			form = (String) evt.getNewValue();
			updateIngredients();
			// selectedIngredients = findIngredientItemsByForm(form);
		}
	}

	/**
	 * 
	 * @param i Ingredient
	 * @return localized_label_string
	 */
	public String switchLabel(Ingredient i) {
		String lang = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().getLanguage();
		if (i != null) {
            switch (lang) {
                case "ar":
                    return i.getLabelar();
                case "fr":
                    return i.getLabelfr();
                default:
                    return i.getLabel();
            }
		}
		return null;
	}

	/**
	 * 
	 * @return whether_an_ingredient_is_present_or_not
	 */
	private boolean isIngredientPresent() {
		try {
			Ingredient i = repository.findByLabelAndForm(
					this.ingredient.getLabel(), this.ingredient.getForm());
			if (i != null) {
				utilityBean.showMessage("ERROR",
						"(Ingredient) Item already exists in database!", "");
				System.out.println("(Ingredient) Item Already exists in DB! ");
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(IngredientBean.class.getName()).log(Level.ALL,
					ex.getMessage());
			System.out.println("Failure checking Ingredient uniqueness! "
					+ ex.getMessage());
		}
		return false;
	}

	/**
	 * Method that ensures a newly created ingredient object is not a duplicate
	 * 
	 * @return whether_ingredient_is_present_on_DB
	 */

	public boolean isOneParentIngredientPresent() {
		// TODO refactor in order to allow edition
		try {
			List<Ingredient> ipList = repository.findByPlantAndForm(
					this.ingredient.getPlant(), this.ingredient.getForm());
			List<Ingredient> ihList = repository.findByHoneyAndForm(
					this.ingredient.getHoney(), this.ingredient.getForm());
			List<Ingredient> isList = repository.findBySubstanceAndForm(
					this.ingredient.getSubstance(), this.ingredient.getForm());
			boolean conditionP = ipList != null
					&& this.ingredient.getSubstance() == null
					&& this.ingredient.getHoney() == null;
			boolean conditionS = isList != null
					&& this.ingredient.getPlant() == null
					&& this.ingredient.getHoney() == null;
			boolean conditionH = ihList != null
					&& this.ingredient.getSubstance() == null
					&& this.ingredient.getPlant() == null;

			if ((conditionP || conditionS || conditionH)
					&& !this.ingredient.getForm().equalsIgnoreCase("mixture")) {
				utilityBean.showMessage("ERROR", "Item exists", "");
				return true;
			}

		} catch (Exception ex) {
			// TODO Properly handle exception
			System.out.println("IngredientBean: Exception in isOneIngredient: "
					+ getClass().getName() + " : " + ex);
			ex.printStackTrace();
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage(), ex);
		}
		return false;
	}

	/**
	 * Supposed to validate ingredient uniqueness
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validateIngredient(FacesContext context, UIComponent component,
			Object value) {
		// TODO logic here : Use this validator for all required select menu
		// components
		if (value instanceof Ingredient || value instanceof Honey
				|| value instanceof Plant) {
			if (isOneParentIngredientPresent() && isIngredientPresent()) {
				utilityBean.showMessage("ERROR",
						"Validation Error: Ingredient already exists ", "");
				// new ValidationException("Validation Error: Item Exists");
			}
		}
	}

	/*
	 * Getters & Setters
	 */

	public List<String> getUnits() {
		return units;
	}

	/**
	 * @return the ingredientForms
	 */
	public List<String> getIngredientForms() {
		return ingredientForms;
	}

	public List<Ingredient> getSelectedIngredients() {
		return selectedIngredients;
	}

	public void setSelectedIngredients(List<Ingredient> selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	private Ingredient add = new Ingredient();

	public Ingredient getAdd() {
		return this.add;
	}

	public Ingredient getAdded() {
		Ingredient added = this.add;
		this.add = new Ingredient();
		return added;
	}
}