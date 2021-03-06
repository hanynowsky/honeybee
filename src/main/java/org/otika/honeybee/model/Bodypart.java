package org.otika.honeybee.model;

// Generated May 19, 2013 4:05:52 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Field;

/**
 * Bodypart generated by hbm2java
 */
@Entity
@Table(name = "bodypart", catalog = "honeybee")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Bodypart.findByLabel", query = "SELECT b FROM Bodypart b WHERE b.label = :label") })
public class Bodypart implements java.io.Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 6939618808529181766L;
	private Long id;
	private Integer version;
	private String label;
	private String labelfr;
	private String labelar;
	private Set<Defect> defects = new HashSet<Defect>(0);
	private Set<Virtue> virtues = new HashSet<Virtue>(0);

	public Bodypart() {
	}

	public Bodypart(String label, String labelfr, String labelar) {
		this.label = label;
		this.labelfr = labelfr;
		this.labelar = labelar;
	}

	public Bodypart(String label, String labelfr, String labelar,
			Set<Defect> defects, Set<Virtue> virtues) {
		this.label = label;
		this.labelfr = labelfr;
		this.labelar = labelar;
		this.defects = defects;
		this.virtues = virtues;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Field
	@Column(name = "label", nullable = false, length = 50)
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Field
	@Column(name = "labelfr", nullable = false, length = 50)
	public String getLabelfr() {
		return this.labelfr;
	}

	public void setLabelfr(String labelfr) {
		this.labelfr = labelfr;
	}

	@Field
	@Column(name = "labelar", nullable = false, length = 50)
	public String getLabelar() {
		return this.labelar;
	}

	public void setLabelar(String labelar) {
		this.labelar = labelar;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bodypart")
	public Set<Defect> getDefects() {
		return this.defects;
	}

	public void setDefects(Set<Defect> defects) {
		this.defects = defects;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bodypart")
	public Set<Virtue> getVirtues() {
		return this.virtues;
	}

	public void setVirtues(Set<Virtue> virtues) {
		this.virtues = virtues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return label + " " + labelar;
	}

}
