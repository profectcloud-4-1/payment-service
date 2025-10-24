package profect.group1.goormdotcom.order.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;


@Entity
@Table(name = "p_common_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class CommonCodeEntity {
    
    @Id
    @Column(length = 100, nullable = false)
    private String code;

    @Column(name = "code_key", nullable = false, length = 100)
    private String codeKey;

    @Column(name = "code_value", nullable = false, length = 100)
    private String codeValue;

    @Column(name= "visible_label", nullable = false, length = 100)
    private String visibleLabel;
    
    @Column(name= "description", nullable = false, length = 255)
    private String description;

}
