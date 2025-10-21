package profect.group1.goormdotcom.settings.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.group1.goormdotcom.settings.domain.CommonCode;
import profect.group1.goormdotcom.settings.service.CommonCodeService;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingService {

	private final CommonCodeService commonCodeService;

	public List<CommonCode> findCommonCodeByCodeKey(final String codeKey) {
		return commonCodeService.findManyByCodeKey(codeKey);
	}

}
