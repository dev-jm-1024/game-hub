package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AuthUserIdConverter implements AttributeConverter<AuthUserId, String> {

    @Override
    public String convertToDatabaseColumn(AuthUserId attribute) {
        return attribute != null ? attribute.getAuthUserId() : null;
    }

    @Override
    public AuthUserId convertToEntityAttribute(String dbData) {
        return dbData != null ? AuthUserId.of(dbData) : null;
    }
}