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

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;

/**
 * Defect generated by hbm2java
 */
@Entity
@Table(name = "defect"
      , catalog = "honeybee")
@XmlRootElement
public class Defect implements java.io.Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = -3160193063044385626L;
   private Long id;
   private Integer version;
   private Bodypart bodypart;
   private String label;
   private String labelfr;
   private String labelar;
   private Set<Ingredient> ingredients = new HashSet<Ingredient>(0);
   private Set<Prescription> prescriptions = new HashSet<Prescription>(0);

   public Defect()
   {
   }

   public Defect(Bodypart bodypart, String label, String labelfr, String labelar)
   {
      this.bodypart = bodypart;
      this.label = label;
      this.labelfr = labelfr;
      this.labelar = labelar;
   }

   public Defect(Bodypart bodypart, String label, String labelfr, String labelar, Set<Ingredient> ingredients, Set<Prescription> prescriptions)
   {
      this.bodypart = bodypart;
      this.label = label;
      this.labelfr = labelfr;
      this.labelar = labelar;
      this.ingredients = ingredients;
      this.prescriptions = prescriptions;
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
   public Integer getVersion()
   {
      return this.version;
   }

   public void setVersion(Integer version)
   {
      this.version = version;
   }

   @IndexedEmbedded
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

   @Field
   @Column(name = "label", nullable = false, length = 100)
   public String getLabel()
   {
      return this.label;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }

   @Field
   @Column(name = "labelfr", nullable = false, length = 100)
   public String getLabelfr()
   {
      return this.labelfr;
   }

   public void setLabelfr(String labelfr)
   {
      this.labelfr = labelfr;
   }

   @Field
   @Column(name = "labelar", nullable = false, length = 100)
   public String getLabelar()
   {
      return this.labelar;
   }

   public void setLabelar(String labelar)
   {
      this.labelar = labelar;
   }

   @ContainedIn
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "defect_ingredient", catalog = "honeybee", joinColumns = {
         @JoinColumn(name = "defect_id", nullable = false, updatable = false) }, inverseJoinColumns = {
         @JoinColumn(name = "ingredient_id", nullable = false, updatable = false) })
   public Set<Ingredient> getIngredients()
   {
      return this.ingredients;
   }

   public void setIngredients(Set<Ingredient> ingredients)
   {
      this.ingredients = ingredients;
   }

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "defect_prescription", catalog = "honeybee", joinColumns = {
         @JoinColumn(name = "defect_id", nullable = false, updatable = false) }, inverseJoinColumns = {
         @JoinColumn(name = "prescription_id", nullable = false, updatable = false) })
   public Set<Prescription> getPrescriptions()
   {
      return this.prescriptions;
   }

   public void setPrescriptions(Set<Prescription> prescriptions)
   {
      this.prescriptions = prescriptions;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return label + " "+labelar + " "+labelfr;
   }

}
