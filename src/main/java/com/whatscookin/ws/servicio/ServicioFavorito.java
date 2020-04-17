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

import com.whatscookin.ws.clases.Favorito;
import com.whatscookin.ws.clases.Receta;

@Path("/favorito")
public class ServicioFavorito {

	// http://localhost:8080/whatscookin/favorito/*

	// Añade una receta a las favoritas del usuario
	@POST
	@Path("/add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response addFavorito(@QueryParam("idUsuario") int idUsuario, @QueryParam("idReceta") int idReceta) {

		ArrayList<Favorito> favoritosUsuario = getFavoritosUsuario(idUsuario);

		for (Favorito f : favoritosUsuario) {
			if (f.getIdReceta() == idReceta && f.getIdUsuario() == idUsuario) {
				return Response.serverError().entity("Esta receta ya está en favoritos").build();
			}
		}

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Favorito favorito = new Favorito();

			favorito.setIdFavorito(0);
			favorito.setIdUsuario(idUsuario);
			favorito.setIdReceta(idReceta);

			entityManager.persist(favorito);

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity("La receta no se ha podido guardar como favorita " + e.getMessage())
					.build();
		}

		return Response.ok().entity("Receta guardada como favorita correctamente").build();
	}

	// Borra una receta de la lista de favoritos del usuario
	@DELETE
	@Path("/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response deleteFavorito(@QueryParam("idUsuario") int idUsuario,
			@QueryParam("idReceta") int idReceta) {
		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery(
					"from Favorito WHERE id_usuario = " + idUsuario + " AND id_receta = " + idReceta, Favorito.class);

			entityManager.remove(query.getSingleResult());
			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			return Response.serverError().entity("No se ha podido eliminar la receta de favoritos").build();
		}

		return Response.ok().entity("Receta eliminada de favoritos eliminado").build();
	}

	// Obtiene los favoritos del usuario (NO las recetas)
	@GET
	@Path("/getFavoritosUsuario")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Favorito> getFavoritosUsuario(@QueryParam("idUsuario") int idUsuario) {

		ArrayList<Favorito> favoritos = new ArrayList<Favorito>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Favorito WHERE id_usuario = " + idUsuario,
					Favorito.class);

			List<Favorito> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Favorito favorito = new Favorito(list.get(i).getIdFavorito(), list.get(i).getIdUsuario(),
						list.get(i).getIdReceta());

				favoritos.add(favorito);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return favoritos;
	}

	// Obtiene las recetas favoritas del usuario

	@GET
	@Path("/getRecetasFavoritasUsuario")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getRecetasFavoritasUsuario(@QueryParam("idUsuario") int idUsuario) {

		ArrayList<Favorito> favoritos = new ArrayList<Favorito>();
		ArrayList<Receta> recetas = new ArrayList<Receta>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Favorito WHERE id_usuario = " + idUsuario,
					Favorito.class);

			List<Favorito> favoritosList = query.list();

			for (int i = 0; i < favoritosList.size(); i++) {
				Favorito favorito = new Favorito(favoritosList.get(i).getIdFavorito(),
						favoritosList.get(i).getIdUsuario(), favoritosList.get(i).getIdReceta());

				favoritos.add(favorito);
			}

			Query queryRecetas;

			for (Favorito f : favoritos) {
				queryRecetas = (Query) entityManager.createQuery("from Receta WHERE id_receta = " + f.getIdReceta());
				recetas.add((Receta) queryRecetas.getSingleResult());
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recetas;
	}

}
