CREATE TABLE topics_students (
    topic_id BIGINT NOT NULL,
    student_id BINARY(16) NOT NULL,
    PRIMARY KEY (topic_id, student_id),
    CONSTRAINT fk_topics_students_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    CONSTRAINT fk_topics_students_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);
