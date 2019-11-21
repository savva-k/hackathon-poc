package com.example.demo;

public class Event {

  private String text;

  public Event() {
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "Event{" +
        "text='" + text + '\'' +
        '}';
  }
}
