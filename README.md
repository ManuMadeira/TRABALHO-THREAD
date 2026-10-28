# Trabalho de Sistemas Distribuídos

Repositório das atividades da disciplina de **Sistemas Distribuídos** do curso de **Bacharelado em Ciência da Computação** da **Universidade Tecnológica Federal do Paraná (UTFPR)**. Os exercícios abordam programação concorrente em Java, sincronização e coordenação entre threads.

## Autores

- Emmanuelly Madeira
- Fernando

## Estrutura do repositório

Os fontes Java estão em `src/main/java` e seguem o pacote-base `br.edu.sistemasdistribuidos`. Os nomes de pacotes são escritos em minúsculas, conforme a convenção da linguagem.

```text
src/main/java/br/edu/sistemasdistribuidos/
├── exercicio01atendimentobar/
│   ├── app/
│   ├── atores/
│   └── controle/
├── exercicio02barbeirodorminhoco/
│   ├── app/
│   ├── atores/
│   └── monitor/
├── exercicio03jantarfilosofos/
│   ├── app/
│   ├── atores/
│   └── recursos/
└── exercicio04problemasclassicos/
    ├── app/
    ├── roletas/
    ├── contas/
    └── produtorconsumidor/
        ├── semaforos/
        └── monitores/
```

### Atividades

1. **`Exercicio01_AtendimentoBar`** (`exercicio01atendimentobar`) — simulação do atendimento no bar com bartender, garçons e clientes, controle das rodadas e organização da fila.
2. **`Exercicio02_BarbeiroDorminhoco`** (`exercicio02barbeirodorminhoco`) — solução do problema do Barbeiro Dorminhoco com monitor e controle das N cadeiras da sala de espera.
3. **`Exercicio03_JantarFilosofos`** (`exercicio03jantarfilosofos`) — simulação do Jantar dos Filósofos, com cinco filósofos e sincronização do acesso aos palitos para prevenir deadlock e evitar starvation.
4. **`Exercicio04_ProblemasClassicos`** (`exercicio04problemasclassicos`) — variações de roletas, operações concorrentes em contas bancárias e implementações do problema Produtor/Consumidor com semáforos e monitores.

> **Estado atual:** as implementações executáveis disponíveis são o Jantar dos Filósofos e os três cenários do Exercício 4. Os Exercícios 1 e 2 têm a estrutura de pacotes preparada, mas ainda precisam de suas classes principais. O menu principal sinaliza isso ao selecionar essas opções.

## Tecnologias utilizadas

- Java e a API padrão da plataforma;
- `Thread` para criar e executar threads;
- `java.util.concurrent.Semaphore` para sincronização baseada em semáforos;
- monitores com `synchronized`, `wait()` e `notifyAll()`.

## Pré-requisitos

Instale um JDK e verifique se `java` e `javac` estão disponíveis no `PATH`:

```powershell
java -version
javac -version
```

Os comandos de terminal a seguir usam **PowerShell** e devem ser executados a partir da raiz do repositório.

## Como compilar e executar pelo terminal

### Menu principal

Compile todos os fontes e execute o menu interativo. O menu permite iniciar os exercícios implementados e retorna à tela principal ao pressionar Enter:

```powershell
$fontes = Get-ChildItem 'src\main\java' -Recurse -Filter '*.java' |
  ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $fontes
java -cp out br.edu.sistemasdistribuidos.MenuPrincipal
```

### Exercício 1 — Atendimento no Bar

Quando a classe principal `Main` estiver implementada em `exercicio01atendimentobar/app`, compile o exercício e execute-o:

```powershell
$fontes = Get-ChildItem 'src\main\java\br\edu\sistemasdistribuidos\exercicio01atendimentobar' -Recurse -Filter '*.java' |
  ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $fontes
java -cp out br.edu.sistemasdistribuidos.exercicio01atendimentobar.app.Main
```

### Exercício 2 — Barbeiro Dorminhoco

Quando a classe principal `Main` estiver implementada em `exercicio02barbeirodorminhoco/app`:

```powershell
$fontes = Get-ChildItem 'src\main\java\br\edu\sistemasdistribuidos\exercicio02barbeirodorminhoco' -Recurse -Filter '*.java' |
  ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $fontes
java -cp out br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.app.Main
```

### Exercício 3 — Jantar dos Filósofos

Compile os fontes do exercício e execute sua classe principal. O argumento opcional define a duração da simulação em segundos; sem argumento, a duração padrão é 15 segundos:

```powershell
$fontes = Get-ChildItem 'src\main\java\br\edu\sistemasdistribuidos\exercicio03jantarfilosofos' -Recurse -Filter '*.java' |
  ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $fontes
java -cp out br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.app.Main 15
```

### Exercício 4 — Problemas Clássicos

Compile todos os fontes do exercício e execute a classe principal. A demonstração roda em sequência as roletas, as operações bancárias e as duas implementações de Produtor/Consumidor:

```powershell
$fontes = Get-ChildItem 'src\main\java\br\edu\sistemasdistribuidos\exercicio04problemasclassicos' -Recurse -Filter '*.java' |
  ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $fontes
java -cp out br.edu.sistemasdistribuidos.exercicio04problemasclassicos.app.Main
```

Opcionalmente, informe primeiro a quantidade de roletas e depois a quantidade de pessoas/clientes. Os padrões são 3 roletas e 10 pessoas:

```powershell
java -cp out br.edu.sistemasdistribuidos.exercicio04problemasclassicos.app.Main 2 8
```

Para compilar todas as atividades de uma vez, use `Get-ChildItem` sobre `src\main\java` no lugar do caminho específico do exercício.

## Como abrir e executar em uma IDE

O repositório usa a estrutura convencional `src/main/java`. Não é necessário mover os arquivos para executar o projeto; basta marcar essa pasta como raiz dos fontes Java se a IDE não fizer isso automaticamente.

### Visual Studio Code

1. Instale um JDK e as extensões Java recomendadas para VS Code, como **Extension Pack for Java**.
2. Abra a pasta raiz do repositório em **File > Open Folder**.
3. Aguarde a extensão importar e indexar os fontes em `src/main/java`.
4. Abra a classe principal do exercício desejado e use **Run** acima do método `main`.
5. Para o Jantar dos Filósofos, a classe é `exercicio03jantarfilosofos/app/Main.java`; para o Exercício 4, use `exercicio04problemasclassicos/app/Main.java`. Configure argumentos opcionais nas opções de execução Java.

### IntelliJ IDEA

1. Selecione **File > Open** e escolha a raiz do repositório.
2. Se necessário, clique com o botão direito em `src/main/java` e escolha **Mark Directory as > Sources Root**.
3. Abra a classe principal do exercício.
4. Clique no ícone de execução ao lado do método `main` e selecione **Run**.
5. Para passar argumentos, edite a configuração em **Run > Edit Configurations**.

### Eclipse

1. Selecione **File > Import > General > Existing Projects into Workspace** e escolha a raiz do repositório.
2. Caso o projeto ainda não tenha metadados do Eclipse, crie um projeto Java e configure `src/main/java` como pasta de fontes em **Build Path**.
3. Abra a classe principal desejada.
4. Clique com o botão direito no arquivo e selecione **Run As > Java Application**.
5. Para informar argumentos, abra **Run Configurations > Arguments**.

Em qualquer IDE, os Exercícios 1 e 2 ainda não podem ser executados até que suas classes principais com `public static void main(String[] args)` sejam implementadas.
