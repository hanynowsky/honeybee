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
 * Usergroup generated by hbm2java
 */
@Entity
@Table(name = "usergroup"
      , catalog = "honeybee")
@XmlRootElement
public class Usergroup implements java.io.Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 4789113478860063390L;
   private Long id;
   private Integer version;
   private String groupcode;
   private String description;
   private Set<Enduser> endusers = new HashSet<Enduser>(0);

   public Usergroup()
   {
   }

   public Usergroup(String groupcode)
   {
      this.groupcode = groupcode;
   }

   public Usergroup(String groupcode, String description, Set<Enduser> endusers)
   {
      this.groupcode = groupcode;
      this.description = description;
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

   @Column(name = "groupcode", nullable = false, length = 15)
   public String getGroupcode()
   {
      return this.groupcode;
   }

   public void setGroupcode(String groupcode)
   {
      this.groupcode = groupcode;
   }

   @Column(name = "description", length = 65535)
   public String getDescription()
   {
      return this.description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   @OneToMany(fetch = FetchType.LAZY, mappedBy = "usergroup")
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
      return groupcode;
   }
}