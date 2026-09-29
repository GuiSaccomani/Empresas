import os

path = 'src/main/java/com/gestao/backend/core/exception/GlobalExceptionHandler.java'
if os.path.exists(path):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    content = content.replace(
        'return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor.");',
        'return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() + " - " + java.util.Arrays.toString(ex.getStackTrace()));'
    )
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
        print("Patched GlobalExceptionHandler.java")
else:
    print("File not found")