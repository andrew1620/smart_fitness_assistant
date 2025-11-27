CREATE TABLE questions
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title        VARCHAR(255) NOT NULL,
    position     INTEGER      NOT NULL,
    target_field VARCHAR(30)
);

CREATE TABLE answers
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    question_id UUID         NOT NULL
);

CREATE TABLE users
(
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    telegram_chat_id         VARCHAR(20) NOT NULL,
    current_question_index   INTEGER     NOT NULL,
    workout_plan_id          UUID,
    nutrition_plan_id        UUID,
    role                     VARCHAR(20),
    password                 VARCHAR(255),

    survey_status            VARCHAR(20) NOT NULL,
    age                      INTEGER,
    aim                      VARCHAR(50),
    fat                      INTEGER,
    water                    VARCHAR(50),
    height                   INTEGER,
    weight                   INTEGER,
    desired_weight           INTEGER,
    body_type                VARCHAR(50),
    desired_body             VARCHAR(50),
    work_body                VARCHAR(100),
    meal_plan                VARCHAR(50),
    junk_food                VARCHAR(50),
    training_level           INTEGER,
    press_up                 VARCHAR(50),
    lifting                  VARCHAR(50),
    training_frequency       VARCHAR(50),
    time                     INTEGER,
    preferred_sport          VARCHAR(100),
    additional_aim           VARCHAR(100),
    preferred_training_space VARCHAR(50),
    inventory                VARCHAR(100)
);

CREATE TABLE workout_plans
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE workouts
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workout_plan_id UUID REFERENCES workout_plans (id),
    description     TEXT,
    day_number      INTEGER NOT NULL
);

CREATE TABLE nutrition_plans
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    calories INTEGER,
    protein  INTEGER,
    fat      INTEGER,
    carbs    INTEGER
);

CREATE TYPE outbox_event_type_enum AS ENUM ('REMIND', 'CLEAN_EXPIRED_SESSIONS');
CREATE TYPE outbox_event_status_enum AS ENUM ('NEW', 'IN_PROCESS', 'COMPLETED', 'FAILED');
CREATE TYPE remind_type_enum AS ENUM ('TRAINING');
CREATE TYPE user_role_enum AS ENUM ('ROLE_USER', 'ROLE_ADMIN');

CREATE TABLE outbox_events
(
    id      UUID PRIMARY KEY                  DEFAULT gen_random_uuid(),
    type    outbox_event_type_enum   NOT NULL,
    payload TEXT,
    status  outbox_event_status_enum NOT NULL DEFAULT 'NEW'
);

CREATE TABLE reminds
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID             NOT NULL,
    type        remind_type_enum NOT NULL,
    message     TEXT             NOT NULL,
    remind_time TIME             NOT NULL,
    repeat      VARCHAR(100),
    stop_repeat DATE
);
