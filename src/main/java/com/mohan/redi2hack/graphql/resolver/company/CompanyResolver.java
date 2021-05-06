package com.mohan.redi2hack.graphql.resolver.company;

import com.mohan.redi2hack.graphql.data.CustomerInput;
import com.mohan.redi2hack.model.Customer;
import com.mohan.redi2hack.model.Event;
import com.mohan.redi2hack.service.CustomerService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyResolver implements GraphQLQueryResolver,
        GraphQLMutationResolver,
        GraphQLSubscriptionResolver {
    private final CustomerService service;

    public List<Customer> customers() {
        return service.customers();
    }

    public Customer createCustomer(CustomerInput customerInput) {
        return service.createCustomer(customerInput);
    }

    public Publisher<Event> subscribeEvents(boolean fromStart) {
        return service.subscribeEvents(fromStart);
    }

    public List<Customer> searchCustomer(String query) {
        return service.searchCustomer(query);
    }

}
