package code.rondev.nasalargest.controller;

import code.rondev.nasalargest.service.ReactiveService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pictures")
public class ReactiveController {

    @GetMapping(value = "/{sol}/largest", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<byte[]> getLargest(@PathVariable int sol) {

        return new ReactiveService().get(UriComponentsBuilder.fromHttpUrl("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos")
                .queryParam("sol", sol)
                .queryParam("api_key", "DEMO_KEY")
                .build()
                .toString());
    }
}
