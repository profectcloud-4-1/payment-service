package profect.group1.goormdotcom.settings.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import profect.group1.goormdotcom.settings.repository.CommonCodeRepository;
import profect.group1.goormdotcom.settings.repository.mapper.CommonCodeMapper;
import profect.group1.goormdotcom.settings.domain.CommonCode;
import profect.group1.goormdotcom.settings.repository.entity.CommonCodeEntity;
import profect.group1.goormdotcom.apiPayload.exceptions.handler.SettingsHandler;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

@Service
public class CommonCodeService {
    private final CommonCodeRepository repo;

    // FIXME: 오케스트레이션 도입 시 redis 등의 외부 캐싱 시스템으로 분리 필요.
	private Map<String, List<CommonCode>> cachedByCodeKey = new HashMap<>();

    public CommonCodeService(CommonCodeRepository repo) {
        this.repo = repo;
    }

    public List<CommonCode> findManyByCodeKey(String codeKey) {
        if (cachedByCodeKey.containsKey(codeKey))
            return cachedByCodeKey.get(codeKey);

        List<CommonCodeEntity> entities = repo.findAllByCodeKey(codeKey);
        List<CommonCode> list = entities.stream()
                .map(CommonCodeMapper::toDomain)
                .collect(Collectors.toList());
        cachedByCodeKey.put(codeKey, list);
        return list;
    }
}