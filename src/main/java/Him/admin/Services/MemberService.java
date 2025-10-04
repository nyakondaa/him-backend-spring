package Him.admin.Services;

import Him.admin.DTO.MembersDTO.MemberRequestDTO;
import Him.admin.Exceptions.ResourceAlreadyExistsException;
import Him.admin.Exceptions.ResourceNotFoundException;
import Him.admin.Models.Branch;
import Him.admin.Models.Member;
import Him.admin.Repositories.BranchRepository;
import Him.admin.Repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BranchRepository branchRepository;

    public Member registerMember(MemberRequestDTO dto) {

        // Check for duplicate email
        if (memberRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Member", "email", dto.email());
        }

        // Optional: check for duplicate phone
        if (memberRepository.findByPhone(dto.phone()).isPresent()) {
            throw new ResourceAlreadyExistsException("Member", "phone", dto.phone());
        }

        // Validate branch
        Branch branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "branchId", String.valueOf(dto.branchId())));

        // Create member
        Member newMember = Member.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phone(dto.phone())
                .address(dto.address())
                .birthDate(dto.birthDate())
                .gender(dto.gender())
                .branch(branch)
                .build();

        return memberRepository.save(newMember);
    }


    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }


    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", String.valueOf(id)));
    }


    public Member updateMember(Long id, MemberRequestDTO dto) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", String.valueOf(id)));

        // Check if new email belongs to another member
        memberRepository.findByEmail(dto.email()).ifPresent(member -> {
            if (!member.getId().equals(id)) {
                throw new ResourceAlreadyExistsException("Member", "email", dto.email());
            }
        });

        // Check if new phone belongs to another member
        memberRepository.findByPhone(dto.phone()).ifPresent(member -> {
            if (!member.getId().equals(id)) {
                throw new ResourceAlreadyExistsException("Member", "phone", dto.phone());
            }
        });

        // Validate branch
        Branch branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "branchId", String.valueOf(dto.branchId())));

        // Update fields
        existingMember.setFirstName(dto.firstName());
        existingMember.setLastName(dto.lastName());
        existingMember.setEmail(dto.email());
        existingMember.setPhone(dto.phone());
        existingMember.setAddress(dto.address());
        existingMember.setBirthDate(dto.birthDate());
        existingMember.setGender(dto.gender());
        existingMember.setBranch(branch);

        return memberRepository.save(existingMember);
    }


    public void deleteMember(Long id) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", String.valueOf(id)));

        memberRepository.delete(existingMember);
    }
}
