package Him.admin.Controllers;

import Him.admin.DTO.ProjectsDTO.ProjectRequestDTO;
import Him.admin.DTO.ProjectsDTO.ProjectResponseDTO;
import Him.admin.Models.Project.ProjectStatus;

import Him.admin.Services.ProjectsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor

public class ProjectController {

    private final ProjectsService projectService;

    // Create Project
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequest) {
        try {
            ProjectResponseDTO createdProject = projectService.createProject(projectRequest);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get All Projects
    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        try {
            List<ProjectResponseDTO> projects = projectService.getAllProjects();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Project by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        try {
            ProjectResponseDTO project = projectService.getProjectById(id);
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Projects by Branch
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByBranch(@PathVariable Long branchId) {
        try {
            List<ProjectResponseDTO> projects = projectService.getProjectsByBranch(branchId);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Projects by Status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        try {
            List<ProjectResponseDTO> projects = projectService.getProjectsByStatus(status);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update Project
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO projectRequest) {
        try {
            ProjectResponseDTO updatedProject = projectService.updateProject(id, projectRequest);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update Project Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponseDTO> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam ProjectStatus status) {
        try {
            ProjectResponseDTO updatedProject = projectService.updateProjectStatus(id, status);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete Project
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Fully Funded Projects
    @GetMapping("/fully-funded")
    public ResponseEntity<List<ProjectResponseDTO>> getFullyFundedProjects() {
        try {
            List<ProjectResponseDTO> projects = projectService.getFullyFundedProjects();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Active Projects Needing Funding
    @GetMapping("/needing-funding")
    public ResponseEntity<List<ProjectResponseDTO>> getActiveProjectsNeedingFunding() {
        try {
            List<ProjectResponseDTO> projects = projectService.getActiveProjectsNeedingFunding();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Branch Funding Summary
    @GetMapping("/branch/{branchId}/funding-summary")
    public ResponseEntity<BigDecimal> getBranchFundingSummary(@PathVariable Long branchId) {
        try {
            BigDecimal totalFunding = projectService.getBranchTotalFunding(branchId);
            return new ResponseEntity<>(totalFunding, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}