import os

path = 'src/main/java/com/gestao/backend/core/exception/GlobalExceptionHandler.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# I patched the 500 error previously with java.util.Arrays.toString(ex.getStackTrace())
content = content.replace(
    'return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() + " - " + java.util.Arrays.toString(ex.getStackTrace()));',
    'return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor.");'
)

if 'DuplicateResourceException' not in content:
    handler = """
    // Erros 409 - Conflito de recursos duplicados
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflito");
        problemDetail.setType(URI.create("https://api.nosso-app.com/errors/conflict"));
        return problemDetail;
    }

    // Erros 500"""
    content = content.replace('// Erros 500', handler)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

path2 = 'src/main/java/com/gestao/backend/company/service/CompanyService.java'
with open(path2, 'r', encoding='utf-8') as f:
    content2 = f.read()

content2 = content2.replace('import org.springframework.stereotype.Service;', 'import org.springframework.stereotype.Service;\nimport com.gestao.backend.core.exception.DuplicateResourceException;')
content2 = content2.replace('throw new RuntimeException("Este E-mail já', 'throw new DuplicateResourceException("Este E-mail já')
content2 = content2.replace('throw new RuntimeException("Este E-mail j', 'throw new DuplicateResourceException("Este E-mail j')
content2 = content2.replace('throw new RuntimeException("Este CPF/CNPJ já', 'throw new DuplicateResourceException("Este CPF/CNPJ já')
content2 = content2.replace('throw new RuntimeException("Este CPF/CNPJ j', 'throw new DuplicateResourceException("Este CPF/CNPJ j')

with open(path2, 'w', encoding='utf-8') as f:
    f.write(content2)

print("Exceptions updated successfully.")