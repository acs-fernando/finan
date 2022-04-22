package com.alura.finan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alura.finan.model.Importacao;

@Repository
public interface ImportacaoRepository extends JpaRepository<Importacao, Long> {
	List<Importacao> findByOrderByDataTransacaoDesc();
}
