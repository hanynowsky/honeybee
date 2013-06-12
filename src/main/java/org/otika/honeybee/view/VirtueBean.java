package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.otika.honeybee.model.Virtue;
import org.otika.honeybee.model.Bodypart;

/**
 * Backing bean for Virtue entities.
 * <p>
 * This class provides CRUD functionality for all Virtue entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class VirtueBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Virtue entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private Virtue virtue;

   public Virtue getVirtue()
   {
      return this.virtue;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {
	   if (this.conversation.isTransient()) {
			this.conversation.begin();
		} else {
			this.conversation.end();
		}
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
      }

      if (this.id == null)
      {
         this.virtue = this.example;
      }
      else
      {
         this.virtue = findById(getId());
      }
   }

   public Virtue findById(Long id)
   {

      return this.entityManager.find(Virtue.class, id);
   }

   /*
    * Support updating and deleting Virtue entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.virtue);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.virtue);
            return "view?faces-redirect=true&id=" + this.virtue.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         this.entityManager.remove(findById(getId()));
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching Virtue entities with pagination
    */

   private int page;
   private long count;
   private List<Virtue> pageItems;

   private Virtue example = new Virtue();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public Virtue getExample()
   {
      return this.example;
   }

   public void setExample(Virtue example)
   {
      this.example = example;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate(AjaxBehaviorEvent evt)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Virtue> root = countCriteria.from(Virtue.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Virtue> criteria = builder.createQuery(Virtue.class);
      root = criteria.from(Virtue.class);
      TypedQuery<Virtue> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Virtue> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      Bodypart bodypart = this.example.getBodypart();
      if (bodypart != null)
      {
         predicatesList.add(builder.equal(root.get("bodypart"), bodypart));
      }
      String label = this.example.getLabel();
      if (label != null && !"".equals(label))
      {
         predicatesList.add(builder.like(root.<String> get("label"), '%' + label + '%'));
      }
      String labelfr = this.example.getLabelfr();
      if (labelfr != null && !"".equals(labelfr))
      {
         predicatesList.add(builder.like(root.<String> get("labelfr"), '%' + labelfr + '%'));
      }
      String labelar = this.example.getLabelar();
      if (labelar != null && !"".equals(labelar))
      {
         predicatesList.add(builder.like(root.<String> get("labelar"), '%' + labelar + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Virtue> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Virtue entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Virtue> getAll()
   {

      CriteriaQuery<Virtue> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Virtue.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Virtue.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final VirtueBean ejbProxy = this.sessionContext.getBusinessObject(VirtueBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((Virtue) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Virtue add = new Virtue();

   public Virtue getAdd()
   {
      return this.add;
   }

   public Virtue getAdded()
   {
      Virtue added = this.add;
      this.add = new Virtue();
      return added;
   }
}