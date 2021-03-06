package org.otika.honeybee.model;

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

import org.hibernate.search.annotations.Field;

/**
 * Author Entity
 */
@Entity
@Table(name = "author", catalog = "honeybee")
@XmlRootElement
public class Author implements java.io.Serializable
{

   private static final long serialVersionUID = 1L;
   private Long id;
   private Integer version;
   private String name;
   private String surname;
   private String namear;
   private String surnamear;
   private String bio;
   private String telephone;
   private String website;
   private String email;
   private String expertise;
   private String country;
   private String countryar;
   private Set<Prescription> prescriptions = new HashSet<Prescription>(0);

   public Author()
   {
   }

   public Author(String name, String surname, String namear, String surnamear)
   {
      this.name = name;
      this.surname = surname;
      this.namear = namear;
      this.surnamear = surnamear;
   }

   public Author(String name, String surname, String namear, String surnamear,
         String bio, String telephone, String website, String email,
         String expertise, String country, String countryar,
         Set<Prescription> prescriptions)
   {
      this.name = name;
      this.surname = surname;
      this.namear = namear;
      this.surnamear = surnamear;
      this.bio = bio;
      this.telephone = telephone;
      this.website = website;
      this.email = email;
      this.expertise = expertise;
      this.country = country;
      this.countryar = countryar;
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

   @Field
   @Column(name = "name", nullable = false, length = 45)
   public String getName()
   {
      return this.name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Field
   @Column(name = "surname", nullable = false, length = 45)
   public String getSurname()
   {
      return this.surname;
   }

   public void setSurname(String surname)
   {
      this.surname = surname;
   }

   @Field
   @Column(name = "namear", nullable = false, length = 45)
   public String getNamear()
   {
      return this.namear;
   }

   public void setNamear(String namear)
   {
      this.namear = namear;
   }

   @Field
   @Column(name = "surnamear", nullable = false, length = 45)
   public String getSurnamear()
   {
      return this.surnamear;
   }

   public void setSurnamear(String surnamear)
   {
      this.surnamear = surnamear;
   }

   @Field
   @Column(name = "bio", length = 3055)
   public String getBio()
   {
      return this.bio;
   }

   public void setBio(String bio)
   {
      this.bio = bio;
   }

   @Field
   @Column(name = "telephone", length = 25)
   public String getTelephone()
   {
      return this.telephone;
   }

   public void setTelephone(String telephone)
   {
      this.telephone = telephone;
   }

   @Field
   @Column(name = "website", length = 100)
   public String getWebsite()
   {
      return this.website;
   }

   public void setWebsite(String website)
   {
      this.website = website;
   }

   @Field
   @Column(name = "email", length = 65)
   public String getEmail()
   {
      return this.email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   @Field
   @Column(name = "expertise", length = 35)
   public String getExpertise()
   {
      return this.expertise;
   }

   public void setExpertise(String expertise)
   {
      this.expertise = expertise;
   }

   @Field
   @Column(name = "country", length = 25)
   public String getCountry()
   {
      return this.country;
   }

   public void setCountry(String country)
   {
      this.country = country;
   }

   @Field
   @Column(name = "countryar", length = 25)
   public String getCountryar()
   {
      return this.countryar;
   }

   public void setCountryar(String countryar)
   {
      this.countryar = countryar;
   }

   @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
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
      return name + " " + namear;
   }

}
