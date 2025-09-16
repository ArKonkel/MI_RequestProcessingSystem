package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.controller;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.CreateRequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.service.RequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/request")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    ResponseEntity<RequestDto> createRequest (@RequestBody CreateRequestDto requestDto){
        log.info("REST request to create request {}", requestDto);

        RequestDto createdRequest = requestService.createRequest(requestDto);

        return ResponseEntity.ok(createdRequest);
    }
}
