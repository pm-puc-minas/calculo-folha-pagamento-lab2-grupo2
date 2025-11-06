# API - folhaPagamento

Este documento descreve como buildar e executar a API de backend, os principais endpoints disponíveis e exemplos de requisição/resposta.

## Requisitos
- Java 17+ (o projeto foi testado com Java 17/21)
- Maven (o projeto inclui o wrapper `mvnw`)
- MySQL (ou outro banco compatível configurado via `application.properties`)
- Opcional: Docker/Docker Compose

As configurações de conexão com banco podem ser definidas via variáveis de ambiente (conforme `src/main/resources/application.properties`):

- DB_HOST (padrão: localhost)
- DB_PORT (padrão: 3306)
- DB_NAME (padrão: folhaPagamento)
- DB_USER (padrão: root)
- DB_PASSWORD (padrão: vazio)

## Build e execução

Na raiz do projeto, para compilar e executar a suíte de testes:

```powershell
.\mvnw.cmd test
```

Para buildar o artefato e empacotar em um JAR:

```powershell
.\mvnw.cmd -DskipTests package
# depois execute com:
java -jar target\*.jar
```

Para executar a aplicação diretamente via plugin Spring Boot (modo desenvolvimento):

```powershell
.\mvnw.cmd spring-boot:run
```

Usando Docker Compose (se houver configuração no repositório):

```powershell
docker-compose up --build
```

## Endpoints principais

Base URL (padrão): http://localhost:8080

1) Autenticação

- POST /api/auth/login
	- Descrição: autentica usuário por login/senha e retorna dados básicos do usuário.
	- Requisição (JSON):

```json
{
	"login": "admin.user",
	"senha": "senha123"
}
```

	- Resposta (200 OK):

```json
{
	"id": 1,
	"login": "admin.user",
	"permissao": 1
}
```

	- Erro comum: 401 Unauthorized com mensagem em texto quando login/senha inválidos.

2) Gestão de funcionários

- POST /api/funcionarios
	- Descrição: cadastra um novo funcionário (cria também o usuário associado).
	- Requisição (exemplo `FuncionarioRequestDTO`):

```json
{
	"nome": "João Silva",
	"cpf": "123.456.789-00",
	"cargo": "Desenvolvedor",
	"dependentes": 0,
	"salarioBase": 5000.00,
	"aptoPericulosidade": false,
	"grauInsalubridade": 0,
	"valeTransporte": true,
	"valeAlimentacao": false,
	"valorVT": 30.00,
	"valorVA": 0.00,
	"diasUteis": 22,
	"login": "joao.silva",
	"senha": "senha123",
	"permissao": 1
}
```

	- Resposta: 201 Created com o objeto `Funcionario` persistido (contendo `id`, `usuario`, etc.).

- GET /api/funcionarios
	- Descrição: retorna lista de todos os funcionários.

- GET /api/funcionarios/by-login/{login}
	- Descrição: busca um funcionário pelo login do usuário associado.

- GET /api/funcionarios/map
	- Descrição: retorna todos os funcionários como um Map<id, Funcionario>.

- PUT /api/funcionarios/{id}/ajuste-salarial
	- Descrição: atualiza o salário do funcionário e dispara evento de ajuste salarial.
	- Requisição (exemplo `AjusteSalarialRequestDTO`):

```json
{
	"novoSalario": 5500.00
}
```

	- Resposta: 200 OK com o objeto `Funcionario` atualizado; 404 se o funcionário não existir.

3) Folha de pagamento

- POST /folha/calcular
	- Descrição: calcula a folha para um funcionário fornecido (sem persistir).
	- Requisição (exemplo):

```json
{
	"funcionario": {
		"id": 1,
		"salarioBase": 5000.00,
		"valeTransporte": true,
		"valorVT": 30.00,
		"valeAlimentacao": false
	},
	"diasUteis": 22
}
```

	- Resposta (200): JSON com os campos do cálculo (salarioBruto, totalDescontos, salarioLiquido, descontoINSS, descontoIRRF, etc.).

- GET /folha/by-login/{login}
	- Descrição: retorna folhas associadas ao funcionário identificado pelo login.

- POST /folha/gerar/{login}?mes={mes}&ano={ano}[&diasUteis={dias}]
	- Descrição: gera e persiste uma folha específica para o funcionário (se já existir, retorna a existente).

- POST /folha/gerar-ultimos/{login}?meses={n}[&diasUteis={dias}]
	- Descrição: gera as últimas `n` folhas (por padrão 6) e retorna a lista.

## Exemplos com curl (PowerShell)

Autenticação:
```powershell
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"login":"admin.user","senha":"senha123"}'
```

Cadastrar funcionário:
```powershell
curl -X POST http://localhost:8080/api/funcionarios -H "Content-Type: application/json" -d @- <<'JSON'
{
	"nome": "João Silva",
	"cpf": "123.456.789-00",
	"cargo": "Dev",
	"dependentes": 0,
	"salarioBase": 5000.00,
	"login": "joao.silva",
	"senha": "senha123",
	"permissao": 1
}
JSON
```

Ajuste salarial:
```powershell
curl -X PUT http://localhost:8080/api/funcionarios/1/ajuste-salarial -H "Content-Type: application/json" -d '{"novoSalario":6000.00}'
```

Calcular folha (exemplo):
```powershell
curl -X POST http://localhost:8080/folha/calcular -H "Content-Type: application/json" -d @- <<'JSON'
{
	"funcionario": { "id": 1, "salarioBase": 5000.00, "valeTransporte": true, "valorVT": 30.00 },
	"diasUteis": 22
}
JSON
```

## Observações e Troubleshooting
- Porta: por padrão a aplicação roda em 8080. Se estiver em uso, ajuste `server.port` em `application.properties` ou defina `SERVER_PORT` via variáveis de ambiente.
- Banco de dados: garanta que o banco esteja disponível com as credenciais configuradas em `application.properties`.
- Tests: rode `.\mvnw.cmd test` para executar a suíte de testes.

## Contato
Se algo não funcionar, cole aqui a saída do comando que executou (`mvnw test` ou `java -jar ...`) para que possamos ajudar com a depuração.

