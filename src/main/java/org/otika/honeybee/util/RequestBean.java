package org.otika.honeybee.util;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

@Model
public class RequestBean implements Service {

	
	private String hello;
	@Inject
	private UtilityBean utilityBean;

	/**
	 * @return the hello
	 */
	public String getHello() {
		return hello;
	}

	/**
	 * @param hello
	 *            the hello to set
	 */
	public void setHello(String hello) {
		this.hello = hello;
	}

	public RequestBean() {

	}

	/**
	 * This method is annotated with @Audit qualifier which means it is the
	 * target of Audit Interceptor Class
	 * This method is intercepted whenever it is invoked. We can thus, apply
	 * actions before and after.
	 * @see AuditInterceptor
	 * @return Hello_STRING
	 */
	@Audit
	public String showHello() {
		hello = "HoneyBee";
		utilityBean.showMessage("info", hello, "--");
		return hello;
	}

	/**
	 * This implemented method (Implemented from Service interface) is called
	 * from Service Decorator instead
	 */
	@Override
	public String message(String msg) {
		System.out.println("Calling RequesBean message(): "+msg);
		utilityBean.showMessage("info", "Calling RequesBean message(): "+msg, "");
		return msg.toUpperCase();
	}

	@Override
	public void log(String info) {
		// TODO Store log information in database	
	}

}
