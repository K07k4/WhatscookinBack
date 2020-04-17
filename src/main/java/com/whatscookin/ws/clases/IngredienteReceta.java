package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ingrediente_receta")
public class IngredienteReceta {
	int idIngredienteReceta;
	int idReceta;
	int idIngrediente;

	public IngredienteReceta(int idIngredienteReceta, int idReceta, int idIngrediente) {
		super();
		this.idIngredienteReceta = idIngredienteReceta;
		this.idReceta = idReceta;
		this.idIngrediente = idIngrediente;
	}

	public IngredienteReceta() {

	}

	@Column(name = "id_ingrediente_receta")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdIngredienteReceta() {
		return idIngredienteReceta;
	}

	public void setIdIngredienteReceta(int idIngredienteReceta) {
		this.idIngredienteReceta = idIngredienteReceta;
	}

	@Column(name = "id_receta")
	public int getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(int idReceta) {
		this.idReceta = idReceta;
	}

	@Column(name = "id_ingrediente")
	public int getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(int idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

}
