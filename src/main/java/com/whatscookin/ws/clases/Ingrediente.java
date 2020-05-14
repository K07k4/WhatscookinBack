package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ingrediente")
public class Ingrediente {
	int idIngrediente;
	int idTipoIngrediente;
	String ingrediente;

	public Ingrediente(int idIngrediente, int idTipoIngrediente, String ingrediente) {
		super();
		this.idIngrediente = idIngrediente;
		this.idTipoIngrediente = idTipoIngrediente;
		this.ingrediente = ingrediente;
	}

	public Ingrediente() {

	}

	@Column(name = "id_ingrediente")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(int idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	@Column(name = "id_tipo_ingrediente")
	public int getIdTipoIngrediente() {
		return idTipoIngrediente;
	}

	public void setIdTipoIngrediente(int idTipoIngrediente) {
		this.idTipoIngrediente = idTipoIngrediente;
	}

	@Column(name = "nombre_ingrediente")
	public String getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(String ingrediente) {
		this.ingrediente = ingrediente;
	}

}
