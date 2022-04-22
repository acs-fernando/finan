package com.alura.finan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alura.finan.model.Banco;
import com.alura.finan.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
	Optional<Conta> findByNumeroAndDigitoAndBanco(String numero, String digito, Banco banco);
}
