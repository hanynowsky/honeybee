package org.otika.honeybee.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.otika.honeybee.events.ContactEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class ContactBean implements Serializable {

	private static final long serialVersionUID = -323869350566070872L;
	private static final String EMAIL_PATTERN = "[a-z0-9!#$%&’*+/=?^_‘{|}~-]+(?:\\."
			+ "[a-z0-9!#$%&’*+/=?^_‘{|}~-]+)*"
			+ "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
	private String email;
	private String subject;
	private String content;
	private String fullname;
	private String captcha;
	private boolean valid;
	private File attachment = null;
	private String formId;
	@Inject
	private Event<ContactEvent> contactEvent;
	@Inject
	private UtilityBean utilityBean;
	@Inject
	private BundleBean bundleBean;

	public ContactBean() {

	}

	/**
	 * Validator for contact form captcha
	 * 
	 * @param context
	 *            FacesContext
	 * @param component
	 *            UIComponent
	 * @param value
	 *            Object value
	 * @throws ValidatorException
	 */
	public void validateCaptcha(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (!String.valueOf(value).equals("HoneyBee")) {
			String wrongCaptcha = bundleBean.i18n("wrong_captcha");
			if (wrongCaptcha == null) {
				wrongCaptcha = "Captcha is wrong!";
			}
			FacesMessage message = new FacesMessage(wrongCaptcha);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(null, message);
			valid = false;
			((UIInput) component).setValid(false);
		} else {
			valid = true;
			System.out.println("Captcha is valid");
		}
	}

	/**
	 * File upload listener
	 * 
	 * @param event
	 *            File upload event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		try {
			UploadedFile uploadedFile = event.getFile();
			attachment = new File(System.getProperty("java.io.tmpdir")
					+ File.separator + event.getFile().getFileName());
			if (!attachment.exists()) {
				attachment.createNewFile();
			}
			OutputStream output = new FileOutputStream(attachment);
			output.write(uploadedFile.getContents());
			uploadedFile.getInputstream().close();
			output.flush();
			output.close(); // TODO bug?
			String msg = "Uploaded File: " + attachment.getName();
			utilityBean.showMessage("info", msg, "");
			Logger.getLogger(getClass().getName()).info(msg);
		} catch (Exception ex) {
			System.err.println("Exception handling file upload");
			Logger.getLogger(getClass().getName()).severe(ex.getMessage());
		}
	}

	/**
	 * Dummy method
	 * 
	 * @return
	 */
	public String dummyAction() {
		System.out.println("Dummy Action");
		System.out.println("Email: " + email);
		System.out.println("Content: " + content);
		System.out.println("Fullname: " + fullname);
		System.out.println("Captcha: " + captcha);
		System.out.println("Valid: " + valid);
		System.out.println("Form ID: " + formId);
		contactEvent.fire(new ContactEvent());
		listener(formId); // TODO has no effect
		return "/misc/contact.xhtml?faces-redirect=false";
	}

	/**
	 * Listener that resets a form
	 * 
	 * @param id
	 */
	public void listener(String id) {
		RequestContext context = RequestContext.getCurrentInstance();
		context.reset(id);
	}

	/**
	 * @return the fullname
	 */
	@NotNull
	public String getFullname() {
		return fullname;
	}

	/**
	 * @param fullname
	 *            the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * @return the email
	 */
	@Pattern(regexp = EMAIL_PATTERN, message = "Invalid email / Email non valide")
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@NotNull
	@Size(max = 60)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the content
	 */
	@NotNull
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	@NotNull
	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public File getAttachment() {
		return attachment;
	}

	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

}
