import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private Jwt jwt;

    public final static BackendClient instance = new BackendClient();

    private BackendClient() {
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
        jwt = objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
        System.out.println(jwt);
    }

    @SneakyThrows
    public List<Worker> allWorkers() {

        HttpClient client = HttpClient.newHttpClient();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(URI.create(baseUrl + "/api/worker"))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Authorization", "Bearer " + jwt.getAccessToken())
                .build();
        return objectMapper.readValue(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeReference<>() {
        });
    }
}
