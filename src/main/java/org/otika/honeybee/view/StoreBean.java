package org.otika.honeybee.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
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

import org.otika.honeybee.model.Store;
import org.otika.honeybee.util.BundleBean;
import org.otika.honeybee.util.Repository;
import org.otika.honeybee.util.UtilityBean;

/**
 * Backing bean for Store entities.
 * <p>
 * This class provides CRUD functionality for all Store entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class StoreBean implements Serializable
{

   private static final long serialVersionUID = 1L;
   @Inject
   private UtilityBean utilityBean;
   private BundleBean bundle = new BundleBean();
   private Long storeId;
   @EJB
   private Repository repository;

   /*
    * Support creating and retrieving Store entities
    */
   
   @PostConstruct
   public void init(){
	  
   }

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private Store store;

   public Store getStore()
   {
      return this.store;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;
   private Class<Store> storeClass;

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
         this.store = this.example;
      }
      else
      {
         this.store = findById(getId());
      }
   }

   public Store findById(Long id)
   {

      return this.entityManager.find(Store.class, id);
   }

   /*
    * Support updating and deleting Store entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.store);
           // return "search?faces-redirect=true";
            utilityBean.showMessage("INFO", bundle.i18n("item_created")+" : "+this.store.getLabel(), "");
            return "view?faces-redirect=false&id=" + this.store.getId();
         }
         else
         {
            this.entityManager.merge(this.store);
            utilityBean.showMessage("INFO", bundle.i18n("item_updated")+" : "+this.store.getLabel(), "");
           // return "view?faces-redirect=true&id=" + this.store.getId();
            return "view?faces-redirect=false&id=" + this.store.getId();
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
    	 utilityBean.showMessage("INFO", bundle.i18n("item_deleted")+" : "+findById(getId()).getLabel(), "");
         this.entityManager.remove(findById(getId()));
         this.entityManager.flush();         
         //return "search?faces-redirect=true";
         return "/misc/redirect.xhtml?faces-redirect=false";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching Store entities with pagination
    */

   private int page;
   private long count;
   private List<Store> pageItems;

   private Store example = new Store();

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

   public Store getExample()
   {
      return this.example;
   }

   public void setExample(Store example)
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
      Root<Store> root = countCriteria.from(Store.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Store> criteria = builder.createQuery(Store.class);
      root = criteria.from(Store.class);
      TypedQuery<Store> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Store> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String label = this.example.getLabel();
      if (label != null && !"".equals(label))
      {
         predicatesList.add(builder.like(root.<String> get("label"), '%' + label + '%'));
      }
      String labelar = this.example.getLabelar();
      if (labelar != null && !"".equals(labelar))
      {
         predicatesList.add(builder.like(root.<String> get("labelar"), '%' + labelar + '%'));
      }
      String address = this.example.getAddress();
      if (address != null && !"".equals(address))
      {
         predicatesList.add(builder.like(root.<String> get("address"), '%' + address + '%'));
      }
      String addressar = this.example.getAddressar();
      if (addressar != null && !"".equals(addressar))
      {
         predicatesList.add(builder.like(root.<String> get("addressar"), '%' + addressar + '%'));
      }
      String phone = this.example.getPhone();
      if (phone != null && !"".equals(phone))
      {
         predicatesList.add(builder.like(root.<String> get("phone"), '%' + phone + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Store> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Store entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Store> getAll() {
      CriteriaQuery<Store> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Store.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Store.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final StoreBean ejbProxy = this.sessionContext.getBusinessObject(StoreBean.class);

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

            return String.valueOf(((Store) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Store add = new Store();

   public Store getAdd()
   {
      return this.add;
   }

   public Store getAdded()
   {
      Store added = this.add;
      this.add = new Store();
      return added;
   }

   /*
    * Utility Methods
    */
   
   /**
	 * Expensive method that retrieves the next ID
	 * @param id
	 * @return store id
	 */
	public Long getNextId(Long id) {		
		try {
			Iterator<Store> iterator = getAll().iterator();
			Store store = findById(id);
			while (iterator.hasNext()) {
				if (store.getId() <=  iterator.next().getId()) {
					storeId = iterator.next().getId();
					break;
				} else {
					// TODO
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(StoreBean.class.getName()).log(Level.ALL, ex.getMessage());
			System.out.println("Next Store ID bug:  " + ex);
		}
		return storeId;
	} // END OF METHOD
   
   
	/**
	 * Expensive method that retrieves the previous ID
	 * @param id
	 * @return Store ID
	 */
	public Long getPreviousId(Long id) {		
		try {
			Iterator<Store> iterator = repository
					.findAllStoreItemsOrderedByIdDESC().iterator();
			Store store = repository.findStoreById(id);
			while (iterator.hasNext()) {				
				if (iterator.next().getId() <= store.getId()) {
					this.storeId = iterator.next().getId();
					break;
				} else {
					// TODO
				}
			}
		} catch (Exception ex) {
			System.err.println("Next ID bug:  " + ex);
			Logger.getLogger(StoreBean.class.getName()).log(Level.ALL,
					ex.getMessage(), ex);
		}
		return this.storeId;
	} // END OF METHOD
	
   /*
    * Getters & Setters
    */
public Class<Store> getStoreClass() {
	return storeClass;
}

public Long getStoreId() {
	return storeId;
}

public void setStoreId(Long storeId) {
	this.storeId = storeId;
}

} // END OF CLASS