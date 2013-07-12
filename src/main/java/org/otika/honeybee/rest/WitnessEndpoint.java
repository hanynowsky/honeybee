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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.otika.honeybee.model.Witness;

/**
 * 
 */
@Stateless
@Path("/witnesses")
public class WitnessEndpoint {
	@PersistenceContext
	private EntityManager em;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Witness entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(WitnessEndpoint.class)
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		Witness entity = em.find(Witness.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") Long id) {
		TypedQuery<Witness> findByIdQuery = em
				.createQuery(
						"SELECT w FROM Witness w LEFT JOIN FETCH w.enduser LEFT JOIN FETCH w.prescription WHERE w.id = :id",
						Witness.class);
		findByIdQuery.setParameter("id", id);
		Witness entity = findByIdQuery.getSingleResult();
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Witness> listAll() {
		return em
				.createQuery(
						"SELECT w FROM Witness w LEFT JOIN FETCH w.enduser LEFT JOIN FETCH w.prescription",
						Witness.class).getResultList();
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") Long id, Witness entity) {
		entity.setId(id);
		em.merge(entity);
		return Response.noContent().build();
	}
}