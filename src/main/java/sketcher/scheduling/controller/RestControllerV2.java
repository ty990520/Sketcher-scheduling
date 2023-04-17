package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sketcher.scheduling.domain.ManagerHopeTime;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.ManagerAssignScheduleDto;
import sketcher.scheduling.dto.ReturnCountAndObject;
import sketcher.scheduling.repository.UserRepository;
import sketcher.scheduling.service.ManagerAssignScheduleService;
import sketcher.scheduling.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@Api(tags = {"RestController 리팩토링 버전"})
public class RestControllerV2{
    private final UserService userService;
    private final UserRepository userRepository;
    private final ManagerAssignScheduleService assignScheduleService;


    @ApiOperation(value = "매니저정보 및 희망시간 조회")
    @GetMapping(value = "/find_All_Manager")
    public ReturnCountAndObject findAllManager() {
        List<ManagerDto> collect = userRepository.findAllManager().stream()
                .map(ManagerDto::new).collect(toList());

        return new ReturnCountAndObject(collect.size(), collect);
    }

    @ApiOperation(value = "saveAll을 사용한 스케줄 저장")
    @RequestMapping(value = "/create_assign_schedule", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public int createAssignScheduleV2(@RequestBody List<Map<String, Object>> param) {
        System.out.println("@@@@@@@@@@@@@@@");
        assignScheduleService.saveAllManagerAssignSchedule(parsingDtoList(param));
        System.out.println("@@@@@@@@@@@@@@@");
        return param.size();
    }


    private List<ManagerAssignScheduleDto> parsingDtoList(List<Map<String, Object>> param) {
        List<ManagerAssignScheduleDto> dtoList = new ArrayList<>();

        for (Map<String, Object> stringObjectMap : param) {
            String startDateString = (String) stringObjectMap.get("startTime"); //2022-07-24T22:00:00.000Z
            String endDateString = (String) stringObjectMap.get("endTime"); //2022-07-24T22:00:00.000Z
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA);

            LocalDateTime startDateUTC = LocalDateTime.parse(startDateString, dateTimeFormatter);
            LocalDateTime endDateUTC = LocalDateTime.parse(endDateString, dateTimeFormatter);

            LocalDateTime startDate = startDateUTC.plusHours(9);
            LocalDateTime endDate = endDateUTC.plusHours(9);

            ManagerAssignScheduleDto dto = ManagerAssignScheduleDto.builder()
                    .user(userService.findByCode((Integer) stringObjectMap.get("usercode")).get())
                    .scheduleDateTimeStart(startDate)
                    .scheduleDateTimeEnd(endDate)
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }


    @Data
    @AllArgsConstructor
    static class ManagerDto {
        private Integer code;
        private String id;
        private String username;
        private List<Integer> start_time;

        ManagerDto(User user) {
            code = user.getCode();
            id = user.getId();
            username = user.getUsername();
            start_time = user.getManagerHopeTimeList().stream()
                    .map(ManagerHopeTime::getStart_time)
                    .collect(toList());
        }
    }
}