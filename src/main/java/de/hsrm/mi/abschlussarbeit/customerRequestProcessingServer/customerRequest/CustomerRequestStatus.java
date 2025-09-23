package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

public enum CustomerRequestStatus {
    RECEIVED,
    IN_CLASSIFICATION,
    SCOPE_CHECK,
    EFFORT_ESTIMATION,
    PAYMENT_CLEARANCE,
    REQUEST_FOR_OFFER,
    OFFER_CREATION,
    WAITING_FOR_CUSTOMER,
    OFFER_REJECTED,
    OFFER_ACCEPTED,
    WAITING_FOR_PROCESSING,
    IN_PROCESS,
    DONE
}
