package org.otika.honeybee.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.otika.honeybee.model.Store;

/**
 * 
 */
@Stateless
@Path("/stores")
public class StoreEndpoint
{
   @PersistenceContext
   private EntityManager em;

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response create(Store entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(StoreEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Store entity = em.find(Store.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response findById(@PathParam("id") Long id)
   {
	   Store entity =  em.find(Store.class,id);     
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<Store> listAll()
   {
	   //MessageBodyWriter<Store> mbw;
      return em.createQuery("SELECT s FROM Store s", Store.class).getResultList();
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response update(@PathParam("id") Long id, Store entity)
   {
      entity.setId(id);
      em.merge(entity);     
      return Response.noContent().build();
   }
}