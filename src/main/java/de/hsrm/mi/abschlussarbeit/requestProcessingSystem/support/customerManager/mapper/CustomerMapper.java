package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.dto.CustomerDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.entity.Customer;
import org.mapstruct.Mapper;

/**
 * Mapper class for {@link Customer} and {@link CustomerDto}
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);

//    Customer toEntity(CustomerDto customerDto);
}