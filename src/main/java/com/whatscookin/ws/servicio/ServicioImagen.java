package com.whatscookin.ws.servicio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/imagen")
public class ServicioImagen {

	// http://localhost:8080/whatscookin/imagen/*

	// Path donde se almacenan las imágenes
	String folder = "D:/Kotka/Desktop/uploads/";

	// Almacena una imagen formato jpg en el equipo del servidor
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @QueryParam("id") int id,
			@QueryParam("tipo") String tipo) {

		// La imagen se almacenará en su carpeta correspondiente según su tipo
		if (tipo.equals("usuario")) {
			folder += "usuarios/";
		} else if (tipo.equals("receta")) {
			folder += "recetas/";
		} else {
			return Response.status(500).entity("Tipo de imagen inválido").build();
		}

		String[] fileNameArray = fileDetail.getFileName().split("\\.");

		String format = fileNameArray[fileNameArray.length - 1].toLowerCase();

		// Si no es formato .jpg/.jpeg no permitirá almacenarse
		if (!format.equals("jpg") && !format.equals("jpeg")) {
			return Response.status(500).entity("Sólo se permite formato jpg/jpeg, no el formato " + format).build();
		}

		// Sea jpg o jpeg se guardará como jpg, por mantener la nomenclatura
		// El nombre de la imagen corresponde al id, así ahorramos un campo en la base
		// de datos
		String uploadedFileLocation = folder + id + ".jpg";

		// La almacena en la carpeta
		// En caso que se quiera cambiar la imagen, se sobreescribirá. Por lo que
		// siempre habrá únicamente 1 imagen por receta/usuario

		if (writeToFile(uploadedInputStream, uploadedFileLocation)) {
			return Response.status(200).entity("Imagen almacenada en: " + folder).build();
		} else {
			return Response.status(500).entity("La imagen debe pesar menos de 5MB").build();
		}

	}

	// TODO: Controlar el tamaño de la imagen antes de subirla
	// Guarda la imagen subida a la dirección local del servidor
	private boolean writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));

			int read = 0;
			int counter = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
				counter++;
			}

			System.out.println("TAMAÑO: " + (Double.valueOf(counter) / 1024) + " MB");

			out.flush();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Obtiene la imagen según el id y el tipo (usuario o receta)
	@GET
	@Path("/get")
	@Produces("image/jpg")
	public Response getImage(@QueryParam("id") String id, @QueryParam("tipo") String tipo) {

		// Se filtra para buscar según el tipo
		if (tipo.equals("usuario")) {
			folder += "usuarios/";
		} else if (tipo.equals("receta")) {
			folder += "recetas/";
		} else if (tipo.equals("tipo")) {
			folder += "tipos/";
		} else {
			return Response.status(500).entity("Tipo de imagen inválido").build();
		}

		File file = null;
		try {
			file = new File(folder + id + ".jpg"); // Se busca la imagen en el servidor de archivos
		} catch (Exception e) {
			return Response.status(500).entity("No se ha encontrado la imagen").build();
		}
		return Response.ok(file, "image/jpg").header("Inline", "filename=\"" + file.getName() + "\"").build(); // Devuelve
																												// la
																												// imagen
																												// con
																												// un
																												// formato
																												// xml
	}

	// Elimina la imagen según el id y el tipo (usuario o receta)
	@DELETE
	@Path("/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFile(@QueryParam("id") String id, @QueryParam("tipo") String tipo) {

		// Se filtra para buscar según el tipo
		if (tipo.equals("usuario")) {
			folder += "usuarios/";
		} else if (tipo.equals("receta")) {
			folder += "recetas/";
		} else {
			return Response.status(500).entity("Tipo de imagen inválido").build();
		}

		File file = null;
		try {
			file = new File(folder + id + ".jpg"); // Se busca la imagen en el servidor de archivos

			if (!file.exists()) { // Si no encuentra la imagen devuelve un error 500 como respuesta
				return Response.status(500)
						.entity("No se ha encontrado la imagen con ID " + id + " de tipo " + tipo.toUpperCase())
						.build();
			}

			file.delete(); // Elimina la imagen

		} catch (Exception e) {
			return Response.status(500)
					.entity("No se ha encontrado la imagen con ID " + id + " de tipo " + tipo.toUpperCase()).build();
		}
		return Response.ok()
				.entity("Imagen con ID " + id + " de tipo " + tipo.toUpperCase() + " se ha eliminado correctamente")
				.build();
	}

}
