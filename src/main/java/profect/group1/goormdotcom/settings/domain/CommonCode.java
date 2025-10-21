package profect.group1.goormdotcom.settings.domain;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class CommonCode {

	private String code;
	private String codeKey;
	private String codeValue;
	private String visibleLabel;
	private String description;

	public CommonCode(
		final String code,
		final String codeKey,	
		final String codeValue,
		final String visibleLabel,
		final String description
	) {
		this.code = code;
		this.codeKey = codeKey;
		this.codeValue = codeValue;
		this.visibleLabel = visibleLabel;
		this.description = description;
	}

	
}
