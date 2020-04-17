package com.whatscookin.ws.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import com.whatscookin.ws.clases.Favorito;
import com.whatscookin.ws.clases.Puntuacion;
import com.whatscookin.ws.clases.Receta;
import com.whatscookin.ws.clases.Usuario;

@Path("/usuario")
public class ServicioUsuario {

	// http://localhost:8080/whatscookin/usuario/*

	// Comprueba que el email tiene un formato válido
	public static boolean isValid(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	// Añade usuario
	@POST
	@Path("/add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addUsuario(@QueryParam("nombre") String nombre, @QueryParam("pass") String pass,
			@QueryParam("email") String email) {

		ArrayList<Usuario> usuarios = getAllUsuarios();

		if (nombre != "" && pass != "" && isValid(email) && !nombre.contains(" ") && !nombre.contains("'") && !nombre.contains("@")
				&& !pass.contains("'") && !email.contains("'")) { // Comprueba que los campos no estén vacíos, que el
																	// email es válido, y protege la base de datos
																	// denegando los caracteres ' y espacio

			for (Usuario u : usuarios) { // Iteración para comprobar que si está registrado el nombre o el email

				if (u.getEmail().equals(email)) {
					return Response.serverError().entity("El email ya está registrado").build();
				}

				if (u.getNombre().equals(nombre)) {
					return Response.serverError().entity("El usuario ya está registrado").build();
				}
			}

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
				EntityManager entityManager = factory.createEntityManager();

				entityManager.getTransaction().begin();

				Usuario usuario = new Usuario();

				usuario.setId(0);
				usuario.setNombre(nombre);
				usuario.setPass(pass);
				usuario.setEmail(email);

				entityManager.persist(usuario);

				entityManager.getTransaction().commit();

				entityManager.close();
				factory.close();

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().entity("Ha ocurrido un error: " + e.getMessage()).build();
			}

		} else {
			return Response.serverError().entity(
					"No se ha podido crear el usuario. Comprueba que no hay campos vacíos y que el email es correcto")
					.build();
		}

		return Response.ok().entity("Usuario creado correctamente").build();
	}

	// Devuelve todos los usuarios registrados
	@GET
	@Path("/getAll")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ArrayList<Usuario> getAllUsuarios() {

		ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Usuario", Usuario.class);

			List<Usuario> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Usuario usuario = new Usuario(list.get(i).getId(), list.get(i).getNombre(), list.get(i).getPass(),
						list.get(i).getEmail());

				usuarios.add(usuario);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return usuarios;
	}

	@GET
	@Path("/getUsuario")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Usuario getUsuario(@QueryParam("idUsuario") int idUsuario) {

		Usuario usuario = new Usuario();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Usuario WHERE id_usuario = " + idUsuario,
					Usuario.class);

			List<Usuario> list = query.list();

			try {
				usuario = list.get(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return usuario;
	}

	// Elimina al usuario junto a sus recetas, comentarios, favoritos y puntuaciones

	@DELETE
	@Path("/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response deleteUsuario(@QueryParam("idUsuario") int idUsuario) {
		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Usuario reference = entityManager.getReference(Usuario.class, idUsuario);

			entityManager.remove(reference);

			Query queryFavorito = (Query) entityManager.createQuery("from Favorito WHERE idUsuario = " + idUsuario,
					Favorito.class);

			List<Favorito> listFavorito = queryFavorito.list();

			for (int i = 0; i < listFavorito.size(); i++) {
				Favorito referenceFavorito = entityManager.getReference(Favorito.class,
						listFavorito.get(i).getIdFavorito());
				entityManager.remove(referenceFavorito);
			}

			Query queryComentario = (Query) entityManager.createQuery("from Comentario WHERE idUsuario = " + idUsuario,
					Comentario.class);

			List<Comentario> listComentario = queryComentario.list();

			for (int i = 0; i < listComentario.size(); i++) {
				Comentario referenceComentario = entityManager.getReference(Comentario.class,
						listComentario.get(i).getIdComentario());
				entityManager.remove(referenceComentario);
			}

			Query queryReceta = (Query) entityManager.createQuery("from Receta WHERE idUsuario = " + idUsuario,
					Receta.class);
			List<Receta> listReceta = queryReceta.list();

			for (int i = 0; i < listReceta.size(); i++) {
				ServicioReceta.deleteReceta(listReceta.get(i).getIdReceta());
			}

			Query queryPuntuacion = (Query) entityManager.createQuery("from Puntuacion WHERE idUsuario = " + idUsuario,
					Puntuacion.class);
			List<Puntuacion> listPuntuacion = queryPuntuacion.list();

			for (int i = 0; i < listPuntuacion.size(); i++) {
				listPuntuacion.get(i).getIdReceta();

				Query queryPuntuacionTemp = (Query) entityManager.createQuery(
						"from Puntuacion WHERE idReceta = " + listPuntuacion.get(i).getIdReceta(), Puntuacion.class);
				List<Puntuacion> listPuntuacionTemp = queryPuntuacionTemp.list();

				double sumaTotal = 0.0;

				for (int e = 0; e < listPuntuacionTemp.size(); e++) {
					sumaTotal += listPuntuacionTemp.get(e).getPuntuacion();
				}

				sumaTotal -= listPuntuacion.get(i).getPuntuacion();

				Query queryRecetaTemp = (Query) entityManager.createQuery(
						"from Receta WHERE idReceta = " + listPuntuacion.get(i).getIdReceta(), Receta.class);
				List<Receta> listRecetaTemp = queryRecetaTemp.list();

				Receta recetaTemp = listRecetaTemp.get(0);

				recetaTemp.setPuntuacion(sumaTotal);
				recetaTemp.setNumeroPuntuaciones(recetaTemp.getNumeroPuntuaciones() - 1);

				entityManager.persist(recetaTemp);

				Puntuacion referencePuntuacion = entityManager.getReference(Puntuacion.class,
						listPuntuacion.get(i).getIdPuntuacion());
				entityManager.remove(referencePuntuacion);

			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return Response.serverError().entity("No se ha podido eliminar el usuario").build();
		}

		return Response.ok().entity("Usuario eliminado").build();
	}

}
