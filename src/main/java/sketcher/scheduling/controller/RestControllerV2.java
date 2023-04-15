package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
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
public class RestControllerV2 implements RestCalendarController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ManagerAssignScheduleService assignScheduleService;


    @ApiOperation(value = "매니저정보 및 희망시간 조회")
    public ReturnCountAndObject findAllManager() {
        List<ManagerDto> collect = userRepository.findAllManager().stream()
                .map(ManagerDto::new).collect(toList());

        return new ReturnCountAndObject(collect.size(), collect);
    }

    @Override
    public int createAssignSchedule(@RequestBody List<Map<String, Object>> param)  {
//       [{username=이혜원, usercode=3, startTime=2023-04-02T15:00:00.000Z, endTime=2023-04-02T16:00:00.000Z}, {username=김희수, usercode=4, startTime=2023-04-02T16:00:00.000Z, endTime=2023-04-02T17:00:00.000Z}, {username=박태영, usercode=1, startTime=2023-04-02T17:00:00.000Z, endTime=2023-04-02T18:00:00.000Z}, {username=정민환, usercode=2, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김민준, usercode=5, startTime=2023-04-02T18:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=박태영, usercode=1, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김희수, usercode=4, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=유지호, usercode=12, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=성지훈, usercode=13, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=이수빈, usercode=32, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김진우, usercode=36, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김가은, usercode=40, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=이혜원, usercode=3, startTime=2023-04-02T20:00:00.000Z, endTime=2023-04-02T21:00:00.000Z}, {username=정예준, usercode=8, startTime=2023-04-02T21:00:00.000Z, endTime=2023-04-02T22:00:00.000Z}, {username=정하준, usercode=10, startTime=2023-04-02T22:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=박서준, usercode=6, startTime=2023-04-02T22:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=박시우, usercode=9, startTime=2023-04-02T22:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=정예준, usercode=8, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=유지호, usercode=12, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=성지훈, usercode=13, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=박지민, usercode=24, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김연우, usercode=28, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김하은, usercode=21, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김희철, usercode=25, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=유다은, usercode=29, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=이수빈, usercode=32, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김진우, usercode=36, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김가은, usercode=40, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=최수민, usercode=44, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=임도윤, usercode=7, startTime=2023-04-03T00:00:00.000Z, endTime=2023-04-03T01:00:00.000Z}, {username=김준우, usercode=14, startTime=2023-04-03T00:00:00.000Z, endTime=2023-04-03T01:00:00.000Z}, {username=서주원, usercode=11, startTime=2023-04-03T01:00:00.000Z, endTime=2023-04-03T02:00:00.000Z}, {username=박건우, usercode=15, startTime=2023-04-03T01:00:00.000Z, endTime=2023-04-03T02:00:00.000Z}, {username=관리자, usercode=65, startTime=2023-04-03T02:00:00.000Z, endTime=2023-04-03T03:00:00.000Z}, {username=매니저, usercode=66, startTime=2023-04-03T02:00:00.000Z, endTime=2023-04-03T03:00:00.000Z}, {username=박서연, usercode=16, startTime=2023-04-03T03:00:00.000Z, endTime=2023-04-03T04:00:00.000Z}, {username=박하은, usercode=20, startTime=2023-04-03T03:00:00.000Z, endTime=2023-04-03T04:00:00.000Z}, {username=이지우, usercode=18, startTime=2023-04-03T04:00:00.000Z, endTime=2023-04-03T05:00:00.000Z}, {username=이서윤, usercode=17, startTime=2023-04-03T04:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박하은, usercode=20, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박서연, usercode=16, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박지민, usercode=24, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김연우, usercode=28, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김하은, usercode=21, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김희철, usercode=25, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=유다은, usercode=29, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김재원, usercode=48, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=정나윤, usercode=52, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=최예성, usercode=56, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김예린, usercode=33, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=정승우, usercode=37, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박서영, usercode=41, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김서현, usercode=19, startTime=2023-04-03T06:00:00.000Z, endTime=2023-04-03T07:00:00.000Z}, {username=김민서, usercode=22, startTime=2023-04-03T06:00:00.000Z, endTime=2023-04-03T07:00:00.000Z}, {username=김채원, usercode=26, startTime=2023-04-03T07:00:00.000Z, endTime=2023-04-03T08:00:00.000Z}, {username=김지원, usercode=30, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=유민서, usercode=23, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=이도현, usercode=27, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=강수현, usercode=45, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=김서현, usercode=19, startTime=2023-04-03T09:00:00.000Z, endTime=2023-04-03T10:00:00.000Z}, {username=서민우, usercode=49, startTime=2023-04-03T10:00:00.000Z, endTime=2023-04-03T11:00:00.000Z}, {username=김성현, usercode=53, startTime=2023-04-03T10:00:00.000Z, endTime=2023-04-03T11:00:00.000Z}, {username=한나은, usercode=57, startTime=2023-04-03T10:00:00.000Z, endTime=2023-04-03T11:00:00.000Z}, {username=이준영, usercode=34, startTime=2023-04-03T11:00:00.000Z, endTime=2023-04-03T12:00:00.000Z}, {username=박채은, usercode=38, startTime=2023-04-03T11:00:00.000Z, endTime=2023-04-03T12:00:00.000Z}, {username=윤민지, usercode=42, startTime=2023-04-03T11:00:00.000Z, endTime=2023-04-03T12:00:00.000Z}, {username=이동현, usercode=46, startTime=2023-04-03T12:00:00.000Z, endTime=2023-04-03T13:00:00.000Z}, {username=김연서, usercode=50, startTime=2023-04-03T12:00:00.000Z, endTime=2023-04-03T13:00:00.000Z}, {username=김우빈, usercode=54, startTime=2023-04-03T12:00:00.000Z, endTime=2023-04-03T13:00:00.000Z}, {username=홍예지, usercode=58, startTime=2023-04-03T13:00:00.000Z, endTime=2023-04-03T14:00:00.000Z}, {username=이지우, usercode=18, startTime=2023-04-03T14:00:00.000Z, endTime=2023-04-03T15:00:00.000Z}]
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

        assignScheduleService.saveAllManagerAssignSchedule(dtoList);


        return param.size();
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