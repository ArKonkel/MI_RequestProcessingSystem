package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise;

import java.util.List;

public interface ExpertiseService {

    List<ExpertiseDto> getAllExpertises();

    Expertise getExpertiseById(Long id);
}
