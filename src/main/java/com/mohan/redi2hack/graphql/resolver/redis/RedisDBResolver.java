package com.mohan.redi2hack.graphql.resolver.redis;


import com.mohan.redi2hack.service.RedisDBService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisDBResolver implements GraphQLMutationResolver {

    private final RedisDBService service;

    public String resetRedis() {
        return service.resetDatabase();
    }
}
