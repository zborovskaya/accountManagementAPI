CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(500),
                       date_of_birth DATE,
                       password VARCHAR(500)
);

CREATE TABLE account (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT UNIQUE NOT NULL,
                         balance NUMERIC,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE email_data (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            email VARCHAR(200) UNIQUE NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE phone_data (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            phone VARCHAR(13) UNIQUE NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id)
);
