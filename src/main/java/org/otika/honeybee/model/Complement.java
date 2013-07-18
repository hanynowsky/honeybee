package org.otika.honeybee.model;

// Generated May 22, 2013 4:41:58 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Complement generated by hbm2java
 */
@Entity
@Table(name = "complement", catalog = "honeybee")
@XmlRootElement
public class Complement implements java.io.Serializable {
	
	private static final long serialVersionUID = -791322671457953092L;
	private Long id;
	private Ingredient ingredient;
	private String content;
	private Integer version;
	private Set<Prescription> prescriptions = new HashSet<Prescription>(0);

	public Complement() {
	}

	public Complement(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public Complement(Ingredient ingredient, String content,
			Set<Prescription> prescriptions) {
		this.ingredient = ingredient;
		this.content = content;
		this.prescriptions = prescriptions;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id", nullable = false)
	public Ingredient getIngredient() {
		return this.ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	@Column(name = "content", length = 65535)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Version
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "complement_prescription", catalog = "honeybee", joinColumns = { @JoinColumn(name = "complement_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "prescription_id", nullable = false, updatable = false) })
	public Set<Prescription> getPrescriptions() {
		return this.prescriptions;
	}

	public void setPrescriptions(Set<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ingredient.getLabel() + " " + ingredient.getLabelar()+" | "+ingredient.getForm();
	}

}
