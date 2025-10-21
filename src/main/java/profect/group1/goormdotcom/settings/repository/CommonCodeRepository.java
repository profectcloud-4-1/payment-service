package profect.group1.goormdotcom.settings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.group1.goormdotcom.settings.repository.entity.CommonCodeEntity;
import java.util.List;

public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, String> {
    List<CommonCodeEntity> findAllByCodeKey(String codeKey);
}
