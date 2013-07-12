package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.model.Language;

/***
 * This Class sets the View Locale upon user choice (fr or en, ar) as specified
 * in Faces Configuration Please put <f:view locale="" /> after <html /> tag in
 * XHTML template
 * 
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


	private Language selectedLang;
	@EJB
	private Repository repository;

	@PostConstruct
	public void init() {
		FacesContext context = FacesContext.getCurrentInstance();
		String email = (context.getExternalContext().getRemoteUser() != null ? context
				.getExternalContext().getRemoteUser() : null);
		if (email != null && !email.equals("")) {
			Enduser user = repository.findByEmail(email);
			selectedLang = user.getLanguage();
		} else {
			selectedLang = new Language(locale.getLanguage(),
					getSelectedLanguageLabel(), null);			
		}
	}

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
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
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

	public Language getSelectedLang() {
		return selectedLang;
	}

	/**
	 * 
	 * @param selectedLang
	 */
	public void setSelectedLang(Language selectedLang) {
		this.selectedLang = selectedLang;
		locale = new Locale(selectedLang.getCode()); // To work with Entity
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	}

	/**
	 * 
	 * @return selected language label
	 */
	public String getSelectedLanguageLabel() {
		String l = locale.getLanguage();
		if (l.equalsIgnoreCase("en")) {
			return "English";
		}
		if (l.equalsIgnoreCase("fr")) {
			return "Français";
		}
		if (l.equalsIgnoreCase("ar")) {
			return "عربية";
		}
		return l;
	}

	/**
	 * Change View locale
	 */
	public void changeUserLocale (String mailAddress, String password){
		FacesContext context = FacesContext.getCurrentInstance();		
		String email = (context.getExternalContext().getRemoteUser() != null ? context
				.getExternalContext().getRemoteUser() : null);
		if (email != null && !email.equals("")) {
			Enduser user = repository.findByEmail(email);
			selectedLang = user.getLanguage();
		locale = new Locale(selectedLang.getCode());
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	}	
}
}