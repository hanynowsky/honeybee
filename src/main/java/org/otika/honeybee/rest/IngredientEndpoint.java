package org.otika.honeybee.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.otika.honeybee.model.Ingredient;

/**
 * 
 */
@Stateless
@Path("/ingredients")
public class IngredientEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Ingredient entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(IngredientEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Ingredient entity = em.find(Ingredient.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("application/xml")
   public Response findById(@PathParam("id") Long id)
   {
      TypedQuery<Ingredient> findByIdQuery = em.createQuery("SELECT i FROM Ingredient i LEFT JOIN FETCH i.tPlant LEFT JOIN FETCH i.tHoney LEFT JOIN FETCH i.tSubstance LEFT JOIN FETCH i.tVirtues LEFT JOIN FETCH i.tPrescriptions LEFT JOIN FETCH i.tDefects WHERE i.tId = :entityId", Ingredient.class);
      findByIdQuery.setParameter("entityId", id);
      Ingredient entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Ingredient> listAll()
   {
      return em.createQuery("SELECT i FROM Ingredient i LEFT JOIN FETCH i.tPlant LEFT JOIN FETCH i.tHoney LEFT JOIN FETCH i.tSubstance LEFT JOIN FETCH i.tVirtues LEFT JOIN FETCH i.tPrescriptions LEFT JOIN FETCH i.tDefects", Ingredient.class).getResultList();
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Ingredient entity)
   {
      entity.setId(id);
      em.merge(entity);
      return Response.noContent().build();
   }
}