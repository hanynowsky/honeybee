package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.otika.honeybee.model.Enduser;

@ManagedBean(name = "userManagerBean")
@SessionScoped
public class UserManagerBean implements Serializable {

	private static final long serialVersionUID = -1158849485465205337L;
	@EJB
	private Repository repository;
	private List<Enduser> connectedUsers;

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
		getRequest().getSession().invalidate();
		/*
		 * HttpSession lsession = (HttpSession)
		 * FacesContext.getCurrentInstance()
		 * .getExternalContext().getSession(false); if (lsession != null) { //
		 * lsession.invalidate();
		 * FacesContext.getCurrentInstance().getExternalContext()
		 * .invalidateSession();
		 }*/
		  FacesMessage msg = new FacesMessage(
		  "Logout successful / Déconnecté");
		  FacesContext.getCurrentInstance().addMessage(null, msg); 
		 
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

	public String getUserPrincipal() {
		try {
			String princ = FacesContext.getCurrentInstance()
					.getExternalContext().getUserPrincipal().getName();
			return princ;
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
	 * @return
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
			if (getConnectedUser().getId() == id
					|| getConnectedUser().getUsergroup().getGroupcode()
							.equals("ADMINISTRATOR")) {
				return true;
			} else {
				return false;
			}
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

	public List<Enduser> getConnectedMembers() {
		return connectedUsers;
	}

	public void setConnectedMembers(List<Enduser> connectedMembers) {
		this.connectedUsers = connectedMembers;
	}
} // END OF CLASS
