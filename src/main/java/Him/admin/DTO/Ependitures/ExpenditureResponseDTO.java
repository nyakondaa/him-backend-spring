package Him.admin.DTO.Ependitures;

import jdk.jshell.Snippet;

import java.time.LocalDate;

public record ExpenditureResponseDTO(
         Long id,
         String description,
         Double amount,
         LocalDate date,
         String branchName,
         String expnditureHeadName
) {

}
