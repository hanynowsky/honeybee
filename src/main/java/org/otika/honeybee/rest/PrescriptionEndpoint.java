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
import org.otika.honeybee.model.Prescription;

/**
 * 
 */
@Stateless
@Path("/prescriptions")
public class PrescriptionEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(Prescription entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(PrescriptionEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Prescription entity = em.find(Prescription.class, id);
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
      TypedQuery<Prescription> findByIdQuery = em.createQuery("SELECT p FROM Prescription p LEFT JOIN FETCH p.tAuthor LEFT JOIN FETCH p.tIngredients LEFT JOIN FETCH p.tWitnesses LEFT JOIN FETCH p.tDefects LEFT JOIN FETCH p.tVirtues WHERE p.tId = :entityId", Prescription.class);
      findByIdQuery.setParameter("entityId", id);
      Prescription entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<Prescription> listAll()
   {
      final List<Prescription> results = em.createQuery("SELECT p FROM Prescription p LEFT JOIN FETCH p.tAuthor LEFT JOIN FETCH p.tIngredients LEFT JOIN FETCH p.tWitnesses LEFT JOIN FETCH p.tDefects LEFT JOIN FETCH p.tVirtues", Prescription.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(@PathParam("id") Long id, Prescription entity)
   {
      entity.setId(id);
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}