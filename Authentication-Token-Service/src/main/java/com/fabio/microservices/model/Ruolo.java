package com.fabio.microservices.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="ruolo")
public class Ruolo {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="nome")
	private String nome;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="ruolo_per_funzione",
				joinColumns=@JoinColumn(name="id_ruolo"),
				inverseJoinColumns=@JoinColumn(name="id_funzione")
			)
	private Set<Funzione> funzioni;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Set<Funzione> getFunzioni() {
		return funzioni;
	}

	public void setFunzioni(Set<Funzione> funzioni) {
		this.funzioni = funzioni;
	}
	
	
}
