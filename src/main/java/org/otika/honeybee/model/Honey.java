package org.otika.honeybee.model;

// Generated May 25, 2013 2:45:04 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.search.annotations.Field;

/**
 * Honey generated by hbm2java
 */
@Entity
@Table(name = "honey", catalog = "honeybee")
public class Honey implements java.io.Serializable {

	private static final long serialVersionUID = -8618130327109911881L;
	private Long id;
	private Integer version;
	private Plant plant;
	private String label;
	private String labelfr;
	private String labelar;
	private String description;
	private String descriptionar;
	private String descriptionfr;
	private Set<Ingredient> ingredients = new HashSet<Ingredient>(0);

	public Honey() {
	}

	public Honey(String label, String labelfr, String labelar) {
		this.label = label;
		this.labelfr = labelfr;
		this.labelar = labelar;
	}

	public Honey(Plant plant, String label, String labelfr, String labelar,
			String description, String descriptionar, String descriptionfr,
			Set<Ingredient> ingredients) {
		this.plant = plant;
		this.label = label;
		this.labelfr = labelfr;
		this.labelar = labelar;
		this.description = description;
		this.descriptionar = descriptionar;
		this.descriptionfr = descriptionfr;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plant_id")
	public Plant getPlant() {
		return this.plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}

	@Field
	@Column(name = "label", nullable = false, length = 45)
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Field
	@Column(name = "labelfr", nullable = false, length = 45)
	public String getLabelfr() {
		return this.labelfr;
	}

	public void setLabelfr(String labelfr) {
		this.labelfr = labelfr;
	}

	@Field
	@Column(name = "labelar", nullable = false, length = 45)
	public String getLabelar() {
		return this.labelar;
	}

	public void setLabelar(String labelar) {
		this.labelar = labelar;
	}

	@Field
	@Column(name = "description", length = 445)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Field
	@Column(name = "descriptionar", length = 445)
	public String getDescriptionar() {
		return this.descriptionar;
	}

	public void setDescriptionar(String descriptionar) {
		this.descriptionar = descriptionar;
	}

	@Field
	@Column(name = "descriptionfr", length = 445)
	public String getDescriptionfr() {
		return this.descriptionfr;
	}

	public void setDescriptionfr(String descriptionfr) {
		this.descriptionfr = descriptionfr;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "honey")
	public Set<Ingredient> getIngredients() {
		return this.ingredients;
	}

	public void setIngredients(Set<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return label + " " + labelar;
	}

}
