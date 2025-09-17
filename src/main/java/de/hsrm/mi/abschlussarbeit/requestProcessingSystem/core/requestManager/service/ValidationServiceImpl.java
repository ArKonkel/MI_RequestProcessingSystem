package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Category;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestCreateDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateRequestCreation(RequestCreateDto requestCreateDto) {

        switch (requestCreateDto.getCategory()){
            case Category.SUGGESTION_FOR_IMPROVEMENT -> {


                //TODO check if fields are filled
                break;
            }
            case Category.BUG_REPORT -> {

                break;
            }
            case Category.FEATURE_REQUEST -> {

                break;
            }
            case Category.TRAINING_REQUEST -> {

                break;
            }
            case Category.OTHER -> {

                break;
            }
            default -> {
                log.error("Invalid category {}", requestCreateDto.getCategory());
                //TODO gescheite Expception
                throw new IllegalArgumentException("Invalid category");
            }

        }
    }

    private void throwMissingFieldsException() {
        //TODO gescheite Expcetion
        throw new IllegalArgumentException("Missing fields");
    }
}
