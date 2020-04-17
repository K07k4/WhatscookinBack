package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "puntuacion")
public class Puntuacion {
	int idPuntuacion;
	int idUsuario;
	int idReceta;
	double puntuacion;

	public Puntuacion(int idPuntuacion, int idUsuario, int idReceta, double puntuacion) {
		super();
		this.idPuntuacion = idPuntuacion;
		this.idUsuario = idUsuario;
		this.idReceta = idReceta;
		this.puntuacion = puntuacion;
	}

	public Puntuacion() {

	}

	@Column(name = "id_puntuacion")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdPuntuacion() {
		return idPuntuacion;
	}

	public void setIdPuntuacion(int idPuntuacion) {
		this.idPuntuacion = idPuntuacion;
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

	@Column(name = "puntuacion")
	public double getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(double puntuacion) {
		this.puntuacion = puntuacion;
	}

	
}
