package account.utils;

import account.dao.UserDao;
import account.dto.UserResponseDto;

public interface UserMapper {

    UserDao mapDtoToDao(UserResponseDto userResponseDto);

    UserResponseDto mapDaoToDto(UserDao userDao);

}