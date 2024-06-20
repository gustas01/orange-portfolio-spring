# Fase 1: Construção usando Maven
FROM maven:3

# Diretório de trabalho
WORKDIR /app

# Copiar o pom.xml e baixar as dependências
COPY pom.xml ./
RUN mvn dependency:resolve

# Copiar o código do projeto
COPY src ./src

# Compilar o projeto
RUN mvn package -DskipTests

# Diretório de trabalho
WORKDIR /app/target

# Expor a porta da aplicação (ajustar conforme necessário)
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "orange-portfolio-0.0.1-SNAPSHOT.jar"]
