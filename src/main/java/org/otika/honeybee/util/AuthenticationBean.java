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
 * @author Hanine .H ALMADANI <hanynowsky@gmail.com>
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
	@Inject
	private BundleBean bundleBean;
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

		response.getClass(); // This has no use, just to remind it should be
								// used later

		String pass = utilityBean.hash(this.password);
		if (!hash) {
			pass = this.password;
		}

		try {
			Enduser user = repository.findByEmailAndPassword(email, pass);
			if (user.getEmail() != null && !user.getEmail().equals("")) {
				if (!repository.findByEmail(email).getIsactive()) {
					context.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									bundleBean.i18n("account_notactivated"), ""));
					return null;
				} else {
					request.login(this.email, pass);
					if (request.getRemoteUser() != null) {
						signinEvent.fire(new SigninEvent(request
								.getRemoteUser()));
					}
					// TODO setting view locale does not work for user check
					// view
					localeBean.setSelectedLang(repository.findByCode(user
							.getLanguage().getCode()));
					// TODO Cookie work here

					String logsuc = bundleBean.i18n("login_successful");
					context.addMessage(null, new FacesMessage(logsuc));
				}
				
			} else {
				if (repository.findByEmail(email) != null) {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									bundleBean.i18n("wrong_credentials"), ""));
				} else {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									bundleBean.i18n("email_notfound"), ""));
				}
				return null;
			}
		} catch (Exception e) {
			LOG.log(Level.ALL, e.getMessage());
			System.out.println(e);
			context.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleBean
							.i18n("login_failed"), ""));
			return "/loginerror";
		}

		if (!outcome.equalsIgnoreCase("") && !outcome.contains("referer")) {
			return outcome;
		} else {
			String ovName = utilityBean.cutRefererString(request
					.getHeader("referer"));
			sessionBean.setOriginalViewName(ovName);
			return ovName + "?faces-redirect=true";
		}

	} // END OF LOGIN METHOD

	/**
	 * 
	 * @return logout outcome
	 */
	public String logout() {
		context = FacesContext.getCurrentInstance();
		request = (HttpServletRequest) context.getExternalContext()
				.getRequest();
		try {
			if (request.getRemoteUser() != null) {
				signoutEvent.fire(new SignoutEvent(request.getRemoteUser()));
				request.logout();
				context.addMessage(null,
						new FacesMessage(bundleBean.i18n("logout_successful")));
				FacesContext
						.getCurrentInstance()
						.getApplication()
						.getNavigationHandler()
						.handleNavigation(FacesContext.getCurrentInstance(),
								null, "/signin?faces-redirect=false");
				return "/signin?faces-redirect=false";
			} else {
				utilityBean.showMessage("warn",
						bundleBean.i18n("already_logged_in"), "");
				return null;
			}
		} catch (ServletException e) {
			LOG.severe(e.getMessage());
			System.out.println(e);
			context.addMessage(null,
					new FacesMessage(bundleBean.i18n("logout_failed")));
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
	 * @param hash
	 *            boolean whether to hash the password
	 * @return outcome
	 */
	public String login(String mail, String pass, boolean hash) {
		context = FacesContext.getCurrentInstance();
		request = (HttpServletRequest) context.getExternalContext()
				.getRequest();
		response = (HttpServletResponse) context.getExternalContext()
				.getResponse();
		String secretword;
		try {
			secretword = utilityBean.hash(pass);
			if (!hash) {
				secretword = pass;
			}
			Enduser user = repository.findByEmailAndPassword(mail, secretword);
			if (user != null
					&& (request.getRemoteUser() == null || !request
							.getRemoteUser().equals(""))) {
				request.login(mail, secretword);
				signinEvent.fire(new SigninEvent(request.getRemoteUser()));
				localeBean.setLocale(new Locale(user.getLanguage().getCode()));
				// response.sendRedirect(request.getRequestURI().toString());
				context.addMessage(null,
						new FacesMessage(bundleBean.i18n("login_successful")));
			} else {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								bundleBean.i18n("wrong_credentials"), ""));
				return null;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "{0} | cause: {1}",
					new Object[] { e.getMessage(), e.getCause().getMessage() });
			System.out.println(e);
			context.addMessage(null,
					new FacesMessage(bundleBean.i18n("login_failed")));
			return "/loginerror";
		}
		return "/home";
	}

	/**
	 * Reset values of password and email to empty This can also be achieved
	 * using _<f:setPropertyActionListener />_
	 */
	public void resetCredentials() {
		email = "";
		password = "";
	}

	/*
	 * Getters & setters
	 */
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
}// END OF CLASS
