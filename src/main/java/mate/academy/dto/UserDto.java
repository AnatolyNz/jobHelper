package mate.academy.dto;

import java.util.Set;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String shippingAddress;
    private Set<String> roles;
}
