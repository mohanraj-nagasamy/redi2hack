package com.mohan.redi2hack.init;

import com.github.javafaker.Faker;
import com.mohan.redi2hack.graphql.data.CustomerInput;
import com.mohan.redi2hack.model.Customer;
import com.mohan.redi2hack.repository.CustomerRepository;
import com.mohan.redi2hack.service.CustomerService;
import com.redislabs.lettusearch.CreateOptions;
import com.redislabs.lettusearch.Field;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.mohan.redi2hack.service.CustomerService.CUSTOMER_SEARCH_INDEX;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerInitializer implements CommandLineRunner {
    public static final int NUMBER_OF_CUSTOMERS_TO_CREATE = 5;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private StatefulRediSearchConnection<String, String> searchConnection;

    @Override
    public void run(String... args) throws Exception {
        log.info("CustomerInitializer.run :: Enter");
        customerRepository.deleteAll();
        createCustomerSearchIndex();

        Faker faker = new Faker();
        long count = customerRepository.count();

        log.info("Before Fake Creation: Current count = " + count);
        for (int i = 1; i <= count + NUMBER_OF_CUSTOMERS_TO_CREATE; i++) {
            var customerInput = CustomerInput.builder()
                    .name(faker.company().name())
                    .industry(faker.company().industry())
                    .build();
            Customer customer = customerService.createCustomer(customerInput);
        }


        for (Customer customer : customerRepository.findAll()) {
            log.info("customer = " + customer);
        }

        log.info("After Fake Creation: Current count = " + customerRepository.count());


        log.info("CustomerInitializer.run :: Exit");
    }

    @SuppressWarnings("unchecked")
    public void createCustomerSearchIndex() {
        log.info("CustomerInitializer.createCustomerSearchIndex :: Enter");

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
        log.info("CustomerInitializer.createCustomerSearchIndex :: Exit");
    }
}
