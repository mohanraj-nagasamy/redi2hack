package com.mohan.redi2hack.graphql.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerUpdateInput {
    private String name;
    private String industry;
}
