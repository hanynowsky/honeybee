package org.otika.honeybee.util;

import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.otika.honeybee.model.Ingredient;

/**
 * 
 * @author Hanine H. ALMADANI <hanynowsky@gmail.com>
 * 
 */
@Model
public class MobileBean {

	@Inject
	private SearchBean searchBean;

	public MobileBean() {
	}

	/**
	 * Faces Method to search ingredient items from a mobile page
	 * 
	 * @return null
	 */
	public String searchItems() {
		searchBean.searchIngredientItems();
		return null;
	}

	/**
	 * Returns a database localized property value
	 * 
	 * @param i
	 * @param property
	 * @return
	 */
	public String localizedProperty(Ingredient i, String property) {
		String lang = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale().getLanguage();
		if (property.equalsIgnoreCase("label")) {
			if (lang.equalsIgnoreCase("fr")) {
				return i.getLabelfr();
			} else if (lang.equalsIgnoreCase("ar")) {
				return i.getLabelar();
			} else {
				return i.getLabel();
			}
		}

		if (property.equalsIgnoreCase("description")) {
			if (lang.equalsIgnoreCase("fr")) {
				return i.getDescriptionfr();
			} else if (lang.equalsIgnoreCase("ar")) {
				return i.getDescriptionar();
			} else {
				return i.getDescription();
			}
		}
		return null;
	}
}
