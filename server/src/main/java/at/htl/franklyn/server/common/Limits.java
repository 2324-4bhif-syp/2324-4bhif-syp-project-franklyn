package at.htl.franklyn.server.common;

public class Limits {
    // region Exam Entity
    public static final int EXAM_TITLE_LENGTH_MIN = 3;
    public static final int EXAM_TITLE_LENGTH_MAX = 100;

    public static final int EXAM_PIN_MIN_VALUE = 0;
    public static final int EXAM_PIN_MAX_VALUE = 999;

    public static final int EXAM_MIN_CAPTURE_INTERVAL_SECONDS = 1;
    public static final int EXAM_MAX_CAPTURE_INTERVAL_SECONDS = 10;
    // endregion Exam Entity

    // region Examinee Entity
    public static final int EXAMINEE_FIRSTNAME_LENGTH_MIN = 2;
    public static final int EXAMINEE_FIRSTNAME_LENGTH_MAX = 50;

    public static final int EXAMINEE_LASTNAME_LENGTH_MIN = 2;
    public static final int EXAMINEE_LASTNAME_LENGTH_MAX = 50;
    // endregion Examinee Entity

    // region Image Entity
    public static final int IMAGE_PATH_LENGTH_MIN = 1; // 4095 = Linux path limit
    public static final int IMAGE_PATH_LENGTH_MAX = 4095; // 4095 = Linux path limit
    // endregion Image Entity
}
