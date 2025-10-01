package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{processItemId}")
    public ResponseEntity<Void> addCommentToProcessItem(@PathVariable Long processItemId, @RequestBody CommentCreateDto comment) {
        log.info("REST request to add comment {} to process item {}", comment.text(), processItemId);

        commentService.addCommentToProcessItem(processItemId, comment);

        return ResponseEntity.ok().build();
    }
}
