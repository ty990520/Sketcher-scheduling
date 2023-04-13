package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.ReturnCountAndObject;
import sketcher.scheduling.repository.EstimatedNumOfCardsPerHourRepository;
import sketcher.scheduling.repository.PercentageOfManagerWeightsRepository;
import sketcher.scheduling.repository.UserRepository;
import sketcher.scheduling.service.KakaoService;
import sketcher.scheduling.service.ManagerAssignScheduleService;
import sketcher.scheduling.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@Api(tags = {"RestController 리팩토링 버전"})
public class RestControllerV2 implements RestCalendarController{
    private final UserRepository userRepository;
    private final UserService userService;
    private final ManagerAssignScheduleService assignScheduleService;
    private final KakaoService kakaoService;
    private final EstimatedNumOfCardsPerHourRepository estimatedNumOfCardsPerHourRepository;
    private final PercentageOfManagerWeightsRepository percentageOfManagerWeightsRepository;

    @ApiOperation(value = "매니저정보 및 희망시간 조회")
    public ReturnCountAndObject findAllManager() {
        List<User> allManager = userRepository.findAllManager();

        List<ManagerDto> collect = allManager.stream()
                .map(m1 -> new ManagerDto(m1.getCode(), m1.getId(), m1.getUsername(),
                        m1.getManagerHopeTimeList().stream()
                        .map(m2 -> m2.getStart_time()) .collect(Collectors.toList())))
                .collect(Collectors.toList());
        return new ReturnCountAndObject(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class ManagerDto {
        private Integer code;
        private String id;
        private String username;
        private List<Integer> start_time;
    }



}