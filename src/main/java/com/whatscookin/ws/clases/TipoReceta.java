package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tipo_receta")
public class TipoReceta {
	int idTipoReceta;
	String nombre;

	public TipoReceta(int idTipoReceta, String nombre) {
		super();
		this.idTipoReceta = idTipoReceta;
		this.nombre = nombre;
	}

	public TipoReceta() {

	}

	@Column(name = "id_tipo_receta")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdTipoReceta() {
		return idTipoReceta;
	}

	public void setIdTipoReceta(int idTipoReceta) {
		this.idTipoReceta = idTipoReceta;
	}

	@Column(name = "nombre")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
