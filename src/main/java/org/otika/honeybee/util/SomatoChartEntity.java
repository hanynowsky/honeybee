package org.otika.honeybee.util;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class SomatoChartEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8126362946541285783L;
	@GeneratedValue
	@Id
	private int id;
	private double endoElement;
	private double mesoElement;
	private double ectoElement;

	public SomatoChartEntity() {

	}

	public SomatoChartEntity(double endo, double meso, double ecto) {
		this.endoElement = endo;
		this.mesoElement = meso;
		this.ectoElement = ecto;
	}

	@PostConstruct
	public void init() {

	}

	/**
	 * @return the endoElement
	 */
	public double getEndoElement() {
		return endoElement;
	}

	/**
	 * @param endoElement
	 *            the endoElement to set
	 */
	public void setEndoElement(double endoElement) {
		this.endoElement = endoElement;
	}

	/**
	 * @return the mesoElement
	 */
	public double getMesoElement() {
		return mesoElement;
	}

	/**
	 * @param mesoElement
	 *            the mesoElement to set
	 */
	public void setMesoElement(double mesoElement) {
		this.mesoElement = mesoElement;
	}

	/**
	 * @return the ectoElement
	 */
	public double getEctoElement() {
		return ectoElement;
	}

	/**
	 * @param ectoElement
	 *            the ectoElement to set
	 */
	public void setEctoElement(double ectoElement) {
		this.ectoElement = ectoElement;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

}
