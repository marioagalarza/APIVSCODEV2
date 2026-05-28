-- Tabla base para verificar que Flyway funciona correctamente
CREATE TABLE hello_messages (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    message     VARCHAR(255) NOT NULL,
    locale      VARCHAR(10)  NOT NULL DEFAULT 'es',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Dato inicial de prueba
INSERT INTO hello_messages (message, locale) VALUES ('Hola Mundo', 'es');
INSERT INTO hello_messages (message, locale) VALUES ('Hello World', 'en');
