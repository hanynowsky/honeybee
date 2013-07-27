package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jboss.logging.Logger;
import org.otika.honeybee.model.Ingredient;

/**
 * Session scope is mandatory in order to keep search list persistent in data
 * table - with pagination - multiple requests
 */
@Named("searchBean")
@Stateful
@SessionScoped
@StatefulTimeout(value = 29, unit = TimeUnit.MINUTES)
public class SearchBean implements Serializable {

    private static final long serialVersionUID = 7346308736815039014L;
    private String keyword;
    private List<?> ingredients;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    protected FullTextSession fullTextSession;
    private FullTextEntityManager fullTextEntityManager;
    @Inject
    private UtilityBean utilityBean;

    @PostConstruct
    public void init() {

        ingredients = new ArrayList<>();

        try {
            fullTextEntityManager = Search
                    .getFullTextEntityManager(this.entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            Logger.getLogger(getClass().getName()).
                    error(e.getMessage() + " : " + e.getCause());
        }
    }

    /**
     * Hibernate Search Method to search ingredients
     */
    public void searchIngredients() {
        if (keyword != null && !keyword.equals("")) {
            QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(Ingredient.class).get();
            org.apache.lucene.search.Query query = qb
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(1)
                    .onFields("label", "labelfr", "labelar", "labelmar",
                    "plant.label", "honey.labelar", "substance.label",
                    "substance.labelar", "substance.labelfr",
                    "honey.labelfr", "honey.label", "plant.labelfr",
                    "plant.labelar", "plant.season","plant.type",
                    "description", "descriptionar",
                    "descriptionfr", "form", "virtues.label",
                    "virtues.labelar", "virtues.labelfr",
                    "defects.label", "defects.labelar",
                    "defects.labelfr", "prescriptions.title",
                    "prescriptions.titlear", "virtues.bodypart.label",
                    "prescriptions.treatment",
                    "virtues.bodypart.labelar",
                    "virtues.bodypart.labelfr",
                    "prescriptions.treatmentfr",
                    "prescriptions.treatmentar",
                    "prescriptions.preparation",
                    "prescriptions.preparationfr",
                    "prescriptions.preparationar",
                    "prescriptions.titlefr", "defects.bodypart.label",
                    "prescriptions.witnesses.subject",
                    "defects.bodypart.labelar",
                    "defects.bodypart.labelfr",
                    "prescriptions.author.name",
                    "prescriptions.author.namear",
                    "prescriptions.author.surname",
                    "prescriptions.author.surnamear",
                    "prescriptions.author.bio",
                    "prescriptions.author.expertise",
                    "prescriptions.author.country",
                    "prescriptions.author.countryar").matching(keyword)
                    .createQuery();

            // TODO add sorting by id
            javax.persistence.Query persistenceQuery = fullTextEntityManager
                    .createFullTextQuery(query, Ingredient.class);
            int i = persistenceQuery.getResultList().size();
            String message = "Search returned " + i + " Results";
            utilityBean.showMessage("INFO", message, "-");
            ingredients = persistenceQuery.getResultList();

            // entityManager.getTransaction().commit();
            // entityManager.close();
        } else {
            String message = "Please type a keyword";
            utilityBean.showMessage("WARN", message, "-");
        }
    }

    /**
     * Reset keyword and ingredient list.
     * <p>
     * The method cannot be decorated by <em>@Remove</em> in order to free from
     * memory the instance of Search Bean, for it is a non-dependant bean
     * </p>
     */
    //@Remove
    public void release() {
        setKeyword("");
        ingredients.clear();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<?> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<?> ingredients) {
        this.ingredients = ingredients;
    }
}
