package com.example.currentplacedetailsonmap.utils;

public class services_offered_details {
    String service_name,service_id,service_code,service_teller;

    public services_offered_details() {}

    public services_offered_details(String service_name,String service_id,String service_code,String service_teller){
        this.service_code=service_code;
        this.service_name=service_name;
        this.service_id=service_id;
        this.service_teller=service_teller;

    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_teller() {
        return service_teller;
    }

    public void setService_teller(String service_teller) {
        this.service_teller = service_teller;
    }
}
