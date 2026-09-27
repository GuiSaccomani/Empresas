package com.gestao.backend.core.security;

import com.gestao.backend.company.entity.Company;
import com.gestao.backend.company.entity.Employee;
import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.company.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tenta buscar como Company (Admin) primeiro
        Optional<Company> companyOpt = companyRepository.findByEmail(username);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            return new CustomUserDetails(
                    company.getId(),
                    company.getEmail(),
                    company.getPassword(),
                    company.getId(),
                    Role.ROLE_ADMIN
            );
        }

        // Se nǜo for Company, tenta buscar como Employee (Staff)
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(username);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            return new CustomUserDetails(
                    employee.getId(),
                    employee.getEmail(),
                    employee.getPassword(),
                    employee.getCompanyId(),
                    employee.getRole()
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
    }
}