package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.customer;

import org.mapstruct.Mapper;

/**
 * Mapper class for {@link Customer} and {@link CustomerDto}
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);

//    Customer toEntity(CustomerDto customerDto);
}