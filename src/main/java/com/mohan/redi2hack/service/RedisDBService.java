package com.mohan.redi2hack.service;

import com.github.javafaker.Faker;
import com.mohan.redi2hack.graphql.data.CustomerCreateInput;
import com.mohan.redi2hack.model.Customer;
import com.mohan.redi2hack.repository.CustomerRepository;
import com.redislabs.lettusearch.CreateOptions;
import com.redislabs.lettusearch.Field;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mohan.redi2hack.service.CustomerService.CUSTOMER_SEARCH_INDEX;

@Service
@AllArgsConstructor
@Slf4j
public class RedisDBService {
    private static final int NUMBER_OF_CUSTOMERS_TO_CREATE = 5;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final RedisTemplate redisTemplate;
    private final StatefulRediSearchConnection<String, String> searchConnection;

    public String resetDatabase() {
        log.info("RedisDBService.run :: Enter");
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        createCustomerSearchIndex();

        Faker faker = new Faker();
        long count = customerRepository.count();

        log.info("Before Fake Creation: Current count = " + count);
        for (int i = 1; i <= count + NUMBER_OF_CUSTOMERS_TO_CREATE; i++) {
            var customerInput = CustomerCreateInput.builder()
                    .name(faker.company().name())
                    .industry(faker.company().industry())
                    .build();
            Customer customer = customerService.createCustomer(customerInput);
        }


        for (Customer customer : customerRepository.findAll()) {
            log.info("customer = " + customer);
        }

        log.info("After Fake Creation: Current count = " + customerRepository.count());


        log.info("RedisDBService.run :: Exit");
        return "RedisDB has been reset and created few customers";
    }

    @SuppressWarnings("unchecked")
    public void createCustomerSearchIndex() {
        log.info("RedisDBService.createCustomerSearchIndex :: Enter");

        RediSearchCommands<String, String> commands = searchConnection.sync();
        try {
            List<Object> ftInfo = commands.ftInfo(CUSTOMER_SEARCH_INDEX);
            log.info("ftInfo = " + ftInfo);
        } catch (RedisCommandExecutionException ex) {
            if (ex.getMessage().equals("Unknown Index name")) {

                CreateOptions<String, String> options = CreateOptions.<String, String>builder()
                        .prefix(String.format("%s:", Customer.class.getName())).build();

                Field<String> id = Field.text("id").build();
                Field<String> name = Field.text("name").sortable(true).build();
                Field<String> industry = Field.text("industry").build();

                commands.create(
                        CUSTOMER_SEARCH_INDEX, //
                        options, //
                        id, name, industry
                );

                log.info("Customer Search Index Created...");
            } else {
                log.error(ex.getMessage(), ex);
            }
        }
        log.info("RedisDBService.createCustomerSearchIndex :: Exit");
    }

}
