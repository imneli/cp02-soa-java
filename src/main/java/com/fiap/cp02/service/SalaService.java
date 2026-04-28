package com.fiap.cp02.service;

import com.fiap.cp02.dto.SalaDTO;
import com.fiap.cp02.entity.Sala;
import com.fiap.cp02.exception.ResourceNotFoundException;
import com.fiap.cp02.repository.SalaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalaService {

    private static final Logger log = LoggerFactory.getLogger(SalaService.class);

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @CacheEvict(value = "salas", allEntries = true)
    public SalaDTO criar(SalaDTO dto) {
        log.info("Creating room: {}", dto.getNome());
        Sala sala = toEntity(dto);
        return toDTO(salaRepository.save(sala));
    }

    @Cacheable("salas")
    @Transactional(readOnly = true)
    public Page<SalaDTO> listar(Pageable pageable) {
        log.debug("Listing rooms, page: {}", pageable.getPageNumber());
        return salaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<SalaDTO> filtrar(Integer capacidadeMinima, String localizacao) {
        if (capacidadeMinima != null) {
            return salaRepository.findByCapacidadeGreaterThanEqual(capacidadeMinima)
                    .stream().map(this::toDTO).toList();
        }
        if (localizacao != null) {
            return salaRepository.findByLocalizacaoContainingIgnoreCase(localizacao)
                    .stream().map(this::toDTO).toList();
        }
        return salaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public SalaDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @CacheEvict(value = "salas", allEntries = true)
    public SalaDTO atualizar(Long id, SalaDTO dto) {
        log.info("Updating room id: {}", id);
        Sala sala = findById(id);
        sala.setNome(dto.getNome());
        sala.setCapacidade(dto.getCapacidade());
        sala.setLocalizacao(dto.getLocalizacao());
        return toDTO(salaRepository.save(sala));
    }

    @CacheEvict(value = "salas", allEntries = true)
    public void remover(Long id) {
        log.info("Removing room id: {}", id);
        findById(id);
        salaRepository.deleteById(id);
    }

    Sala findById(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada: " + id));
    }

    private SalaDTO toDTO(Sala sala) {
        return new SalaDTO(sala.getId(), sala.getNome(), sala.getCapacidade(), sala.getLocalizacao());
    }

    private Sala toEntity(SalaDTO dto) {
        return new Sala(null, dto.getNome(), dto.getCapacidade(), dto.getLocalizacao());
    }
}
