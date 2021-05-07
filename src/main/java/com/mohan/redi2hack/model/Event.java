package com.mohan.redi2hack.model;

import lombok.*;

import java.util.Map;

enum EventType {
    CustomerCreated,
    CustomerUpdated,
    CustomerDeleted

}

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
public class Event {
    public static final String EVENT_KEY_PREFIX = "redi2hack:event-stream";

    private String message;
    private EventType eventType;

    public static Event createdOf(Customer customer) {
        return Event.builder()
                .message(String.format("Customer [%s] has been created", customer.getName()))
                .eventType(EventType.CustomerCreated)
                .build();
    }

    public static Event updatedOf(Customer customer) {
        return Event.builder()
                .message(String.format("Customer [%s] has been updated", customer.getName()))
                .eventType(EventType.CustomerUpdated)
                .build();
    }

    public static Event deletedOf(Customer customer) {
        return Event.builder()
                .message(String.format("Customer [%s] has been deleted", customer.getName()))
                .eventType(EventType.CustomerDeleted)
                .build();
    }

    public static Event from(Map<String, String> map) {

        return Event.builder()
                .message(map.get("message"))
                .eventType(EventType.valueOf(map.get("eventType")))
                .build();
    }

    public Map<String, String> toMap() {
        return Map.of("message", message, "eventType", eventType.toString());
    }
}