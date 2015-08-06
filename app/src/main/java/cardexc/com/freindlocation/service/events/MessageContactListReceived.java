package cardexc.com.freindlocation.service.events;

import java.util.ArrayList;

import cardexc.com.freindlocation.data.Contact;

public class MessageContactListReceived implements  ServiceEventsInterface{

    public final ArrayList<Contact> message;

    public MessageContactListReceived(ArrayList<Contact> message) {
        this.message = message;
    }

}
