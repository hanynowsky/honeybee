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

import org.otika.honeybee.model.Virtue;

/**
 * 
 */
@Stateless
@Path("/virtues")
public class VirtueEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Virtue entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(VirtueEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Virtue entity = em.find(Virtue.class, id);
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
      TypedQuery<Virtue> findByIdQuery = em.createQuery("SELECT v FROM Virtue v LEFT JOIN FETCH v.tBodypart LEFT JOIN FETCH v.tPrescriptions LEFT JOIN FETCH v.tIngredients WHERE v.tId = :entityId", Virtue.class);
      findByIdQuery.setParameter("entityId", id);
      Virtue entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Virtue> listAll()
   {
     return em.createQuery("SELECT v FROM Virtue v LEFT JOIN FETCH v.tBodypart LEFT JOIN FETCH v.tPrescriptions LEFT JOIN FETCH v.tIngredients", Virtue.class).getResultList();
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Virtue entity)
   {
      entity.setId(id);
   em.merge(entity);
      return Response.noContent().build();
   }
}