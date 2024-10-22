package at.htl.franklyn.server.exam;

import at.htl.franklyn.server.feature.exam.Exam;
import at.htl.franklyn.server.feature.exam.ExamDto;
import at.htl.franklyn.server.feature.exam.ExamState;
import at.htl.franklyn.server.feature.examinee.ExamineeDto;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExamResourceTest {
    private static final String BASE_URL = "exams";
    private static final String JOIN_URL = "join";

    private static Exam createdExam;
    private static String userSession;

    @Test
    @Order(1)
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

        createdExam = exam;
    }

    @Test
    @Order(2)
    void test_simpleGetExamById_ok() {
        // Arrange
        // created Exam is taken from the post test with @Order(1)

        // Act
        Response response = given()
                .basePath(BASE_URL)
            .when()
                .get(createdExam.getId().toString());

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        Exam actualExam = response.then()
                .log().body()
                .extract().as(Exam.class);

        assertThat(actualExam)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(createdExam);

        assertThat(actualExam.getPlannedStart())
                .isCloseTo(createdExam.getPlannedStart(), within(1, ChronoUnit.MINUTES));

        assertThat(actualExam.getPlannedEnd())
                .isCloseTo(createdExam.getPlannedEnd(), within(1, ChronoUnit.MINUTES));
    }

    @Test
    @Order(3)
    void test_simpleGetAllExams_ok() {
        // Arrange
        // created Exam is taken from the post test with @Order(1)
        Exam expectedExam1 = new Exam(
                LocalDateTime.of(2024, 10, 17, 10, 1),
                LocalDateTime.of(2024, 10, 17, 12, 30),
                null,
                null,
                "test",
                123,
                ExamState.ONGOING,
                5L
        );
        expectedExam1.setId(1L);

        // Act
        Response response = given()
                .basePath(BASE_URL)
            .when()
                .get();

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        Exam[] exams = response.then()
                .log().body()
                .extract().as(Exam[].class);

        assertThat(exams)
                .hasSize(2);

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
                .withEqualsForType(
                        (actualDate, expectedDate) ->
                                ChronoUnit.MINUTES.between(actualDate, expectedDate) < 1, LocalDateTime.class)
                .withIgnoredFields("actualStart", "actualEnd")
                .withIgnoredFieldsOfTypes(LocalDateTime.class)
                .withIgnoredFieldsMatchingRegexes(".*hibernate.*")
                .build();

        assertThat(exams)
                .usingRecursiveComparison(configuration)
                .isEqualTo(new Exam[] { expectedExam1, createdExam });
    }

    @Test
    @Order(4)
    void test_simpleGetExamineesOfExam_ok() {
        // Arrange
        ExamineeDto expectedExaminee = new ExamineeDto(
                "Max",
                "Mustermann",
                true
        );

        // Act
        Response response = given()
                .basePath(BASE_URL)
            .when()
                .get(String.format("%d/examinees", 1)); // import sql creates exam with id 1

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        ExamineeDto[] examinees = response.then()
                .log().body()
                .extract().as(ExamineeDto[].class);

        assertThat(examinees)
                .hasSize(1);

        ExamineeDto actualExaminee = examinees[0];

        assertThat(actualExaminee)
                .usingRecursiveComparison()
                .ignoringFields("isConnected") // Potential race with cleanup job
                .isEqualTo(expectedExaminee);
    }

    @Test
    @Order(5)
    void test_simpleJoinExamWithValidUser_ok() {
        // Arrange
        ExamineeDto examineeDto = new ExamineeDto(
            "Test",
            "User",
            false // value of is_connected does not matter on connection and is ignored
        );

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(examineeDto)
                .basePath(BASE_URL)
            .when()
                .post(String.format("%s/%3d", JOIN_URL, createdExam.getPin()));

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.CREATED);

        assertThat(response.header("Location"))
                .matches(".*connect/.*");

        String[] parts = response.header("Location").split("/");
        userSession = parts[parts.length - 1];
    }

    @Test
    @Order(6)
    void test_simpleStartExam_ok() {
        // Arrange
        // created Exam is taken from the post test with @Order(1)

        // Act
        Response startResponse = given()
                .basePath(BASE_URL)
            .when()
                .post(String.format("%s/start", createdExam.getId()));

        Response response = given()
                .basePath(BASE_URL)
            .when()
                .get(createdExam.getId().toString());

        // Assert
        assertThat(startResponse.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        Exam actualExam = response.then()
                .log().body()
                .extract().as(Exam.class);

        assertThat(actualExam.getActualStart())
                .isNotNull();
        assertThat(actualExam.getState())
                .isEqualTo(ExamState.ONGOING);
    }

    // TODO: Move somewhere else?
    @Test
    @Order(7)
    void test_simpleUploadAlpha_ok() {
        // Arrange
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("/alpha-frame.png")).getFile());

        // Act
        Response response = given()
                .contentType(ContentType.MULTIPART)
                .basePath("/telemetry")
                .multiPart("image", file)
            .when()
                .post(String.format("/by-session/%s/screen/upload/alpha", userSession));

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);
    }

    // TODO: Move somewhere else?
    @Test
    @Order(8)
    void test_simpleUploadBeta_ok() {
        // Arrange
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("beta-frame.png")).getFile());

        // Act
        Response response = given()
                .contentType(ContentType.MULTIPART)
                .basePath("/telemetry")
                .multiPart("image", file)
                .when()
                .post(String.format("/by-session/%s/screen/upload/beta", userSession));

        // Assert
        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);
    }

    @Test
    @Order(9)
    void test_simpleCompleteExam_ok() {
        // Arrange
        // created Exam is taken from the post test with @Order(1)

        // Act
        Response startResponse = given()
                .basePath(BASE_URL)
                .when()
                .post(String.format("%s/complete", createdExam.getId()));

        Response response = given()
                .basePath(BASE_URL)
                .when()
                .get(createdExam.getId().toString());

        // Assert
        assertThat(startResponse.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        assertThat(response.statusCode())
                .isEqualTo(RestResponse.StatusCode.OK);

        Exam actualExam = response.then()
                .log().body()
                .extract().as(Exam.class);

        assertThat(actualExam.getActualEnd())
                .isNotNull();
        assertThat(actualExam.getState())
                .isEqualTo(ExamState.DONE);
    }
}
