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
import org.otika.honeybee.model.Usergroup;

/**
 * 
 */
@Stateless
@Path("/usergroups")
public class UsergroupEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Usergroup entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(UsergroupEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Usergroup entity = em.find(Usergroup.class, id);
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
      TypedQuery<Usergroup> findByIdQuery = em.createQuery("SELECT u FROM Usergroup u LEFT JOIN FETCH u.tEndusers WHERE u.tId = :entityId", Usergroup.class);
      findByIdQuery.setParameter("entityId", id);
      Usergroup entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Usergroup> listAll()
   {
      final List<Usergroup> results = em.createQuery("SELECT u FROM Usergroup u LEFT JOIN FETCH u.tEndusers", Usergroup.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Usergroup entity)
   {
      entity.setId(id);
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}