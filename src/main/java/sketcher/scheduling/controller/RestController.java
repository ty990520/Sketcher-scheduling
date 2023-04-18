package sketcher.scheduling.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import sketcher.scheduling.dto.EstimatedNumOfCardsPerHourDto;
import sketcher.scheduling.repository.EstimatedNumOfCardsPerHourRepository;
import sketcher.scheduling.repository.PercentageOfManagerWeightsRepository;
import sketcher.scheduling.service.ManagerHopeTimeService;
import sketcher.scheduling.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@Api(tags = {"스케줄 배정 관련 Rest API"})
public class RestController {
    private final UserService userService;
//    private final KakaoService kakaoService;
    private final ManagerHopeTimeService hopeTimeService;
    private final EstimatedNumOfCardsPerHourRepository estimatedNumOfCardsPerHourRepository;
    private final PercentageOfManagerWeightsRepository percentageOfManagerWeightsRepository;

    @ApiOperation(value = "매니저 희망시간 전체 조회")
    @GetMapping(value = "/find_All_Manager_Hope_Time")
    public List<ManagerHopeTime> findAllManagerHopeTime() {
        return hopeTimeService.findAll();
    }


    @ApiOperation(value = "현재까지 저장된 배정 시간 계산")
    @RequestMapping(value = "/current_status_info", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public JSONObject currentStatusInfo(@RequestBody List<Map<String, Object>> param)  {
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
        ScheduleResultJsonObj result = getScheduleResultJsonObj(date, day);

        for (ResultScheduling scheduling : schedulings) {
            JSONObject scheduleItem = new JSONObject();
            scheduleItem.put("scheduleStartTime", scheduling.startTime);
            scheduleItem.put("userCode", scheduling.userCode);
            result.scheduleJsonList.add(scheduleItem);
            if (!result.userList.containsKey(scheduling.userCode)) {
                result.userList.put(scheduling.userCode, scheduling.currentTime);
            }
        }

        result.schedulingJsonObj.put("scheduleResults", result.scheduleJsonList);

        for (Map.Entry<Integer, Integer> userStatus : result.userList.entrySet()) {
            JSONObject scheduleItem = new JSONObject();
            scheduleItem.put("userCode", userStatus.getKey());
            scheduleItem.put("userCurrentTime", userStatus.getValue());
            result.userJsonList.add(scheduleItem);
        }

        result.schedulingJsonObj.put("userResults", result.userJsonList);
        return result.schedulingJsonObj;
    }

    private static ScheduleResultJsonObj getScheduleResultJsonObj(String date, String day) {
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
        ScheduleResultJsonObj result = new ScheduleResultJsonObj(schedulingJsonObj, scheduleJsonList, userJsonList, userList);
        return result;
    }

    private static class ScheduleResultJsonObj {
        public final JSONObject schedulingJsonObj;
        public final JSONArray scheduleJsonList;
        public final JSONArray userJsonList;
        public final HashMap<Integer, Integer> userList;

        public ScheduleResultJsonObj(JSONObject schedulingJsonObj, JSONArray scheduleJsonList, JSONArray userJsonList, HashMap<Integer, Integer> userList) {
            this.schedulingJsonObj = schedulingJsonObj;
            this.scheduleJsonList = scheduleJsonList;
            this.userJsonList = userJsonList;
            this.userList = userList;
        }
    }

    private void settingHopeTimeList(List<List<Integer>> hopeTimeList, String hopetimeStr) {
        String[] split = hopetimeStr.replace("[", "").replace("]", "").split(", ");
        List<Integer> hopetimes = new ArrayList<>();
        for (String hopetime : split) {
            hopetimes.add(Integer.parseInt(hopetime));
        }
        hopeTimeList.add(hopetimes);
    }


    @ApiOperation(value = "예상 카드건수 수정")
    @RequestMapping(value = "/update_est_cards", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public int updateEstCards(@RequestBody List<Map<String, Object>> param){
        for (Map<String, Object> stringObjectMap : param) {
            Integer time = Integer.parseInt(stringObjectMap.get("time").toString());
            Integer value = Integer.parseInt(stringObjectMap.get("value").toString());
            estimatedNumOfCardsPerHourRepository.save(getEstimatedNumOfCardsPerHourDto(time, value).toEntity());
        }
        return param.size();
    }

    private static EstimatedNumOfCardsPerHourDto getEstimatedNumOfCardsPerHourDto(Integer time, Integer value) {
        EstimatedNumOfCardsPerHourDto dto = EstimatedNumOfCardsPerHourDto.builder()
                .time(time)
                .numOfCards(value)
                .build();
        return dto;
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