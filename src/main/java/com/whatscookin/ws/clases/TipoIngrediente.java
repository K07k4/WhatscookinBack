package com.whatscookin.ws.clases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tipo_ingrediente")
public class TipoIngrediente {
	int idTipoIngrediente;
	String tipoIngrediente;

	public TipoIngrediente(int idTipoIngrediente, String tipoIngrediente) {
		super();
		this.idTipoIngrediente = idTipoIngrediente;
		this.tipoIngrediente = tipoIngrediente;
	}

	public TipoIngrediente() {

	}

	@Column(name = "id_tipo_ingrediente")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getIdTipoIngrediente() {
		return idTipoIngrediente;
	}

	public void setIdTipoIngrediente(int idTipoIngrediente) {
		this.idTipoIngrediente = idTipoIngrediente;
	}

	@Column(name = "nombre_tipo_ingrediente")
	public String getTipoIngrediente() {
		return tipoIngrediente;
	}

	public void setTipoIngrediente(String tipoIngrediente) {
		this.tipoIngrediente = tipoIngrediente;
	}

}
