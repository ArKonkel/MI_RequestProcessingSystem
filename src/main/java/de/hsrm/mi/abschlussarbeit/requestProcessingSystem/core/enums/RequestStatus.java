package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums;

public enum RequestStatus {
    RECEIVED("Eingegangen"),
    CLASSIFICATION("In Klassifizierung"),
    SCOPE_CHECK("In Umfangsprüfung"),
    EFFORT_ESTIMATION("In Aufwandsschätzung"),
    PAYMENT_CLEARANCE("In Zahlungspflichtabklärung"),
    REQUEST_FOR_OFFER("Bitte um Angebotserstellung"),
    OFFER_CREATION("In Angebotserstellung"),
    WAITING_FOR_CUSTOMER("Warten auf Kundenrückmeldung"),
    OFFER_REJECTED("Angebot abgelehnt"),
    OFFER_ACCEPTED("Angebot angenommen"),
    WAITING_FOR_PROCESSING("Warten auf Bearbeitung"),
    PROCESSING("In Bearbeitung"),
    DONE("Erledigt");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
