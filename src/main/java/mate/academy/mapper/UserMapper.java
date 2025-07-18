package mate.academy.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.dto.UserDto;
import mate.academy.dto.UserResponseDto;
import mate.academy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToNames")
    UserDto toDto(User user);

    UserResponseDto toResponseDto(User user);

    @Named("rolesToNames")
    default Set<String> mapRolesToNames(Set<mate.academy.model.Role> roles) {
        return roles.stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
    }
}
