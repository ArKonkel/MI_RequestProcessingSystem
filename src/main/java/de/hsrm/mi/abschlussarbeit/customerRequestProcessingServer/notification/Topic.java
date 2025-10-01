package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import lombok.Getter;

@Getter
public enum Topic {
    PROCESS_ITEM_ASSIGNED("/topic/processItem-assigned"),
    IN_COMMENT_MENTIONED("/topic/in-comment-mentioned"),
    CUSTOMER_REQUEST_CHANGED("/topic/customer-request"),
    PROJECT_CHANGED("/topic/project"),
    TASK_CHANGED("/topic/task");

    private String path;

    Topic(String path) {
        this.path = path;
    }
}
