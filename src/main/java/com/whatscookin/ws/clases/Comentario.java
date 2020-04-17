package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "comentario")
public class Comentario {
	int idComentario;
	int idUsuario;
	int idReceta;
	String comentario;

	public Comentario(int idComentario, int idUsuario, int idReceta, String comentario) {
		super();
		this.idComentario = idComentario;
		this.idUsuario = idUsuario;
		this.idReceta = idReceta;
		this.comentario = comentario;
	}

	public Comentario() {

	}

	@Column(name = "id_comentario")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdComentario() {
		return idComentario;
	}

	public void setIdComentario(int idComentario) {
		this.idComentario = idComentario;
	}

	@Column(name = "id_usuario")
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "id_receta")
	public int getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(int idReceta) {
		this.idReceta = idReceta;
	}

	@Column(name = "mensaje_comentario")
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

}
