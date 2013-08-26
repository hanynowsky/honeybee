package org.otika.honeybee.util;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Session bean being session scoped and serves as an utility bean for holding
 * values that remain alive through the user session
 * @author Hanine H.A.M <kyoshuu.madani@gmail.com>
 *
 */
@Named
@SessionScoped
public class SessionBean implements Serializable {

	private static final long serialVersionUID = -8662407596761655172L;
	private String originalViewName = "/index.xhtml";

	public SessionBean() {
		
	}
	
	@PostConstruct
	public void init(){
		// TODO
	}
	
	
	public String getOriginalViewName() {
		return originalViewName;
	}

	public void setOriginalViewName(String originalViewName) {
		this.originalViewName = originalViewName;
	}

}