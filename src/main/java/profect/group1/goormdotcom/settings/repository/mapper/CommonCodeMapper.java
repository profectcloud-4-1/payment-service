package profect.group1.goormdotcom.settings.repository.mapper;

import org.springframework.stereotype.Component;
import profect.group1.goormdotcom.settings.domain.CommonCode;
import profect.group1.goormdotcom.settings.repository.entity.CommonCodeEntity;

@Component
public class CommonCodeMapper {

	public static CommonCode toDomain(final CommonCodeEntity entity) {
		return new CommonCode(
				entity.getCode(),
				entity.getCodeKey(),
				entity.getCodeValue(),
				entity.getVisibleLabel(),
				entity.getDescription()
		);
	}

	public static CommonCodeEntity toEntity(final CommonCode domain) {
		return new CommonCodeEntity(
				domain.getCode(),
				domain.getCodeKey(),
				domain.getCodeValue(),
				domain.getVisibleLabel(),
				domain.getDescription()
		);
	}
}
