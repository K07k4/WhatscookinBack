package com.whatscookin.ws.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import com.whatscookin.ws.clases.Dificultad;
import com.whatscookin.ws.clases.Favorito;
import com.whatscookin.ws.clases.IngredienteReceta;
import com.whatscookin.ws.clases.Puntuacion;
import com.whatscookin.ws.clases.Receta;
import com.whatscookin.ws.clases.TipoReceta;

@Path("/receta")
public class ServicioReceta {

	// http://localhost:8080/whatscookin/receta/*

	// Añadir receta
	@POST
	@Path("/add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response addReceta(@QueryParam("titulo") String tituloReceta, @QueryParam("idUsuario") int idUsuario,
			@QueryParam("idTipoReceta") int idTipoReceta, @QueryParam("idDificultad") int idDificultad,
			@QueryParam("duracion") int duracion, @QueryParam("instrucciones") String instrucciones,
			@QueryParam("idIngrediente") List<Integer> listaIdIngrediente) {

		if (tituloReceta != "" && instrucciones != "" && idDificultad > 0 && idUsuario > 0 && duracion > 0
				&& idTipoReceta > 0 && listaIdIngrediente.get(0) != null) {

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
				EntityManager entityManager = factory.createEntityManager();

				entityManager.getTransaction().begin();

				Receta receta = new Receta();

				receta.setTitulo(tituloReceta);
				receta.setIdDificultad(idDificultad);
				receta.setIdTipoReceta(idTipoReceta);
				receta.setIdUsuario(idUsuario);
				receta.setDuracion(duracion);
				receta.setInstrucciones(instrucciones);

				Query query = (Query) entityManager
						.createQuery("from Receta WHERE titulo_receta LIKE '" + tituloReceta + "'", Receta.class);

				List<Receta> listReceta = query.list();

				// Si existe una receta con el mismo titulo notificará un error
				try {
					Receta original = listReceta.get(0);
					return Response.serverError()
							.entity("El usuario ya ha subido una receta llamada \"" + tituloReceta + "\"").build();
				} catch (Exception e) {// De lo contrario, creará la receta

					receta.setTitulo(tituloReceta);
					receta.setIdTipoReceta(idTipoReceta);
					receta.setIdDificultad(idDificultad);
					receta.setIdUsuario(idUsuario);
					receta.setDuracion(duracion);
					receta.setInstrucciones(instrucciones);

					entityManager.persist(receta);
					entityManager.flush();

					int idReceta = receta.getIdReceta();

					for (int i = 0; i < listaIdIngrediente.size(); i++) {

						IngredienteReceta ingredienteReceta = new IngredienteReceta();
						ingredienteReceta.setIdReceta(idReceta);
						ingredienteReceta.setIdIngrediente(listaIdIngrediente.get(i));

						entityManager.persist(ingredienteReceta);
					}

					entityManager.getTransaction().commit();

					entityManager.close();
					factory.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return Response.ok().entity("Receta creada correctamente").build();

		} else {
			return Response.serverError().entity("No se ha podido crear la receta").build();
		}
	}

	@GET
	@Path("/getAll")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getAllRecetas() {

		ArrayList<Receta> recetas = new ArrayList<Receta>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Receta", Receta.class);

			List<Receta> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Receta receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(),
						list.get(i).getTitulo(), list.get(i).getInstrucciones(), list.get(i).getIdDificultad(),
						list.get(i).getIdUsuario(), list.get(i).getDuracion(), list.get(i).getPuntuacion(),
						list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recetas;
	}

	// Agrega la puntuación a la receta, de 0.0 a 5.0
	@POST
	@Path("/puntuar")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response puntuar(@QueryParam("idReceta") int idReceta, @QueryParam("idUsuario") int idUsuario,
			@QueryParam("puntuacion") Double puntuacion) {

		boolean puntuado = false;

		if (puntuacion <= 5.0 && puntuacion >= 0.0) {

			try {
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
				EntityManager entityManager = factory.createEntityManager();

				entityManager.getTransaction().begin();

				Puntuacion puntuacionItem = new Puntuacion();

				puntuacionItem.setIdPuntuacion(0);
				puntuacionItem.setIdReceta(idReceta);
				puntuacionItem.setIdUsuario(idUsuario);
				puntuacionItem.setPuntuacion(puntuacion);

				Query query = (Query) entityManager.createQuery(
						"from Puntuacion WHERE id_receta = " + idReceta + " AND id_usuario = " + idUsuario,
						Puntuacion.class);

				List<Puntuacion> listPuntuacion = query.list();

				// Si existe la puntuación del usuario en dicha receta, sustituirá el valor por
				// el nuevo
				try {
					Puntuacion original = listPuntuacion.get(0);

					puntuacionItem.setIdPuntuacion(original.getIdPuntuacion());

					entityManager.merge(puntuacionItem);

					puntuado = true;

				} catch (Exception e) { // De lo contrario, creará un nuevo registro
					entityManager.persist(puntuacionItem);
				}

				// Se obtiene la receta donde se almacenará su puntuación y número de
				// puntuaciones
				Query queryReceta = (Query) entityManager.createQuery("from Receta WHERE id_receta = " + idReceta,
						Receta.class);
				List<Receta> listReceta = queryReceta.list();

				Receta receta = listReceta.get(0);

				Query queryPuntuacion = (Query) entityManager
						.createQuery("from Puntuacion WHERE id_receta = " + idReceta);
				List<Puntuacion> listPuntuacionReceta = queryPuntuacion.list();

				double suma = puntuacion; // Se inicializa la media con la puntuación que se da
				int numeroPuntuaciones = receta.getNumeroPuntuaciones();

				// Se suman todas las puntuaciones de la receta
				for (int i = 0; i < numeroPuntuaciones; i++) {
					suma += listPuntuacionReceta.get(i).getPuntuacion();
				}

				// Si el usuario ya ha puntuado esta receta no se sumará al número total
				if (!puntuado) {
					numeroPuntuaciones++;
				}

				receta.setNumeroPuntuaciones(numeroPuntuaciones);

				// Se calcula la media de puntuación
				double resultado = suma / numeroPuntuaciones;

				receta.setPuntuacion(resultado);

				entityManager.persist(receta); // Se actualiza la receta con su nueva puntuación y su número de votos

				entityManager.getTransaction().commit();

				entityManager.close();
				factory.close();

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().entity("Ha ocurrido un error con la puntuación").build();
			}
			return Response.ok().entity("Puntuación añadida correctamente").build();
		} else {
			return Response.serverError().entity("Puntuación incorrecta").build();
		}

	}

	// Devuelve una receta según el id
	@GET
	@Path("/getReceta")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getReceta(@QueryParam("idReceta") int idReceta) {

		ArrayList<Receta> recetas = new ArrayList<Receta>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Receta WHERE id_receta = " + idReceta, Receta.class);

			List<Receta> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Receta receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(),
						list.get(i).getTitulo(), list.get(i).getInstrucciones(), list.get(i).getIdDificultad(),
						list.get(i).getIdUsuario(), list.get(i).getDuracion(), list.get(i).getPuntuacion(),
						list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recetas;
	}

	// Devuelve todos los tipos de recetas
	@GET
	@Path("/getAllTipos")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<TipoReceta> getAllTiposRecetas() {

		ArrayList<TipoReceta> tipoRecetas = new ArrayList<TipoReceta>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from TipoReceta", TipoReceta.class);

			List<TipoReceta> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				TipoReceta tipoReceta = new TipoReceta(list.get(i).getIdTipoReceta(), list.get(i).getNombre());
				tipoRecetas.add(tipoReceta);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tipoRecetas;
	}

	// Devuelve una receta al azar
	@GET
	@Path("/getAleatoria")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static int getAleatoria() {

		ArrayList<Receta> recetas = new ArrayList<Receta>();

		Receta receta = new Receta();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Receta", Receta.class);

			List<Receta> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(), list.get(i).getTitulo(),
						list.get(i).getInstrucciones(), list.get(i).getIdDificultad(), list.get(i).getIdUsuario(),
						list.get(i).getDuracion(), list.get(i).getPuntuacion(), list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			receta = recetas.get(new Random().nextInt(recetas.size()));

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return receta.getIdReceta();
	}

	// Devuelve todas las recetas, ordenadas de mayor a menor puntuación media. 10
	// como máximo
	@GET
	@Path("/getMejores")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getMejores() {

		ArrayList<Receta> recetas = new ArrayList<Receta>();
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
		EntityManager entityManager = factory.createEntityManager();

		Query query = (Query) entityManager.createQuery("from Receta ORDER BY puntuacion_receta DESC", Receta.class);
		try {

			entityManager.getTransaction().begin();

			List<Receta> list = query.list();

			int max = 0;

			if (list.size() < 10) {
				max = list.size();
			} else {
				max = 10;
			}

			for (int i = 0; i < max; i++) {
				Receta receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(),
						list.get(i).getTitulo(), list.get(i).getInstrucciones(), list.get(i).getIdDificultad(),
						list.get(i).getIdUsuario(), list.get(i).getDuracion(), list.get(i).getPuntuacion(),
						list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			entityManager.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			entityManager.close();
			factory.close();
		}

		return recetas;
	}

	// Devuelve todas las recetas, ordenadas de mayor a menor puntuación media, 10
	// como máximo
	@GET
	@Path("/getMasPuntuadas")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getMasPuntuadas() {

		ArrayList<Receta> recetas = new ArrayList<Receta>();

		EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
		EntityManager entityManager = factory.createEntityManager();

		Query query = (Query) entityManager.createQuery("from Receta ORDER BY numero_puntuaciones DESC", Receta.class);

		try {
			entityManager.getTransaction().begin();

			List<Receta> list = query.list();

			int max = 0;

			if (list.size() < 10) {
				max = list.size();
			} else {
				max = 10;
			}

			for (int i = 0; i < max; i++) {
				Receta receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(),
						list.get(i).getTitulo(), list.get(i).getInstrucciones(), list.get(i).getIdDificultad(),
						list.get(i).getIdUsuario(), list.get(i).getDuracion(), list.get(i).getPuntuacion(),
						list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			entityManager.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			entityManager.close();
			factory.close();
		}

		return recetas;
	}

	// Devuelve todas las recetas, ordenadas de mayor a menor puntuación media, 10
	// como máximo
	@GET
	@Path("/getMasRecientes")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getMasRecientes() {

		ArrayList<Receta> recetas = new ArrayList<Receta>();
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
		EntityManager entityManager = factory.createEntityManager();

		Query query = (Query) entityManager.createQuery("from Receta ORDER BY id_receta DESC", Receta.class);

		try {

			entityManager.getTransaction().begin();

			List<Receta> list = query.list();

			int max = 0;

			if (list.size() < 10) {
				max = list.size();
			} else {
				max = 10;
			}

			for (int i = 0; i < max; i++) {
				Receta receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(),
						list.get(i).getTitulo(), list.get(i).getInstrucciones(), list.get(i).getIdDificultad(),
						list.get(i).getIdUsuario(), list.get(i).getDuracion(), list.get(i).getPuntuacion(),
						list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			entityManager.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			entityManager.close();
			factory.close();
		}

		return recetas;
	}

	// Búsqueda de recetas completa, por tipo de receta, usuario, dificultad,
	// duración, puntuación mínima, ingredientes...
	@GET
	@Path("/getRecetasBusqueda")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Receta> getRecetasBusqueda(@QueryParam("tituloReceta") String tituloReceta,
			@QueryParam("idTipoReceta") int idTipoReceta, @QueryParam("idUsuario") int idUsuario,
			@QueryParam("idDificultadMin") int idDificultadMin, @QueryParam("idDificultadMax") int idDificultadMax,
			@QueryParam("duracionMin") int duracionMin, @QueryParam("duracionMax") int duracionMax,
			@QueryParam("puntuacionMin") double puntuacionMin, @QueryParam("puntuacionMax") double puntuacionMax,
			@QueryParam("idIngrediente") List<Integer> listaIdIngrediente) {

		ArrayList<Receta> recetas = new ArrayList<Receta>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			String queryString = "from Receta WHERE id_receta > 0";

			try {

				if (listaIdIngrediente.get(0) != null) {

					queryString = "from Receta WHERE id_receta IN(SELECT idReceta FROM IngredienteReceta WHERE id_ingrediente = "
							+ listaIdIngrediente.get(0) + ")";

					for (int i = 1; i < listaIdIngrediente.size(); i++) {
						queryString += " AND id_receta IN(SELECT idReceta FROM IngredienteReceta WHERE id_ingrediente = "
								+ listaIdIngrediente.get(i) + ")";
					}

				}
				
			} catch (Exception e) {
				System.out.println("No hay ingredientes en esta receta");
			}

			System.out.println("EL ID DE USUARIO: " + idUsuario);

			if (!tituloReceta.isEmpty()) {
				queryString += " AND titulo_receta LIKE '%" + tituloReceta + "%'";
			}

			if (idUsuario > 0) {
				queryString += " AND idUsuario = " + idUsuario;
			}

			if (idTipoReceta > 0) {
				queryString += " AND idTipoReceta = " + idTipoReceta;
			}

			if (idDificultadMin > 0) {
				queryString += " AND idDificultad >= " + idDificultadMin + " AND idDificultad <= " + idDificultadMax;
			}

			if (duracionMin > 0) {
				queryString += " AND duracion >= " + duracionMin;

				if (duracionMax >= 120) {
					queryString += " AND duracion <= " + duracionMax;
				}
			}

			if (puntuacionMin > 0) {
				queryString += " AND puntuacion >= " + puntuacionMin + " AND puntuacion <= " + puntuacionMax;
			}

			System.out.println(queryString);

			Query query = (Query) entityManager.createQuery(queryString, Receta.class);

			List<Receta> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Receta receta = new Receta(list.get(i).getIdReceta(), list.get(i).getIdTipoReceta(),
						list.get(i).getTitulo(), list.get(i).getInstrucciones(), list.get(i).getIdDificultad(),
						list.get(i).getIdUsuario(), list.get(i).getDuracion(), list.get(i).getPuntuacion(),
						list.get(i).getNumeroPuntuaciones());
				recetas.add(receta);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recetas;
	}

	// Elimina una receta junto a los comentarios, puntuación, asociación de
	// ingredientes y favoritos
	@DELETE
	@Path("/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static Response deleteReceta(@QueryParam("idReceta") int idReceta) {
		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Receta reference = entityManager.getReference(Receta.class, idReceta);

			entityManager.remove(reference);

			Query queryIngredienteReceta = (Query) entityManager
					.createQuery("from IngredienteReceta WHERE idReceta = " + idReceta, IngredienteReceta.class);

			List<IngredienteReceta> listqueryIngredienteReceta = queryIngredienteReceta.list();

			for (int i = 0; i < listqueryIngredienteReceta.size(); i++) {
				IngredienteReceta referenceIngredienteReceta = entityManager.getReference(IngredienteReceta.class,
						listqueryIngredienteReceta.get(i).getIdIngredienteReceta());
				entityManager.remove(referenceIngredienteReceta);
			}

			Query queryPuntuacion = (Query) entityManager.createQuery("from Puntuacion WHERE idReceta = " + idReceta,
					Puntuacion.class);

			List<Puntuacion> listPuntuacion = queryPuntuacion.list();

			for (int i = 0; i < listPuntuacion.size(); i++) {
				Puntuacion referencePuntuacion = entityManager.getReference(Puntuacion.class,
						listPuntuacion.get(i).getIdPuntuacion());
				entityManager.remove(referencePuntuacion);
			}

			Query queryFavorito = (Query) entityManager.createQuery("from Favorito WHERE idReceta = " + idReceta,
					Favorito.class);

			List<Favorito> listFavorito = queryFavorito.list();

			for (int i = 0; i < listFavorito.size(); i++) {
				Favorito referenceFavorito = entityManager.getReference(Favorito.class,
						listFavorito.get(i).getIdFavorito());
				entityManager.remove(referenceFavorito);
			}

			Query queryComentario = (Query) entityManager.createQuery("from Comentario WHERE idReceta = " + idReceta,
					Comentario.class);

			List<Comentario> listComentario = queryComentario.list();

			for (int i = 0; i < listComentario.size(); i++) {
				Comentario referenceComentario = entityManager.getReference(Comentario.class,
						listComentario.get(i).getIdComentario());
				entityManager.remove(referenceComentario);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return Response.serverError().entity("No se ha podido eliminar la receta").build();
		}

		return Response.ok().entity("Receta eliminada").build();
	}

	@GET
	@Path("/getDificultad")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Dificultad> getDificultad(@QueryParam("idDificultad") int idDificultad) {

		ArrayList<Dificultad> dificultades = new ArrayList<Dificultad>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Dificultad WHERE id_dificultad = " + idDificultad,
					Dificultad.class);

			List<Dificultad> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Dificultad dificultad = new Dificultad(list.get(i).getIdDificultad(), list.get(i).getDificultad());
				dificultades.add(dificultad);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dificultades;
	}
	
	@GET
	@Path("/getAllDificultad")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<Dificultad> getAllDificultad() {

		ArrayList<Dificultad> dificultades = new ArrayList<Dificultad>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from Dificultad",
					Dificultad.class);

			List<Dificultad> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				Dificultad dificultad = new Dificultad(list.get(i).getIdDificultad(), list.get(i).getDificultad());
				dificultades.add(dificultad);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dificultades;
	}

	@GET
	@Path("/getTipoReceta")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public static ArrayList<TipoReceta> getTipoReceta(@QueryParam("idTipoReceta") int idTipoReceta) {

		ArrayList<TipoReceta> tiposRecetas = new ArrayList<TipoReceta>();

		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("whatscookin");
			EntityManager entityManager = factory.createEntityManager();

			entityManager.getTransaction().begin();

			Query query = (Query) entityManager.createQuery("from TipoReceta WHERE id_tipo_receta = " + idTipoReceta,
					TipoReceta.class);

			List<TipoReceta> list = query.list();

			for (int i = 0; i < list.size(); i++) {
				TipoReceta tipoReceta = new TipoReceta(list.get(i).getIdTipoReceta(), list.get(i).getNombre());
				tiposRecetas.add(tipoReceta);
			}

			entityManager.getTransaction().commit();

			entityManager.close();
			factory.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tiposRecetas;
	}

}
