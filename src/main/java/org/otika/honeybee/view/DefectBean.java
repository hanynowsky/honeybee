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

import org.otika.honeybee.model.Defect;
import org.otika.honeybee.model.Bodypart;

/**
 * Backing bean for Defect entities.
 * <p>
 * This class provides CRUD functionality for all Defect entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class DefectBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Defect entities
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

   private Defect defect;

   public Defect getDefect()
   {
      return this.defect;
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
         this.defect = this.example;
      }
      else
      {
         this.defect = findById(getId());
      }
   }

   public Defect findById(Long id)
   {

      return this.entityManager.find(Defect.class, id);
   }

   /*
    * Support updating and deleting Defect entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.defect);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.defect);
            return "view?faces-redirect=true&id=" + this.defect.getId();
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
    * Support searching Defect entities with pagination
    */

   private int page;
   private long count;
   private List<Defect> pageItems;

   private Defect example = new Defect();

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

   public Defect getExample()
   {
      return this.example;
   }

   public void setExample(Defect example)
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
      Root<Defect> root = countCriteria.from(Defect.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Defect> criteria = builder.createQuery(Defect.class);
      root = criteria.from(Defect.class);
      TypedQuery<Defect> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Defect> root)
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

   public List<Defect> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Defect entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Defect> getAll()
   {

      CriteriaQuery<Defect> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Defect.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Defect.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final DefectBean ejbProxy = this.sessionContext.getBusinessObject(DefectBean.class);

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

            return String.valueOf(((Defect) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Defect add = new Defect();

   public Defect getAdd()
   {
      return this.add;
   }

   public Defect getAdded()
   {
      Defect added = this.add;
      this.add = new Defect();
      return added;
   }
}