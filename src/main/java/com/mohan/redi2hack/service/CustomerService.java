package com.mohan.redi2hack.service;

import com.mohan.redi2hack.graphql.data.CustomerCreateInput;
import com.mohan.redi2hack.graphql.data.CustomerUpdateInput;
import com.mohan.redi2hack.model.Customer;
import com.mohan.redi2hack.model.Event;
import com.mohan.redi2hack.repository.CustomerRepository;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.SearchOptions;
import com.redislabs.lettusearch.SearchResults;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mohan.redi2hack.model.Event.EVENT_KEY_PREFIX;


@AllArgsConstructor
@Service
@Slf4j
@Transactional
public class CustomerService {
    public static final String CUSTOMER_SEARCH_INDEX = "redi2hack:customer-idx";
    public static final Duration EVENT_STREAM_POLL_TIMEOUT = Duration.ofSeconds(2);

    private final CustomerRepository customerRepository;
    private final RedisTemplate<String, Customer> redisTemplate;
    private final ReactiveRedisConnectionFactory connectionFactory;
    private final StatefulRediSearchConnection<String, String> searchConnection;

    public List<Customer> customers() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Customer createCustomer(CustomerCreateInput customerCreateInput) {
        Long customerId = redisTemplate.opsForValue().increment("customerId");
        var customer = Customer.builder()
                .id(customerId)
                .name(customerCreateInput.getName())
                .industry(customerCreateInput.getIndustry())
                .build();

        customerRepository.save(customer);

        Event event = Event.createdOf(customer);

        StringRecord stringRecord = StreamRecords.string(event.toMap()).withStreamKey(EVENT_KEY_PREFIX);

        RecordId recordId = redisTemplate.opsForStream().add(stringRecord);
        log.info("created event recordId = " + recordId);
        return customer;
    }

    public Customer updateCustomer(Long customerId, CustomerUpdateInput customerUpdateInput) {

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Customer customer = customerOpt.orElseThrow(() -> CompanyNotFoundException.COMPANY_NOT_FOUND_EXCEPTION);
        if (Objects.nonNull(customerUpdateInput.getName())) {
            customer.setName(customerUpdateInput.getName());
        }
        if (Objects.nonNull(customerUpdateInput.getIndustry())) {
            customer.setIndustry(customerUpdateInput.getIndustry());
        }

        customerRepository.save(customer);

        Event event = Event.updatedOf(customer);

        StringRecord stringRecord = StreamRecords.string(event.toMap()).withStreamKey(EVENT_KEY_PREFIX);

        RecordId recordId = redisTemplate.opsForStream().add(stringRecord);
        log.info("updated event recordId = " + recordId);
        return customer;
    }

    public Customer deleteCustomer(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Customer customer = customerOpt.orElseThrow(() -> CompanyNotFoundException.COMPANY_NOT_FOUND_EXCEPTION);

        customerRepository.delete(customer);

        Event event = Event.deletedOf(customer);

        StringRecord stringRecord = StreamRecords.string(event.toMap()).withStreamKey(EVENT_KEY_PREFIX);

        RecordId recordId = redisTemplate.opsForStream().add(stringRecord);
        log.info("deleted event recordId = " + recordId);
        return customer;
    }


    public List<Customer> searchCustomers(String query) {
        RediSearchCommands<String, String> commands = searchConnection.sync();
        SearchResults<String, String> results;
        try {
            var sortByName = SearchOptions.SortBy.<String>builder()
                    .field("name")
                    .build();
            var searchOptions = SearchOptions.<String>builder()
                    .sortBy(sortByName)
                    .build();

            results = commands.search(CustomerService.CUSTOMER_SEARCH_INDEX, query, searchOptions);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return List.of();
        }

        log.info("Customer search results counts: [{}] ", results.getCount());
        return results.stream()
                .map(Customer::from)
                .collect(Collectors.toList());
    }

    public Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> CompanyNotFoundException.COMPANY_NOT_FOUND_EXCEPTION);
    }

    public Publisher<Event> subscribeEvents(boolean fromStart) {
        log.info("CustomerService.subscribeEvents fromStart = " + fromStart);
        var options = StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(EVENT_STREAM_POLL_TIMEOUT)
                .build();

        var receiver = StreamReceiver.create(connectionFactory, options);

        Flux<MapRecord<String, String, String>> messages;
        if (fromStart) {
            messages = receiver.receive(StreamOffset.fromStart(EVENT_KEY_PREFIX));
        } else {
            messages = receiver.receive(StreamOffset.latest(EVENT_KEY_PREFIX));
        }

        return messages.doOnNext(message -> {
            log.info("MessageId: " + message.getId());
            log.info("Stream: " + message.getStream());
            log.info("Body: " + message.getValue());
        }).map(entries -> {
            log.info("entries = " + entries);
            Map<String, String> value = entries.getValue();

            return Event.from(value);
        });

    }
}

class CompanyNotFoundException extends RuntimeException implements GraphQLError {
    public static final String MESSAGE = "Customer not found: use customers query to find an existing ones";
    public static final CompanyNotFoundException COMPANY_NOT_FOUND_EXCEPTION = new CompanyNotFoundException(MESSAGE);

    public CompanyNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Collections.emptyMap();
    }
}