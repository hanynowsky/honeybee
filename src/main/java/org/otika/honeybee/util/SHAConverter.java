package org.otika.honeybee.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Password string to SHA256 conversion utility class
 * 
 * @author Hanine.H.A.M <hanynowsky@gmail.com>
 */
@FacesConverter("org.otika.honeybee.util.SHAConverter")
public class SHAConverter implements Converter {
	
	private UserManagerBean userManagerBean = new UserManagerBean();

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		if (value.equals("")) {
			return "";
		} else if (value.length() < 5) {
			FacesMessage msg = new FacesMessage(
					"Password cannot be less than 5 letters!");
			FacesContext.getCurrentInstance().addMessage("newPassword", msg);
			return value;
		}
		try {
			
			System.out.println("Hashing value: " +value.toString());
			userManagerBean.setOriginalPassword(value.toString());
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(value.getBytes("UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				stringBuilder.append(Integer.toString((hash[i] & 0xff) + 0x100,
						16).substring(1));
			}
			System.out.println("Hashed value: " +stringBuilder.toString());
			return stringBuilder.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(SHAConverter.class.getName()).severe(
					ex.getMessage());
			return "";
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			String msg = "SHAConverter.getAsObject: "
					+ unsupportedEncodingException.getMessage();
			Logger.getLogger(SHAConverter.class.getName()).severe(msg);
			return "";
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value.equals("")) {
			return "";
		} else {
			return String.valueOf(value);
		}
	}
}
