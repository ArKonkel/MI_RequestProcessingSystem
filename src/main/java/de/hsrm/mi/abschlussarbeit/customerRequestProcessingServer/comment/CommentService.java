package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

public interface CommentService {

    void addCommentToProcessItem(Long processItemId, CommentCreateDto comment);

}
