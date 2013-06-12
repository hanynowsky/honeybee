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
import org.otika.honeybee.model.Defect;

/**
 * 
 */
@Stateless
@Path("/defects")
public class DefectEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Defect entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(DefectEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Defect entity = em.find(Defect.class, id);
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
      TypedQuery<Defect> findByIdQuery = em.createQuery("SELECT d FROM Defect d LEFT JOIN FETCH d.tBodypart LEFT JOIN FETCH d.tIngredients LEFT JOIN FETCH d.tPrescriptions WHERE d.tId = :entityId", Defect.class);
      findByIdQuery.setParameter("entityId", id);
      Defect entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Defect> listAll()
   {
      final List<Defect> results = em.createQuery("SELECT d FROM Defect d LEFT JOIN FETCH d.tBodypart LEFT JOIN FETCH d.tIngredients LEFT JOIN FETCH d.tPrescriptions", Defect.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Defect entity)
   {
      entity.setId(id);
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}