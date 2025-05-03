package business.mapper;

import business.dto.CustomerDTO;
import msa.commons.commands.createreservation.model.CustomerInfo;

// @Mapper
public interface BookingMapper {

    // BookingCreationMapper INSTANCE = Mappers.getMapper(BookingCreationMapper.class);

    static CustomerInfo dtoToCustomerInfo(CustomerDTO customerDTO) {

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
