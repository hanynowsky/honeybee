package org.otika.honeybee.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.otika.honeybee.model.Author;
import org.otika.honeybee.model.Store;

@Stateless
@Path("/resources")
public class RestService {

	@PersistenceContext
	private EntityManager em;

	@GET()
	@Produces({ MediaType.TEXT_HTML })
	public Response helloWorld() {
		StringBuilder sb = new StringBuilder();
		try {
			String filePath = System.getProperty("java.io.tmpdir")
					+ File.separator + "honeybee" + File.separator
					+ Math.random() + File.separator;
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(filePath + "restinfo.html");

			InputStream inputStream;
			OutputStream out;

			if (new File(filePath).isDirectory()) {
				inputStream = getClass().getResourceAsStream(
						File.separator + file.getName());
				out = new FileOutputStream(file);
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
			}

			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.ALL,
					e.getMessage(), e);
		}
		return Response.ok(sb.toString()).build();
	}

	@Path("/helloworld/{username}")
	@GET()
	@Produces({ MediaType.TEXT_HTML })
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
	@Produces({ MediaType.APPLICATION_JSON })
	public List<?> ingsList(@PathParam("ordered") String ordered) {
		if (ordered.equalsIgnoreCase("ordered")) {
			return em.createQuery(
					"SELECT i.id, i.label, i.form, i.plant.labelar,i.plant.labelfr,i.plant.label "
							+ "FROM Ingredient i ORDER BY i.label")
					.getResultList();
		}
		return null;
	}

	/**
	 * List of prescription items
	 * 
	 * @param ordered
	 * @return
	 */
	@Path("/prescriptions/{ordered}")
	@GET()
	@Produces({ MediaType.APPLICATION_JSON })
	public List<?> presList(@PathParam("ordered") String ordered) {
		if (ordered.equalsIgnoreCase("ordered")) {
			return em.createQuery(
					"SELECT p.id, p.title, p.treatment,p.titlefr,p.titlear,p.preparation "
							+ "FROM Prescription p order by p.title")
					.getResultList();
		}
		return null;
	}

	/**
	 * List of Plant items
	 * 
	 * @param ordered
	 * @return
	 */
	@Path("/plants/{ordered}")
	@GET()
	@Produces({ MediaType.APPLICATION_JSON })
	public List<?> plantList(@PathParam("ordered") String ordered) {
		if (ordered.equalsIgnoreCase("ordered")) {
			return em
					.createQuery(
							"SELECT p.id, p.labellat, p.label, p.labelfr, p.labelar, p.category, p.type,"
									+ "p.season, p.family, p.description, p.descriptionfr, p.descriptionar"
									+ " FROM Plant p order by p.labellat")
					.getResultList(); //
		}
		return null;
	}

	/**
	 * Author by ID
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/authors/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") Long id) {
		Author author = em
				.createQuery(
						"SELECT a FROM Author a LEFT JOIN FETCH a.prescriptions WHERE a.id = :aid",
						Author.class).setParameter("aid", id).getSingleResult();
		if (id == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(author, MediaType.APPLICATION_JSON).build();
	}

	/**
	 * List of Store items
	 * 
	 * @param ordered
	 * @return
	 */
	@Path("/stores/{ordered}")
	@GET()
	@Produces({ MediaType.APPLICATION_JSON })
	public List<?> storeList(@PathParam("ordered") String ordered) {
		if (ordered.equalsIgnoreCase("ordered")) {
			return em.createQuery("SELECT s FROM Store s order by s.label",
					Store.class).getResultList();
		}
		return null;
	}

	/**
	 * List of Honey items
	 * @param ordered
	 * @return
	 */
	@Path("/honeys/{ordered}")
	@GET()
	@Produces({ MediaType.APPLICATION_JSON })
	public List<?> honeysList(@PathParam("ordered") String ordered) {
		if (ordered.equalsIgnoreCase("ordered")) {
			return em.createQuery(
					"SELECT h.id, h.label, h.labelfr,h.labelar "
							+ "FROM Honey h order by h.label").getResultList();
		}
		return null;
	}

}
