package org.otika.honeybee.util;

import java.util.Date;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.otika.honeybee.model.Registery;

/**
 * The injected interface Service is implemented by RequestBean and this
 * decorator. We used an abstract class instead of a concrete class in order not
 * to implement all the methods from the interface
 * 
 * @author Hanine H.A.M <hanynowsky@gmail.com>
 * 
 */
@Decorator
public abstract class ServiceDecorator implements Service {

	@Inject
	@Delegate
	@Any
	Service service;
	@Inject
	private UtilityBean utilityBean;
	@Inject
	private UserManagerBean userManagerBean;
	@Inject
	private InfoBean infoBean;

	@PersistenceContext
	EntityManager em;

	public ServiceDecorator() {
		// TODO
	}

	@Override
	public String message(String msg) {
		System.out.println("Calling Service Decorator message() for: " + msg);
		System.out.println("Calling Service Decorator message(): to: "
				+ msg.concat(" :HoneyBEE "));
		utilityBean.showMessage("info",
				"For: " + msg + " Calling Service Decorator message(): to: "
						+ msg.concat(" :HoneyBEE "), "");
		return msg.concat(" :HoneyBEE ");
	}

	/**
	 * Log information on registery table
	 * @param info Action name (e.g. SIGNIN)
	 */
	@Override
	public void log(String info) {
		// TODO write conditionally information on database : registery table
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		Registery registery = new Registery();
		registery.setAction(info);
		registery.setActiontime(new Date());
		registery.setUseragent(request.getHeader("User-Agent"));
		String uri;
		if (request.getHeader("referer") == null
				|| request.getHeader("referer").equals("")) {
			uri = request.getRequestURI();
		} else {
			uri = request.getHeader("referer");
		}		
		registery.setRefererurl(uri);
		registery.setUsername(userManagerBean.getUser());
		registery.setIpaddress(infoBean.getFacesClientIpAddress());
		infoBean.findIPLocation();
		registery.setCountryname(infoBean.getCountry());
		registery.setCityname(infoBean.getCity());
		em.persist(registery);
	}

}
