package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import sketcher.scheduling.algorithm.AutoScheduling;
import sketcher.scheduling.algorithm.ResultScheduling;
import sketcher.scheduling.domain.ManagerHopeTime;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.EstimatedNumOfCardsPerHourDto;
import sketcher.scheduling.dto.ManagerAssignScheduleDto;
import sketcher.scheduling.repository.EstimatedNumOfCardsPerHourRepository;
import sketcher.scheduling.repository.ManagerHopeTimeRepository;
import sketcher.scheduling.repository.PercentageOfManagerWeightsRepository;
import sketcher.scheduling.repository.UserRepository;
import sketcher.scheduling.service.KakaoService;
import sketcher.scheduling.service.ManagerAssignScheduleService;
import sketcher.scheduling.service.ManagerHopeTimeService;
import sketcher.scheduling.service.UserService;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@Api(tags = {"RestController 리팩토링 전"})
public class RestController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final ManagerAssignScheduleService assignScheduleService;
    private final KakaoService kakaoService;
    private final ManagerHopeTimeService hopeTimeService;
    private final ManagerHopeTimeRepository managerHopeTimeRepository;
    private final EstimatedNumOfCardsPerHourRepository estimatedNumOfCardsPerHourRepository;
    private final PercentageOfManagerWeightsRepository percentageOfManagerWeightsRepository;

    public List<User> findAllManager() {
        return userRepository.findAllManager();
    }
    @GetMapping(value = "/find_All_Manager_Hope_Time")
    public List<ManagerHopeTime> findAllManagerHopeTime() {
        return hopeTimeService.findAll();
    }

    @RequestMapping(value = "/create_assign_schedule", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public int createAssignSchedule(@RequestBody List<Map<String, Object>> param) throws ParseException, IOException {
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
            assignScheduleService.saveManagerAssignSchedule(dto);
        }

//        sendKakaoMessage();

        return param.size();
    }

    @RequestMapping(value = "/current_status_info", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public JSONObject currentStatusInfo(@RequestBody List<Map<String, Object>> param) throws ParseException {
        String date = "";
        String day = "";
        int usercode[] = new int[param.size() - 1];
        int userCurrentTime[] = new int[param.size() - 1];
        List<List<Integer>> hopeTimeList = new ArrayList<>();
        int flag = 0;
        int index = 0;
        for (Map<String, Object> stringObjectMap : param) {
            System.out.println(stringObjectMap.toString());
            if (flag == 1) {    //
                usercode[index] = (int) stringObjectMap.get("userCode");
                userCurrentTime[index] = (int) stringObjectMap.get("userCurrentTime");
                String hopetimeStr = stringObjectMap.get("hopetime").toString();
                settingHopeTimeList(hopeTimeList, hopetimeStr);
                index++;
            } else {
                flag = 1;
                date = (String) stringObjectMap.get("date");
                day = (String) stringObjectMap.get("day");
            }
        }

        AutoScheduling autoScheduling = new AutoScheduling(userService, estimatedNumOfCardsPerHourRepository, percentageOfManagerWeightsRepository);
        ArrayList<ResultScheduling> schedulings = autoScheduling.runAlgorithm(usercode, userCurrentTime, hopeTimeList);

        JSONObject schedulingJsonObj = schedulingResultsToJson(date, day, schedulings);

        System.out.println(schedulingJsonObj.toJSONString());

        return schedulingJsonObj;
    }

    private JSONObject schedulingResultsToJson(String date, String day, ArrayList<ResultScheduling> schedulings) {
        JSONObject schedulingJsonObj = new JSONObject();

        JSONArray selectedDate = new JSONArray();
        JSONArray scheduleJsonList = new JSONArray();
        JSONArray userJsonList = new JSONArray();

        HashMap<Integer, Integer> userList = new HashMap<>();

        JSONObject dateInfo = new JSONObject();
        dateInfo.put("date", date);
        dateInfo.put("day", day);
        selectedDate.add(dateInfo);
        schedulingJsonObj.put("date", selectedDate);

        for (ResultScheduling scheduling : schedulings) {
            JSONObject scheduleItem = new JSONObject();
            scheduleItem.put("scheduleStartTime", scheduling.startTime);
            scheduleItem.put("userCode", scheduling.userCode);
            scheduleJsonList.add(scheduleItem);
//            System.out.println(scheduling.startTime+" / "+scheduling.userCode+"번 매니저 / 현재 배정시간 : "+scheduling.currentTime);
            if (!userList.containsKey(scheduling.userCode)) {
                userList.put(scheduling.userCode, scheduling.currentTime);
            }
        }

        schedulingJsonObj.put("scheduleResults", scheduleJsonList);

        for (Map.Entry<Integer, Integer> userStatus : userList.entrySet()) {
            JSONObject scheduleItem = new JSONObject();
            scheduleItem.put("userCode", userStatus.getKey());
            scheduleItem.put("userCurrentTime", userStatus.getValue());
            userJsonList.add(scheduleItem);
        }

        schedulingJsonObj.put("userResults", userJsonList);
        return schedulingJsonObj;
    }

    private void settingHopeTimeList(List<List<Integer>> hopeTimeList, String hopetimeStr) {
        String[] split = hopetimeStr.replace("[", "").replace("]", "").split(", ");
        List<Integer> hopetimes = new ArrayList<>();
        for (String hopetime : split) {
            hopetimes.add(Integer.parseInt(hopetime));
        }
        hopeTimeList.add(hopetimes);
    }

    @RequestMapping(value = "/update_est_cards", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public int updateEstCards(@RequestBody List<Map<String, Object>> param) throws ParseException, IOException {
        for (Map<String, Object> stringObjectMap : param) {
            Integer time = Integer.parseInt(stringObjectMap.get("time").toString());
            Integer value = Integer.parseInt(stringObjectMap.get("value").toString());
            EstimatedNumOfCardsPerHourDto dto = EstimatedNumOfCardsPerHourDto.builder()
                    .time(time)
                    .numOfCards(value)
                    .build();

            estimatedNumOfCardsPerHourRepository.save(dto.toEntity());
        }
        return param.size();
    }


    /* 카카오톡 메시지 전송하기 만료
    public void sendKakaoMessage() throws IOException {
//            String refresh_Token = kakaoService.getRefreshToken(code);
        String refresh_Token = "uIYs7FKmV4Y-s5EAb8OjEpHvvLtZN3zDoD6p2i_HCilwUAAAAYIu4OjU";
        String access_Token = kakaoService.refreshAccessToken(refresh_Token);
        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_Token);
        boolean isSendMessage = kakaoService.isSendMessage(access_Token);
        HashMap<String, Object> friendsId = kakaoService.getFriendsList(access_Token);
        boolean isSendMessageToFriends = kakaoService.isSendMessageToFriends(access_Token, friendsId);
        // 친구에게 메시지 보내기는 월 전송 제한이 있음 -> 주석 처리

//        session.setAttribute("refresh_Token", refresh_Token);
//        session.setAttribute("access_Token", access_Token);

    }*/
}