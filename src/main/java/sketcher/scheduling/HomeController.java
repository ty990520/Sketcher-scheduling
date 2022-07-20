package sketcher.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sketcher.scheduling.domain.EstimatedNumOfCardsPerHour;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.UserSearchCondition;
import sketcher.scheduling.repository.EstimatedNumOfCardsPerHourRepository;
import sketcher.scheduling.repository.UserRepositoryCustom;
import sketcher.scheduling.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    @Autowired
    EstimatedNumOfCardsPerHourRepository estimatedNumOfCardsRepository;

    @GetMapping(value = "/config_create_schedule")
    public String configCreateSchedule() {
        return "full-calendar/calendar_create_config";
    }

    @GetMapping("/set_est_num_of_cards")
    @ResponseBody
    public List<EstimatedNumOfCardsPerHour> setEstNumOfCards() {
        return estimatedNumOfCardsRepository.findAll();
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request) {
        return "full-calendar/calendar";
    }

    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    public String calendar(HttpServletRequest request) {
        //사용자 권한에 따라 admin이면 /calendar_damin으로 이동할 수 있도록 조건문 추가
        return "full-calendar/calendar";
    }

    @RequestMapping(value = "/calendar_admin", method = RequestMethod.GET)
    public String calendar_admin(HttpServletRequest request) {
        return "full-calendar/calendar-admin";
    }

    @RequestMapping(value = "/calendar_admin_update", method = RequestMethod.GET)
    public String calendar_admin_update(HttpServletRequest request) {
        return "full-calendar/calendar-admin-update";
    }

    @GetMapping(value = "/create_schedule")
    public String createSchedule() {
        return "full-calendar/calendar_create";
    }


}