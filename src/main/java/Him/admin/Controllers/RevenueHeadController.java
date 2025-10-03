package Him.admin.Controllers;

import Him.admin.DTO.RevenueHeadDTO.RevenueHeadRequestDTO;
import Him.admin.DTO.RevenueHeadDTO.RevenueHeadResponseDTO;
import Him.admin.Models.RevenueHeads;
import Him.admin.Repositories.RevenueHeadRepository;
import Him.admin.Services.RevenueHeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/revenueheads")
public class RevenueHeadController {
    private final RevenueHeadRepository revenueHeadRepository;
    private final RevenueHeadService revenueHeadService;

    @PostMapping
    public ResponseEntity<RevenueHeadResponseDTO> createRevenueHead(@Valid @RequestBody RevenueHeadRequestDTO dto){
        RevenueHeads revenueHeads = revenueHeadService.createRevenueHeads(dto.name(), dto.description(), dto.code(), dto.branchID());
        RevenueHeadResponseDTO responseDTO = new RevenueHeadResponseDTO(
                revenueHeads.getName(),
                revenueHeads.getCode(),
                revenueHeads.getDescription()
        );

        return ResponseEntity.ok().body(responseDTO);

    }

    @GetMapping
    public ResponseEntity<List<RevenueHeads>> getAllRevenueHeads() {
        List<RevenueHeads> revenueHeads = revenueHeadService.getAllRevenueHeads();
        return ResponseEntity.ok(revenueHeads);
    }

   //update revenue-id
    @PutMapping("/{id}")
    public ResponseEntity<RevenueHeadResponseDTO> updateRevenueHead(

            @Valid @RequestBody RevenueHeadRequestDTO dto, @PathVariable Long id){
        RevenueHeads revenueHeads =  revenueHeadService.updateRevenueHeads(dto.name(), dto.description(), dto.code(), id);
        RevenueHeadResponseDTO responseDTO = new RevenueHeadResponseDTO(
                revenueHeads.getName(),
                revenueHeads.getCode(),
                revenueHeads.getDescription()
        );

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        revenueHeadService.deleteRevenueHead(id);
        return ResponseEntity.ok("Revenue head deleted successfully");
    }

}
