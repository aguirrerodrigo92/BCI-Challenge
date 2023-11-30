package com.bci.challenge.mapper;

import com.bci.challenge.dto.PhoneDto;
import com.bci.challenge.model.Phone;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PhoneMapper {
    PhoneMapper INSTANCE = Mappers.getMapper(PhoneMapper.class);

    Phone toEntity(PhoneDto phoneDto);

    PhoneDto toDto(Phone phone);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Phone partialUpdate(PhoneDto phoneDto, @MappingTarget Phone phone);
}