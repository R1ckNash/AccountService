package account.utils;

import account.dao.UserDao;
import account.dto.UserResponseDto;
import account.model.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserMapperImpl implements UserMapper {

  @Override
  public UserDao mapDtoToDao(UserResponseDto userResponseDto) {
    if (userResponseDto == null) {
      return null;
    }

    UserDao userDao = new UserDao();
    userDao.setName(userResponseDto.getName());
    userDao.setLastname(userResponseDto.getLastname());
    userDao.setEmail(userResponseDto.getEmail());
    userDao.setPassword(userResponseDto.getPassword());

    return userDao;
  }

  @Override
  public UserResponseDto mapDaoToDto(UserDao userDao) {
    if (userDao == null) {
      return null;
    }


    UserResponseDto userResponseDto = new UserResponseDto();
    userResponseDto.setId(userDao.getId());
    userResponseDto.setName(userDao.getName());
    userResponseDto.setLastname(userDao.getLastname());
    userResponseDto.setEmail(userDao.getEmail());
    userResponseDto.setRoles((userDao.getUserRoles()));

    return userResponseDto;
  }

    }