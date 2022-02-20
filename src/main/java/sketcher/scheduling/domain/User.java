package sketcher.scheduling.domain;

import lombok.Builder;
import lombok.Getter;
import org.apache.catalina.Manager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user")
@Getter
public class User extends UserTimeEntity implements UserDetails {


    /**
     * NotEmpty 어노테이션은 우선 필요에 따라 설정하시거나 빼시면 될 것 같아요 !
     * NotEmpty 가 들어가면 테스트시에도 무조건 들어가야 하는 값(NULL = X)이에요!
     * Column 은 DB 에 들어가는 이름입니다. id 로 사용 -> DB 에는 user_id 로 저장.
     */
    @Id
    @Column(name = "user_id")
    @NotEmpty
//    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "아이디를 3~12자로 입력해주세요. [특수문자 X]")
    private String id;

    @NotEmpty
    @Column(name = "auth_role")
    private String authRole;

    @NotEmpty
    @Column(name = "user_pw")
//    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "비밀번호를 3~12자로 입력해주세요.")
    private String password;

    @NotEmpty
    @Column(name = "user_name")
//    @Pattern(regexp = "[a-zA-Z0-9]*")
    private String username;

    @NotEmpty
    @Column(name = "user_tel")
    private String userTel;

    /**
     * UserTimeEntity 로 수정  / 생성 시간 자동 생성 . 쿼리문 없어도 자동으로 DB 에 입력되는 순간을 기점으로 생성.
     */
//    @NotEmpty
//    private LocalDateTime user_joinDate;

    @Column(name = "manager_score")
    private Double managerScore;

    @Column(name = "dropout_req_check")
    private Character dropoutReqCheck;

    @OneToMany(mappedBy = "user")
    private List<ManagerHopeTime> managerHopeTimeList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ManagerWorkingSchedule> managerWorkingScheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ManagerAssignSchedule> managerAssignScheduleList = new ArrayList<>();

    protected User() {
    }

    /**
     * domain 은 DB 에 저장된 데이터를 꺼내오기 위한 클래스로 설정
     * getter 만 열어두었습니다!
     * Setter 는 dto 에 오픈해두었는데 DB 에 값 입력은 Builder 를 이용해봄이?..(생성자랑 비슷한 개념이에요!)
     */
    @Builder
    public User(String id, String authRole, String password, String username, String userTel, Double managerScore, Character dropoutReqCheck) {
        this.id = id;
        this.authRole = authRole;
        this.password = password;
        this.username = username;
        this.userTel = userTel;
        this.managerScore = managerScore;
        this.dropoutReqCheck = dropoutReqCheck;
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
}