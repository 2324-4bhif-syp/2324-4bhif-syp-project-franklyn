package at.htl.franklyn.server.feature.examinee;

import io.smallrye.mutiny.Uni;
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
    public Uni<Boolean> exists(String firstname, String lastname) {
        return examineeRepostiory
                .count("from Examinee where firstname = ?1 and lastname = ?2", firstname, lastname)
                .onItem().transform(c -> c != 0);
    }

    /**
     * Depending on whether an examinee with the given first and last name exists or not this function either:
     * 1) Creates a new examinee and returns it
     * 2) queries the examinee from the db and returns it
     * @param firstname firstname of the examinee
     * @param lastname lastname of the examinee
     * @return examinee with the given first and lastname
     */
    public Uni<Examinee> getOrCreateExaminee(String firstname, String lastname) {
        return examineeRepostiory
                .find("select e from Examinee e where firstname = ?1 and lastname = ?2", firstname, lastname)
                .firstResult()
                .onItem().ifNull().continueWith(new Examinee(firstname, lastname))
                .chain((e) -> {
                    if (e.getId() == null) {
                        return examineeRepostiory.persist(e);
                    } else {
                        return Uni.createFrom().item(e);
                    }
                });
    }
}
