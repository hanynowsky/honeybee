package org.otika.honeybee.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
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

import org.otika.honeybee.events.PrescriptionPrintedEvent;
import org.otika.honeybee.model.Author;
import org.otika.honeybee.model.Complement;
import org.otika.honeybee.model.Defect;
import org.otika.honeybee.model.Ingredient;
import org.otika.honeybee.model.Prescription;
import org.otika.honeybee.model.Virtue;
import org.otika.honeybee.model.Witness;
import org.otika.honeybee.util.DownloadFileBean;
import org.otika.honeybee.util.UtilityBean;

/**
 * Backing bean for Prescription entities.
 * <p>
 * This class provides CRUD functionality for all Prescription entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class PrescriptionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	@Inject
	private UtilityBean utilityBean;
	@Inject
	private DownloadFileBean downloadFileBean;
	@Inject
	private Event<PrescriptionPrintedEvent> prescriptionPrintedEvent;

	/*
	 * Support creating and retrieving Prescription entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Prescription prescription;

	public Prescription getPrescription() {
		return this.prescription;
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
			this.prescription = this.example;
		} else {
			this.prescription = findById(getId());
		}
	}

	public Prescription findById(Long id) {

		return this.entityManager.find(Prescription.class, id);
	}

	/*
	 * Support updating and deleting Prescription entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.prescription);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.prescription);
				return "view?faces-redirect=true&id="
						+ this.prescription.getId();
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
	 * Support searching Prescription entities with pagination
	 */

	private int page;
	private long count;
	private List<Prescription> pageItems;

	private Prescription example = new Prescription();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Prescription getExample() {
		return this.example;
	}

	public void setExample(Prescription example) {
		this.example = example;
	}

	public void search() {
		this.page = 0;
	}

	public void paginate(AjaxBehaviorEvent evt) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Prescription> root = countCriteria.from(Prescription.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Prescription> criteria = builder
				.createQuery(Prescription.class);
		root = criteria.from(Prescription.class);
		TypedQuery<Prescription> query = this.entityManager
				.createQuery(criteria.select(root).where(
						getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Prescription> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Author author = this.example.getAuthor();
		if (author != null) {
			predicatesList.add(builder.equal(root.get("author"), author));
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
		String preparation = this.example.getPreparation();
		if (preparation != null && !"".equals(preparation)) {
			predicatesList.add(builder.like(root.<String> get("preparation"),
					'%' + preparation + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Prescription> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Prescription entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<Prescription> getAll() {

		CriteriaQuery<Prescription> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Prescription.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Prescription.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final PrescriptionBean ejbProxy = this.sessionContext
				.getBusinessObject(PrescriptionBean.class);

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

				return String.valueOf(((Prescription) value).getId());
			}
		};
	}

	/**
	 * Returns number of positive witnesses for a given prescription
	 * 
	 * @param p
	 *            Prescription
	 * @return counter Number of positive witnesses
	 */
	public int getPostiveWitnessesCount(Prescription p) {
		int counter = 0;
		try {
			Set<Witness> witnesses;
			witnesses = p.getWitnesses();
			if (witnesses.size() > 0) {
				for (Witness w : witnesses) {
					if (w.isResult()) {
						counter++;
					}
				}
			}
			return counter;
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).severe(
					"Failure getting count for prescription witnesses");
		}
		return 0;
	}

	/**
	 * Returns number of negative witnesses for a given prescription
	 * 
	 * @param p
	 *            <em>Prescription</em>
	 * @return counter
	 *         <p>
	 *         Number of negative witnesses
	 *         </p>
	 */
	public int getNegativeWitnessesCount(Prescription p) {
		int counter = 0;
		try {
			Set<Witness> witnesses;
			witnesses = p.getWitnesses();
			if (witnesses.size() > 0) {
				for (Witness w : witnesses) {
					if (!w.isResult()) {
						counter++;
					}
				}
			}
			return counter;
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).severe(
					"Failure getting count for prescription witnesses");
		}
		return 0;
	}

	/**
	 * Prints a prescription
	 */
	public void printPrescription(boolean writeCss, boolean openFox, boolean download) {
		// TODO re-factor this method using i-text library
		// Or create a temporary file instead of a physical file
		try {
			if (id != null) {
				Prescription prescription = findById(id);
				String authorName = prescription.getAuthor().getName();
				String p = System.getProperty("user.home");
				String path = p + File.separator + "honeybee" + File.separator
						+ "export" + File.separator;

				File presFile = new File(path + "presFile" + "_"
						+ prescription.getId() + ".html");
				new File(p + File.separator + "honeybee" + File.separator
						+ "export").mkdirs();
				presFile.createNewFile();

				InputStream is = getClass().getResourceAsStream("/css.css");

				if (writeCss) {
					File cssFile = new File(path + "css.css");
					cssFile.createNewFile();
					OutputStream output = new FileOutputStream(cssFile);
					int read;
					byte[] bytes = new byte[1024];
					while ((read = is.read(bytes)) != -1) {
						output.write(bytes, 0, read);
					}
					is.close();
					output.flush();
					output.close();
				}

				FileWriter fstream = new FileWriter(presFile, false);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("<!DOCTYPE HTML>");
				out.write("<html>");
				out.newLine();
				out.write("<head>");
				out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
				out.write("<title>");
				out.write(prescription.getTitle() + " : "
						+ prescription.getId());
				out.write("</title>");
				// out.write("<link href=\"css.css\" rel=\"stylesheet\" type=\"text/css\" />");
				out.write("<style type=\"text/css\">");
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				while ((br.read()) != -1) {
					sb.append(br.readLine());
				}
				out.write(sb.substring(0));
				out.write("</style>");
				out.append("</head>");
				out.append("<body>");
				out.append("<article>Powered by Honeybee: https://honeybee-otika.rhcloud.com</article>");
				out.newLine();
				out.write("<h1>");
				out.write("Prescription: " + prescription.getTitle() + " : "
						+ prescription.getId());
				out.write("</h1>");
				out.write("<table>");
				out.write("<tbody>");
				out.write("<tr>");
				out.write("<td><label>Author Name</label></td>");
				out.write("<td>" + authorName + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Title (Ar)</label></td>");
				out.write("<td>" + prescription.getTitlear() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Title (Fr)</label></td>");
				out.write("<td>" + prescription.getTitlefr() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Coefficient</label></td>");
				out.write("<td>" + prescription.getCoefficient() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Creation Date</label></td>");
				out.write("<td>" + prescription.getCreationdate() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Preparation</label></td>");
				out.write("<td>" + prescription.getPreparation() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Preparation (Ar)</label></td>");
				out.write("<td>" + prescription.getPreparationar() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Preparation (Fr)</label></td>");
				out.write("<td>" + prescription.getPreparationfr() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Treatment</label></td>");
				out.write("<td>" + prescription.getTreatment() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Treatment (Fr)</label></td>");
				out.write("<td>" + prescription.getTreatmentfr() + "</td>");
				out.write("</tr>");
				out.write("<tr>");
				out.write("<td><label>Treatment (Ar)</label></td>");
				out.write("<td>" + prescription.getTreatmentar() + "</td>");
				out.write("</tr>");
				out.write("</tbody>");
				out.write("</table>");

				// Virtues
				out.write("<h2>Virtues</h2>");
				out.write("<table>");
				out.write("<tbody>");
				out.write("<thead>");
				out.write("<tr>");
				out.write("<th>");
				out.write("Label(Ar)");
				out.write("</th>");
				out.write("<th>");
				out.write("Label");
				out.write("</th>");
				out.write("<th>");
				out.write("Label (Fr)");
				out.write("</th>");
				out.write("</tr>");
				out.write("</thead>");
				for (Virtue v : prescription.getVirtues()) {
					out.write("<tr>");
					out.write("<td>");
					out.write(v.getLabelar());
					out.write("</td>");
					out.write("<td>");
					out.write(v.getLabel());
					out.write("</td>");
					out.write("<td>");
					out.write(v.getLabelfr());
					out.write("</td>");
					out.write("</tr>");
				}
				out.write("</tbody>");
				out.write("</table>");

				// Defects
				out.write("<h2>Defects</h2>");
				out.write("<table>");
				out.write("<tbody>");
				out.write("<thead>");
				out.write("<tr>");
				out.write("<th>");
				out.write("Label Ar");
				out.write("</th>");
				out.write("<th>");
				out.write("Label");
				out.write("</th>");
				out.write("<th>");
				out.write("Label Fr");
				out.write("</th>");
				out.write("</tr>");
				out.write("</thead>");
				for (Defect d : prescription.getDefects()) {
					out.write("<tr>");
					out.write("<td>");
					out.write(d.getLabelar());
					out.write("</td>");
					out.write("<td>");
					out.write(d.getLabel());
					out.write("</td>");
					out.write("<td>");
					out.write(d.getLabelfr());
					out.write("</td>");
					out.write("</tr>");
				}
				out.write("</tbody>");
				out.write("</table>");

				// Ingredients
				out.write("<h2>Ingredients</h2>");
				out.write("<table>");
				out.write("<tbody>");
				out.write("<thead>");
				out.write("<tr>");
				out.write("<th>");
				out.write("Label Ar");
				out.write("</th>");
				out.write("<th>");
				out.write("Label");
				out.write("</th>");
				out.write("<th>");
				out.write("Label Fr");
				out.write("</th>");
				out.write("<th>");
				out.write("Form");
				out.write("</th>");
				out.write("<th>");
				out.write("Honey");
				out.write("</th>");
				out.write("<th>");
				out.write("Plant");
				out.write("</th>");
				out.write("<th>");
				out.write("Substance");
				out.write("</th>");
				out.write("</tr>");
				out.write("</thead>");
				for (Ingredient i : prescription.getIngredients()) {
					out.write("<tr>");
					out.write("<td>");
					out.write(i.getLabelar());
					out.write("</td>");
					out.write("<td>");
					out.write(i.getLabel());
					out.write("</td>");
					out.write("<td>");
					out.write(i.getLabelfr());
					out.write("</td>");
					out.write("<td>");
					out.write(i.getForm());
					out.write("</td>");
					String honey = (i.getHoney() != null ? i.getHoney()
							.getLabel() : "N/A");
					String plant = (i.getPlant() != null ? i.getPlant()
							.getLabel() : "N/A");
					String substance = (i.getSubstance() != null ? i
							.getSubstance().getLabel() : "N/A");
					out.write("<td>");
					out.write(honey);
					out.write("</td>");
					out.write("<td>");
					out.write(plant);
					out.write("</td>");
					out.write("<td>");
					out.write(substance);
					out.write("</td>");
					out.write("</tr>");
				}
				out.write("</tbody>");
				out.write("</table>");

				// Complements
				out.write("<h2>Complements</h2>");
				out.write("<table>");
				out.write("<tbody>");
				out.write("<thead>");
				out.write("<tr>");
				out.write("<th>");
				out.write("Label");
				out.write("</th>");
				out.write("<th>");
				out.write("Label Fr");
				out.write("</th>");
				out.write("<th>");
				out.write("Label Ar");
				out.write("</th>");
				out.write("<th>");
				out.write("Content");
				out.write("</th>");
				out.write("</tr>");
				out.write("</thead>");
				for (Complement c : prescription.getComplements()) {
					out.write("<tr>");
					out.write("<td>");
					out.write(c.getIngredient().getLabel());
					out.write("</td>");
					out.write("<td>");
					out.write(c.getIngredient().getLabelfr());
					out.write("</td>");
					out.write("<td>");
					out.write(c.getIngredient().getLabelar());
					out.write("</td>");
					out.write("<td>");
					out.write(c.getContent());
					out.write("</td>");
					out.write("</tr>");
				}
				out.write("</tbody>");
				out.write("</table>");

				// Witnesses
				out.write("<h2>Witnesses</h2>");
				out.write("<table>");
				out.write("<tbody>");
				out.write("<thead>");
				out.write("<tr>");
				out.write("<th>");
				out.write("Subject");
				out.write("</th>");
				out.write("<th>");
				out.write("Date");
				out.write("</th>");
				out.write("<th>");
				out.write("Positive");
				out.write("</th>");
				out.write("<th>");
				out.write("Comment");
				out.write("</th>");
				out.write("</tr>");
				out.write("</thead>");
				for (Witness w : prescription.getWitnesses()) {
					out.write("<tr>");
					out.write("<td>");
					out.write(w.getSubject());
					out.write("</td>");
					out.write("<td>");
					out.write(w.getCreationdate().toString());
					out.write("</td>");
					out.write("<td>");
					out.write(String.valueOf(w.isResult()));
					out.write("</td>");
					out.write("<td>");
					out.write(w.getComment());
					out.write("</td>");
					out.write("</tr>");
				}
				out.write("</tbody>");
				out.write("</table>");

				// -------------
				out.write("</body>");
				out.write("</html>");
				Logger.getLogger(getClass().getName()).info(
						"- presFile.html File Created in "
								+ presFile.getCanonicalPath() + " | Encoded: "
								+ fstream.getEncoding());
				out.close();

				// Fire Printed Prescription Event
				if (this.prescription.getId() != null) {
					prescriptionPrintedEvent.fire(new PrescriptionPrintedEvent(
							this.prescription.getId()));
				}

				// Faces Message
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage("Prescription File Created in "
								+ presFile.getCanonicalPath()));

				// Download the file
				// TODO this method prevents Faces Messages from showing at the
				// current view, and instead invoked after a redirect
				if (download){
				downloadFileBean.downloadFile(presFile);
				}
				
				// Open created file in FireFox in Ubuntu Linux
				if (System.getenv("DESKTOP_SESSION") != null && openFox) {
					utilityBean.execBash("firefox -new-window "
							+ presFile.getCanonicalPath());
				}
			}

		} catch (Exception ex) {
			utilityBean.showMessage("ERROR", "Error printing the file", "");
			System.out.println(ex + " Error printing the file");
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					ex.getMessage(), ex);
		}
	}

	/**
	 * Faces Method: Print prescription
	 * */
	public String printPrescription() {
		printPrescription(false, false, false);
		// return "/prescription/view?id=" + this.prescription.getId();
		return null;
	}

	/***
	 * Test Method
	 * 
	 * @return null
	 */
	public String testMethod() {
		utilityBean.showMessage("info", "Prescription exported", "");
		return null;
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Prescription add = new Prescription();

	public Prescription getAdd() {
		return this.add;
	}

	public Prescription getAdded() {
		Prescription added = this.add;
		this.add = new Prescription();
		return added;
	}
}