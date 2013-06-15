package org.otika.honeybee.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "store", catalog = "honeybee")
@NamedQueries(value = {
		@NamedQuery(name = "Store.findById", query = "SELECT s FROM Store s WHERE s.id = :id"),
		@NamedQuery(name = "Store.findByLabel", query = "SELECT s FROM Store s WHERE s.label = :label") })
public class Store implements java.io.Serializable {

	private static final long serialVersionUID = -840081089926856558L;
	private Long id;
	private Integer version;
	private String label;
	private String labelar;
	private String description;
	private String descriptionar;
	private String address;
	private String addressar;
	private String phone;
	private String fax;
	private String cellular;
	private String email;
	private String mapaddress;
	private String website;

	public Store() {
	}

	public Store(String label, String labelar) {
		this.label = label;
		this.labelar = labelar;
	}

	public Store(String label, String labelar, String address,
			String addressar, String phone, String fax, String cellular,
			String email, String mapaddress, String website) {
		this.label = label;
		this.labelar = labelar;
		this.address = address;
		this.addressar = addressar;
		this.phone = phone;
		this.fax = fax;
		this.cellular = cellular;
		this.email = email;
		this.mapaddress = mapaddress;
		this.website = website;
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

	@Column(name = "label", nullable = false, length = 45)
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "labelar", nullable = false, length = 45)
	public String getLabelar() {
		return this.labelar;
	}

	public void setLabelar(String labelar) {
		this.labelar = labelar;
	}

	@Column(name = "address", length = 445)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "addressar", length = 445)
	public String getAddressar() {
		return this.addressar;
	}

	public void setAddressar(String addressar) {
		this.addressar = addressar;
	}

	@Column(name = "phone", length = 45)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "fax", length = 45)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "cellular", length = 45)
	public String getCellular() {
		return this.cellular;
	}

	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	@Column(name = "email", length = 85)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "mapaddress", length = 155)
	public String getMapaddress() {
		return this.mapaddress;
	}

	public void setMapaddress(String mapaddress) {
		this.mapaddress = mapaddress;
	}

	@Column(name = "website", length = 105)
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Column(name = "description", length = 105)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "descriptionar", length = 105)
	public String getDescriptionar() {
		return descriptionar;
	}

	public void setDescriptionar(String descriptionar) {
		this.descriptionar = descriptionar;
	}

	@Override
	public String toString() {
		return label + " " + labelar;
	}
}
