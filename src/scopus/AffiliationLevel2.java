package scopus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crco0001 on 1/11/2018.
 */
public class AffiliationLevel2 implements Serializable {

    private String afid;
    private String dptid;
    private List<String> organisations = new ArrayList<>(2);
    private String city;
    private String country;


    public String getAfid() {
        return afid;
    }

    public void setAfid(String afid) {
        this.afid = afid;
    }

    public String getDptid() {
        return dptid;
    }

    public void setDptid(String dptid) {
        this.dptid = dptid;
    }

    public List<String> getOrganisations() {
        return organisations;
    }

    public void addOrganisations(String organisation) {
        this.organisations.add(organisation);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    @Override
    public String toString() {
        return "AffiliationLevel2{" +
                "afid='" + afid + '\'' +
                ", dptid='" + dptid + '\'' +
                ", organisations=" + organisations +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
