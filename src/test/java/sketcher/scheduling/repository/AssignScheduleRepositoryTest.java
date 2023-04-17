package sketcher.scheduling.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sketcher.scheduling.domain.ManagerAssignSchedule;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.ManagerAssignScheduleDto;
import sketcher.scheduling.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AssignScheduleRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ManagerAssignScheduleRepository managerAssignScheduleRepository;

    @Test
    @Transactional
    public void callCustom() {
        // given
        UserDto userDto = UserDto.builder()
                .id("userlhw")
                .authRole("MANAGER")
                .password(new BCryptPasswordEncoder().encode("1234"))
                .username("이혜원")
                .userTel("010-1234-5678")
                .build();
        userRepository.save(userDto.toEntity());

        User user = userRepository.findById("userlhw").get();

        LocalDateTime date1 = LocalDateTime.of(2022,3,6,2,00);
        LocalDateTime date2 = LocalDateTime.of(2022,3,6,4,00);
        LocalDateTime date3 = LocalDateTime.of(2022,3,7,6,00);
        LocalDateTime date4 = LocalDateTime.of(2022,3,7,9,00);
        LocalDateTime date5 = LocalDateTime.of(2022,3,13,13,00);
        LocalDateTime date6 = LocalDateTime.of(2022,3,13,16,00);
        LocalDateTime date7 = LocalDateTime.of(2022,3,14,20,00);
        LocalDateTime date8 = LocalDateTime.of(2022,3,14,22,00);

        ManagerAssignScheduleDto assignSchedule = ManagerAssignScheduleDto.builder()
                .user(user)
                .scheduleDateTimeStart(date1)
                .scheduleDateTimeEnd(date2)
                .build();

        ManagerAssignScheduleDto assignSchedule2 = ManagerAssignScheduleDto.builder()
                .user(user)
                .scheduleDateTimeStart(date3)
                .scheduleDateTimeEnd(date4)
                .build();

        ManagerAssignScheduleDto assignSchedule3 = ManagerAssignScheduleDto.builder()
                .user(user)
                .scheduleDateTimeStart(date5)
                .scheduleDateTimeEnd(date6)
                .build();

        ManagerAssignScheduleDto assignSchedule4 = ManagerAssignScheduleDto.builder()
                .user(user)
                .scheduleDateTimeStart(date7)
                .scheduleDateTimeEnd(date8)
                .build();


        List<ManagerAssignSchedule> entities = new ArrayList<>();
        entities.add(assignSchedule.toEntity());
        entities.add(assignSchedule2.toEntity());
        entities.add(assignSchedule3.toEntity());
        entities.add(assignSchedule4.toEntity());
        //when
        managerAssignScheduleRepository.assignScheduleBulkInsertWithBatchSize100(entities);

        //then

    }
}
