package org.otika.honeybee.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the configuration database table.
 * 
 */
@Entity
@Table(name = "configuration", catalog = "honeybee")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Configuration.findAll", query = "select c from Configuration c order by c.id") })
public class Configuration implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean inmaintenance;
	private String licence;
	private String mailpass;
	private int hitcounts;

	public Configuration() {
	}

	public Configuration(boolean inmaintenance, String licence,
			String mailpass, int hitcounts) {
		this.inmaintenance = inmaintenance;
		this.licence = licence;
		this.mailpass = mailpass;
		this.hitcounts = hitcounts;
	}

	public Configuration(Long id, boolean inmaintenance, String licence,
			String mailpass, int hitcounts) {
		this.id = id;
		this.inmaintenance = inmaintenance;
		this.licence = licence;
		this.mailpass = mailpass;
		this.hitcounts = hitcounts;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the inmaintenance
	 */
	@Column(name = "inmaintenance", nullable = false)
	public boolean isInmaintenance() {
		return inmaintenance;
	}

	/**
	 * @param inmaintenance
	 *            the inmaintenance to set
	 */
	public void setInmaintenance(boolean inmaintenance) {
		this.inmaintenance = inmaintenance;
	}

	@Column(name = "licence", nullable = false)
	public String getLicence() {
		return this.licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	@Column(name = "mailpass", nullable = false, length = 16)
	public String getMailpass() {
		return this.mailpass;
	}

	public void setMailpass(String mailpass) {
		this.mailpass = mailpass;
	}

	@Column(name = "hitcounts", nullable = false)
	public int getHitcounts() {
		return hitcounts;
	}

	public void setHitcounts(int hitcounts) {
		this.hitcounts = hitcounts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(id);
	}

}