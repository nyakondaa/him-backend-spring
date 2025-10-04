package Him.admin.DTO.MembersDTO;

import java.time.LocalDate;

public record MemberResponseDTO(

        String firstName,
        String lastName,
        LocalDate birthDate,
        String gender,
        String address,
        String phone,
        String email

) {
}
