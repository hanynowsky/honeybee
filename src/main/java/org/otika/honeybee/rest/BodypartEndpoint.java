package org.otika.honeybee.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.otika.honeybee.model.Bodypart;

/**
 * 
 */
@Stateless
@Path("/bodyparts")
public class BodypartEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Bodypart entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(BodypartEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Bodypart entity = em.find(Bodypart.class, id);
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
      TypedQuery<Bodypart> findByIdQuery = em.createQuery("SELECT b FROM Bodypart b LEFT JOIN FETCH b.tDefects LEFT JOIN FETCH b.tVirtues WHERE b.tId = :entityId", Bodypart.class);
      findByIdQuery.setParameter("entityId", id);
      Bodypart entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Bodypart> listAll()
   {
      final List<Bodypart> results = em.createQuery("SELECT b FROM Bodypart b LEFT JOIN FETCH b.tDefects LEFT JOIN FETCH b.tVirtues", Bodypart.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Bodypart entity)
   {
      entity.setId(id);
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}