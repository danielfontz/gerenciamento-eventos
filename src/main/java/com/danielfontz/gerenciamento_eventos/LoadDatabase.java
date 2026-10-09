package com.danielfontz.gerenciamento_eventos;

import com.danielfontz.gerenciamento_eventos.model.Evento;
import com.danielfontz.gerenciamento_eventos.model.Inscricao;
import com.danielfontz.gerenciamento_eventos.model.Local;
import com.danielfontz.gerenciamento_eventos.model.Palestrante;
import com.danielfontz.gerenciamento_eventos.model.Participante;
import com.danielfontz.gerenciamento_eventos.model.StatusEvento;
import com.danielfontz.gerenciamento_eventos.repository.EventoRepository;
import com.danielfontz.gerenciamento_eventos.repository.InscricaoRepository;
import com.danielfontz.gerenciamento_eventos.repository.LocalRepository;
import com.danielfontz.gerenciamento_eventos.repository.PalestranteRepository;
import com.danielfontz.gerenciamento_eventos.repository.ParticipanteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EventoRepository eventoRepository,
                                   ParticipanteRepository participanteRepository,
                                   LocalRepository localRepository,
                                   PalestranteRepository palestranteRepository,
                                   InscricaoRepository inscricaoRepository) {
        return args -> {

            // ---- Locais ----
            Local l1 = localRepository.save(new Local("Centro de Convenções", "Av. Paulista, 1000", "São Paulo"));
            Local l2 = localRepository.save(new Local("Auditório Central", "Rua das Flores, 250", "Rio de Janeiro"));
            Local l3 = localRepository.save(new Local("Espaço Coworking", "Av. Afonso Pena, 500", "Belo Horizonte"));

            // ---- Palestrantes ----
            Palestrante pa1 = palestranteRepository.save(new Palestrante("Ana Silva", "ana@exemplo.com", "Java"));
            Palestrante pa2 = palestranteRepository.save(new Palestrante("Bruno Costa", "bruno@exemplo.com", "Spring Boot"));
            Palestrante pa3 = palestranteRepository.save(new Palestrante("Carla Dias", "carla@exemplo.com", "Arquitetura de Software"));

            // ---- Eventos (com Local + Palestrantes) ----
            Evento e1 = new Evento("Workshop de Spring Boot", LocalDate.now().plusMonths(1), StatusEvento.PLANEJADO);
            e1.setLocal(l1);
            e1.getPalestrantes().add(pa1);
            e1.getPalestrantes().add(pa2);
            eventoRepository.save(e1);

            Evento e2 = new Evento("Conferência Java", LocalDate.now().plusMonths(2), StatusEvento.CONFIRMADO);
            e2.setLocal(l2);
            e2.getPalestrantes().add(pa1);
            eventoRepository.save(e2);

            Evento e3 = new Evento("Meetup de Arquitetura", LocalDate.now().plusMonths(3), StatusEvento.PLANEJADO);
            e3.setLocal(l3);
            e3.getPalestrantes().add(pa3);
            eventoRepository.save(e3);

            // ---- Participantes ----
            Participante p1 = participanteRepository.save(new Participante("João Souza", "joao@exemplo.com", "(11) 99999-0000"));
            Participante p2 = participanteRepository.save(new Participante("Maria Oliveira", "maria@exemplo.com", "(21) 98888-1111"));
            Participante p3 = participanteRepository.save(new Participante("Carlos Pereira", "carlos@exemplo.com", "(31) 97777-2222"));

            // ---- Inscrições (ligando Evento + Participante) ----
            inscricaoRepository.save(new Inscricao(LocalDateTime.now(), "ATIVA", e1, p1));
            inscricaoRepository.save(new Inscricao(LocalDateTime.now(), "ATIVA", e2, p1));
            inscricaoRepository.save(new Inscricao(LocalDateTime.now(), "PENDENTE", e1, p2));
            inscricaoRepository.save(new Inscricao(LocalDateTime.now(), "CANCELADA", e3, p3));

            log.info("Dados pré-carregados com sucesso!");
        };
    }
}