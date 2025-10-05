package Him.admin.Controllers;

import Him.admin.DTO.MembersDTO.MemberRequestDTO;
import Him.admin.DTO.MembersDTO.MemberResponseDTO;
import Him.admin.Models.Member;
import Him.admin.Services.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<MemberResponseDTO> createMember(@Valid @RequestBody MemberRequestDTO dto) {
        Member createdMember = memberService.registerMember(dto);
        return new ResponseEntity<>(convertToResponseDTO(createdMember), HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> getAllMembers() {
        List<MemberResponseDTO> members = memberService.getAllMembers()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(convertToResponseDTO(member));
    }


    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> updateMember(@PathVariable Long id, @Valid @RequestBody MemberRequestDTO dto) {
        Member updatedMember = memberService.updateMember(id, dto);
        return ResponseEntity.ok(convertToResponseDTO(updatedMember));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    private MemberResponseDTO convertToResponseDTO(Member member) {
        return new MemberResponseDTO(
                member.getId(),
                
                member.getFirstName(),
                member.getLastName(),
                member.getBirthDate(),
                member.getGender(),
                member.getAddress(),
                member.getPhone(),
                member.getEmail()


        );
    }
}
