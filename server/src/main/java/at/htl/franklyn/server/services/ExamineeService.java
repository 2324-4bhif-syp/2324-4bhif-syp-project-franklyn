package at.htl.franklyn.server.services;

import at.htl.franklyn.server.control.ExamineeRepostiory;
import at.htl.franklyn.server.entity.Examinee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExamineeService {
    @Inject
    ExamineeRepostiory examineeRepostiory;

    /**
     * Checks weather or not an examinee with the given firstname and lastname exists
     * @param firstname firstname of the examinee
     * @param lastname lastname of the examinee
     * @return true - in case the examinee exists otherwise false
     */
    public boolean exists(String firstname, String lastname) {
        return examineeRepostiory.count("from Examinee where firstname = ?1 and lastname = ?2", firstname, lastname) != 0;
    }

    /**
     * Depending on whether an examinee with the given first and last name exists or not this function either:
     * 1) Creates a new examinee and returns it
     * 2) queries the examinee from the db and returns it
     * @param firstname firstname of the examinee
     * @param lastname lastname of the examinee
     * @return examinee with the given first and lastname
     */
    public Examinee getOrCreateExaminee(String firstname, String lastname) {
        Examinee examinee = examineeRepostiory
                .find("select e from Examinee e where firstname = ?1 and lastname = ?2", firstname, lastname)
                .firstResultOptional()
                .orElse(null);

        if(examinee == null) {
            examinee = new Examinee(firstname, lastname);
            examineeRepostiory.persist(examinee);
        }

        return examinee;
    }
}
