package com.alura.finan.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alura.finan.model.Transacao;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
	
	@Query("select max(t) from Transacao t where t.data between :begin and :end")
	Optional<Transacao> findByData(@Param("begin")LocalDateTime begin, @Param("end")LocalDateTime end);
	
	@Query("select t from Transacao t where t.data between :begin and :end")
	List<Transacao> findByDataLista(@Param("begin")LocalDateTime begin, @Param("end")LocalDateTime end);
	
	List<Transacao> findByOrderByDataAsc();
}
