package sketcher.scheduling.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user")
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_code")
    private Integer code;

    @Column(name = "user_id")
    @NotEmpty
//    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "아이디를 3~12자로 입력해주세요. [특수문자 X]")
    private String id;

    @Column(name = "auth_role")
//    @Enumerated(EnumType.STRING)
    private String authRole;

    @Column(name = "user_pw")
//    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "비밀번호를 3~12자로 입력해주세요.")
    private String password;

    @Column(name = "user_name")
//    @Pattern(regexp = "[a-zA-Z0-9]*")
    private String username;

    @Column(name = "user_tel")
    private String userTel;

    @Column(name = "user_joindate")
    private LocalDateTime user_joinDate;

    @Column(name = "manager_score")
    private Double managerScore;

    @Column(name = "dropout_req_check")
    private Character dropoutReqCheck;

    @Column(name = "vacation_req_check")
    private Character vacationReqCheck;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ManagerHopeTime> managerHopeTimeList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ManagerAssignSchedule> managerAssignScheduleList = new ArrayList<>();

    protected User() {
    }

    @Builder
    public User(Integer code, String id, String authRole, String password, String username, String userTel,
                LocalDateTime user_joinDate, Double managerScore, Character dropoutReqCheck, Character vacationReqCheck) {
        this.code = code;
        this.id = id;
        this.authRole = authRole;
        this.password = password;
        this.username = username;
        this.userTel = userTel;
        this.user_joinDate = user_joinDate;
        this.managerScore = managerScore;
        this.dropoutReqCheck = dropoutReqCheck;
        this.vacationReqCheck = vacationReqCheck;
    }

    // 사용자의 권한을 콜렉션 형태로 반환
    // 단, 클래스 자료형은 GrantedAuthority를 구현해야함
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (String role : authRole.split(",")) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return roles;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠금되지 않았음
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능
    }

    public void update(String authRole, String userTel, Character dropoutReqCheck) {
        this.authRole = authRole;
        this.userTel = userTel;
        this.dropoutReqCheck = dropoutReqCheck;
    }
}