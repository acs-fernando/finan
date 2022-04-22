package com.alura.finan.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alura.finan.model.Banco;
import com.alura.finan.model.Conta;
import com.alura.finan.model.Importacao;
import com.alura.finan.model.Transacao;
import com.alura.finan.repository.BancoRepository;
import com.alura.finan.repository.ContaRepository;
import com.alura.finan.repository.ImportacaoRepository;
import com.alura.finan.repository.TransacaoRepository;
import com.alura.finan.storage.StorageService;


@Controller
@RequestMapping("transacoes")
public class TransacoesController {
	
	@Autowired
	private TransacaoRepository transacaoRepository;
	
	@Autowired
	private ImportacaoRepository importacaoRepository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	private final StorageService storageService;

	@Autowired
	public TransacoesController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/formulario")
	public String formulario(Model model) {
		model.addAttribute("importacoes", importacaoRepository.findByOrderByDataTransacaoDesc());
		return "transacoes/formulario";
	}
	
	@GetMapping("/detalhes")
	public String detalhes(Model model, @RequestParam("id") Long id) {
		LocalDateTime data = transacaoRepository.findById(id).get().getData();
		model.addAttribute("transacoes", transacaoRepository.findByDataLista(data.withHour(0), data.withHour(23)));
		return "transacoes/detalhes";
	}

	@PostMapping("/processa")
	public String processa(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
		
		List<String> erros = new ArrayList<>();
		
		if(new String(file.getBytes()).isBlank()) {
			erros.add("Nenhum dado encontrado");
			redirectAttributes.addFlashAttribute("erros", erros);
			return "redirect:formulario";
		}
		
		try {
			String[] fileData = new String(file.getBytes()).split("\n");
			
			String[] firstLine = fileData[0].split(",");
			LocalDateTime dataTransacao = LocalDateTime.parse(firstLine[firstLine.length-1].replace("\r","").trim()).withHour(0);
			if(transacaoRepository.findByData(dataTransacao.withHour(0), dataTransacao.withHour(23)).isPresent()) {
				erros.add("A data " + dataTransacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " já foi processada");
				redirectAttributes.addFlashAttribute("erros", erros);
				return "redirect:formulario";
			}
			
			storageService.store(file);
			redirectAttributes.addFlashAttribute("message", "Arquivo gravado com sucesso!");
			
			List<Stream<String>> lines = Stream.of(fileData)
							.filter(st -> Stream.of(st.split(",")).noneMatch(ss -> ss.isEmpty()))
							.filter(fs -> LocalDateTime.parse(fs.split(",")[7].replace("\r","").trim()).getDayOfMonth() == dataTransacao.getDayOfMonth())
							.map(s -> Stream.of(s.replaceAll("\r", "").split(",")))
							.collect(Collectors.toList());
			
			lines.forEach(line -> {
				List<String> fields = line.collect(Collectors.toList());
				Transacao transacao = new Transacao();
				//1 - Nome banco origem
				Optional<Banco> bancoOrigemOp = bancoRepository.findByNome(fields.get(0));
				Banco bancoOrigem = new Banco();
				if(!bancoOrigemOp.isPresent()) {
					bancoOrigem.setNome(fields.get(0));
					bancoRepository.save(bancoOrigem);
				}else {
					bancoOrigem = bancoOrigemOp.get();
				}
				//2 - Conta Origem
				Optional<Conta> contaOrigemOp = contaRepository.findByNumeroAndDigitoAndBanco(fields.get(2).split("-")[0], fields.get(2).split("-")[1], bancoOrigem);
				Conta contaOrigem = new Conta();
				if(!contaOrigemOp.isPresent()) {
					contaOrigem.setAgencia(fields.get(1));
					contaOrigem.setNumero(fields.get(2).split("-")[0]);
					contaOrigem.setDigito(fields.get(2).split("-")[1]);
					contaOrigem.setBanco(bancoOrigem);
					contaRepository.save(contaOrigem);
				}else {
					contaOrigem = contaOrigemOp.get();
				}
				transacao.setContaOrigem(contaOrigem);
				//3 - Nome banco destino
				Optional<Banco> bancoDestinoOp = bancoRepository.findByNome(fields.get(3));
				Banco bancoDestino = new Banco();
				if(!bancoDestinoOp.isPresent()) {
					bancoDestino.setNome(fields.get(3));
					bancoRepository.save(bancoDestino);
				}else {
					bancoDestino = bancoDestinoOp.get();
				}
				//4 - Conta Destino
				Optional<Conta> contaDestinoOp = contaRepository.findByNumeroAndDigitoAndBanco(fields.get(5).split("-")[0], fields.get(5).split("-")[1], bancoDestino);
				Conta contaDestino = new Conta();
				if(!contaDestinoOp.isPresent()) {
					contaDestino.setAgencia(fields.get(4));
					contaDestino.setNumero(fields.get(5).split("-")[0]);
					contaDestino.setDigito(fields.get(5).split("-")[1]);
					contaDestino.setBanco(bancoDestino);
					contaRepository.save(contaDestino);
				}else {
					contaDestino = contaDestinoOp.get();
				}
				transacao.setContaDestino(contaDestino);
				//5 - Valor
				transacao.setValor(new BigDecimal(fields.get(6)));
				//6 - Data da transação
				transacao.setData(LocalDateTime.parse(fields.get(7).trim()));
				transacaoRepository.save(transacao);
			});
			
			Importacao importacao = new Importacao();
			importacao.setDataImportacao(LocalDateTime.now());
			importacao.setDataTransacao(dataTransacao.toLocalDate());
			importacaoRepository.save(importacao);
		}catch(Exception e) {
			erros.add("Arquivo inválido!");
			redirectAttributes.addFlashAttribute("erros", erros);
			return "redirect:formulario";
		}
		
		redirectAttributes.addFlashAttribute("importacoes", importacaoRepository.findAll());
		return "redirect:formulario";
	}
}