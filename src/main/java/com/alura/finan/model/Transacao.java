package com.alura.finan.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Transacao {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull(message = "Informe a conta de origem")
	@ManyToOne
	private Conta contaOrigem;
	@NotNull(message = "Informe a conta de destino")
	@ManyToOne
	private Conta contaDestino;
	@NotNull(message = "Informe o valor da transação")
	private BigDecimal valor;
	@NotNull(message = "Informe a data da transação")
	private LocalDateTime data;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Conta getContaOrigem() {
		return contaOrigem;
	}
	
	public void setContaOrigem(Conta contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	
	public Conta getContaDestino() {
		return contaDestino;
	}
	
	public void setContaDestino(Conta contaDestino) {
		this.contaDestino = contaDestino;
	}
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public LocalDateTime getData() {
		return data;
	}
	
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
}
