package felleskap.punkt.entity;

import jakarta.persistence.*;

@Entity
public class PostAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streetName;
    private String houseNumber;
    private String postCode;
    private String city;
    private String country;

    public PostAddress() {}

    public PostAddress(Long id, String postCode, String city, String streetName) {
        this.id = id;
        this.postCode = postCode;
        this.city = city;
        this.streetName = streetName;
        this.country = country;
        this.houseNumber = houseNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
}


