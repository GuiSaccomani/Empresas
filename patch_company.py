import os

path = 'src/main/java/com/gestao/backend/company/entity/Company.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    '@Column(name = "usa_agenda", nullable = false)', 
    '@Column(name = "usa_agenda", nullable = false, columnDefinition = "boolean default true")'
)
content = content.replace(
    '@Column(name = "usa_financeiro", nullable = false)', 
    '@Column(name = "usa_financeiro", nullable = false, columnDefinition = "boolean default true")'
)
content = content.replace(
    '@Column(name = "usa_clientes", nullable = false)', 
    '@Column(name = "usa_clientes", nullable = false, columnDefinition = "boolean default true")'
)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)