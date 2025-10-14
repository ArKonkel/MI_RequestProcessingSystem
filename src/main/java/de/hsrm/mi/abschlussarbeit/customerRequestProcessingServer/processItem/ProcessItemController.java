package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.FileDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/processItems")
public class ProcessItemController {

    private final ProcessItemService processItemService;

    private final CommentService commentService;


    @PostMapping("/{processItemId}/assign/{userId}")
    public ResponseEntity<Void> assignProcessItemToUser(
            @PathVariable Long processItemId,
            @PathVariable Long userId) {
        log.info("REST request to assign process item {} to user {}", processItemId, userId);

        processItemService.assignProcessItemToUser(processItemId, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{processItemId}/comments")
    public ResponseEntity<Void> addCommentToProcessItem(@PathVariable Long processItemId, @RequestBody CommentCreateDto comment) {
        log.info("REST request to add comment {} to process item {}", comment.text(), processItemId);

        commentService.addCommentToProcessItem(processItemId, comment);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<FileDto> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        FileDto dto = processItemService.addAttachment(id, file);
        return ResponseEntity.ok(dto);
    }
}
