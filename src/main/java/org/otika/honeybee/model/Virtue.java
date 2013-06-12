package org.otika.honeybee.model;

// Generated May 19, 2013 4:05:52 PM by Hibernate Tools 3.4.0.CR1

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
 * Virtue generated by hbm2java
 */
@Entity
@Table(name = "virtue"
      , catalog = "honeybee")
@XmlRootElement
public class Virtue implements java.io.Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 8493853862743847230L;
   private Long id;
   private Long version;
   private Bodypart bodypart;
   private String label;
   private String labelfr;
   private String labelar;
   private Set<Prescription> prescriptions = new HashSet<Prescription>(0);
   private Set<Ingredient> ingredients = new HashSet<Ingredient>(0);

   public Virtue()
   {
   }

   public Virtue(Bodypart bodypart, String label, String labelfr, String labelar)
   {
      this.bodypart = bodypart;
      this.label = label;
      this.labelfr = labelfr;
      this.labelar = labelar;
   }

   public Virtue(Bodypart bodypart, String label, String labelfr, String labelar, Set<Prescription> prescriptions, Set<Ingredient> ingredients)
   {
      this.bodypart = bodypart;
      this.label = label;
      this.labelfr = labelfr;
      this.labelar = labelar;
      this.prescriptions = prescriptions;
      this.ingredients = ingredients;
   }

   @Id
   @GeneratedValue(strategy = IDENTITY)
   @Column(name = "id", unique = true, nullable = false)
   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   @Version
   @Column(name = "version")
   public Long getVersion()
   {
      return this.version;
   }

   public void setVersion(Long version)
   {
      this.version = version;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "bodypart_id", nullable = false)
   public Bodypart getBodypart()
   {
      return this.bodypart;
   }

   public void setBodypart(Bodypart bodypart)
   {
      this.bodypart = bodypart;
   }

   @Column(name = "label", nullable = false, length = 145)
   public String getLabel()
   {
      return this.label;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }

   @Column(name = "labelfr", nullable = false, length = 145)
   public String getLabelfr()
   {
      return this.labelfr;
   }

   public void setLabelfr(String labelfr)
   {
      this.labelfr = labelfr;
   }

   @Column(name = "labelar", nullable = false, length = 145)
   public String getLabelar()
   {
      return this.labelar;
   }

   public void setLabelar(String labelar)
   {
      this.labelar = labelar;
   }

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "virtue_prescription", catalog = "honeybee", joinColumns = {
         @JoinColumn(name = "virtue_id", nullable = false, updatable = false) }, inverseJoinColumns = {
         @JoinColumn(name = "prescription_id", nullable = false, updatable = false) })
   public Set<Prescription> getPrescriptions()
   {
      return this.prescriptions;
   }

   public void setPrescriptions(Set<Prescription> prescriptions)
   {
      this.prescriptions = prescriptions;
   }

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "virtue_ingredient", catalog = "honeybee", joinColumns = {
         @JoinColumn(name = "virtue_id", nullable = false, updatable = false) }, inverseJoinColumns = {
         @JoinColumn(name = "ingredient_id", nullable = false, updatable = false) })
   public Set<Ingredient> getIngredients()
   {
      return this.ingredients;
   }

   public void setIngredients(Set<Ingredient> ingredients)
   {
      this.ingredients = ingredients;
   }

   @Override
   public String toString()
   {
      return label +" " +labelar;
   }
}
