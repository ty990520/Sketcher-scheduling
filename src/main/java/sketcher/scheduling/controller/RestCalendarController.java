package sketcher.scheduling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import sketcher.scheduling.dto.ReturnCountAndObject;

public interface RestCalendarController {
    @GetMapping(value = "/find_All_Manager")
    ReturnCountAndObject findAllManager();
}
