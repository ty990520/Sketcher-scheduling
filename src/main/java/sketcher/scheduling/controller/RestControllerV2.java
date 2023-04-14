package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.ReturnCountAndObject;
import sketcher.scheduling.repository.UserRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@Api(tags = {"RestController 리팩토링 버전"})
public class RestControllerV2 implements RestCalendarController {
    private final UserRepository userRepository;

    @ApiOperation(value = "매니저정보 및 희망시간 조회")
    public ReturnCountAndObject findAllManager() {
        List<ManagerDto> collect = userRepository.findAllManager().stream()
                .map(ManagerDto::new).collect(toList());

        return new ReturnCountAndObject(collect.size(), collect);
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
                    .map(m2 -> m2.getStart_time())
                    .collect(toList());
        }
    }
}