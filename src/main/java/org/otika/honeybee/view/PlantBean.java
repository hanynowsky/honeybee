package org.otika.honeybee.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.otika.honeybee.util.LocaleBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
import javax.imageio.ImageIO;
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
import org.otika.honeybee.model.Pcategory;

import org.otika.honeybee.model.Plant;
import org.otika.honeybee.model.Ptype;
import org.otika.honeybee.util.Repository;
import org.otika.honeybee.util.UtilityBean;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * Backing bean for Plant entities.
 * <p>
 * This class provides CRUD functionality for all Plant entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */
@Named
@Stateful
@ConversationScoped
public class PlantBean implements Serializable {

	@Inject
	private Conversation conversation;
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	private Plant plant;
	private static final long serialVersionUID = 1L;
	private StreamedContent graphic;
	private List<String> seasons;
	private List<Ptype> types;
	private List<Pcategory> categories;
	@EJB
	private Repository repository;
	@Inject
	private UtilityBean utilityBean;
	private int imageLimit = 600;
	@Inject
	private LocaleBean localeBean;

	/*
	 * Support creating and retrieving Plant entities
	 */
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Plant getPlant() {
		return this.plant;
	}

	public StreamedContent getGraphic() {
		return this.graphic;
	}

	@PostConstruct
	public void init() {
		// TODO create season table with months in database instead
		seasons = new ArrayList<>();
		seasons.add("summer");
		seasons.add("winter");
		seasons.add("autumn");
		seasons.add("spring");
		seasons.add("all");

		types = new ArrayList<>();
		categories = new ArrayList<>();
		try {
			for (Ptype type : repository.allPtypeItems()) {
				types.add(type);
			}
			for (Pcategory category : repository.allPcategoryItems()) {
				categories.add(category);
			}
		} catch (Exception ex) {
			System.err.println(ex);
			Logger.getLogger(PlantBean.class.getName()).severe(ex.getMessage());
		}
		/*
		 * types.add("vegetable"); types.add("fruit"); types.add("herb");
		 * types.add("condiment"); types.add("sub_tree"); types.add("other");
		 */

		/*
		 * categories.add("condiments"); categories.add("aromatics");
		 * categories.add("bulbs"); categories.add("leaves");
		 * categories.add("flowers"); categories.add("fruits");
		 * categories.add("seeds"); categories.add("roots");
		 * categories.add("stems"); categories.add("tubercles");
		 */
	}

	/**
	 * 
	 * @return
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
			this.plant = this.example;
		} else {
			this.plant = findById(getId());
		}
	}

	public Plant findById(Long id) {

		return this.entityManager.find(Plant.class, id);
	}

	/*
	 * Support updating and deleting Plant entities
	 */
	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.plant);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.plant);
				return "view?faces-redirect=true&id=" + this.plant.getId();
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
	 * Support searching Plant entities with pagination
	 */
	private int page;
	private long count;
	private List<Plant> pageItems;
	private Plant example = new Plant();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Plant getExample() {
		return this.example;
	}

	public void setExample(Plant example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Plant> root = countCriteria.from(Plant.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Plant> criteria = builder.createQuery(Plant.class);
		root = criteria.from(Plant.class);
		TypedQuery<Plant> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Plant> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String label = this.example.getLabel();
		if (label != null && !"".equals(label)) {
			predicatesList.add(builder.like(root.<String> get("label"),
					'%' + label + '%'));
		}

		String season = this.example.getSeason();
		if (season != null && !"".equals(season)) {
			predicatesList.add(builder.like(root.<String> get("season"),
					'%' + season + '%'));
		}

		String type = this.example.getType();
		if (type != null && !"".equals(type)) {
			predicatesList.add(builder.like(root.<String> get("type"),
					'%' + type + '%'));
		}

		String category = this.example.getCategory();
		if (category != null && !"".equals(category)) {
			predicatesList.add(builder.like(root.<String> get("category"),
					'%' + category + '%'));
		}

		String labelfr = this.example.getLabelfr();
		if (labelfr != null && !"".equals(labelfr)) {
			predicatesList.add(builder.like(root.<String> get("labelfr"),
					'%' + labelfr + '%'));
		}
		String labellat = this.example.getLabellat();
		if (labellat != null && !"".equals(labellat)) {
			predicatesList.add(builder.like(root.<String> get("labellat"),
					'%' + labellat + '%'));
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

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Plant> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Plant entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */
	public List<Plant> getAll() {

		CriteriaQuery<Plant> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Plant.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Plant.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final PlantBean ejbProxy = this.sessionContext
				.getBusinessObject(PlantBean.class);

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

				return String.valueOf(((Plant) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */
	private Plant add = new Plant();

	public Plant getAdd() {
		return this.add;
	}

	public Plant getAdded() {
		Plant added = this.add;
		this.add = new Plant();
		return added;
	}

	/**
	 * @return the seasons
	 */
	public List<String> getSeasons() {
		return seasons;
	}

	/**
	 * @param seasons
	 *            the seasons to set
	 */
	public void setSeasons(List<String> seasons) {
		this.seasons = seasons;
	}

	public List<Ptype> getTypes() {
		return types;
	}

	public void setTypes(List<Ptype> types) {
		this.types = types;
	}

	public List<Pcategory> getCategories() {
		return categories;
	}

	public void setCategories(List<Pcategory> categories) {
		this.categories = categories;
	}

	public int getImageLimit() {
		return imageLimit;
	}

	public void setImageLimit(int imageLimit) {
		this.imageLimit = imageLimit;
	}

	/**
	 * File upload listener
	 * 
	 * @param event
	 *            File Upload Event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		String p = System.getProperty("java.io.tmpdir");
		String separator = File.separator;
		UploadedFile file = event.getFile();
		try {
			File f = new File(p + separator + "honeybee" + separator
					+ file.getFileName());
			if (!f.exists()) {
				new File(p + separator + "honeybee").mkdirs();
				f.createNewFile();
			}
			try (OutputStream output = new FileOutputStream(f)) {
				output.write(file.getContents());
				file.getInputstream().close();
				output.flush();
			}

			/*
			 * byte[] content = new byte[1024]; int read; while ((read =
			 * file.getInputstream().read(content)) != -1) {
			 * output.write(content, 0, read); }
			 */

			// Plant plant = new Plant("P2", "P2", "P2", "P2");
			int height = ImageIO.read(file.getInputstream()).getHeight();
			int width = ImageIO.read(file.getInputstream()).getWidth();
			if ((width <= imageLimit) && (height <= imageLimit)) {
				System.out.println("w: " + width + " == " + " h: " + height
						+ " Image proportions are OK");
				this.plant.setGraphic(file.getContents());
				// this.plant.setGraphic(file.getContents());
			} else {
				System.out.println(width + " != " + height
						+ " Image must not be larger than " + imageLimit
						+ " px");
				FacesMessage msg = new FacesMessage(
						"Image must not be marger than " + imageLimit + " px");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			// plant.setId(Long.valueOf("0"));
			// this.entityManager.persist(plant);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage("File was uploaded: " + file.getFileName()
							+ " : to: " + f.getPath()));
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"File was not uploaded: " + file.getFileName(),
							null));
			Logger.getLogger(PlantBean.class.getName()).severe(ex.getMessage());
		}

	} // END OF METHOD

	/**
	 * Validator for plant graphic
	 * 
	 * @param context
	 *            FacesContext
	 * @param component
	 *            UIComponent
	 * @param value
	 *            Object value
	 */
	public void imageValidator(FacesContext context, UIComponent component,
			Object value) {
		/*
		 * TODO this validator is not invoked from primefaces upload file
		 * component
		 */
		try {
			int height = ImageIO.read(((UploadedFile) value).getInputstream())
					.getHeight();
			int width = ImageIO.read(((UploadedFile) value).getInputstream())
					.getWidth();
			System.out
					.println("image validator: w: " + width + " h: " + height);
			if ((width <= imageLimit) && (height <= imageLimit)) {
				((UIInput) component).setValid(false);
				String msg = "File was not uploaded: Graphic size is invalid: "
						+ width + " & " + height + " # "
						+ String.valueOf(imageLimit);
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, msg, null));
			}
		} catch (IOException e) {
			String msg = "IO Exception reading graphic width & height "
					+ e.getMessage();
			Logger.getLogger(getClass().getName()).severe(msg);
		}
	}

	/**
	 * Return localized label
	 * 
	 * @param value
	 * @return
	 */
	public String getLocalizedLabel(String value, String type) {
		try {
			if (type.equals("type")) {
				Ptype p = repository.findPtypeByLabel(value);
				if (localeBean.getSelectedLang().getCode().equals("ar")) {
					return p.getLabelar();
				}
				if (localeBean.getSelectedLang().getCode().equals("fr")) {
					return p.getLabelfr();
				}
				if (localeBean.getSelectedLang().getCode().equals("en")) {
					return p.getLabel();
				}
			}
			if (type.equals("category")) {
				Pcategory pc = (Pcategory) repository
						.findPcategoryByLabel(value);
				if (localeBean.getSelectedLang().getCode().equals("ar")) {
					return pc.getLabelar();
				}
				if (localeBean.getSelectedLang().getCode().equals("fr")) {
					return pc.getLabelfr();
				}
				if (localeBean.getSelectedLang().getCode().equals("en")) {
					return pc.getLabel();
				}
			}

		} catch (Exception ex) {
			String msg = ex.getCause().getMessage() + " " + ex.getMessage();
			Logger.getLogger(getClass().getName()).severe(msg);
			return null;
		}
		return null;
	}
} // END OF CLASS
