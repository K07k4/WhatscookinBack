package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "favorito")
public class Favorito {
	int idFavorito;
	int idUsuario;
	int idReceta;

	public Favorito(int idFavorito, int idUsuario, int idReceta) {
		super();
		this.idFavorito = idFavorito;
		this.idUsuario = idUsuario;
		this.idReceta = idReceta;
	}

	public Favorito() {

	}

	@Column(name = "id_favorito")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdFavorito() {
		return idFavorito;
	}

	public void setIdFavorito(int idFavorito) {
		this.idFavorito = idFavorito;
	}

	@Column(name = "id_usuario")
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "id_receta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(int idReceta) {
		this.idReceta = idReceta;
	}

}
