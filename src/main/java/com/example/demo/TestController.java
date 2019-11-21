package com.example.demo;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@RestController
public class TestController {

  private Queue<Event> eventQueue = new LinkedBlockingQueue<>();

  private ConnectableFlux<Object> eventStream;

  private synchronized ConnectableFlux<Object> getCompletableEventStream() {
    if (eventStream == null) {
      eventStream = Flux.create(emitter -> {
        while (true) {
          Event event = eventQueue.poll();
          if (event != null) {
            System.out.println("sending " + event);
            emitter.next(event);
          }
        }
      })
          .sample(Duration.ofSeconds(1))
          .doOnSubscribe(s -> System.out.println("subscribed: " + s))
          .doOnComplete(() -> System.out.println("complete"))
          .doOnRequest(c -> System.out.println("on request " + c))
          .publish();

      eventStream.connect();
    }

    return eventStream;
  }


  @RequestMapping(path = "/stream", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Object> getEventStream() {
    return getCompletableEventStream();
  }

  @PostMapping("event")
  public void receiveEvent(Event event) {
    System.out.println(eventQueue.size());
    eventQueue.add(event);
  }
}
