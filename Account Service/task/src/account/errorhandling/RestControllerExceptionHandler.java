package account.errorhandling;

import account.dto.responses.BaseExceptionResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

@ControllerAdvice
public class RestControllerExceptionHandler {

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public BaseExceptionResponse handleMethodArgumentNotValidException(
      HttpServletRequest request, MethodArgumentNotValidException exception) {
    BaseExceptionResponse response = new BaseExceptionResponse();

    List<String> errors =
        exception.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .sorted()
            .collect(Collectors.toList());
    for (String error : errors) {
      if (error.contains("Password length must be 12 chars minimum!")) {
        response.setTimestamp(LocalDateTime.now().format(ISO_DATE_TIME) + "+00:00"); // todo fix date
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage(error);
        response.setPath(request.getRequestURI());
      }
    }
    return response;
  }

  @ExceptionHandler(value = DateTimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public BaseExceptionResponse handleDateNotValidException(
          HttpServletRequest request, DateTimeException exception) {
    BaseExceptionResponse response = new BaseExceptionResponse();

      if (exception.getMessage().contains("could not be parsed")) {
        response.setTimestamp(LocalDateTime.now().format(ISO_DATE_TIME) + "+00:00"); // todo fix date
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage("Invalid date");
        response.setPath(request.getRequestURI());
      }
    return response;
  }
}