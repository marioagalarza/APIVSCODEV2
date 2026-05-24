APP      := apivscodev2
VERSION  := 0.0.1-SNAPSHOT
JAR      := target/$(APP)-$(VERSION).jar
IMAGE    := $(APP)

.PHONY: build test run docker-build docker-run docker-stop clean db-up db-down db-logs db-shell

build:
	./mvnw package -DskipTests -q

test:
	./mvnw test

run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local

docker-build:
	docker build -t $(IMAGE):local .

docker-run: docker-build
	docker compose up -d

docker-stop:
	docker compose down

clean:
	./mvnw clean
	docker compose down --rmi local 2>/dev/null || true

db-up:
	docker compose up -d postgres

db-down:
	docker compose stop postgres

db-logs:
	docker compose logs -f postgres

db-shell:
	docker compose exec postgres psql -U appuser -d apivscodev2
