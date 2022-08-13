package ai.ecma.auth.mapper;

import ai.ecma.lib.entity.User;
import ai.ecma.lib.payload.UserDto;
import org.mapstruct.Mapper;

/**
 * This interface not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 21.01.2022
 */
@Mapper(componentModel = "spring", imports = DistrictMapper.class)
public interface UserMapper {
    UserDto toDto(User user);
}
