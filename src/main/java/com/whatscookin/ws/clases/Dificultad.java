package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dificultad")
public class Dificultad {
	int idDificultad;
	String dificultad;

	public Dificultad(int idDificultad, String dificultad) {
		super();
		this.idDificultad = idDificultad;
		this.dificultad = dificultad;
	}

	public Dificultad() {

	}

	@Column(name = "id_dificultad")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdDificultad() {
		return idDificultad;
	}

	public void setIdDificultad(int idDificultad) {
		this.idDificultad = idDificultad;
	}

	@Column(name = "nombre_dificultad")
	public String getDificultad() {
		return dificultad;
	}

	public void setDificultad(String dificultad) {
		this.dificultad = dificultad;
	}

}
