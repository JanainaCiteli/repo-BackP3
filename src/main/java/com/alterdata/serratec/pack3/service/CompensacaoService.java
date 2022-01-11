
package com.alterdata.serratec.pack3.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alterdata.serratec.pack3.domain.Compensacao;
import com.alterdata.serratec.pack3.repository.ColaboradorRepository;
import com.alterdata.serratec.pack3.repository.CompensacaoRepository;

@Service
public class CompensacaoService {

	@Autowired
	private CompensacaoRepository compensacaoRepository;

	@Autowired
	private ColaboradorRepository colaboradorRepository;

	@Autowired
	private ColaboradorService colaboradorService;

	public List<Compensacao> pesquisarTodos() {
		return compensacaoRepository.findAll();
	}

	public Optional<Compensacao> pesquisarUm(Long idCompensacao) {
		return compensacaoRepository.findById(idCompensacao);

	}

	public Compensacao inserir(Compensacao compensacao) {

		LocalDate diaSemana = LocalDate.now();
		DayOfWeek dia = diaSemana.getDayOfWeek();
		System.out.println(diaSemana.getDayOfWeek());

		// int diaMes = diaSemana.getDayOfMonth();

		if (dia == DayOfWeek.SATURDAY && compensacao.getTotalCompensacao() <= 8) {
			compensacao.setDataCompensacao(diaSemana);
			Long idColaborador = compensacao.getColaborador().getIdColaborador();
			List<Compensacao> compensacoes = compensacaoRepository.findAllByIdColaborador(idColaborador);
			Double horaDisponivel = colaboradorRepository.getById(idColaborador).getHoraDisponivel();
			Double comp = 0.0;
			Double totalPost = compensacao.getTotalCompensacao();
			for (Compensacao c : compensacoes) {
				comp += c.getTotalCompensacao();
			}
			horaDisponivel = comp + totalPost;
			colaboradorService.editarHorasDisponiveis(idColaborador, horaDisponivel);

		}

		else if ((dia == DayOfWeek.MONDAY || dia == DayOfWeek.TUESDAY || dia == DayOfWeek.WEDNESDAY
				|| dia == DayOfWeek.THURSDAY || dia == DayOfWeek.FRIDAY) && compensacao.getTotalCompensacao() <= 2) {

			compensacao.setDataCompensacao(diaSemana);
			Long idColaborador = compensacao.getColaborador().getIdColaborador();
			List<Compensacao> compensacoes = compensacaoRepository.findAllByIdColaborador(idColaborador);
			Double horaDisponivel = colaboradorRepository.getById(idColaborador).getHoraDisponivel();
			Double comp = 0.0;
			Double totalPost = compensacao.getTotalCompensacao();
			for (Compensacao c : compensacoes) {
				comp += c.getTotalCompensacao();
			}
			horaDisponivel = comp + totalPost;
			colaboradorService.editarHorasDisponiveis(idColaborador, horaDisponivel);

		}

		else {
			throw new Error(
					"Em dias de semana, sua compensação não pode passar de 2 horas em um mesmo dia. Aos sábados, não deve passar de 8 horas");
		}

		return compensacaoRepository.saveAndFlush(compensacao);

	}

	/*
	 * if (diaMes == 25 || diaMes == 26 || diaMes == 27 || diaMes == 28 || diaMes ==
	 * 29 || diaMes == 30 || diaMes == 31) { System.out.
	 * println("Últimos dias do mês! Fique atento ao vencimento das suas horas acumuladas no banco de horas!"
	 * ); }
	 */
	public boolean idExiste(Long idCompensacao) {
		return compensacaoRepository.existsById(idCompensacao);
	}

	public void remover(Long idCompensacao) {
		compensacaoRepository.deleteById(idCompensacao);
	}

	public Compensacao editar(Compensacao compensacao) {
		return compensacaoRepository.save(compensacao);
	}

}
