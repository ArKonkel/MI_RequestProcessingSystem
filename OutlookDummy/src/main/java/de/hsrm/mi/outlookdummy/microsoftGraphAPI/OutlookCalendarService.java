package de.hsrm.mi.outlookdummy.microsoftGraphAPI;

import de.hsrm.mi.outlookdummy.microsoftGraphAPI.types.OutlookCalendarViewResponse;

public interface OutlookCalendarService {
    OutlookCalendarViewResponse getCalendarView(String userPrincipalName, String start, String end);
}
