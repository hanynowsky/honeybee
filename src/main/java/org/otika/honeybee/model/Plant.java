package org.otika.honeybee.model;

// Generated May 25, 2013 2:45:04 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.UniqueConstraint;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Plant generated by hbm2java
 */
@Entity
@Table(name = "plant", catalog = "honeybee", uniqueConstraints = {
		@UniqueConstraint(columnNames = "label"),
		@UniqueConstraint(columnNames = "labelfr"),
		@UniqueConstraint(columnNames = "labelar"),
		@UniqueConstraint(columnNames = "labellat")})
@NamedQueries(value = {
		@NamedQuery(name = "Plant.findById", query = "SELECT p FROM Plant p WHERE p.id = :id"),
		@NamedQuery(name = "Plant.findByLabel", query = "select p from Plant p where p.label = :label") })
public class Plant implements java.io.Serializable {

	private static final long serialVersionUID = 6844752294966591246L;
	private Long id;
	private Integer version;
	private String label;
	private String labelfr;
	private String labellat;
	private String labelar;
	private String labelmar;
	private String image;
	private byte[] graphic;
	private String description;
	private String descriptionfr;
	private String descriptionar;
	private Set<Honey> honeys = new HashSet<Honey>(0);
	private Set<Ingredient> ingredients = new HashSet<Ingredient>(0);

	public Plant() {
	}

	public Plant(String label, String labelfr, String labellat, String labelar) {
		this.label = label;
		this.labelfr = labelfr;
		this.labellat = labellat;
		this.labelar = labelar;
	}

	public Plant(String label, String labelfr, String labellat, String labelar,
			String labelmar, String image, byte[] graphic, String description,
			String descriptionfr, String descriptionar, Set<Honey> honeys,
			Set<Ingredient> ingredients) {
		this.label = label;
		this.labelfr = labelfr;
		this.labellat = labellat;
		this.labelar = labelar;
		this.labelmar = labelmar;
		this.image = image;
		this.graphic = graphic;
		this.description = description;
		this.descriptionfr = descriptionfr;
		this.descriptionar = descriptionar;
		this.honeys = honeys;
		this.ingredients = ingredients;
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

	@Column(name = "label", nullable = false, length = 45, unique = true)
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "labelfr", nullable = false, length = 45, unique = true)
	public String getLabelfr() {
		return this.labelfr;
	}

	public void setLabelfr(String labelfr) {
		this.labelfr = labelfr;
	}

	@Column(name = "labellat", nullable = false, length = 45, unique = true)
	public String getLabellat() {
		return this.labellat;
	}

	public void setLabellat(String labellat) {
		this.labellat = labellat;
	}

	@Column(name = "labelar", nullable = false, length = 45, unique = true)
	public String getLabelar() {
		return this.labelar;
	}

	public void setLabelar(String labelar) {
		this.labelar = labelar;
	}

	@Column(name = "labelmar", length = 45)
	public String getLabelmar() {
		return this.labelmar;
	}

	public void setLabelmar(String labelmar) {
		this.labelmar = labelmar;
	}

	@Column(name = "image", length = 100)
	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Column(name = "graphic")
	public byte[] getGraphic() {
		return this.graphic;
	}

	public void setGraphic(byte[] graphic) {
		this.graphic = graphic;
	}

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "descriptionfr", length = 65535)
	public String getDescriptionfr() {
		return this.descriptionfr;
	}

	public void setDescriptionfr(String descriptionfr) {
		this.descriptionfr = descriptionfr;
	}

	@Column(name = "descriptionar", length = 65535)
	public String getDescriptionar() {
		return this.descriptionar;
	}

	public void setDescriptionar(String descriptionar) {
		this.descriptionar = descriptionar;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plant")
	public Set<Honey> getHoneys() {
		return this.honeys;
	}

	public void setHoneys(Set<Honey> honeys) {
		this.honeys = honeys;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plant")
	public Set<Ingredient> getIngredients() {
		return this.ingredients;
	}

	public void setIngredients(Set<Ingredient> ingredients) {
		this.ingredients = ingredients;
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
