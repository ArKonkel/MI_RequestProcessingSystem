package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ExpertiseServiceImpl implements ExpertiseService {

    private final ExpertiseRepository expertiseRepository;

    private final ExpertiseMapper expertiseMapper;

    @Override
    public List<ExpertiseDto> getAllExpertises() {
        log.info("Get all expertises");

        return expertiseRepository.findAll().stream().map(expertiseMapper::toDto).toList();
    }

    @Override
    public Expertise getExpertiseById(Long id) {
        log.info("Get expertise with id {}", id);

        return expertiseRepository.findById(id).orElseThrow(() -> new NotFoundException("Expertise with id " + id + " not found"));
    }
}
