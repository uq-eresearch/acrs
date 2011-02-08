package org.acrs.data.model;

import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.Date;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:05:37 PM
 */

@Entity
public class Member {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    @Column(length = 500)
    private String address;

    private String phone;

    private String occupation;

    private String country;

    private String passwordHash;

    private String passwordResetId;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date registrationDate;

    public Member() {
        this.registrationDate = new Date();
    }

    public Member(String firstName, String lastName, String email, String passwordHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordResetId() {
        return passwordResetId;
    }

    public void setPasswordResetId(String passwordResetId) {
        this.passwordResetId = passwordResetId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
