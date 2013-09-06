package org.otika.honeybee.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.otika.honeybee.model.Configuration;

@Named(value = "mailBean")
@RequestScoped
public class MailBean {

	@EJB
	private Repository repository;
	final String host = "smtp.gmail.com";
	final String from = "opentika.contact@gmail.com";
	final private String username = "opentika.contact@gmail.com";
	private String password = ""; // TODO encode password
	final String port = "465";
	private URL pic;
	private final String fileName = "/opentika-logo-small.png";
	private Properties props;
	private Session session;
	@Inject
	private BundleBean bundleBean;
	private ClassLoader classLoader;
	private DataSource fds;
	private URLDataSource uds;
	private static final Logger LOG = Logger
			.getLogger(MailBean.class.getName());

	// TODO get mail session from container subsystem as a @Resource
	// @Resource
	// private SessionContext sessionContext;

	@PostConstruct
	public void init() {
		// Mail password
		try {
			Configuration c = repository.findAllConfigurationItems().get(0);
			password = c.getMailpass();
			System.out.println("assigning mail password: " + password);
		} catch (Exception ex) {
			String msg = "Exception assigning mail password " + ex.getMessage();
			System.err.println(msg);
			LOG.severe(msg);
		}
	}

	public MailBean() {

		// Set properties
		props = new Properties();
		props.put("mail.smtp.host", host); // HOST
		props.put("mail.debug", "false"); // TODO DEBUG
		props.put("mail.smtp.auth", "true"); // AUTH
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.socketFactory.port", port); // SSL PORT
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory"); // SSL

		// Get session
		session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Java Mail API
	 * 
	 * @param email
	 */
	public void sendMail(String email) {

		try {
			// Instantiate a message
			Message msg = new MimeMessage(session);
			Multipart mp = new MimeMultipart();
			MimeBodyPart part1 = new MimeBodyPart();
			MimeBodyPart part2 = new MimeBodyPart();
			MimeBodyPart part3 = new MimeBodyPart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(
					"<h1>Welcome</h1><p>Please browse our website for further information.</p>"
							+ "<img src='cid:opentika_logo'>" + "<br />"
							+ "<b>OpenTika @2013</b>", "text/html");
			part1.setText("OpenTika thanks you for trusting us.");
			part2.setText("<a href='http://www.opentika.com'>http://www.opentika.com<a>");

			// // Create a new part for the attached image and set the CID image
			// identifier

			classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = this.getClass().getClassLoader();
				if (classLoader == null) {
					System.out.println("CLassLoader is still null!");
				}
			}
			try {
				pic = new URL(
						"http://www.viridian.com/App_Themes/Corporate/images/newhp/background_hp_findrate.jpg");
				pic.getHost();
			} catch (MalformedURLException e) {
				System.err.println(e);
				// TODO Add Error Faces message here
			}
			MimeBodyPart imagePart = new MimeBodyPart();

			if (classLoader == null) {
				fds = new URLDataSource(getClass().getResource(fileName));
			} else {
				fds = new URLDataSource(classLoader.getResource(fileName));
			}

			imagePart.setDataHandler(new DataHandler(fds));
			imagePart.setHeader("Content-ID", "opentika_logo");

			part3.attachFile(fileName);

			mp.addBodyPart(imagePart);
			mp.addBodyPart(htmlPart);
			mp.addBodyPart(part1);
			mp.addBodyPart(part2);

			// Set the FROM message
			msg.setFrom(new InternetAddress(from));

			// The recipients can be more than one so we use an array but you
			// can use 'new InternetAddress(to)' for only one address.
			InternetAddress[] address = { new InternetAddress(email) };
			msg.setRecipients(Message.RecipientType.TO, address);

			// Set the message subject and date we sent it.
			msg.setSubject("OpenTika new subscription");
			msg.setSentDate(new Date());

			// Set message content
			// msg.setText("This is the text for this simple demo using JavaMail.");

			msg.setContent(mp, "text/html");

			// Send the message
			Transport.send(msg);
			FacesContext.getCurrentInstance().addMessage(email,
					new FacesMessage("Mail sent to: " + email));
		} catch (MessagingException | IOException e) {
			System.err.println("MAIL DELIVERY FAILURE: " + e);
		}

	} // END OF METHOD

	/**
	 * Apache Commons API. Method to send an email
	 * 
	 * @param mail
	 *            (member email)
	 * @param name
	 *            (member name)
	 * @param pseudo
	 *            (user name)
	 * @param upassword
	 *            (User password)
	 * @param attach
	 *            (boolean - Attach a file)
	 * @param uuid
	 *            (user key)
	 */
	@Asynchronous
	public Future<String> deliverEmail(String mail, String name, String pseudo,
			String upassword, boolean attach, String uuid) {

		String status;

		// Check if new member has been persisted to DB
		long i = repository.findByEmail(mail).getId();
		boolean b;
		// must ensure : !sessionContext.wasCancelCalled()
		b = i >= 0;
		String msg = "There is no saved member with the email: " + mail;
		if (b) {
			// Class Loader
			classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = this.getClass().getClassLoader();
				if (classLoader == null) {
					System.out.println("CLassLoader is still null!");
				}
			}

			if (classLoader == null) {
				uds = new URLDataSource(getClass().getResource(fileName));
			} else {
				uds = new URLDataSource(classLoader.getResource(fileName));
			}

			// Attachments
			EmailAttachment attachment = new EmailAttachment();

			try {
				URL url = new URL(
						"https://update.cabinetoffice.gov.uk/sites/default/files/resources/Open_Source_Options_v2_0.pdf");
				attachment.setURL(url);
				attachment.setDisposition(EmailAttachment.ATTACHMENT);
				attachment.setDescription("Attachment");
				attachment.setName("HoneyBee_Attachment");
			} catch (MalformedURLException e1) {
				System.out.println("Favicon url failure..." + e1.getCause());
				LOG.log(Level.ALL, e1.getMessage(), e1);
			}

			HtmlEmail email = new HtmlEmail();

			try {
				String content = "Thank you for subscribing to HoneyBee.<br /> Your <em><b>username</b></em> is: "
						+ pseudo
						+ " and <em><b>password</b></em>: "
						+ upassword
						+ " <br /> <em><b>Your activation code is: </b></em>: "
						+ uuid
						+ "<br />"
						+ "<a href='https://honeybee-otika.rhcloud.com/misc/activation.xhtml?key="
						+ uuid
						+ "'"
						+ " target='_blank'>Click here to activate your account.</a>";
				URL logoUrl = new URL(
						"https://dl.dropbox.com/u/67261634/opentika-logo-small.png");
				String cid = email.embed(logoUrl, "opentika_logo");
				email.setHtmlMsg("<html><img src=\"cid:"
						+ cid
						+ "\"><h1>Opentika</h1><h2>Open Source Based IT Engineering</h2><br /><a href='http://www.opentika.com'>http://www.opentika.com</a>"
						+ "<p>" + "" + content + "" + "</p>" + "</html>");
				email.setSentDate(new Date());
				email.setMailSession(session);
				email.addTo(mail, pseudo);
				email.setFrom(from, "OpenTika");
				email.setSubject("New Subscription to HoneyBee");
				if (attach) {
					email.attach(attachment);
				}
				email.attach(uds, "image/png", "opentika.png", "O_Logo");
				email.send();
				FacesMessage successMessage = new FacesMessage("Mail sent to "
						+ mail);
				successMessage.setDetail("");
				successMessage.setSeverity(FacesMessage.SEVERITY_INFO);
				FacesContext.getCurrentInstance().addMessage(null,
						successMessage);
				status = successMessage.getSummary();
			} catch (MalformedURLException | EmailException e) {
				System.err.println("Email Delivery Failure - Apache C : " + e);
				FacesMessage errorMessage = new FacesMessage(
						"FAILURE to send Mail to " + mail);
				errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				errorMessage.setDetail("");
				FacesContext.getCurrentInstance()
						.addMessage(null, errorMessage);
				status = errorMessage.getSummary();
			}
		} else {
			FacesMessage fmsg = new FacesMessage(msg, null);
			FacesContext.getCurrentInstance().addMessage(null, fmsg);
			status = fmsg.getSummary();
		}
		return new AsyncResult<>(status);
	}

	/**
	 * Method that send an email with a new password
	 * 
	 * @param mail
	 *            Email address
	 * @param pass
	 *            User password
	 * @return null Asynchronous result
	 */
	@Asynchronous
	public Future<String> simpleMail(String mail, String pass) {
		HtmlEmail email = new HtmlEmail();
		String html = "<p>Your password is </p>" + "<strong>" + pass
				+ "</strong>";
		String html1 = "<p>Login here: </p>"
				+ "<strong>http://honeybee-otika.rhcloud.com/redirect.html</strong>"
				+ "<p><em>You can change your password by editing your profile.</em></p>";
		String img = "<img src=\"https://dl.dropbox.com/u/67261634/opentika-logo-small.png\" />";
		try {
			email.addTo(mail);
			email.setFrom("opentika.contact@gmail.com");
			String prMessage = bundleBean.i18n("password_recovery");
			email.setSubject(prMessage);
			email.setSentDate(new Date());
			email.setHtmlMsg(img + html + html1);
			email.setMailSession(session);
			email.send();
			FacesMessage successMessage = new FacesMessage("Password sent to "
					+ mail);
			successMessage.setDetail("");
			successMessage.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, successMessage);
		} catch (EmailException e) {
			FacesMessage errorMessage = new FacesMessage(
					"FAILURE to send Mail to " + mail);
			errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			errorMessage.setDetail("");
			FacesContext.getCurrentInstance().addMessage(null, errorMessage);
			System.out.println("Simple Email exception!");
			System.out.println(e);
		}
		return new AsyncResult<>(null);
	}

	/**
	 * Method that send an email with a file attachment
	 * 
	 * @param email
	 *            receipient email
	 * @param db
	 *            File
	 * @return null Asynchronous result
	 */
	@Asynchronous
	public Future<String> emailDatabase(String email, File db) {
		String status;
		if (FacesContext.getCurrentInstance().getExternalContext().getContext() == null) {
			status = "Context Session Canceled! Mail not sent.";
			System.out.println("Cleaning up! Emailing User not possible!");
		} else {
			HtmlEmail mail = new HtmlEmail();
			String htmlmail = "<h1>HoneyBee Database Dump</h1>"
					+ "<p>Please find attached HoneyBee database dump.</p>";
			try {
				mail.addTo(email);
				mail.setFrom("opentika.contact@gmail.com");
				mail.setSubject("HoneyBee Database Dump");
				mail.setSentDate(new Date());
				mail.setHtmlMsg(htmlmail);

				if (db != null) {
					System.out.println("Attaching File to email "
							+ db.getName());
					mail.attach(db);
					System.out
							.println("Attached File to email " + db.getName());
				}

				mail.setMailSession(session);
				System.out.println("Sending mail ");
				mail.send();
				System.out.println("Mail probably sent!");
				FacesMessage successMessage = new FacesMessage(
						"Database recovery ok! " + email);
				successMessage.setDetail("");
				successMessage.setSeverity(FacesMessage.SEVERITY_INFO);
				FacesContext.getCurrentInstance().addMessage(null,
						successMessage);
			} catch (EmailException e) {
				FacesMessage errorMessage = new FacesMessage(
						"FAILURE to send Mail to " + email);
				errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				errorMessage.setDetail("");
				FacesContext.getCurrentInstance()
						.addMessage(null, errorMessage);
				System.out.println("DB Email exception!");
				System.out.println(e);
			}
			status = "db_emailed";
			System.out.println("Email being sent to " + email);
		}
		return new AsyncResult<>(status);
	}

	/**
	 * Contact Email
	 * 
	 * @param email
	 *            Sender email
	 * @param fullname
	 *            Sender Full name
	 * @param content
	 *            Contact email content
	 * @param valid
	 *            validity of captcha
	 * @param attachment
	 *            Attached file
	 * @return AsyncResult
	 */
	@Asynchronous
	public Future<String> contactEmail(String email, String fullname,
			String content, boolean valid, File attachment, String subject) {
		System.out.println("Triggering Contact Email");
		String status;
		if (FacesContext.getCurrentInstance().getExternalContext().getContext() == null) {
			status = "Context Session Canceled! Mail not sent.";
			System.out.println("Cleaning up! Emailing User not possible!");
		} else {
			HtmlEmail mail = new HtmlEmail();
			String htmlmail = "<h3>HoneyBee: Contact Email</h3>" + "" + content;
			try {
				System.out.println("Appending contact Email properties");
				mail.addTo("opentika.contact@gmail.com");
				mail.setFrom(email);
				List<InternetAddress> cia = new ArrayList<>();
				cia.add(0, new InternetAddress("hanynowsky@gmail.com"));
				mail.setCc(cia);
				mail.setSubject("Honeybee Contact: " + subject);
				mail.setSentDate(new Date());
				mail.setHtmlMsg(htmlmail);
				if (attachment != null) {
					System.out.println("Trying to attach file: "
							+ attachment.getName());
					mail.attach(attachment);
				} else {
					System.out.println("Attachment is null!");
				}
				mail.setMailSession(session);
				mail.send();
				if (valid) {
					FacesMessage successMessage = new FacesMessage(
							"Mail sent from " + email);
					successMessage.setDetail("");
					successMessage.setSeverity(FacesMessage.SEVERITY_INFO);
					FacesContext.getCurrentInstance().addMessage(null,
							successMessage);
				} else {
					FacesMessage successMessage = new FacesMessage(
							"Captcha not valid. Mail not sent from " + email);
					successMessage.setDetail("");
					successMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null,
							successMessage);
				}
			} catch (EmailException | AddressException e) {
				FacesMessage errorMessage = new FacesMessage(
						"FAILURE to send Contact Mail");
				errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				errorMessage.setDetail("");
				FacesContext.getCurrentInstance()
						.addMessage(null, errorMessage);
				System.out.println("Contact Email exception!");
				System.out.println(e);
			}
			status = "/misc/contact.xhtml?faces-redirect=false";
			System.out.println("Email being sent to opentika from " + email);
		}
		return new AsyncResult<>(status);
	}
} // END OF CLASS
