package org.otika.honeybee.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

@Model
public class RemoteDumpBean {

	/**
	 * Values of Password and Username must be initiated as they're used at startup
	 */
	private String username = "adminQTnK1ed"; 
	private String password = "dY7YPB_VVzgR";
	private String host = "127.7.193.130";
	private String recipient = "opentika.contact@gmail.com";
	private String db = "honeybee";
	@Inject
	private UtilityBean utilityBean;
	@Inject
	private MailBean mailBean;

	public RemoteDumpBean() {

	}

	/**
	 * Dump DB File and send it by email
	 * 
	 * @return null
	 */
	public String dumpFile() {
		try {
			Date date = new Date();
			DateFormat monthFormat = new SimpleDateFormat("MM");
			DateFormat yearFormat = new SimpleDateFormat("yyyy");
			DateFormat dayFormat = new SimpleDateFormat("dd");
			String dateFormat = yearFormat.format(date)
					+ monthFormat.format(date) + dayFormat.format(date);
			String fileName = System.getProperty("java.io.tmpdir")
					+ File.separator + "honeybeedump" + dateFormat + ".sql";
			System.out.println("Attempt to dump DB");
			utilityBean.execBash("mysqldump -h " + host + " -v -u " + username
					+ " -p" + password + " " + db + " >" + fileName);
			utilityBean.showMessage("info", "Dumped Raw SQL DB", "");

			/* Send DB by email */

			if (utilityBean.hasInternet()) {
				System.out
						.println("We have internet access: Attempt to email DB");
				Logger.getLogger(getClass().getName()).info(
						"Attempt to send DB email");
				mailBean.emailDatabase(recipient,
						utilityBean.dumpFile(fileName));

			} else {
				Logger.getLogger(getClass().getName()).info(
						"No Internet access: Dumped DB was not emailed");
			}
			File tempFile = new File(fileName);
			if (tempFile.exists()) {
				if (System.getProperty("user.home").contains("hanin")) {
					File cFile = utilityBean.dumpFile(fileName);
					utilityBean.execBash("mv " + cFile
							+ " $HOME/app-root/data/");
				}
				System.out.println("Deleting DB temp file");
				tempFile.delete();
			}
			// TODO use SCP command instead
			File zFile = new File(fileName + ".zip");
			if (tempFile.exists()) {
				utilityBean.execBash("mkdir -v -p $HOME/app-root/data");
				utilityBean.execBash("mv " + zFile + " $HOME/app-root/data/");
			}
		} catch (Exception ex) {
			String msg = "Exception handling dump file: " + ex.getMessage();
			Logger.getLogger(getClass().getName()).severe(msg);
		}
		return null;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the recipient
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient
	 *            the recipient to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return the database
	 */
	public String getDb() {
		return db;
	}

	/**
	 * @param database
	 *            the database to set
	 */
	public void setDb(String db) {
		this.db = db;
	}

}
