package at.htl.franklyn.server.exam;

import at.htl.franklyn.server.feature.exam.Exam;
import at.htl.franklyn.server.feature.exam.ExamDto;
import at.htl.franklyn.server.feature.exam.ExamState;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class ExamResourceTest {
    private static final String BASE_URL = "exams";

    @Test
    void test_simpleCreateValidExam_ok() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusYears(2);
        LocalDateTime end = LocalDateTime.now().plusYears(3);
        String title = "test1";
        long interval = 5L;

        ExamDto examDto = new ExamDto(
                title,
                start,
                end,
               interval
        );

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(examDto)
                .basePath(BASE_URL)
            .when()
                .post();

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.CREATED);

        Exam exam = response.then()
                .log().body()
                .extract().as(Exam.class);

        assertThat(response.header("Location"))
                .contains(BASE_URL)
                .contains(exam.getId().toString());

        assertThat(exam.getId())
                .isNotNull();
        assertThat(exam.getActualEnd())
                .isNull();
        assertThat(exam.getActualStart())
                .isNull();
        assertThat(exam.getPin())
                .isBetween(0, 1000);
        assertThat(exam.getPlannedStart())
                .isEqualTo(start);
        assertThat(exam.getPlannedEnd())
                .isEqualTo(end);
        assertThat(exam.getState())
                .isEqualTo(ExamState.CREATED);
        assertThat(exam.getTitle())
                .isEqualTo(title);
        assertThat(exam.getScreencaptureInterval())
                .isEqualTo(interval);
    }
}
