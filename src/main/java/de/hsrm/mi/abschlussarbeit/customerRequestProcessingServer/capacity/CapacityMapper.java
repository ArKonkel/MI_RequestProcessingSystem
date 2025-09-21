package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface CapacityMapper {

    MatchingEmployeeCapacitiesDto toDto(MatchingEmployeeCapacitiesVO vo);

    CalculatedCapacitiesOfMatchDto toDto(CalculatedCapacitiesOfMatchVO vo);

    CalculatedCapacityCalendarEntryDto toDto(CalculatedCapacityCalendarEntryVO vo);

    CalculatedCapacitiesOfMatchVO toVo(CalculatedCapacitiesOfMatchDto dto);

}
