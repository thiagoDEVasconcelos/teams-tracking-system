package com.teamstracking.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AgentRequest {

        @NotBlank(message = "Nome é obrigatório")
        private String name;

        private String role;
        private String team;
        private String phone;

        @Email(message = "Email inválido")
        private String email;
}