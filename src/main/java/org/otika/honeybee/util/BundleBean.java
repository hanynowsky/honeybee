package org.otika.honeybee.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="bundleBean")
@ViewScoped
public class BundleBean implements java.io.Serializable {

	private static final long serialVersionUID = 2311408848698752949L;
	private ResourceBundle i18n = ResourceBundle.getBundle("/i18n");;
	private FacesContext facesContext = FacesContext.getCurrentInstance();;

	public BundleBean() {
	}

	/**
	 * @PostConstruct public void init(){
	 * 
	 *                }
	 */

	/**
	 * 
	 * @param key
	 * @return The Bundle (i18n) String corresponding to the key parameter
	 */
	public String i18n(String key) {
		facesContext = FacesContext.getCurrentInstance();
		String lang = facesContext.getViewRoot().getLocale().getLanguage();
		Locale locale = facesContext.getViewRoot().getLocale();
		if (lang.equals("en") || lang.equals("en_US")) {
			i18n = ResourceBundle.getBundle("/i18n");
		}

		else if (lang.equals("fr") || lang.equals("fr_FR")) {
			i18n = ResourceBundle.getBundle("/i18n", locale);
		}

		else if (lang.equals("ar") || lang.equals("ar_MA")) {
			i18n = ResourceBundle.getBundle("/i18n", locale);
		} else {
			i18n = ResourceBundle.getBundle("/i18n");
		}
		String s = i18n.getString(key);
		if (s == null || s.equals("")) {
			return "unknown";
		}
		return s;
	}

	/**
	 * @return the i18n
	 */
	public ResourceBundle getI18n() {
		return i18n;
	}

}
