package profect.group1.goormdotcom.settings.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import profect.group1.goormdotcom.common.domain.BaseEntity;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder

@Entity
@Table(name = "p_common_code")
@EntityListeners(AuditingEntityListener.class)
public class CommonCodeEntity extends BaseEntity {

	@Id
	private String code;

	@Column(name = "code_key", nullable = false)
	@Comment("무엇을 표현하는 코드인지. 예: ORDER_STATUS, USER_ROLE, 등")
	private String codeKey;

	@Column(name = "code_value", nullable = false)
	@Comment("하나의 code_key 안에서 구분되는 값. 예: IN_DELIVERY, PENDING, 등")
	private String codeValue;

	@Column(name = "visible_label")
	@Comment("code_value를 프론트 단에 표시할 라벨. 예: 배송중, 대기, 등")
	private String visibleLabel;
	
	@Comment("코드에 대한 설명")
	@Column(name = "description")
	private String description;

	public CommonCodeEntity(final String code, final String codeKey, final String codeValue, final String visibleLabel, final String description) {
		this.code = code;
		this.codeKey = codeKey;
		this.codeValue = codeValue;
		this.visibleLabel = visibleLabel;
		this.description = description;
	}
}
