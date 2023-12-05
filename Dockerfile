FROM maven:3.8.4-amazoncorretto-17
WORKDIR /usr/src/app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/conversion-project.jar"]