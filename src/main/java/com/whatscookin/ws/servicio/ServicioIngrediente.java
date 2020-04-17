package com.whatscookin.ws.servicio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.query.Query;

import com.whatscookin.ws.clases.Ingrediente;
import com.whatscookin.ws.clases.TipoIngrediente;

@Path("/ingrediente")
public class ServicioIngrediente {

	// http://localhost:8080/whatscookin/ingrediente/*

	// Añade un nuevo ingrediente, indicando su tipo
	@POST
	@Path("/add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addIngrediente(@QueryParam("ingrediente") String nombreIngrediente,
			@QueryParam("idTipo") int idTipo) {

		if (nombreIngrediente != "") {

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
				EntityManager entityManager = factory.createEntityManager();

				entityManager.getTransaction().begin();

				Ingrediente ingrediente = new Ingrediente();

				// Se almacena con la primera letra en mayúsculas
				nombreIngrediente = nombreIngrediente.toLowerCase();
				nombreIngrediente = nombreIngrediente.substring(0, 1).toUpperCase()
						+ nombreIngrediente.substring(1, nombreIngrediente.length());

				Query query = (Query) entityManager.createQuery(
						"from Ingrediente WHERE nombre_ingrediente LIKE '" + nombreIngrediente + "'",
						Ingrediente.class);

				List<Ingrediente> listIngrediente = query.list();

				// Si existe el ingrediente notificará de error
				try {
					Ingrediente original = listIngrediente.get(0);
					return Response.serverError().entity("El ingrediente " + nombreIngrediente + " ya existe").build();
				} catch (Exception e) {// De lo contrario, creará un nuevo ingrediente
					ingrediente.setIdIngrediente(0);
					ingrediente.setIdTipoIngrediente(idTipo);
					ingrediente.setIngrediente(nombreIngrediente);

					entityManager.persist(ingrediente);

					entityManager.getTransaction().commit();

					entityManager.close();
					factory.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().entity(e.getMessage()).build();
			}

		}
		return Response.ok().entity("Ingrediente creado correctamente").build();
	}

	// Devuelve todos los ingredientes
	@GET
	@Path("/getAll")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ArrayList<Ingrediente> getAllIngredientes() {

		ArrayList<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Ingrediente", Ingrediente.class);

			List<Ingrediente> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Ingrediente ingrediente = new Ingrediente(list.get(i).getIdIngrediente(),
						list.get(i).getIdTipoIngrediente(), list.get(i).getIngrediente());

				ingredientes.add(ingrediente);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ingredientes;
	}

	// Añade un nuevo tipo de ingrediente
	@POST
	@Path("/addTipo")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addtTipoIngrediente(@QueryParam("tipo") String tipo) {

		if (tipo != "") {

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
				EntityManager entityManager = factory.createEntityManager();

				entityManager.getTransaction().begin();

				TipoIngrediente tipoIngrediente = new TipoIngrediente();

				// Se almacena con la primera letra en mayúsculas
				tipo = tipo.toLowerCase();
				tipo = tipo.substring(0, 1).toUpperCase() + tipo.substring(1, tipo.length());

				tipoIngrediente.setIdTipoIngrediente(0);

				tipoIngrediente.setTipoIngrediente(tipo);

				entityManager.persist(tipoIngrediente);

				entityManager.getTransaction().commit();

				entityManager.close();
				factory.close();

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().entity(e.getMessage()).build();
			}

		}
		return Response.ok().entity("Tipo de ingrediente creado correctamente").build();
	}

	// Devuelve todos los tipos de ingredientes
	@GET
	@Path("/getAllTipos")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ArrayList<TipoIngrediente> getAllTipos() {

		ArrayList<TipoIngrediente> tiposIngredientes = new ArrayList<TipoIngrediente>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from TipoIngrediente", TipoIngrediente.class);

			List<TipoIngrediente> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				TipoIngrediente tipoIngrediente = new TipoIngrediente(list.get(i).getIdTipoIngrediente(),
						list.get(i).getTipoIngrediente());

				tiposIngredientes.add(tipoIngrediente);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tiposIngredientes;
	}

	// Devuelve todos los ingredientes del tipo que se indique
	@GET
	@Path("/getAllDeTipo")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ArrayList<Ingrediente> getAllIngredientesDeTipo(@QueryParam("id") int id) {

		ArrayList<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Ingrediente WHERE id_tipo_ingrediente = " + id,
					Ingrediente.class);

			List<Ingrediente> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Ingrediente ingrediente = new Ingrediente();
				
				ingrediente.setIdIngrediente(list.get(i).getIdIngrediente());
				ingrediente.setIdTipoIngrediente(list.get(i).getIdTipoIngrediente());
				ingrediente.setIngrediente(list.get(i).getIngrediente());

				ingredientes.add(ingrediente);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ingredientes;
	}

}
