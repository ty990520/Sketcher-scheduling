package sketcher.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sketcher.scheduling.domain.EstimatedNumOfCardsPerHour;
import sketcher.scheduling.domain.User;
import sketcher.scheduling.dto.ManagerHopeTimeDto;
import sketcher.scheduling.dto.PercentageOfManagerWeightsDto;
import sketcher.scheduling.dto.UserDto;
import sketcher.scheduling.repository.EstimatedNumOfCardsPerHourRepository;
import sketcher.scheduling.repository.PercentageOfManagerWeightsRepository;
import sketcher.scheduling.repository.UserRepository;
import sketcher.scheduling.service.ManagerHopeTimeService;
import sketcher.scheduling.service.UserService;

import java.time.LocalDateTime;

@Component
@Transactional
@RequiredArgsConstructor
public class ApplicationRunner implements org.springframework.boot.ApplicationRunner {

    private final ManagerHopeTimeService managerHopeTimeService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EstimatedNumOfCardsPerHourRepository cardsPerHourRepository;
    private final PercentageOfManagerWeightsRepository percentageOfManagerWeightsRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //        LocalDateTime date1 = LocalDateTime.of(2022,3,10,1,00);
//        LocalDateTime date3 = LocalDateTime.of(2022,3,10,20,00);
//        LocalDateTime date5 = LocalDateTime.of(2022,3,10,17,00);
//        LocalDateTime date7 = LocalDateTime.of(2022,3,10,7,00);

        if (userNotExist()) {
            insertUserSet();
        }
        if (cardIsEmpty()) {
            insertCardSet();
        }
        if (weightsIsEmpty()) {
            insertWeightSet();
        }
    }

    private void insertWeightSet() {
        PercentageOfManagerWeightsDto percentageOfManagerWeightsDto = PercentageOfManagerWeightsDto.builder()
                .high(50)
                .middle(25)
                .low(25)
                .build();

        //when
        percentageOfManagerWeightsRepository.save(percentageOfManagerWeightsDto.toEntity());
    }

    private boolean weightsIsEmpty() {
        return percentageOfManagerWeightsRepository.findAll().size() == 0;
    }

    private boolean cardIsEmpty() {
        return cardsPerHourRepository.findAll().size() == 0;
    }

    private boolean userNotExist() {
        return userService.findAll().size() == 0;
    }

    private void insertCardSet() {
        final Integer TIME_LEN = 20;
        Integer[] time = {0, 1, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
        Integer[] value = {65, 60, 93, 130, 195, 265, 222, 183, 289, 300, 181, 136, 124, 200, 271, 294, 178, 155, 89, 62};

        for (int i = 0; i < TIME_LEN; i++) {
            EstimatedNumOfCardsPerHour num = new EstimatedNumOfCardsPerHour(time[i], value[i]);
            cardsPerHourRepository.save(num);
        }
    }

    private void insertUserSet() {
        String names[] = {"박태영", "정민환", "이혜원", "김희수", "김민준", "박서준", "임도윤", "정예준", "박시우", "정하준", "서주원", "유지호", "성지훈", "김준우", "박건우", "박서연", "이서윤", "이지우", "김서현", "박하은", "김하은", "김민서", "유민서", "박지민", "김희철", "김채원", "이도현", "김연우", "유다은", "김지원", "서지원", "이수빈", "김예린",
                "이준영", "박시후", "김진우", "정승우", "박채은", "채유나", "김가은", "박서영", "윤민지", "최예나", "최수민", "강수현", "이동현", "최한결", "김재원", "서민우", "김연서", "강다연", "정나윤", "김성현", "김우빈", "정지한", "최예성", "한나은", "홍예지"};


        for (int i = 0; i < names.length; i++) {
            UserDto userA = makeUserDto(names, i);
            String user1 = userService.saveUser(userA);
            User userT = userRepository.findById(user1).get();

            settingUsersHopeTime(i, userT);
        }
    }

    private static UserDto makeUserDto(String[] names, int i) {
        return UserDto.builder()
                .id("user" + (i + 1))
                .authRole("MANAGER")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .username(names[i])
                .userTel("010-1234-5678")
                .user_joinDate(LocalDateTime.now())
                .managerScore(5.0)
                .build();
    }

    private void settingUsersHopeTime(int i, User userT) {
        if (i < 5) {
            setHopeTime(userT, 0, 6);
        } else if (i < 10) {
            setHopeTime(userT, 6, 12);
        } else if (i < 15) {
            setHopeTime(userT, 0, 6);
            setHopeTime(userT, 6, 12);
        } else if (i < 20) {
            setHopeTime(userT, 12, 18);
            setHopeTime(userT, 18, 24);
        } else if (i < 30) {
            setHopeTime(userT, 6, 12);
            setHopeTime(userT, 12, 18);
            setHopeTime(userT, 18, 24);
        } else {
            setHopeTime(userT, 0, 6);
            setHopeTime(userT, 6, 12);
            setHopeTime(userT, 12, 18);
            setHopeTime(userT, 18, 24);

        }
    }

    private void setHopeTime(User userT, int i2, int i3) {
        ManagerHopeTimeDto hopeC = ManagerHopeTimeDto.builder()
                .start_time(i2)
                .finish_time(i3)
                .user(userT)
                .build();
        managerHopeTimeService.saveManagerHopeTime(hopeC);
    }

}