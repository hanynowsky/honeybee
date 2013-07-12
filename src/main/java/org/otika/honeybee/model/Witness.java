package org.otika.honeybee.model;

// Generated May 19, 2013 4:05:52 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Resolution;
import org.otika.honeybee.util.EntityDebugListener;

import javax.persistence.Cacheable;

/**
 * Witness entity
 */
@EntityListeners({ EntityDebugListener.class })
@Entity
/*
 * We added an unique Constraint including a natural ID in order to disallow
 * creating more than one witness per user and per prescription
 */
@Table(name = "witness", catalog = "honeybee", uniqueConstraints = @UniqueConstraint(columnNames = {
		"enduser_id", "prescription_id" }))
@XmlRootElement
@NamedQueries(value = { @NamedQuery(name = "Witness.findByPrescriptionAndEnduser", query = "SELECT w FROM Witness w where w.prescription = :prescription AND w.enduser = :enduser") })
@Cacheable(false)
@org.hibernate.annotations.NamedQuery(name = "Witness.findByResult", query = "select w from Witness w where w.result = :result")
public class Witness implements java.io.Serializable {

	private static final long serialVersionUID = 6655577878100922952L;
	private Long id;
	private boolean result;
	private Integer version;
	private Enduser enduser;
	private Prescription prescription;
	private String comment;
	private String subject;
	private Date creationdate = new Date();

	public Witness() {
	}

	public Witness(Enduser enduser, Prescription prescription, String subject,
			String comment) {
		this.enduser = enduser;
		this.prescription = prescription;
		this.comment = comment;
		this.subject = subject;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "enduser_id", nullable = false)
	public Enduser getEnduser() {
		return this.enduser;
	}

	public void setEnduser(Enduser enduser) {
		this.enduser = enduser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prescription_id", nullable = false)
	public Prescription getPrescription() {
		return this.prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	@Field
	@Column(name = "comment", nullable = false, length = 65535)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Field
	@Column(name = "subject", nullable = false, length = 30)
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "result", nullable = false)
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	@DateBridge(resolution = Resolution.MONTH)
	@Temporal(TemporalType.DATE)
	@Column(name = "creationdate")
	public Date getCreationdate() {
		return creationdate;
	}

	public void setCreationdate(Date creationdate) {
		this.creationdate = creationdate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " " + subject;
	}

}
