package org.otika.honeybee.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * <p>
 * This bean must be request scoped - Otherwise you would get an EL illegal
 * state when using an El expression in a javascript function and changing
 * locale
 * </p>
 * 
 * @author Hanine.H <hanynowsky@gmail.com>
 * 
 */
@Model
public class UtilityBean implements Serializable {

	private static final long serialVersionUID = -5489916236604357383L;
	private ExternalContext externalContext;
	private FacesContext facesContext;
	private StreamedContent image;
	@EJB
	private Repository repository;
	private String viewPath;
	private Long storeId;

	private BundleBean bundle;
	public static ProcessBuilder pb;

	/**
	 * Constructor
	 */
	public UtilityBean() {

	}

	@PostConstruct
	public void init() {
		bundle = new BundleBean();
		facesContext = FacesContext.getCurrentInstance();
	}

	public void dummyMethod() {

	}

	/**
	 * Method to convert an array of bytes to a file
	 */
	public StreamedContent convertBytesToFile(byte[] blob) {

		try {
			InputStream is = new ByteArrayInputStream(blob);
			StreamedContent graphic = new DefaultStreamedContent(is,
					"image/png");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(graphic.getContentType()));
			return graphic;
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Image retrieval problem ", null));
			Logger.getLogger(UtilityBean.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	} // END OF METHOD

	/**
	 * Utility method to show a JSF message
	 * 
	 * @param severity
	 *            <p>
	 *            must be a string in capital letters (e.g. WARN)
	 *            </p>
	 * @param summary
	 * @param detail
	 */
	public void showMessage(String severity, String summary, String detail) {
		FacesMessage.Severity SEV = FacesMessage.SEVERITY_INFO;
		FacesMessage.Severity INFO = FacesMessage.SEVERITY_INFO;
		FacesMessage.Severity WARN = FacesMessage.SEVERITY_WARN;
		FacesMessage.Severity FATAL = FacesMessage.SEVERITY_FATAL;
		FacesMessage.Severity ERROR = FacesMessage.SEVERITY_ERROR;
		if (severity.equals("INFO")) {
			SEV = INFO;
		} else if (severity.equals("WARN")) {
			SEV = WARN;
		} else if (severity.equals("ERROR")) {
			SEV = ERROR;
		} else if (severity.equals("FATAL")) {
			SEV = FATAL;
		} else {
			SEV = INFO;
		}
		FacesMessage message = new FacesMessage(SEV, summary, detail);
		facesContext.addMessage(null, message);
	}

	// GETTERS & SETTERS

	public StreamedContent getImage() {
		return image;
	}

	public void setImage(StreamedContent image) {
		this.image = image;
	}

	/**
	 * @return the externalContext
	 */
	public ExternalContext getExternalContext() {
		return externalContext;
	}

	/**
	 * @return the facesContext
	 */
	public FacesContext getFacesContext() {
		return facesContext;
	}

	public String getViewName() {

		return facesContext.getViewRoot().getViewId();
	}

	public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	/*
	 * Utilities
	 */
	/**
	 * Method that fetches an Object from database by ID
	 * 
	 * @param id_as_Entity_ID
	 * @param Object_o_is_any_object_of_the_same_type_as_T
	 * @return
	 */
	public <T> T getRepoObject(Long id, Object type) {
		return repository.findById((long) id, type);
	}

	/**
	 * 
	 * @param e_Any_instance_of_T
	 * @return
	 */
	public <T> List<T> getEntityItems(Object type) {
		return repository.findAll(type);
	} // END OF METHOD

	/**
	 * 
	 * @param input
	 * @return localized_name
	 */
	public String IngformName(String input) {
		String name = "";
		if (!input.equals("")) {
			name = bundle.i18n(input);
		}
		return name;
	}

	/**
	 * Executes a bash command -Applicable only in UNIX
	 * 
	 * @param command
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void execBash(String command) {
		try {
			if (System.getProperty("os.name").contains("inux")) {
				java.util.List<String> commands = new ArrayList<String>();
				commands.add("bash"); // or /bin/cat
				commands.add("-c");
				// commands.add("echo Han | grep [^*]");
				// commands.add("notify-send Bye $USER -i face-laugh -t 600 -u low -a honeybee");
				commands.add(command);
				System.out.println("Executing: " + commands);

				// Run macro on target
				pb = new ProcessBuilder(commands);
				pb.directory(new File(System.getProperty("user.home")));
				pb.redirectErrorStream(true);
				Process process = pb.start();

				// Read output
				StringBuilder sbout = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				String line = null, previous = null;
				while ((line = br.readLine()) != null) {
					if (!line.equals(previous)) {
						previous = line;
						sbout.append(line).append('\n');
						System.out.println(line);
					}
				}
				// Check result
				if (process.waitFor() == 0) { // Normal value is 0
					System.out.println("Success!");
					System.out.println("Here is the Bash-return: "
							+ sbout.toString());
				} else {
					System.err.println(commands);
					System.err.println(sbout.toString());
					System.exit(1);
				}
				// System.exit(0);
			}
		} catch (Exception ex) {
			System.out.println("Bash command exception!: " + ex);
			Logger.getLogger(UtilityBean.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	} // END OF METHOD	

} // END OF CLASS
