package felleskap.punkt.entity;

import jakarta.persistence.*;

@Entity
public class PostAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postCode;
    private String city;
    private String streetName;
    private String houseNumber;
    private String country;

    public PostAddress() {
    }

    public PostAddress(Long id, String postCode, String city, String streetName, String houseNumber, String country) {
        this.id = id;
        this.postCode = postCode;
        this.city = city;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCity() {
        return city;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
