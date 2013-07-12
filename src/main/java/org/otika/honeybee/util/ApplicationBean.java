package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.otika.honeybee.model.Enduser;

@DeclareRoles(value = { "ADMIN", "REGISTERED", "GUEST", "AUTHOR" })
@Named("applicationBean")
@ApplicationScoped
public class ApplicationBean implements Serializable {

	private static final long serialVersionUID = 5L;
	private List<Enduser> connectedMembers;
	private List<Integer> sessionsCount;
	private boolean inConstruction = false;
	private Integer constructionInterval = 5;
	@Inject
	private UserManagerBean userManagerBean;
	@Resource
	private SessionContext sessionContext;

	public ApplicationBean() {
		connectedMembers = new ArrayList<Enduser>(0);
		sessionsCount = new ArrayList<Integer>(0);
	}

	public void removeConnectedMember() {
		try {
			if (connectedMembers.contains(userManagerBean.getConnectedUser())) {
				connectedMembers.remove(userManagerBean.getConnectedUser());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void addConnectedMember() {
		if (!connectedMembers.contains(userManagerBean.getConnectedUser())) {
			connectedMembers.add(userManagerBean.getConnectedUser());
		}
	}

	public List<Enduser> getConnectedMembers() {
		return connectedMembers;
	}

	public void setConnectedMembers(List<Enduser> connectedMembers) {
		this.connectedMembers = connectedMembers;
	}

	public List<Integer> getSessionsCount() {
		return sessionsCount;
	}

	public void setSessionsCount(List<Integer> sessionsCount) {
		this.sessionsCount = sessionsCount;
	}

	public boolean isInConstruction() {
		return inConstruction;
	}

	@RolesAllowed(value = { "ADMIN" })
	public void setInConstruction(boolean inConstruction) {
		if (sessionContext.isCallerInRole("ADMIN")) {
			this.inConstruction = inConstruction;
		} else {
			FacesMessage msg = new FacesMessage(
					"Setting the application to In-Construction mode! "
							+ "Only Admin can perform this action");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public Integer getConstructionInterval() {
		return constructionInterval;
	}

	public void setConstructionInterval(Integer constructionInterval) {
		this.constructionInterval = constructionInterval;
	}

}
