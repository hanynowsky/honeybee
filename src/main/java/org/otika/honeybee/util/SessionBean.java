package org.otika.honeybee.util;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Session bean being session scoped and serves as an utility bean for holding
 * values that remain alive through the user session
 * 
 * @author Hanine H.A.M <kyoshuu.madani@gmail.com>
 * 
 */
@Named
@SessionScoped
public class SessionBean implements Serializable {

	private static final long serialVersionUID = -8662407596761655172L;
	private String originalViewName = "/index.xhtml";
	private double svgCX;
	private double svgCY;
	private Long id;

	public SessionBean() {

	}

	@PostConstruct
	public void init() {
		// TODO
	}

	public void giveIdNewValue(long value) {
		System.out.println("Setting ID value to : " + this.id);
		setId(value);
	}

	public String getOriginalViewName() {
		return originalViewName;
	}

	public void setOriginalViewName(String originalViewName) {
		this.originalViewName = originalViewName;
	}

	/**
	 * @return the svgCX
	 */
	public double getSvgCX() {
		return svgCX;
	}

	/**
	 * @param svgCX
	 *            the svgCX to set
	 */
	public void setSvgCX(double svgCX) {
		this.svgCX = svgCX;
	}

	/**
	 * @return the svgCY
	 */
	public double getSvgCY() {
		return svgCY;
	}

	/**
	 * @param svgCY
	 *            the svgCY to set
	 */
	public void setSvgCY(double svgCY) {
		this.svgCY = svgCY;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

}