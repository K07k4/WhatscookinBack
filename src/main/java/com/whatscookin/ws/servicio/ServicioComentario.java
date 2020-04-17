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

@Path("/comentario")
public class ServicioComentario {

	// http://localhost:8080/whatscookin/comentario/*

	@POST
	@Path("/add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response addComentario(@QueryParam("idUsuario") int idUsuario, @QueryParam("idReceta") int idReceta,
			@QueryParam("mensaje") String mensaje) {

		if (mensaje != "") {

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
				EntityManager entityManager = factory.createEntityManager();

				entityManager.getTransaction().begin();

				Comentario comentario = new Comentario();

				comentario.setIdComentario(0);
				comentario.setIdUsuario(idUsuario);
				comentario.setIdReceta(idReceta);
				comentario.setComentario(mensaje);

				entityManager.persist(comentario);

				entityManager.getTransaction().commit();

				entityManager.close();
				factory.close();

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().entity(e.getMessage()).build();
			}

		}
		return Response.ok().entity("Comentario publicado correctamente").build();
	}

	@DELETE
	@Path("/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response deleteComentario(@QueryParam("id") int id) {
		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Comentario reference = entityManager.getReference(Comentario.class, id);

			entityManager.remove(reference);
			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return Response.serverError().entity("No se ha podido eliminar el comentario").build();
		}

		return Response.ok().entity("Comentario eliminado").build();
	}
	
	@GET
	@Path("/getComentariosEnReceta")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Comentario> getComentariosEnReceta(@QueryParam("id") int idReceta) {

		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Comentario WHERE id_receta = " + idReceta, Comentario.class);

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
	@Path("/getComentariosDeUsuario")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Comentario> getComentariosDeUsuario(@QueryParam("id") int idUsuario) {

		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Comentario WHERE id_usuario = " + idUsuario, Comentario.class);

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
