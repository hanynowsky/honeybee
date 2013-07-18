package org.otika.honeybee.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Registery 
 */
@Entity
@Table(name = "registery", catalog = "honeybee")
@XmlRootElement
public class Registery implements java.io.Serializable {

	private static final long serialVersionUID = -8014098692975443547L;
	private Long id;
	private String username;
	private String action;
	private Date actiontime;
	private String ipaddress;
	private String countryname;
	private String cityname;
	private String useragent;
	private String refererurl;
	private String os;
	private String miscellaneous;

	public Registery() {
	}

	public Registery(String action) {
		this.action = action;
	}

	public Registery(String username, String action, Date actiontime,
			String ipaddress, String countryname, String cityname,
			String useragent, String refererurl, String os, String miscellaneous) {
		this.username = username;
		this.action = action;
		this.actiontime = actiontime;
		this.ipaddress = ipaddress;
		this.countryname = countryname;
		this.cityname = cityname;
		this.useragent = useragent;
		this.refererurl = refererurl;
		this.os = os;
		this.miscellaneous = miscellaneous;
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

	@Column(name = "username", length = 65)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "action", nullable = false, length = 145)
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "actiontime", length = 19)
	public Date getActiontime() {
		return this.actiontime;
	}

	public void setActiontime(Date actiontime) {
		this.actiontime = actiontime;
	}

	@Column(name = "ipaddress", length = 15)
	public String getIpaddress() {
		return this.ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	@Column(name = "countryname", length = 45)
	public String getCountryname() {
		return this.countryname;
	}

	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}

	@Column(name = "cityname", length = 45)
	public String getCityname() {
		return this.cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	@Column(name = "useragent", length = 245)
	public String getUseragent() {
		return this.useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	@Column(name = "refererurl", length = 145)
	public String getRefererurl() {
		return this.refererurl;
	}

	public void setRefererurl(String refererurl) {
		this.refererurl = refererurl;
	}

	@Column(name = "os", length = 25)
	public String getOs() {
		return this.os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	@Column(name = "miscellaneous", length = 65535)
	public String getMiscellaneous() {
		return this.miscellaneous;
	}

	public void setMiscellaneous(String miscellaneous) {
		this.miscellaneous = miscellaneous;
	}

}
