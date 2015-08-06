package cardexc.com.freindlocation.data;


public class Contact {

    public String getPhone() {
        return phone;
    }

    public Boolean getApproved() {
        return approved;
    }

    private String name;
    private String IMEI;
    private String id;
    private String phone;
    private Boolean approved;

    public Contact(String id, String phone, String IMEI, Boolean approved) {
        this.IMEI = IMEI;
        this.id = id;
        this.phone = phone;
        this.approved = approved;
    }

    public Contact(String id, String phone, String IMEI, Boolean approved, String name) {

        this.name = name;
        this.IMEI = IMEI;
        this.id = id;
        this.phone = phone;
        this.approved = approved;

    }

    public String getName() {
        return name;
    }

    public String getIMEI() {
        return IMEI;
    }

    public String getId() {
        return id;
    }
}
