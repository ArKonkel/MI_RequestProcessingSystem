package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.File;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    private final FileService fileService;


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


    @GetMapping("/attachments/{fileId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String fileId) {
        log.info("REST request to download file with id {}", fileId);

        File file = fileService.getFileById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                //change inline to attachment if you want to view it inside the browser
                //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(new ByteArrayResource(file.getData()));
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<Void> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST request to upload attachment to process item {}", id);

        processItemService.addAttachment(id, file);

        return ResponseEntity.ok().build();
    }
}
