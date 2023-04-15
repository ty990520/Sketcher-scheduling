package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sketcher.scheduling.algorithm.AutoScheduling;
import sketcher.scheduling.algorithm.ResultScheduling;
import sketcher.scheduling.domain.ManagerHopeTime;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.EstimatedNumOfCardsPerHourDto;
import sketcher.scheduling.dto.ManagerAssignScheduleDto;
import sketcher.scheduling.repository.EstimatedNumOfCardsPerHourRepository;
import sketcher.scheduling.repository.PercentageOfManagerWeightsRepository;
import sketcher.scheduling.repository.UserRepository;
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
//    private final KakaoService kakaoService;
    private final ManagerHopeTimeService hopeTimeService;
    private final EstimatedNumOfCardsPerHourRepository estimatedNumOfCardsPerHourRepository;
    private final PercentageOfManagerWeightsRepository percentageOfManagerWeightsRepository;

    public List<User> findAllManager() {
        return userRepository.findAllManager();
    }
    @GetMapping(value = "/find_All_Manager_Hope_Time")
    public List<ManagerHopeTime> findAllManagerHopeTime() {
        return hopeTimeService.findAll();
    }

//    @RequestMapping(value = "/create_assign_schedule", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public int createAssignSchedule(@RequestBody List<Map<String, Object>> param) throws ParseException, IOException {
        /*
        * [{username=이혜원, usercode=3, startTime=2023-04-02T15:00:00.000Z, endTime=2023-04-02T16:00:00.000Z}, {username=김희수, usercode=4, startTime=2023-04-02T16:00:00.000Z, endTime=2023-04-02T17:00:00.000Z}, {username=박태영, usercode=1, startTime=2023-04-02T17:00:00.000Z, endTime=2023-04-02T18:00:00.000Z}, {username=정민환, usercode=2, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김민준, usercode=5, startTime=2023-04-02T18:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=박태영, usercode=1, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김희수, usercode=4, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=유지호, usercode=12, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=성지훈, usercode=13, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=이수빈, usercode=32, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김진우, usercode=36, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=김가은, usercode=40, startTime=2023-04-02T19:00:00.000Z, endTime=2023-04-02T20:00:00.000Z}, {username=이혜원, usercode=3, startTime=2023-04-02T20:00:00.000Z, endTime=2023-04-02T21:00:00.000Z}, {username=정예준, usercode=8, startTime=2023-04-02T21:00:00.000Z, endTime=2023-04-02T22:00:00.000Z}, {username=정하준, usercode=10, startTime=2023-04-02T22:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=박서준, usercode=6, startTime=2023-04-02T22:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=박시우, usercode=9, startTime=2023-04-02T22:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=정예준, usercode=8, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=유지호, usercode=12, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=성지훈, usercode=13, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=박지민, usercode=24, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김연우, usercode=28, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김하은, usercode=21, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김희철, usercode=25, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=유다은, usercode=29, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=이수빈, usercode=32, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김진우, usercode=36, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=김가은, usercode=40, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=최수민, usercode=44, startTime=2023-04-02T23:00:00.000Z, endTime=2023-04-03T00:00:00.000Z}, {username=임도윤, usercode=7, startTime=2023-04-03T00:00:00.000Z, endTime=2023-04-03T01:00:00.000Z}, {username=김준우, usercode=14, startTime=2023-04-03T00:00:00.000Z, endTime=2023-04-03T01:00:00.000Z}, {username=서주원, usercode=11, startTime=2023-04-03T01:00:00.000Z, endTime=2023-04-03T02:00:00.000Z}, {username=박건우, usercode=15, startTime=2023-04-03T01:00:00.000Z, endTime=2023-04-03T02:00:00.000Z}, {username=관리자, usercode=65, startTime=2023-04-03T02:00:00.000Z, endTime=2023-04-03T03:00:00.000Z}, {username=매니저, usercode=66, startTime=2023-04-03T02:00:00.000Z, endTime=2023-04-03T03:00:00.000Z}, {username=박서연, usercode=16, startTime=2023-04-03T03:00:00.000Z, endTime=2023-04-03T04:00:00.000Z}, {username=박하은, usercode=20, startTime=2023-04-03T03:00:00.000Z, endTime=2023-04-03T04:00:00.000Z}, {username=이지우, usercode=18, startTime=2023-04-03T04:00:00.000Z, endTime=2023-04-03T05:00:00.000Z}, {username=이서윤, usercode=17, startTime=2023-04-03T04:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박하은, usercode=20, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박서연, usercode=16, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박지민, usercode=24, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김연우, usercode=28, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김하은, usercode=21, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김희철, usercode=25, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=유다은, usercode=29, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김재원, usercode=48, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=정나윤, usercode=52, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=최예성, usercode=56, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김예린, usercode=33, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=정승우, usercode=37, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=박서영, usercode=41, startTime=2023-04-03T05:00:00.000Z, endTime=2023-04-03T06:00:00.000Z}, {username=김서현, usercode=19, startTime=2023-04-03T06:00:00.000Z, endTime=2023-04-03T07:00:00.000Z}, {username=김민서, usercode=22, startTime=2023-04-03T06:00:00.000Z, endTime=2023-04-03T07:00:00.000Z}, {username=김채원, usercode=26, startTime=2023-04-03T07:00:00.000Z, endTime=2023-04-03T08:00:00.000Z}, {username=김지원, usercode=30, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=유민서, usercode=23, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=이도현, usercode=27, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=강수현, usercode=45, startTime=2023-04-03T08:00:00.000Z, endTime=2023-04-03T09:00:00.000Z}, {username=김서현, usercode=19, startTime=2023-04-03T09:00:00.000Z, endTime=2023-04-03T10:00:00.000Z}, {username=서민우, usercode=49, startTime=2023-04-03T10:00:00.000Z, endTime=2023-04-03T11:00:00.000Z}, {username=김성현, usercode=53, startTime=2023-04-03T10:00:00.000Z, endTime=2023-04-03T11:00:00.000Z}, {username=한나은, usercode=57, startTime=2023-04-03T10:00:00.000Z, endTime=2023-04-03T11:00:00.000Z}, {username=이준영, usercode=34, startTime=2023-04-03T11:00:00.000Z, endTime=2023-04-03T12:00:00.000Z}, {username=박채은, usercode=38, startTime=2023-04-03T11:00:00.000Z, endTime=2023-04-03T12:00:00.000Z}, {username=윤민지, usercode=42, startTime=2023-04-03T11:00:00.000Z, endTime=2023-04-03T12:00:00.000Z}, {username=이동현, usercode=46, startTime=2023-04-03T12:00:00.000Z, endTime=2023-04-03T13:00:00.000Z}, {username=김연서, usercode=50, startTime=2023-04-03T12:00:00.000Z, endTime=2023-04-03T13:00:00.000Z}, {username=김우빈, usercode=54, startTime=2023-04-03T12:00:00.000Z, endTime=2023-04-03T13:00:00.000Z}, {username=홍예지, usercode=58, startTime=2023-04-03T13:00:00.000Z, endTime=2023-04-03T14:00:00.000Z}, {username=이지우, usercode=18, startTime=2023-04-03T14:00:00.000Z, endTime=2023-04-03T15:00:00.000Z}]
         * */
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
/*
* {"date":[{"date":"2023-4-18","day":"화요일"}],"userResults":[{"userCurrentTime":3,"userCode":1},{"userCurrentTime":4,"userCode":2},{"userCurrentTime":4,"userCode":3},{"userCurrentTime":4,"userCode":4},{"userCurrentTime":3,"userCode":5},{"userCurrentTime":3,"userCode":6},{"userCurrentTime":3,"userCode":8},{"userCurrentTime":3,"userCode":9},{"userCurrentTime":3,"userCode":10},{"userCurrentTime":1,"userCode":11},{"userCurrentTime":3,"userCode":12},{"userCurrentTime":3,"userCode":13},{"userCurrentTime":1,"userCode":14},{"userCurrentTime":1,"userCode":15},{"userCurrentTime":2,"userCode":16},{"userCurrentTime":2,"userCode":17},{"userCurrentTime":2,"userCode":18},{"userCurrentTime":2,"userCode":19},{"userCurrentTime":2,"userCode":20},{"userCurrentTime":2,"userCode":21},{"userCurrentTime":1,"userCode":22},{"userCurrentTime":1,"userCode":23},{"userCurrentTime":2,"userCode":24},{"userCurrentTime":2,"userCode":25},{"userCurrentTime":1,"userCode":26},{"userCurrentTime":1,"userCode":27},{"userCurrentTime":2,"userCode":28},{"userCurrentTime":2,"userCode":29},{"userCurrentTime":1,"userCode":30},{"userCurrentTime":1,"userCode":31},{"userCurrentTime":2,"userCode":32},{"userCurrentTime":1,"userCode":33},{"userCurrentTime":1,"userCode":34},{"userCurrentTime":1,"userCode":35},{"userCurrentTime":2,"userCode":36},{"userCurrentTime":1,"userCode":37},{"userCurrentTime":1,"userCode":38},{"userCurrentTime":1,"userCode":39},{"userCurrentTime":2,"userCode":40},{"userCurrentTime":1,"userCode":41},{"userCurrentTime":1,"userCode":42},{"userCurrentTime":1,"userCode":44},{"userCurrentTime":1,"userCode":45},{"userCurrentTime":1,"userCode":46},{"userCurrentTime":1,"userCode":48},{"userCurrentTime":1,"userCode":49},{"userCurrentTime":1,"userCode":50},{"userCurrentTime":1,"userCode":52},{"userCurrentTime":1,"userCode":53},{"userCurrentTime":1,"userCode":54},{"userCurrentTime":1,"userCode":56},{"userCurrentTime":1,"userCode":57},{"userCurrentTime":1,"userCode":58},{"userCurrentTime":1,"userCode":65},{"userCurrentTime":1,"userCode":66}],"scheduleResults":[{"userCode":12,"scheduleStartTime":6},{"userCode":13,"scheduleStartTime":7},{"userCode":14,"scheduleStartTime":7},{"userCode":11,"scheduleStartTime":7},{"userCode":8,"scheduleStartTime":8},{"userCode":9,"scheduleStartTime":8},{"userCode":6,"scheduleStartTime":8},{"userCode":10,"scheduleStartTime":8},{"userCode":13,"scheduleStartTime":8},{"userCode":12,"scheduleStartTime":8},{"userCode":24,"scheduleStartTime":8},{"userCode":28,"scheduleStartTime":8},{"userCode":21,"scheduleStartTime":8},{"userCode":25,"scheduleStartTime":8},{"userCode":29,"scheduleStartTime":8},{"userCode":32,"scheduleStartTime":8},{"userCode":36,"scheduleStartTime":8},{"userCode":40,"scheduleStartTime":8},{"userCode":44,"scheduleStartTime":8},{"userCode":15,"scheduleStartTime":9},{"userCode":65,"scheduleStartTime":9},{"userCode":66,"scheduleStartTime":10},{"userCode":22,"scheduleStartTime":10},{"userCode":26,"scheduleStartTime":11},{"userCode":30,"scheduleStartTime":11},{"userCode":16,"scheduleStartTime":12},{"userCode":20,"scheduleStartTime":12},{"userCode":17,"scheduleStartTime":13},{"userCode":18,"scheduleStartTime":13},{"userCode":17,"scheduleStartTime":14},{"userCode":20,"scheduleStartTime":14},{"userCode":16,"scheduleStartTime":14},{"userCode":24,"scheduleStartTime":14},{"userCode":28,"scheduleStartTime":14},{"userCode":21,"scheduleStartTime":14},{"userCode":25,"scheduleStartTime":14},{"userCode":29,"scheduleStartTime":14},{"userCode":48,"scheduleStartTime":14},{"userCode":52,"scheduleStartTime":14},{"userCode":56,"scheduleStartTime":14},{"userCode":33,"scheduleStartTime":14},{"userCode":37,"scheduleStartTime":14},{"userCode":41,"scheduleStartTime":14},{"userCode":19,"scheduleStartTime":15},{"userCode":23,"scheduleStartTime":15},{"userCode":27,"scheduleStartTime":16},{"userCode":45,"scheduleStartTime":17},{"userCode":49,"scheduleStartTime":17},{"userCode":53,"scheduleStartTime":17},{"userCode":57,"scheduleStartTime":17},{"userCode":19,"scheduleStartTime":18},{"userCode":34,"scheduleStartTime":19},{"userCode":38,"scheduleStartTime":19},{"userCode":42,"scheduleStartTime":19},{"userCode":46,"scheduleStartTime":20},{"userCode":50,"scheduleStartTime":20},{"userCode":54,"scheduleStartTime":20},{"userCode":58,"scheduleStartTime":21},{"userCode":31,"scheduleStartTime":21},{"userCode":35,"scheduleStartTime":21},{"userCode":39,"scheduleStartTime":22},{"userCode":18,"scheduleStartTime":23},{"userCode":2,"scheduleStartTime":0},{"userCode":3,"scheduleStartTime":1},{"userCode":2,"scheduleStartTime":2},{"userCode":4,"scheduleStartTime":3},{"userCode":1,"scheduleStartTime":4},{"userCode":5,"scheduleStartTime":4},{"userCode":4,"scheduleStartTime":4},{"userCode":2,"scheduleStartTime":4},{"userCode":12,"scheduleStartTime":4},{"userCode":13,"scheduleStartTime":4},{"userCode":32,"scheduleStartTime":4},{"userCode":36,"scheduleStartTime":4},{"userCode":40,"scheduleStartTime":4},{"userCode":3,"scheduleStartTime":5}]}
 * */
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