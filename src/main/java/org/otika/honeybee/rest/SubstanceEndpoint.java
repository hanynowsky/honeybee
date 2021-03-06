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

import org.otika.honeybee.model.Substance;

/**
 * 
 */
@Stateless
@Path("/substances")
public class SubstanceEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Substance entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(SubstanceEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Substance entity = em.find(Substance.class, id);
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
      TypedQuery<Substance> findByIdQuery = em.createQuery("SELECT s FROM Substance s LEFT JOIN FETCH s.tIngredients WHERE s.tId = :entityId", Substance.class);
      findByIdQuery.setParameter("entityId", id);
      Substance entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Substance> listAll()
   {
      return em.createQuery("SELECT s FROM Substance s LEFT JOIN FETCH s.tIngredients", Substance.class).getResultList();
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Substance entity)
   {
      entity.setId(id);
    em.merge(entity);
      return Response.noContent().build();
   }
}