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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Language generated by hbm2java
 */
@Entity
@Table(name = "language"
      , catalog = "honeybee")
@XmlRootElement
public class Language implements java.io.Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 4715909298837085033L;
   private Long id;
   private Integer version;
   private String code;
   private String label;
   private Set<Enduser> endusers = new HashSet<Enduser>(0);

   public Language()
   {
   }

   public Language(String code)
   {
      this.code = code;
   }

   public Language(String code, String label, Set<Enduser> endusers)
   {
      this.code = code;
      this.label = label;
      this.endusers = endusers;
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

   @Column(name = "code", nullable = false, length = 6)
   public String getCode()
   {
      return this.code;
   }

   public void setCode(String code)
   {
      this.code = code;
   }

   @Column(name = "label", length = 45)
   public String getLabel()
   {
      return this.label;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }

   @OneToMany(fetch = FetchType.LAZY, mappedBy = "language")
   public Set<Enduser> getEndusers()
   {
      return this.endusers;
   }

   public void setEndusers(Set<Enduser> endusers)
   {
      this.endusers = endusers;
   }

   @Override
   public String toString()
   {
      return code +" "+label;
   }
}