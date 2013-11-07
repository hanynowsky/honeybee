package org.otika.honeybee.util;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.Serializable;
import java.util.concurrent.Future;

@Named("mailServerBean")
@SessionScoped
public class MailServerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5290656716306730424L;
	private String to;
	private String from;
	private String subject;
	private String body;
	@Resource(mappedName = "java:jboss/mail/otikamail")
	private Session mailServerSession;

	public MailServerBean() {
		// TODO set session protocol to SSL/TLC for Google mail
	}

	/**
	 * Method to send a mail using JBoss AS 7 server mail session
	 */
	@Asynchronous
	public Future<String> send() {
		try {
			Message message = new MimeMessage(mailServerSession);
			message.setFrom(new InternetAddress(from));
			Address toAddress = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, toAddress);
			message.setSubject(subject);
			message.setContent(body, "text/html");
			Transport.send(message);
			System.out.println("Email is probably sent");
		} catch (Exception ex) {
			String emsg = "Exception in Sending message " + ex.getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName())
					.error(emsg);
		}
		return new AsyncResult<>("mail_sent");
	}

	public String sendEmail(){
		System.out.println("Trying to send email with JBoss AS 7 mail session");
		setTo("hanynowsky@gmail.com");
		setFrom("contact.opentika@gmail.com");
		setSubject("Mail from Java Mail JBoss AS 7.1.1");
		setBody("<strong>Hello Hanyn</strong>");
		send();
		return null;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

}
