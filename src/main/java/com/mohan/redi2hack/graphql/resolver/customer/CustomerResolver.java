package com.mohan.redi2hack.graphql.resolver.customer;

import com.mohan.redi2hack.graphql.data.CustomerCreateInput;
import com.mohan.redi2hack.graphql.data.CustomerUpdateInput;
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
public class CustomerResolver implements GraphQLQueryResolver,
        GraphQLMutationResolver,
        GraphQLSubscriptionResolver {
    private final CustomerService service;

    public List<Customer> customers() {
        return service.customers();
    }

    public Customer createCustomer(CustomerCreateInput customerCreateInput) {
        return service.createCustomer(customerCreateInput);
    }

    public Customer updateCustomer(Long customerId, CustomerUpdateInput customerUpdateInput) {
        return service.updateCustomer(customerId, customerUpdateInput);
    }

    public Customer deleteCustomer(Long customerId) {
        return service.deleteCustomer(customerId);
    }
    public Customer findCustomer(Long customerId) {
        return service.findCustomer(customerId);
    }

    public Publisher<Event> subscribeEvents(boolean fromStart) {
        return service.subscribeEvents(fromStart);
    }

    public List<Customer> searchCustomers(String query) {
        return service.searchCustomers(query);
    }

}
