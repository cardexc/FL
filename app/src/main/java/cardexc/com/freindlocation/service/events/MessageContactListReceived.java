package cardexc.com.freindlocation.service.events;

import java.util.Map;

public class MessageContactListReceived implements  ServiceEventsInterface{

    public final Map<String, Boolean> message;

    public MessageContactListReceived(Map<String, Boolean> message) {
        this.message = message;
    }

}
