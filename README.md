# Orange Portfólio - Back End - Spring Boot

![Static Badge](https://img.shields.io/badge/Spring_Boot-orange)
![Static Badge](https://img.shields.io/badge/Java_21-orange)
![Static Badge](https://img.shields.io/badge/JUnit-orange)
![Static Badge](https://img.shields.io/badge/Mockito-orange)

## 💻 Sobre
Backend do Orange Portfolio similir ao feito em NestJS na 4ª edição do hackaton da FCamara, agora sendo refeito por mim em Java com Spring Boot. As funcionalidades são basicamente as mesmas, porém adicionado um requisito não funcional que a autorização por Roles (cargos - funções) para criação, atualização e deleção das Tags que identificam o conteúdo dos projetos; além de testes unitários e de integração para ajudar a garantir a confiabilidade e consistência do projeto.

## 🛠️ Tecnologias utilizadas
<a href="https://spring.io/projects/spring-boot"><img alt="SPRINGBOOT" src="https://img.shields.io/badge/spring-ff5522?style=for-the-badge&logo=spring&logoColor=white"></a>
<a href="https://docs.oracle.com/en/java/javase/21/"><img alt="JAVA" src="https://img.shields.io/badge/Java-FF5722?style=for-the-badge&logo=java&logoColor=white"></a>
<a href="https://www.postgresql.org/"><img alt="POSTGRE" src="https://img.shields.io/badge/postgresql-ff5522?style=for-the-badge&logo=postgresql&logoColor=white"></a>
<a href="https://junit.org/junit5/"><img alt="TYPEORM" src="https://img.shields.io/badge/JUnit-FF5722?style=for-the-badge&logo=junit5&logoColor=white"></a>
<a href="https://site.mockito.org/"><img alt="SWAGGER" src="https://img.shields.io/badge/Mockito-FF5722?style=for-the-badge&logo=mockito&logoColor=white"></a>
<a href="[https://site.mockito.org/](https://www.h2database.com/)"><img alt="SWAGGER" src="https://img.shields.io/badge/H2-FF5722?style=for-the-badge&logo=h2&logoColor=white"></a>
<a href="[https://site.mockito.org/](https://www.h2database.com/)"><img alt="SWAGGER" src="https://img.shields.io/badge/Hibernate-FF5722?style=for-the-badge&logo=hibernate&logoColor=white"></a>
<a href="[https://site.mockito.org/](https://www.h2database.com/)"><img alt="SWAGGER" src="https://img.shields.io/badge/JPA-FF5722?style=for-the-badge&logo=java&logoColor=white"></a>


### ⚙️ Funcionalidades

- [x] Cadastro de usuário
- [x] Login com o Google
- [x] Login com email e senha
- [x] Cadastro de Projetos
- [x] Edição de projetos
- [x] Exclusão de projetos
- [x] Descoberta de novos projetos
- [x] Filtragem projetos por categorias


## 👨‍💻 Como executar localmente
OBS: Tanto o Java quanto o Maven instalados e suas variáveis de ambiente configuradas no local em que será executada a aplicação. <br><br>
1º passo - Depois de clonar o projeto, crie um arquivo chamado `application-local.properties` dentro de `src/main/resources` e o preencha com as informações que se pede:
```bash
# A url de conexão com o seu banco de dados.
spring.datasource.url=

# O dialeto do banco que será usado pelo Hibernate. Caso vá usar o PostgreSQL, não é necessário alterar essa informação.
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# A string secreta que será usada para gerar os tokens
api.security.token.secret=

# ID do clinte imgur para upload das imagens.
client_id_imgur=

# ID do cliente do google para o login (oauth with google)
spring.security.oauth2.client.registration.google.client-id=

# Secret key encontrada no mesmo lugar do ID, e para o mesmo propósito
spring.security.oauth2.client.registration.google.client-secret=
```
2º passo - No terminal aberto na raiz do projeto, execute o comando `mvn dependency:resolve` para baixar as dependências e compilar a aplicação.<br><br>
3º passo - No terminal aberto na raiz do projeto, execute o comando `mvn spring-boot:run` para executar a aplicação.


## 👨‍💻 Como executar os testes localmente
No terminal aberto na raiz do projeto, execute o comando `mvn test` para executar os testes da aplicação.


## Gerar artefato para produção
Para gerar o artefato para fazer o deploy da aplicação em produção, execute o comando `mvn clean install`, assim o maven vai baixar as dependências, compilar, executar os testes e gerar o artefato .jar.
