package profect.group1.goormdotcom.settings.controller.v1;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import profect.group1.goormdotcom.settings.service.SettingService;

@RestController
@RequestMapping("/settings")
public class SettingsController implements SettingsApiDocs {

	private final SettingService settingService;

	public SettingsController(SettingService settingService) {
		this.settingService = settingService;
	}
	
}
