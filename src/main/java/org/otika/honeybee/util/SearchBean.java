package org.otika.honeybee.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.otika.honeybee.model.Ingredient;

@Named("searchBean")
@Stateful
@SessionScoped
public class SearchBean implements Serializable {

	private static final long serialVersionUID = 7346308736815039014L;
	private String keyword;
	private List<?> ingredients;
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	FullTextSession fullTextSession;
	FullTextEntityManager fullTextEntityManager;

	@PostConstruct
	public void init() {

		ingredients = new ArrayList<Ingredient>();

		try {
			fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Hibernate Search Method to search ingredients
	 */
	public void searchIngredients() {
		if (keyword != null) {
			QueryBuilder qb = fullTextEntityManager.getSearchFactory()
					.buildQueryBuilder().forEntity(Ingredient.class).get();
			org.apache.lucene.search.Query query = qb
					.keyword()
					.onFields("label", "labelfr", "labelar", "labelmar",
							"plant.label", "honey.label", "substance.label",
							"description", "descriptionar", "descriptionfr",
							"form", "virtues.label", "virtues.labelar",
							"virtues.labelfr", "defects.label",
							"defects.labelar", "defects.labelfr",
							"prescriptions.title", "prescriptions.titlear",
							"virtues.bodypart.label",
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

			javax.persistence.Query persistenceQuery = fullTextEntityManager
					.createFullTextQuery(query, Ingredient.class);
			List<?> result = persistenceQuery.getResultList();
			ingredients = result;
			// entityManager.getTransaction().commit();
			// entityManager.close();
		}
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
