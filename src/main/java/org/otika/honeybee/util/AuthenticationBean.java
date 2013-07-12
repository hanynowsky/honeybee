package org.otika.honeybee.util;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.otika.honeybee.events.SigninEvent;
import org.otika.honeybee.events.SignoutEvent;
import org.otika.honeybee.model.Enduser;

/**
 * 
 * @author hanine
 */
@Stateless
@Named(value = "authenticationBean")
public class AuthenticationBean {

	private static final Logger LOG = Logger.getLogger(AuthenticationBean.class
			.getName());
	@Inject
	private UtilityBean utilityBean;
	@EJB
	private Repository repository;
	@Inject
	private Event<SignoutEvent> signoutEvent;
	@Inject
	private Event<SigninEvent> signinEvent;
	@Inject
	private LocaleBean localeBean;
	@Inject
	private SessionBean sessionBean;
	private FacesContext context;
	private HttpServletRequest request;
	private HttpServletResponse response;
	@NotNull
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email / Email non valide")
	@Size(min = 8, max = 50, message = "Email size must be between 8 and 50")
	private String email = "";
	@NotNull
	@Size(min = 5, max = 252, message = "Password value must be at least 5")
	private String password = "";

	public AuthenticationBean() {
	}

	@PostConstruct
	public void init() {
		context = FacesContext.getCurrentInstance();
		request = (HttpServletRequest) context.getExternalContext()
				.getRequest();
		response = (HttpServletResponse) context.getExternalContext()
				.getResponse();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Programmatic login method. Hashing the password is done using a
	 * converter. But if no faces converter is used, we set hash parameter to
	 * true
	 * 
	 * @param hash
	 *            boolean specifying whether to hash the password or not
	 * @param the
	 *            success outcome
	 * @return outcome for faces navigation
	 */
	public String login(boolean hash, String outcome) {
		context = FacesContext.getCurrentInstance();
		request = (HttpServletRequest) context.getExternalContext()
				.getRequest();
		response = (HttpServletResponse) context.getExternalContext()
				.getResponse();

		try {
			String pass = utilityBean.hash(this.password);
			if (!hash) {
				pass = this.password;
			}
			Enduser user = repository.findByEmailAndPassword(email, pass);
			if (user != null) {
				request.login(this.email, pass);
				signinEvent.fire(new SigninEvent(request.getRemoteUser()));
				// TODO setting view locale does not work for user check view
				localeBean.setLocale(new Locale(user.getLanguage().getCode()));
				// response.sendRedirect(request.getContextPath()+
				// File.separator+request.getRequestURI().toString());

				Cookie cookie = new Cookie("honeybee", "honeybee");
				cookie.setValue(user.getEmail());
				cookie.setMaxAge(29000000);
				response.addCookie(cookie);
				System.out.println(response.getCharacterEncoding());
				String uri = "/" + request.getRequestURI().split("/")[2];
				System.out.println("returning: " + uri);
				context.addMessage(null, new FacesMessage("Login Successful."));
				// return uri;
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Wrong credentials. Try again!", ""));
				return null;
			}
		} catch (Exception e) {
			LOG.log(Level.FINEST, e.getMessage());
			System.out.println(e);
			context.addMessage(null, new FacesMessage("Login failed."));
			return "/loginerror";
		}
		if (!outcome.equalsIgnoreCase("") && outcome != null
				&& !outcome.contains("referer")) {
			return outcome;
		} else if (outcome.contains("referer")) {
			if (sessionBean.getOriginalViewName().contains("/signin.xhtml")) {
				return "/index?faces-redirect=false";
			} else {
				System.out.println("Redirecting to: "
						+ sessionBean.getOriginalViewName());
				return sessionBean.getOriginalViewName()+"?faces-redirect=true";
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return logout outcome
	 */
	public String logout() {
		context = FacesContext.getCurrentInstance();
		request = (HttpServletRequest) context.getExternalContext()
				.getRequest();
		try {
			signoutEvent.fire(new SignoutEvent(request.getRemoteUser()));
			request.logout();			
			context.addMessage(null, new FacesMessage("Logout successful."));
			FacesContext
					.getCurrentInstance()
					.getApplication()
					.getNavigationHandler()
					.handleNavigation(FacesContext.getCurrentInstance(), null,
							"/signin.xhtml?faces-redirect=false");
			return "/signin.xhtml?faces-redirect=false";
		} catch (ServletException e) {
			LOG.severe(e.getMessage());
			System.out.println(e);
			context.addMessage(null, new FacesMessage("Logout failed."));
			return "/loginerror";
		}
	}

	/**
	 * Login Method
	 * 
	 * @param mail
	 *            email address
	 * @param pass
	 *            password
	 *            @param hash boolean whether to hash the password
	 * @return outcome
	 */
	public String login(String mail, String pass, boolean hash) {
		context = FacesContext.getCurrentInstance();
		request = (HttpServletRequest) context.getExternalContext()
				.getRequest();
		response = (HttpServletResponse) context.getExternalContext()
				.getResponse();
		try {
			String password = utilityBean.hash(pass);
			if (!hash) {
				password = pass;
			}
			Enduser user = repository.findByEmailAndPassword(mail, password);
			if (user != null
					&& (request.getRemoteUser() == null || !request
							.getRemoteUser().equals(""))) {
				request.login(mail, password);
				signinEvent.fire(new SigninEvent(request.getRemoteUser()));
				localeBean.setLocale(new Locale(user.getLanguage().getCode()));
				// response.sendRedirect(request.getRequestURI().toString());
				Cookie cookie = new Cookie("honeybee", "honeybee");
				cookie.setValue("honeybee");
				cookie.setMaxAge(29000000);
				response.addCookie(cookie);
				context.addMessage(null, new FacesMessage("Login Successful."));
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Wrong credentials. Try again!", ""));
				return null;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "{0} | cause: {1}",
					new Object[] { e.getMessage(), e.getCause().getMessage() });
			System.out.println(e);
			context.addMessage(null, new FacesMessage("Login failed."));
			return "/loginerror";
		}
		return "/index";
	}

	/**
	 * Reset values of password and email to empty This can also be achieved
	 * using _<f:setPropertyActionListener />_
	 */
	public void resetCredentials() {
		email = "";
		password = "";
	}
}// END OF CLASS
