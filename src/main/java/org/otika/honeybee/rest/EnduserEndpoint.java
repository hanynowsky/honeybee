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
import org.otika.honeybee.model.Enduser;

/**
 * 
 */
@Stateless
@Path("/endusers")
public class EnduserEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Enduser entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(EnduserEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Enduser entity = em.find(Enduser.class, id);
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
      TypedQuery<Enduser> findByIdQuery = em.createQuery("SELECT e FROM Enduser e LEFT JOIN FETCH e.tUsergroup LEFT JOIN FETCH e.tLanguage LEFT JOIN FETCH e.tWitnesses WHERE e.tId = :entityId", Enduser.class);
      findByIdQuery.setParameter("entityId", id);
      Enduser entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Enduser> listAll()
   {
      final List<Enduser> results = em.createQuery("SELECT e FROM Enduser e LEFT JOIN FETCH e.tUsergroup LEFT JOIN FETCH e.tLanguage LEFT JOIN FETCH e.tWitnesses", Enduser.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Enduser entity)
   {
      entity.setId(id);
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}