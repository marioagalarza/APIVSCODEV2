APP      := apivscodev2
VERSION  := 0.0.1-SNAPSHOT
JAR      := target/$(APP)-$(VERSION).jar
IMAGE    := $(APP)

.PHONY: build test run docker-build docker-run docker-stop clean

build:
	./mvnw package -DskipTests -q

test:
	./mvnw test

run: build
	SPRING_PROFILES_ACTIVE=local java -jar $(JAR)

docker-build:
	docker build -t $(IMAGE):local .

docker-run: docker-build
	docker compose up -d

docker-stop:
	docker compose down

clean:
	./mvnw clean
	docker compose down --rmi local 2>/dev/null || true
