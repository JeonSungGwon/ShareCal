package Capstone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupMessageDto {
    private Long id;
    private String message;
    private Long groupId;
    private String email;
    private String memberName;
    private Long ownerId;
    private String sharedCode;
    public GroupMessageDto(Long id, String message, Long groupId, String email, String memberName, Long ownerId, String sharedCode) {
        this.id = id;
        this.message = message;
        this.groupId = groupId;
        this.email = email;
        this.memberName = memberName;
        this.ownerId = ownerId;
        this.sharedCode = sharedCode;
    }
}
