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

	public Configuration() {
	}

	public Configuration(boolean inmaintenance, String licence, String mailpass) {
		this.inmaintenance = inmaintenance;
		this.licence = licence;
		this.mailpass = mailpass;
	}

	public Configuration(Long id, boolean inmaintenance, String licence,
			String mailpass) {
		this.id = id;
		this.inmaintenance = inmaintenance;
		this.licence = licence;
		this.mailpass = mailpass;
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