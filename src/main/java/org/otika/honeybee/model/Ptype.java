/*
 * License
 * .
 */
package org.otika.honeybee.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author hanine
 */
@Entity
@Table(name = "ptype", catalog = "honeybee")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Ptype.findAll", query = "SELECT p FROM Ptype p"),
		@NamedQuery(name = "Ptype.findById", query = "SELECT p FROM Ptype p WHERE p.id = :id"),
		@NamedQuery(name = "Ptype.findByLabel", query = "SELECT p FROM Ptype p WHERE p.label = :label") })
public class Ptype implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String label;
	private String labelfr;
	private String labelar;
	private Integer version;

	public Ptype() {
	}

	public Ptype(Long id) {
		this.id = id;
	}

	public Ptype(Long id, String label, String labelfr, String labelar) {
		this.id = id;
		this.label = label;
		this.labelfr = labelfr;
		this.labelar = labelar;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 25)
	@Column(name = "label", unique = true)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@NotNull
	@Size(min = 1, max = 25)
	@Column(name = "labelfr")
	public String getLabelfr() {
		return labelfr;
	}

	public void setLabelfr(String labelfr) {
		this.labelfr = labelfr;
	}

	@NotNull
	@Size(min = 1, max = 25)
	@Column(name = "labelar")
	public String getLabelar() {
		return labelar;
	}

	public void setLabelar(String labelar) {
		this.labelar = labelar;
	}

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Ptype)) {
			return false;
		}
		Ptype other = (Ptype) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return id + "";
	}
}
