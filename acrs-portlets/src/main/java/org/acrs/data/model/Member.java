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

    @NotNull
    private String title;
    private String firstName;
    private String lastName;
    private String streetAddress;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String email;
    private String phone;
    
    @Column(length=500)
    private String institution;
    @Column(length=500)
    private String researchInterest;
    
    private String newsletterPref;
    private String membershipType;
    
    private Double membershipAmount;
    
    private String renewalFlag;
    private String acrsEmailListFlag;
    private String paypalRef;
    
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
        this.registrationDate = new Date();
        this.passwordHash = passwordHash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
   
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
	
    public String getResearchInterest() {
        return researchInterest;
    }

    public void setResearchInterest(String researchInterest) {
        this.researchInterest = researchInterest;
    }
	
    public String getNewsletterPref() {
        return newsletterPref;
    }

    public void setNewsletterPref(String newsletterPref) {
        this.newsletterPref = newsletterPref;
    }
	
    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
    
    public Double getMembershipAmount() {
        return membershipAmount;
    }

    public void setMembershipAmount(Double membershipAmount) {
        this.membershipAmount = membershipAmount;
    }
	
    
    public String getRenewalFlag() {
        return renewalFlag;
    }

    public void setRenewalFlag(String renewalFlag) {
        this.renewalFlag = renewalFlag;
    }
    
    public String getAcrsEmailListFlag() {
        return acrsEmailListFlag;
    }

    public void setAcrsEmailListFlag(String acrsEmailListFlag) {
        this.acrsEmailListFlag = acrsEmailListFlag;
    }

    public String getPaypalRef() {
        return paypalRef;
    }

    public void setPaypalRef(String paypalRef) {
        this.paypalRef = paypalRef;
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
