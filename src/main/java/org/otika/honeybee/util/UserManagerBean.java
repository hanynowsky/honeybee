package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otika.honeybee.events.SignoutEvent;
import org.otika.honeybee.model.Enduser;

@ManagedBean(name = "userManagerBean")
@SessionScoped
public class UserManagerBean implements Serializable {

	private static final long serialVersionUID = -1158849485465205337L;
	@EJB
	private Repository repository;
	private List<Enduser> connectedUsers;
	@Inject
	private UtilityBean utilityBean;
	@Inject
	private Event<SignoutEvent> signoutEvent;
	private String originalPassword;

	public UserManagerBean() {

	}

	@PostConstruct
	public void init() {
		connectedUsers = new ArrayList<Enduser>(0);
	}

	private HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
	}

	public String logout() {
		try {
			getRequest().getSession().invalidate();
			/*
			 * HttpSession lsession = (HttpSession)
			 * FacesContext.getCurrentInstance()
			 * .getExternalContext().getSession(false); if (lsession != null) {
			 * // lsession.invalidate();
			 * FacesContext.getCurrentInstance().getExternalContext()
			 * .invalidateSession(); }
			 */
			FacesMessage msg = new FacesMessage(
					"Logout successful / Déconnecté");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			signoutEvent.fire(new SignoutEvent(getUserName()));
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					ex.getMessage() + " : " + ex.getCause().getMessage());
			if (ex instanceof ViewExpiredException) {
				System.out
						.println("View Expired. Forcing user to log out though!");
				return "/index.xhtml?faces-redirect=true";
			}
		}

		// return "logout";
		// return "logout?faces-redirect=true";
		return "/index.xhtml?faces-redirect=true";
	}

	public void login() {
		if (!FacesContext.getCurrentInstance().getExternalContext()
				.getRemoteUser().isEmpty()) {
			// Login here
			FacesMessage msg = new FacesMessage("Login action successful");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			// User is already logged in
			FacesMessage msg = new FacesMessage("You are already logged in");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public Enduser getUserObject() {
		try {
			if (getUser() != null && !getUser().equals("")) {
				return repository.findByEmail(getUser());
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.ALL, "No User: ",
					e);
		}
		return null;
	}

	/**
	 * User principal credential
	 * @return
	 */
	public String getUserPrincipal() {
		try {
			return FacesContext.getCurrentInstance()
					.getExternalContext().getUserPrincipal().getName();
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.FINEST,
					e.getMessage() + " - CAUSE : " + e.getCause());
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			return null;
		}
	}

	public String getUser() {
		try {
			String user = FacesContext.getCurrentInstance()
					.getExternalContext().getRemoteUser();
			if (user == null) {
				return null;
			} else {
				return user;
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.FINEST,
					e.getMessage() + " - CAUSE : " + e.getCause());
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			return null;
		}
	}

	public String getUserName() {
		if (getUser() == null) {
			return null;
		} else {
			return repository.findByEmail(getUser()).getEmail();
		}

	}

	/**
	 * 
	 * @param role
	 * @return boolean true if user in role
	 */
	public boolean isUserInRole(String role) {
		try {
			return FacesContext.getCurrentInstance().getExternalContext()
					.isUserInRole(role);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.FINEST,
					e.getMessage() + " - CAUSE: " + e.getCause());
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			return false;
		}
	}

	/**
	 * 
	 * @return Group code
	 */
	public String getUserRole() {
		if (getUser() == null) {
			return null;
		} else {
			return repository.findByEmail(getUser()).getUsergroup()
					.getGroupcode();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Enduser getConnectedUser() {
		try {
			if (getUser() != null) {
				return repository.findByEmail(getUser());
			} else {
				return null;
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					"No connected user: ", e);
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean isRemoteUserId(int id) {
		try {
            return getConnectedUser().getId() == id
                    || getConnectedUser().getUsergroup().getGroupcode()
                    .equals("ADMINISTRATOR");
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					"No remote user: ", e);
		}
		return false;
	}

	public void loginListener() {
		if (getConnectedUser() != null) {
			connectedUsers.add(getConnectedUser());
		}
	}

	public void logoutListener() {
		FacesMessage msg = new FacesMessage("Logout successful / Déconnecté");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/**
	 * Listener for faces ajax that redirects to home page once session has
	 * expired.
	 * 
	 * @return AynchResult<String>
	 */
	@Asynchronous
	public Future<String> redirectToHome() {
		// TODO refactor Container does not know about Thread ?
		// javax.enterprise.concurrent should be used when upgrading to Java EE
		// 7
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			String path = request.getContextPath();
			HttpServletResponse response = (HttpServletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			long delay = 5000; // 5 seconds = 5000 ms
			utilityBean.showMessage("warn",
					"Refreshing session in " + String.valueOf(delay / 1000)
							+ " seconds", "");
			Thread.sleep(delay);
			System.out.println("Redirecting to : " + path + "/index.xhtml");
			response.sendRedirect(path + "/index.xhtml");
			utilityBean.showMessage("info", "Session refreshed", "");
			return new AsyncResult<String>("Session Expired: Redirection done!");
			/*
			 * FacesContext.getCurrentInstance().getExternalContext()
			 * .invalidateSession();
			 */
		} catch (Exception e) {
			utilityBean.showMessage("error", e.getMessage(), e.getCause()
					.getMessage());
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					e.getMessage(), e);
			Logger.getLogger(getClass().getName()).severe(
					e.getCause().getMessage());
			System.out.println("Thread Interruption: " + e);
		}
		return null;
	}

	public List<Enduser> getConnectedMembers() {
		return connectedUsers;
	}

	public void setConnectedMembers(List<Enduser> connectedMembers) {
		this.connectedUsers = connectedMembers;
	}

	public String getOriginalPassword() {
		return originalPassword;
	}

	public void setOriginalPassword(String originalPassword) {
		this.originalPassword = originalPassword;
	}
} // END OF CLASS
