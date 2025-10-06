package Him.admin.Services;


import Him.admin.DTO.ProjectsDTO.ProjectRequestDTO;
import Him.admin.DTO.ProjectsDTO.ProjectResponseDTO;
import Him.admin.Models.Branch;
import Him.admin.Models.Project;
import Him.admin.Models.Project.ProjectStatus;

import Him.admin.Repositories.BranchRepository;
import Him.admin.Repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectsService {

    private final ProjectRepository projectRepository;
    private final BranchRepository branchRepository;

    // Create Project from DTO
    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequest) {
        Branch branch = branchRepository.findById(projectRequest.branchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + projectRequest.branchId()));

        Project project = Project.builder()
                .title(projectRequest.title())
                .description(projectRequest.description())
                .fundingGoal(projectRequest.fundingGoal())
                .currentFunding(BigDecimal.ZERO)
                .status(projectRequest.status() != null ? projectRequest.status() : ProjectStatus.DRAFT)
                .branch(branch)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Project savedProject = projectRepository.save(project);
        return convertToResponseDTO(savedProject);
    }

    // Read - All projects
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Read - By ID
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return convertToResponseDTO(project);
    }

    // Read - By Branch
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByBranch(Long branchId) {
        return projectRepository.findByBranchId(branchId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Read - By Status
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Update Project from DTO
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO projectRequest) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        Branch branch = branchRepository.findById(projectRequest.branchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + projectRequest.branchId()));

        project.setTitle(projectRequest.title());
        project.setDescription(projectRequest.description());
        project.setFundingGoal(projectRequest.fundingGoal());
        project.setStatus(projectRequest.status());
        project.setBranch(branch);
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return convertToResponseDTO(updatedProject);
    }

    // Partial Update - Status only
    public ProjectResponseDTO updateProjectStatus(Long id, ProjectStatus status) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return convertToResponseDTO(updatedProject);
    }

    // Delete
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        if (!project.getTransactions().isEmpty()) {
            throw new RuntimeException("Cannot delete project with existing transactions");
        }

        projectRepository.delete(project);
    }

    // Business Logic methods returning DTOs
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getFullyFundedProjects() {
        return projectRepository.findFullyFundedProjects().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getActiveProjectsNeedingFunding() {
        return projectRepository.findActiveProjectsNeedingFunding().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getFullyFundedProjectsByBranch(Long branchId) {
        return projectRepository.findFullyFundedProjectsByBranch(branchId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getBranchTotalFunding(Long branchId) {
        return projectRepository.getTotalFundingByBranch(branchId);
    }

    // Convert Entity to Response DTO
    private ProjectResponseDTO convertToResponseDTO(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getFundingGoal(),
                project.getCurrentFunding(),
                project.getStatus(),
                project.getBranch().getId(),
                project.getBranch().getBranchName(), // Assuming Branch has getName()
                project.getCreatedAt(),
                project.getUpdatedAt()

        );
    }
}