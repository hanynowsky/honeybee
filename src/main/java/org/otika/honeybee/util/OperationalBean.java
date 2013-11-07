package org.otika.honeybee.util;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
/**
 * Session Bean implementation class OperationalBean
 */
@Stateless
@LocalBean
@DeclareRoles({"ADMIN","AUTHOR"})
@SecurityDomain("HoneybeeRealm")
public class OperationalBean {

	/**
	 * Default constructor.
	 */
	public OperationalBean() {

	}

	@RolesAllowed("ADMIN")
	public String sayHello() {
		return "Hello " + System.getProperty("user.name");
	}

}
