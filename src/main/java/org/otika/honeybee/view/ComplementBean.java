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

import org.otika.honeybee.model.Complement;
import org.otika.honeybee.model.Ingredient;

/**
 * Backing bean for Complement entities.
 * <p>
 * This class provides CRUD functionality for all Complement entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ComplementBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Complement entities
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

   private Complement complement;

   public Complement getComplement()
   {
      return this.complement;
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
         this.complement = this.example;
      }
      else
      {
         this.complement = findById(getId());
      }
   }

   public Complement findById(Long id)
   {

      return this.entityManager.find(Complement.class, id);
   }

   /*
    * Support updating and deleting Complement entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.complement);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.complement);
            return "view?faces-redirect=true&id=" + this.complement.getId();
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
    * Support searching Complement entities with pagination
    */

   private int page;
   private long count;
   private List<Complement> pageItems;

   private Complement example = new Complement();

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

   public Complement getExample()
   {
      return this.example;
   }

   public void setExample(Complement example)
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
      Root<Complement> root = countCriteria.from(Complement.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Complement> criteria = builder.createQuery(Complement.class);
      root = criteria.from(Complement.class);
      TypedQuery<Complement> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Complement> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      Ingredient ingredient = this.example.getIngredient();
      if (ingredient != null)
      {
         predicatesList.add(builder.equal(root.get("ingredient"), ingredient));
      }
      String content = this.example.getContent();
      if (content != null && !"".equals(content))
      {
         predicatesList.add(builder.like(root.<String> get("content"), '%' + content + '%'));
      }           

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Complement> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Complement entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Complement> getAll()
   {

      CriteriaQuery<Complement> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Complement.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Complement.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final ComplementBean ejbProxy = this.sessionContext.getBusinessObject(ComplementBean.class);

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

            return String.valueOf(((Complement) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Complement add = new Complement();

   public Complement getAdd()
   {
      return this.add;
   }

   public Complement getAdded()
   {
      Complement added = this.add;
      this.add = new Complement();
      return added;
   }
}