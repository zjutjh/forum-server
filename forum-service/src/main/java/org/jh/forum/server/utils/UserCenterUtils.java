package org.jh.forum.server.utils;

import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.GenderEnum;
import org.jh.usercenter.UserCenterException;

public class UserCenterUtils {
    public static ExceptionEnum toForumException(Integer code) {
        UserCenterException userCenterException = UserCenterException.fromCode(code);
        if (userCenterException == null) {
            return null;
        }
        return switch (userCenterException) {
            case ServerError, HttpTimeout, RequestError, Unknown -> ExceptionEnum.SERVER_ERROR;
            case ParamError -> ExceptionEnum.INVALID_PARAMETER;
            case NotFound -> ExceptionEnum.NOT_FOUND_ERROR;
            case WrongPassword, WrongAccount, UserNotFound, PasswordLengthError, AuthError, UserNotExit ->
                    ExceptionEnum.WRONG_USERNAME_OR_PASSWORD;
            case UserAlreadyExit -> ExceptionEnum.USER_EXISTED;
            case NotActivatedError -> ExceptionEnum.OAUTH_NOT_ACTIVATED;
            case ClosedError -> ExceptionEnum.OAUTH_CLOSED;
        };
    }

    public static GenderEnum toGenderEnum(String gender) {
        if (gender == null) {
            return GenderEnum.UNKNOWN;
        }
        return switch (gender.toLowerCase()) {
            case "male" -> GenderEnum.MALE;
            case "female" -> GenderEnum.FEMALE;
            default -> GenderEnum.UNKNOWN;
        };
    }
}
