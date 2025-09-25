# Cartões CRC (Classe-Responsabilidade-Colaborador)

### Classe: `Colaborador`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Conhecer seus dados pessoais (nome, CPF, etc.). | - Nenhuma |
| 2. Conhecer seus dados contratuais (salário bruto, cargo, data de admissão). | |
| 3. Conhecer seus parâmetros de cálculo (dependentes, nível de insalubridade, etc.). | |
| 4. Conhecer se é um administrador do sistema. | |

### Classe: `FolhaPagamento`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Conhecer o `Colaborador` a que se refere. | - `Colaborador` |
| 2. Conhecer o mês de referência do cálculo. | |
| 3. Conhecer o detalhamento de todos os proventos (salário, adicionais, benefícios). | |
| 4. Conhecer o detalhamento de todas as deduções (INSS, IRRF, vales). | |
| 5. Conhecer os totais (salário bruto, total de descontos, salário líquido). | |
| 6. Conhecer as bases de cálculo para INSS, IRRF e FGTS. | |

### Classe: `ServicoFolhaPagamento`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Orquestrar o cálculo completo da folha de pagamento para um colaborador. | - `Colaborador` |
| 2. Calcular proventos (salário, periculosidade, insalubridade, vale-alimentação). | - `CalculadoraINSS` |
| 3. Calcular deduções (vale-transporte, faltas). | - `CalculadoraIRRF` |
| 4. Invocar calculadoras de impostos para obter os valores de INSS e IRRF. | - `FolhaPagamento` |
| 5. Gerar e retornar um objeto `FolhaPagamento` preenchido com os resultados. | |
| 6. Calcular o depósito do FGTS. | |

### Classe: `CalculadoraINSS`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Conhecer as faixas e alíquotas da tabela de contribuição do INSS. | - Nenhuma |
| 2. Calcular o valor do desconto de INSS com base no salário. | |

### Classe: `CalculadoraIRRF`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Conhecer as faixas, alíquotas e parcelas a deduzir da tabela do IRRF. | - Nenhuma |
| 2. Calcular a base de cálculo (salário - INSS - dependentes). | |
| 3. Calcular o valor do desconto de IRRF com base na base de cálculo. | |

### Classe: `GerenciadorColaboradores`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Criar, ler, atualizar e remover (CRUD) `Colaborador`. | - `Colaborador` |
| 2. Manter e fornecer uma lista de todos os colaboradores cadastrados. | |

### Classe: `GestaoAcesso`
| Responsabilidades | Colaboradores |
| :--- | :--- |
| 1. Autenticar um usuário com base em credenciais (ID e senha). | - `GerenciadorColaboradores` |
| 2. Recuperar os dados do colaborador após um login bem-sucedido. | - `Colaborador` |
| 3. Verificar o nível de permissão do colaborador (se é administrador). | |
| 4. Gerenciar a sessão do usuário (iniciar e encerrar). | |
