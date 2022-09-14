import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BackendClient {
    private final String baseUrl = "http://localhost:8080";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkerInformation getWorkerInformation() {
        return workerInformation;
    }

    private WorkerInformation workerInformation = null;
    public final static BackendClient instance = new BackendClient();

    private BackendClient() {
    }

    public void disconnect(){
        workerInformation = null;

    }
    @SneakyThrows
    public void authenticate(String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/login"))
                .queryParam("username", username)
                .queryParam("password", password)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();


        Jwt jwt = objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });

        DecodedJWT decodedJWT = JWT.decode(jwt.getAccessToken());
        workerInformation = new WorkerInformation(jwt, decodedJWT.getClaims().get("roles").asList(Job.class), decodedJWT.getClaims().get("shop").as(Long.class));
    }

    @SneakyThrows
    public List<Worker> allWorkers() {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/worker"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    public Worker upsertWorker(Worker worker) {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/worker"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(worker)))
                .build();
        Worker updated = objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
        return updated;
    }

    @SneakyThrows
    public Customer upsertCustomer(Customer worker) {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/customer"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(worker)))
                .build();
        Customer updated = objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
        return updated;
    }

    @SneakyThrows
    public void deleteWorker(String workerId) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/worker"))
                .path("/" + workerId)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @SneakyThrows
    public Shop getShop(Long shopId) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/shop"))
                .path("/" + shopId)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    public List<ItemQuantity> getShopItems(Long shopId) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/shop"))
                .path("/" + shopId)
                .path("/items")
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    public Customer getCustomer(String customerId) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/customer"))
                .path("/" + customerId)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }
    @SneakyThrows
    public List<Customer> allCustomers() {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/customer"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }


    @SneakyThrows
    public TransactionResult buy(TransactionDetails transactionDetails) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/transaction/buy"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(transactionDetails)))
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    public TransactionResult sell(TransactionDetails transactionDetails) {
        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/transaction/sell"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(transactionDetails)))
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });


    }
    @SneakyThrows
    public List<SellsPerShopReport> getShopLog() {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/report/shop"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }
    @SneakyThrows
    public List<SellsPerCategoryReport> getCategoryLog() {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/report/category"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }
    @SneakyThrows
    public List<SellsPerItemReport> getItemLog() {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/report/item"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + workerInformation.getJwt().getAccessToken())
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WorkerInformation {
        private Jwt jwt;
        private List<Job> jobs;
        private Long shopId;
    }
}
