package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "receta")
public class Receta {
	int idReceta;
	int idTipoReceta;
	String instrucciones;
	String titulo;
	int idDificultad;
	int idUsuario;
	int duracion;
	double puntuacion;
	int numeroPuntuaciones;

	public Receta(int idReceta, int idTipoReceta, String titulo, String instrucciones, int idDificultad, int idUsuario, int duracion,
			double puntuacion, int numeroPuntuaciones) {
		super();
		this.idReceta = idReceta;
		this.idTipoReceta = idTipoReceta;
		this.titulo = titulo;
		this.instrucciones = instrucciones;
		this.idDificultad = idDificultad;
		this.idUsuario = idUsuario;
		this.duracion = duracion;
		this.puntuacion = puntuacion;
		this.numeroPuntuaciones = numeroPuntuaciones;
	}

	public Receta() {

	}

	@Column(name = "id_receta")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(int idReceta) {
		this.idReceta = idReceta;
	}

	@Column(name = "id_tipo_receta")
	public int getIdTipoReceta() {
		return idTipoReceta;
	}

	public void setIdTipoReceta(int idTipoReceta) {
		this.idTipoReceta = idTipoReceta;
	}

	@Column(name = "titulo_receta")
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "instrucciones_receta")
	public String getInstrucciones() {
		return instrucciones;
	}

	public void setInstrucciones(String instrucciones) {
		this.instrucciones = instrucciones;
	}

	@Column(name = "id_dificultad")
	public int getIdDificultad() {
		return idDificultad;
	}

	public void setIdDificultad(int idDificultad) {
		this.idDificultad = idDificultad;
	}

	@Column(name = "id_usuario")
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "duracion_receta")
	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	@Column(name = "puntuacion_receta")
	public double getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(double puntuacion) {
		this.puntuacion = puntuacion;
	}

	@Column(name = "numero_puntuaciones")
	public int getNumeroPuntuaciones() {
		return numeroPuntuaciones;
	}

	public void setNumeroPuntuaciones(int numeroPuntuaciones) {
		this.numeroPuntuaciones = numeroPuntuaciones;
	}

}
