package org.otika.honeybee.model;

// Generated May 19, 2013 4:05:52 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Help
 */
@Entity
@Table(name = "help", catalog = "honeybee")
@XmlRootElement
public class Help implements java.io.Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private Long id;
   private boolean valid;
   private Integer version;
   private String title;
   private String content;

   public Help()
   {
   }

   public Help(String title, String content)
   {
      this.title = title;
      this.content = content;
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

   @Column(name = "title", nullable = false, length = 45)
   public String getTitle()
   {
      return this.title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   @Column(name = "content", nullable = false, length = 65535)
   public String getContent()
   {
      return this.content;
   }

   public void setContent(String content)
   {
      this.content = content;
   }

   /**
    * @return valid a valid help object
    */
   @Column(name = "valid", nullable = false)
   public boolean isValid()
   {
      return valid;
   }

   /**
    * @param valid
    *            the valid to set
    */
   public void setValid(boolean valid)
   {
      this.valid = valid;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "Help [id=" + id + ", title=" + title + "]";
   }

}
