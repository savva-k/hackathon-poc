package com.example.demo;

import java.util.function.Consumer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TestController {

  private Consumer<Event> onEventAdded;

  private Flux<Object> eventStream = Flux
      .create(emitter -> onEventAdded = emitter::next)
      .log()
      .share();


  @RequestMapping(path = "/stream", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Object> getEventStream() {
    return eventStream;
  }

  @PostMapping("event")
  public void receiveEvent(Event event) {
    onEventAdded.accept(event);
  }
}
