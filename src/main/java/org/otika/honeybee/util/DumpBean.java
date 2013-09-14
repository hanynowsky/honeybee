package org.otika.honeybee.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.enterprise.event.Observes;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.otika.honeybee.events.ContactEvent;
import org.otika.honeybee.events.PrescriptionPrintedEvent;
import org.otika.honeybee.events.RegistrationEvent;
import org.otika.honeybee.events.SigninEvent;
import org.otika.honeybee.events.SignoutEvent;
import org.otika.honeybee.model.Enduser;

/**
 * This EJB has a scheduled method that dumps the database once a day and other
 * methods that observe events
 * 
 * @author Hanine <hanynosky@gmail.com>
 * 
 */
@Singleton
@Startup
public class DumpBean {

	@Inject
	private UtilityBean utilityBean;
	@Inject
	private MailBean mailBean;
	@EJB
	private Repository repository;
	@Inject
	private RequestBean requestBean;
	// @Inject private UserManagerBean userManagerBean;
	@Inject
	private BundleBean bundleBean;
	@Inject
	private ContactBean contactBean;

	/**
	 * Default constructor.
	 */
	public DumpBean() {
		String s = System.getProperty("user.dir");
		System.out.println(s + " | Startup bean invoked");
	}

	@PostConstruct
	public void init() {
		// TODO
	}

	/**
	 * Scheduled method that executes periodically
	 * 
	 * @param t
	 *            Timer
	 */
	// @Schedule(second = "*/30", minute = "*", hour = "*", dayOfWeek = "*",
	// dayOfMonth = "*", month = "*", year = "*", info = "DataBaseDumpTimer")
	@Schedule(second = "11", minute = "06", hour = "16", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*", info = "DataBaseDumpTimer")
	private void scheduledTimeout(final Timer t) {
		/*
		 * Date date = new Date(); DateFormat timeFormat = new
		 * SimpleDateFormat("HH:mm"); DateFormat monthFormat = new
		 * SimpleDateFormat("MM"); DateFormat yearFormat = new
		 * SimpleDateFormat("yyyy"); DateFormat dayFormat = new
		 * SimpleDateFormat("dd");
		 * 
		 * String dateFormat = yearFormat.format(date) +
		 * monthFormat.format(date) + dayFormat.format(date);
		 */

		if (System.getProperty("user.home").contains("hanin")) {
			String dumpPath = System.getProperty("java.io.tmpdir")
					+ File.separator + "honeybee.sql";
			utilityBean.execBash("mkdir -v -p $HOME/app-root/data");

			utilityBean.execBash("mysqldump -v -u root -pmonsql honeybee > "
					+ dumpPath);
			try {
				System.out.println("Attempt to call zip file function");
				utilityBean.zipFile(new File(dumpPath)); 
				// Produces exception if we use faces Message
				System.out.println("zip file function called");

			} catch (Exception ex) {
				String msg = "Hanin: Exception handling dump file: "
						+ ex.getMessage();
				Logger.getLogger(getClass().getName()).severe(msg);
			}

			try {
				File tempFile = new File(dumpPath);
				File comFile = new File(dumpPath + ".zip");
				if (tempFile.exists()) {
					utilityBean
							.execBash("notify-send -u low -a HoneyBee -i emblem-default HoneyBee-DB-Dump-Done");
					utilityBean.execBash("mv " + dumpPath + ""
							+ " $HOME/app-root/data/");
					tempFile.delete();
				}
				if (comFile.exists()) {
					utilityBean.execBash("mv " + dumpPath + ".zip"
							+ " $HOME/app-root/data/");
				}
			} catch (Exception exp) {
				System.err.println(exp.getMessage());
			}

		} else {
			String username = System.getenv("OPENSHIFT_MYSQLDB_USERNAME");
			String pass = System.getenv("OPENSHIFT_MYSQLDB_PASSWORD");
			if (pass == null || pass.equals("")) {
				pass = "1GK_z_NqUW7g";
			}
			if (username == null || username.equals("")) {
				username = "adminwNJulYt";
			}

			try {

				String fileName = System.getProperty("java.io.tmpdir")
						+ File.separator + "honeybee.sql";
				Logger.getLogger(getClass().getName()).log(Level.INFO,
						"Attempt to dump DB : username & pass = {0} {1}",
						new Object[] { username, pass });
				utilityBean.execBash("mysqldump -h 127.7.193.130 -v -u "
						+ username + " -p" + pass + " honeybee >" + fileName);

				/* Send DB by email */
				Logger.getLogger(getClass().getName()).info(
						"Attempt to send DB email");
				mailBean.emailDatabase("opentika.contact@gmail.com",
						utilityBean.dumpFile(fileName));

				File tempFile = new File(fileName);
				if (tempFile.exists()) {
					System.out.println("Deleting DB temp file");
					tempFile.delete();
				}
				// TODO use SCP instead
				utilityBean.execBash("mv " + fileName + ".zip"
						+ " $HOME/app-root/data/");
			} catch (Exception ex) {
				String msg = "Exception handling dump file: " + ex.getMessage();
				Logger.getLogger(getClass().getName()).severe(msg);
			}
		}

		System.out.println("@Schedule called at: " + new java.util.Date());
		System.out.println(t.getSchedule());
	}

	/**
	 * Observes an event
	 * 
	 * @param event
	 *            (PrescriptionPrintedEvent)
	 */
	public void watchPrintedPrescription(
			@Observes PrescriptionPrintedEvent event) {
		String msg = "Printing prescription number: " + event.getId();
		System.out.println("System output: " + msg);
		Logger.getLogger(getClass().getName()).log(Level.ALL, msg);
	}

	/**
	 * 
	 * @param event
	 *            Signout
	 */
	public void watchSignout(@Observes SignoutEvent event) {
		requestBean.log("SIGNOUT"); // The bean is decorated
		String msg = "Sign out successful: " + event.getEmail();
		System.out.println("System output: " + msg);
		Logger.getLogger(getClass().getName()).log(Level.ALL, msg);
	}

	/**
	 * 
	 * @param event
	 *            Sign-in
	 */
	public void watchSignin(@Observes SigninEvent event) {
		requestBean.log("SIGNIN"); // The bean is decorated
		String msg = "Sign in successful: " + event.getEmail();
		System.out.println("System output: " + msg);
		// TODO set redirection to referer here instead of login method
		Logger.getLogger(getClass().getName()).info(msg);
	}

	/**
	 * Observer for the end user registration
	 * 
	 * @param event
	 *            Registration event
	 */
	public void watchRegistration(@Observes RegistrationEvent event) {
		try {
			String msg = bundleBean.i18n("registration_successful") + ": "
					+ event.getEnduser().getEmail();
			System.out.println("System output: " + msg);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(msg));
			Logger.getLogger(getClass().getName()).log(Level.ALL, msg);
			Enduser user = event.getEnduser();
			System.out.println("Sending mail to : " + user.getEmail());
			String uPassword;
			if (UserManagerBean.getOriginalPassword() == null) {
				uPassword = utilityBean.getUnhashedPassword();
			} else {
				uPassword = UserManagerBean.getOriginalPassword();
			}
			System.out.println("With original password : " + uPassword);
			if (utilityBean.hasInternet()) {
				mailBean.deliverEmail(user.getEmail(), user.getName(),
						user.getEmail(), uPassword, false, user.getUserkey());
			} else {
				System.out.println("No Internet. Mail not delivered.");
				utilityBean.showMessage("warn", "No internet connectivity", "");
			}

		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage(), ex);
			System.out.println("Watch Registration Observer: Exception : "
					+ ex.getMessage() + " | " + ex.getCause());
		}
	}

	/**
	 * Observes Contact Event and sends the contact email
	 * 
	 * @param event
	 */
	public void watchContact(@Observes ContactEvent event) {
		try {
			System.out.println("Triggering Watch contact");
			if (utilityBean.hasInternet()) {
				System.out.println("Internet is on");
				System.out.println("Contact full name: "
						+ contactBean.getFullname());
				mailBean.contactEmail(contactBean.getEmail(),
						contactBean.getFullname(), contactBean.getContent(),
						contactBean.isValid(), contactBean.getAttachment(),
						contactBean.getSubject());
				System.out.println("contactEmail() invoked!");
				contactBean.resetValues();
			} else {
				System.out.println("No Internet. Contact Mail not delivered.");
				utilityBean.showMessage("warn", "No internet connectivity", "");
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage(), ex);
			System.err.println("Watch Contact Observer: Exception : "
					+ ex.getMessage() + " | " + ex.getCause());
			System.err.println(ex);
		}
	}
} // END OF CLASS
