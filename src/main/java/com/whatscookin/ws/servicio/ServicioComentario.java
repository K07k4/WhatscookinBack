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

import com.whatscookin.ws.clases.Comentario;

@Path("/comentario") // La ruta inicial de todos los métodos dentro de este servicio
public class ServicioComentario {

	// http://localhost:8080/whatscookin/comentario/*

	@POST // Indica es una request POST
	@Path("/add") // La ruta de este método
	@Consumes({ MediaType.APPLICATION_JSON }) // El tipo de información que consume (JSON)
	@Produces({ MediaType.APPLICATION_JSON }) // El tipo de información que devuelve (JSON)
	public static Response addComentario(@QueryParam("idUsuario") int idUsuario, @QueryParam("idReceta") int idReceta,
			@QueryParam("mensaje") String mensaje) { // Los campos necesarios y sus nombres en la URL

		if (!mensaje.isEmpty()) { // Comprueba que el mensaje no esté vacío

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin"); // Conecta a la
																										// base de datos
				EntityManager entityManager = factory.createEntityManager(); // Crea una entidad encargada de la
																				// comunicación entre el servidor y la
																				// base de datos

				entityManager.getTransaction().begin(); // Inicia la transacción a realizar

				Comentario comentario = new Comentario(); // Crea una nueva instancia de la clase comentario

				comentario.setIdComentario(0); // Se le asigna el valor 0
				comentario.setIdUsuario(idUsuario); // Se asigna el id de usuario del creador del comentario
				comentario.setIdReceta(idReceta); // Se asigna el id de receta donde se publicará el comentario
				comentario.setComentario(mensaje); // Se asigna el mensaje del comentario

				entityManager.persist(comentario); // Se prepara un nuevo registro con los datos anteriores

				entityManager.getTransaction().commit(); // Se confirman los cambios realizados y se almacena

				entityManager.close(); // Se cierra la entidad comunicadora
				factory.close(); // Se cierra la conexión con la base de datos

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().entity(e.getMessage()).build(); // Si falla, devuelve el mensaje de error
																				// del servidor al front-end
			}

		}

		return Response.ok().entity("Comentario publicado correctamente").build(); // Envía un mensaje de confirmación
																					// al front-end, junto al código de
																					// estado 200
	}

	@DELETE // Indica que es una request DELETE
	@Path("/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response deleteComentario(@QueryParam("id") int id) { // Necesita el ID del comentario para eliminarlo
		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Comentario reference = entityManager.getReference(Comentario.class, id); // Busca el comentario por ID

			entityManager.remove(reference); // Prepara la eliminación del comentario en la base de datos
			entityManager.getTransaction().commit(); // Confirma y elimina el comentario

			entityManager.close();
			factory.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return Response.serverError().entity("No se ha podido eliminar el comentario").build();
		}

		return Response.ok().entity("Comentario eliminado").build();
	}

	@GET // Indica que es una request GET
	@Path("/getComentariosEnReceta")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Comentario> getComentariosEnReceta(@QueryParam("id") int idReceta) {

		ArrayList<Comentario> comentarios = new ArrayList<Comentario>(); // Inicializa una lista de Comentario

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Comentario WHERE id_receta = " + idReceta,
					Comentario.class); // Ejecuta la query SQL para buscar los comentarios que tengan la ID de la
										// receta

			List<Comentario> list = query.list(); // Almacena los resultados en list

			for (int i = 0; i < list.size(); i++) { // Estos resultados los envía a la lista de Comentario previamente
													// declarada
				Comentario comentario = new Comentario(list.get(i).getIdComentario(), list.get(i).getIdUsuario(),
						list.get(i).getIdReceta(), list.get(i).getComentario()); // Añade cada uno de los valores

				comentarios.add(comentario); // Añade la instancia de comentario en la lista
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return comentarios; // Devuelve la lista de comentarios
	}

	@GET
	@Path("/getComentariosDeUsuario")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Comentario> getComentariosDeUsuario(@QueryParam("id") int idUsuario) {

		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Comentario WHERE id_usuario = " + idUsuario,
					Comentario.class);

			List<Comentario> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Comentario comentario = new Comentario(list.get(i).getIdComentario(), list.get(i).getIdUsuario(),
						list.get(i).getIdReceta(), list.get(i).getComentario());

				comentarios.add(comentario);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return comentarios;
	}

	@GET
	@Path("/getAll")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Comentario> getAllComentarios() {

		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Comentario", Comentario.class);

			List<Comentario> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Comentario comentario = new Comentario(list.get(i).getIdComentario(), list.get(i).getIdUsuario(),
						list.get(i).getIdReceta(), list.get(i).getComentario());

				comentarios.add(comentario);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return comentarios;
	}

}
