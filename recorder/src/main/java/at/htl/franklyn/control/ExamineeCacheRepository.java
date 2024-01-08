package at.htl.franklyn.control;

import at.htl.franklyn.entity.Examinee;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class ExamineeCacheRepository {
    // Key: username
    ConcurrentMap<String, Examinee> allExaminees = new ConcurrentHashMap<>();

    // Key: username, Value: ip address
    ConcurrentMap<String, String> cachedExaminees = new ConcurrentHashMap<>();

    public boolean updateCache(List<Examinee> examinees) {
        boolean hasChanged = examinees.size() != allExaminees.size();

        for(Examinee newExaminee : examinees) {
            // Remove examinee from cache in case any attributes have changed
            if(!newExaminee.equals(allExaminees.get(newExaminee.getUsername()))) {
                cachedExaminees.remove(newExaminee.getUsername());
                hasChanged = true;
            }
        }

        // clear examinees and refill
        allExaminees.clear();
        for(Examinee newExaminee : examinees) {
            allExaminees.put(newExaminee.getUsername(), newExaminee);
        }

        return hasChanged;
    }

    public void saveToCache(String username, String ipAddress) {
        this.cachedExaminees.put(username, ipAddress);
    }

    public List<Examinee> getAllActiveExaminees() {
        return allExaminees.values().stream().filter(Examinee::isConnected).toList();
    }

    // Key: username, value: ip address
    public Map<String, String> getAllCachedExaminees() {
        return cachedExaminees;
    }
}
