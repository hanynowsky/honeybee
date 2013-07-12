package org.otika.honeybee.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/resources")
public class RestService {

    @PersistenceContext
    private EntityManager em;

    @GET()
    @Produces({MediaType.TEXT_HTML})
    public Response helloWorld() {
        String msg = "<em>Welcome " + "" + " to <b>HoneyBee</b> Rest Resources</em>";
        return Response.ok(msg).build();
    }

    @Path("/ingredients/{all}")
    @GET()
    @Produces({MediaType.APPLICATION_JSON})
    public List<?> ingredientsList(@PathParam("all") String all) {
        if (all.equalsIgnoreCase("all")) {
            Query query = em
                    .createQuery("SELECT i.plant.labelar, i.label, i.form, i.id FROM Ingredient i");
            return query.getResultList();
        }
        return null;
    }

    @Path("/helloworld/{username}")
    @GET()
    @Produces({MediaType.TEXT_HTML})
    public Response helloWorld(@PathParam("username") String username) {
        String msg = "";
        if (username != null && !username.equalsIgnoreCase("")) {
            msg = "<em>Welcome " + username
                    + " to <b>HoneyBee</b> Rest Resources</em>";
            return Response.ok(msg).build();
        }
        return Response.ok(msg).build();
    }

    @Path("/ingredients/{ordered}")
    @GET()
    @Produces({MediaType.APPLICATION_JSON})
    public List<?> ingsList(@PathParam("ordered") String ordered) {
        if (ordered.equalsIgnoreCase("ordered")) {
            return em
                    .createQuery(
                    "SELECT i.id,i.label, i.form FROM Ingredient i order by i.label")
                    .getResultList();
        }
        return null;
    }

    @Path("/prescriptions/{ordered}")
    @GET()
    @Produces({MediaType.APPLICATION_JSON})
    public List<?> presList(@PathParam("ordered") String ordered) {
        if (ordered.equalsIgnoreCase("ordered")) {
            return em
                    .createQuery(
                    "SELECT p.id, p.title, p.treatment,p.titlefr,p.titlear,p.preparation FROM Prescription p order by p.title")
                    .getResultList();
        }
        return null;
    }
}
