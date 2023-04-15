package sketcher.scheduling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sketcher.scheduling.dto.ReturnCountAndObject;

import java.util.List;
import java.util.Map;

public interface RestCalendarController {
    @GetMapping(value = "/find_All_Manager")
    ReturnCountAndObject findAllManager();

    @RequestMapping(value = "/create_assign_schedule", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    int createAssignSchedule(@RequestBody List<Map<String, Object>> param);
}
