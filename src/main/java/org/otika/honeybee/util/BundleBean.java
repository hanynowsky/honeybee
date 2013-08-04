package org.otika.honeybee.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "bundleBean")
@ViewScoped
public class BundleBean implements java.io.Serializable {

	private static final long serialVersionUID = 2311408848698752949L;
	private ResourceBundle i18n = ResourceBundle.getBundle("/i18n");

	public BundleBean() {
	}

	/**
	 * @PostConstruct public void init(){
	 * 
	 *                }
	 */
	/**
	 * Method that gets the localized value from the Bundle
	 * 
	 * @param key
	 *            bundle key
	 * @return The Bundle (i18n) String corresponding to the key parameter
	 */
	public String i18n(String key) {
		if (key != null && !key.equals("")) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			String lang = facesContext.getViewRoot().getLocale().getLanguage();
			Locale locale = facesContext.getViewRoot().getLocale();
			switch (lang) {
			case "en":
			case "en_US":
			case "fr":
			case "fr_FR":
			case "ar":
			case "ar_MA":
				i18n = ResourceBundle.getBundle("/i18n", locale);
				break;
			default:
				i18n = ResourceBundle.getBundle("/i18n");
				break;
			}
			String s = i18n.getString(key);
			if (s == null || s.equals("")) {
				return "unknown";
			}
			return s;
		} else {
			System.err.println("BundleBean: Key is null");
			return "UNKNOWN";
		}
	}

	/**
	 * @return the i18n
	 */
	public ResourceBundle getI18n() {
		return i18n;
	}
}
