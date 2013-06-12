package org.otika.honeybee.util;
import java.io.Serializable;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/***
 * This Class sets the View Locale upon user choice (fr or en, ar) as specified
 * in Faces Configuration
 * Please put <f:view  locale="" /> after <html /> tag in XHTML template
 * @version 1.0
 * @author Hanine.H.A <hanynowsky@gmail.com>
 */

@ManagedBean(name = "localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The Locale value is retrieved from the JSF view root.
	 */
	private Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		if (locale == null) {
			return new Locale("en");
		} else {
			return locale;
		}
	}

	/**
	 * 
	 * @return The language from the locale
	 */
	public String getLanguage() {
		return locale.getLanguage();
	}

	/**
	 * Setter for the locale language | It sets the locale that takes language
	 * as a parameter to the JSF View Root
	 * 
	 * @param language
	 */
	public void setLanguage(String language) {
		locale = new Locale(language);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	}

}