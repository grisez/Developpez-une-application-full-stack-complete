CREATE TABLE users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(254) NOT NULL,
    username    VARCHAR(30)  NOT NULL,
    password    VARCHAR(128) NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE topics (
    id           INT          NOT NULL AUTO_INCREMENT,
    name         VARCHAR(50)  NOT NULL,
    description  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_topics_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE subscriptions (
    user_id   BIGINT NOT NULL,
    topic_id  INT    NOT NULL,
    PRIMARY KEY (user_id, topic_id),
    CONSTRAINT fk_subscriptions_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_subscriptions_topic_id FOREIGN KEY (topic_id) REFERENCES topics (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE posts (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(150) NOT NULL,
    content     TEXT         NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    author_id   BIGINT       NOT NULL,
    topic_id    INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_posts_author_id FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_posts_topic_id FOREIGN KEY (topic_id) REFERENCES topics (id),
    INDEX idx_posts_topic_id_created_at (topic_id, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE comments (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    content     VARCHAR(1000) NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    author_id   BIGINT        NOT NULL,
    post_id     BIGINT        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comments_post_id FOREIGN KEY (post_id) REFERENCES posts (id),
    INDEX idx_comments_post_id_created_at (post_id, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
