package business.saga.bookingcreation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.dto.CustomerDTO;
import msa.commons.microservices.reservationairline.commandevent.model.CustomerInfo;

@Mapper
public interface BookingCreationMapper {

    BookingCreationMapper INSTANCE = Mappers.getMapper(BookingCreationMapper.class);

    default CustomerInfo dtoToCustomerInfo(CustomerDTO customerDTO) {

        if (customerDTO == null)
            return null;

        CustomerInfo customerInfo = new CustomerInfo();

        customerInfo.setDni(customerDTO.getDni());
        customerInfo.setEmail(customerDTO.getEmail());
        customerInfo.setName(customerDTO.getName());
        customerInfo.setPhone(customerDTO.getPhone());

        return customerInfo;
    }

}
