package at.htl.franklyn.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "F_EXAMINEE")
public class Examinee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "E_ID")
    private Long id;

    @NotNull(message = "Firstname can not be null")
    @NotBlank(message = "Firstname can not be blank")
    @Size(message = "Firstname must have a length between 2 and 50 characters", min = 2, max = 50)
    @Column(name = "E_FIRSTNAME", nullable = false, length = 50)
    private String firstname;

    @NotNull(message = "Lastname can not be null")
    @NotBlank(message = "Lastname can not be blank")
    @Size(message = "Lastname must have a length between 2 and 50 characters", min = 2, max = 50)
    @Column(name = "E_LASTNAME", nullable = false, length = 50)
    private String lastname;

    public Examinee() {
    }

    public Examinee(String firstname, String lastname) {
        this.setFirstname(firstname);
        this.setLastname(lastname);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "Examinee{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
