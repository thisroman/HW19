package code.rondev.nasalargest.service;

import code.rondev.nasalargest.ImageData;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


@Service
public class ReactiveService {

    public Mono<byte[]> get(String url) {
        return WebClient.create(url)
                .get()
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(JsonNode.class))
                .flatMapIterable(jsonNode -> jsonNode.findValues("img_src"))
                .map(JsonNode::asText)
                .flatMap(imgUrl -> WebClient.builder()
                        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                        .exchangeStrategies(ExchangeStrategies.builder()
                                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(10_000_000))
                                .build())
                        .baseUrl(imgUrl)
                        .build()
                        .get()
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .map(ImageData::new))
                .reduce((o1, o2) -> o1.getData().length > o2.getData().length ? o1 : o2)
                .map(ImageData::getData);
    }
}
