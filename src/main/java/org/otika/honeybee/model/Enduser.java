package org.otika.honeybee.model;

// Generated May 19, 2013 4:05:52 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Enduser generated by hbm2java
 */
@Entity
@Table(name = "enduser", catalog = "honeybee", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@XmlRootElement
@NamedQueries(value = {
		@NamedQuery(name = "Enduser.findByUserkey", query = "SELECT e FROM Enduser e WHERE e.userkey = :userkey"),
		@NamedQuery(name = "Enduser.findByEmail", query = "select e from Enduser e where e.email = :email") })
public class Enduser implements java.io.Serializable {

	private static final long serialVersionUID = -5640669438836852305L;
	private Long id;
	private Integer version;
	private Usergroup usergroup;
	private Language language;
	private String name;
	private String surname;
	private String password;
	private String passconf;
	private String email;
	private String telephone;
	private String address;
	private String facebook;
	private String googleplus;
	private String twitter;
	private String website;
	private boolean isactive = false;
	private String gender;
	private Date datejoined;
	private String userkey = UUID.randomUUID().toString();
	private Set<Witness> witnesses = new HashSet<Witness>(0);

	public Enduser() {
	}

	public Enduser(Usergroup usergroup, Language language, String name,
			String surname, String password, String passconf, String email) {
		this.usergroup = usergroup;
		this.language = language;
		this.name = name;
		this.surname = surname;
		this.password = password;
		this.passconf = passconf;
		this.email = email;
	}

	public Enduser(Usergroup usergroup, Language language, String name,
			String surname, String password, String passconf, String email,
			String telephone, String address, String facebook,
			String googleplus, String twitter, String website,
			boolean isactive, String gender, Date datejoined,
			Set<Witness> witnesses) {
		this.usergroup = usergroup;
		this.language = language;
		this.name = name;
		this.surname = surname;
		this.password = password;
		this.passconf = passconf;
		this.email = email;
		this.telephone = telephone;
		this.address = address;
		this.facebook = facebook;
		this.googleplus = googleplus;
		this.twitter = twitter;
		this.website = website;
		this.isactive = isactive;
		this.gender = gender;
		this.datejoined = datejoined;
		this.witnesses = witnesses;
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
	@JoinColumn(name = "usergroup_id", nullable = false)
	public Usergroup getUsergroup() {
		return this.usergroup;
	}

	public void setUsergroup(Usergroup usergroup) {
		this.usergroup = usergroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id", nullable = false)
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@NotEmpty
	@Column(name = "name", nullable = false, length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotEmpty
	@Column(name = "surname", nullable = false, length = 45)
	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@NotEmpty
	@Column(name = "password", nullable = false, length = 12)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotEmpty
	@Column(name = "passconf", nullable = false, length = 12)
	public String getPassconf() {
		return this.passconf;
	}

	public void setPassconf(String passconf) {
		this.passconf = passconf;
	}

	@NotEmpty
	@Email
	@Column(name = "email", unique = true, nullable = false, length = 65)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "telephone", length = 25)
	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Column(name = "address", length = 145)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "facebook", length = 65)
	public String getFacebook() {
		return this.facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	@Column(name = "googleplus", length = 85)
	public String getGoogleplus() {
		return this.googleplus;
	}

	public void setGoogleplus(String googleplus) {
		this.googleplus = googleplus;
	}

	@Column(name = "twitter", length = 45)
	public String getTwitter() {
		return this.twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	@Column(name = "website", length = 85)
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	@Column(name = "isactive",nullable=false)
	public boolean getIsactive() {
		return this.isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	@Column(name = "gender", length = 7)
	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "datejoined", length = 10)
	public Date getDatejoined() {
		return this.datejoined;
	}

	public void setDatejoined(Date datejoined) {
		this.datejoined = datejoined;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "enduser")
	public Set<Witness> getWitnesses() {
		return this.witnesses;
	}

	public void setWitnesses(Set<Witness> witnesses) {
		this.witnesses = witnesses;
	}

	@Column(name = "userkey")
	public String getUserkey() {
		return userkey;
	}

	public void setUserkey(String userkey) {
		this.userkey = userkey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + " " + surname;
	}

}
