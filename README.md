# Kurush (Geração de PDF)

API responsável pela geração de documentos PDF a partir dos dados do Monolito.  
Disponibiliza o endpoint:

```
GET /api/doc/pdf/{id}
```

Basta informar o **ID do documento** para receber o PDF gerado.

---

## 🐳 Docker

- **Repositório da Imagem**: https://hub.docker.com/repository/docker/yuri000/kurush_api/general

### Executando via Docker

```bash
docker run -d \
  --name kurush_api \
  -p 13500:13500 \
  -e QUARKUS_HTTP_PORT=13500 \
  -e QUARKUS_MONGODB_CONNECTION_STRING="mongodb://admin:admin@localhost:27017/monolito?authSource=admin" \
  -e QUARKUS_HTTP_CORS_ORIGINS="*" \
  -e QUARKUS_HTTP_CORS_METHODS="GET,POST,PUT,DELETE,OPTIONS" \
  -e QUARKUS_HTTP_CORS_HEADERS="*" \
  -e QUARKUS_HTTP_CORS_EXPOSED_HEADERS="Content-Disposition" \
  yuri000/kurush_api:latest
```

---

## Pré-requisitos para ambiente local

- **Java 21+**
- **Maven 3.8+**
- **Docker (opcional para builds nativos)**
- **MongoDB em execução**  
  (usado para buscar os dados antes da geração do PDF)

---

## Ambiente de Desenvolvimento

Inicie o modo de desenvolvimento com hot-reload:

```bash
./mvnw quarkus:dev
```

A Dev UI estará disponível em:

```
http://localhost:8080/q/dev/
```

---

## Empacotando a aplicação

Gerar o pacote padrão:

```bash
./mvnw package
```

O jar será criado em:

```
target/quarkus-app/quarkus-run.jar
```

Executar o jar:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

---

## Criando um Über-Jar

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

Executar:

```bash
java -jar target/*-runner.jar
```

---

## Build Nativo (opcional)

Criar binário nativo:

```bash
./mvnw package -Dnative
```

Ou usando container (sem GraalVM local):

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Executar:

```bash
./target/api-kurush-*-runner
```

---

## Guias Relacionados

- MongoDB Panache: https://quarkus.io/guides/rest-data-panache  
- Qute Templates: https://quarkus.io/guides/qute-reference  
- RESTEasy Reactive: https://quarkus.io/guides/resteasy-reactive
