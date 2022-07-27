package account.dao;

import account.model.PaymentDao;
import account.model.UserRole;
import account.utils.Regexp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = { "roles", "enabled", "accountNonExpired", "credentialsNonExpired", "authorities",
        "accountNonLocked", "username" })
public class UserDao{

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @Column(unique = true)
    @NotBlank
    @Pattern(regexp = Regexp.EMPLOYEE_EMAIL)
    private String email;

    @NotBlank
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column
    Boolean locked = false;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> userRoles = new HashSet<>();

    @Column
    @NotNull
    @OneToMany(
            mappedBy = "userDao",
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<PaymentDao> paymentDaos;

    public void addRole(UserRole role) {
        userRoles.add(role);
    }

}
