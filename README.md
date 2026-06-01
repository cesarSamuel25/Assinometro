# 📱 Assinômetro

Um aplicativo Android nativo para controle e gerenciamento de assinaturas e serviços financeiros recorrentes. O app permite cadastrar seus gastos, acompanhar vencimentos e manter um histórico de serviços que foram cancelados.

---

## 🚀 Funcionalidades

* **Dashboard Financeiro**: Exibe o resumo do total gasto e gráficos/valores agrupados por categorias (Streaming, Saúde, Aplicativos, etc.).
* **Listagem Ativa Inteligente**: Organiza seus serviços por **ordem de proximidade do vencimento**, garantindo que você visualize primeiro o que está mais perto de vencer.
* **Filtros por Categoria**: Permite filtrar rapidamente os serviços ativos na tela principal para melhor organização.
* **Soft Delete (Ocultar)**: Ao deletar um serviço da listagem ativa, ele não é apagado imediatamente; o app registra a **data exata do cancelamento** e o envia para o histórico.
* **Histórico de Cancelados**: Uma tela dedicada para gerenciar tudo o que você já cancelou, exibindo a data exata da ação, ordenada dos cancelamentos mais recentes para os mais antigos.
* **Limpeza Definitiva**: Opção de "Limpar Tudo" dentro do histórico para apagar permanentemente os registros desativados do banco de dados.

---

## 🎨 Diferenciais de Interface (UI/UX)

* **Design Blindado (Light Mode)**: O aplicativo possui uma identidade visual própria baseada nas cores Azul e Laranja com fundo limpo. O tema escuro do sistema é ignorado para evitar distorções visuais e garantir a legibilidade.
* **Orientação Travada**: Interface otimizada e travada estritamente na orientação vertical (`portrait`), evitando quebras de layout.
* **Lista Fluida com Delimitadores**: O `RecyclerView` possui limites visuais sutis que indicam ao usuário o início e o fim da rolagem dos cards de forma amigável.
* **Efeito de Profundidade**: Uso de elevações (`elevation`) e sombreamentos refinados nos componentes para separar as barras de navegação fixas dos cartões de serviços em movimento.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

* **Linguagem**: [Kotlin](https://kotlinlang.org/) (100% Nativo)
* **Arquitetura**: MVVM (Model-View-ViewModel) com componentes de arquitetura do Android (`LiveData`, `ViewModel`).
* **Interface Gráfica**: `ConstraintLayout` para layouts responsivos e [Material Design 3](https://m3.material.io/) para os componentes visuais (`MaterialCardView`, `MaterialButton`).
* **Banco de Dados Local**: `SQLite` encapsulado via `SQLiteOpenHelper` para persistência dos dados financeiros de forma rápida e offline.
* **Manipulação de Telas**: `ViewBinding` para vinculação segura de componentes XML diretamente do código Kotlin.

---

## 📦 Como rodar o projeto

1. Certifique-se de ter o **Android Studio** instalado (versão igual ou superior à recomendada).
2. Clone este repositório no seu computador:
   ```bash
   git clone [https://github.com/cesarSamuel25/Assinometro.git](https://github.com/cesarSamuel25/Assinometro.git)
3. Abra o Android Studio e selecione a opção Open an Existing Project.
4. Selecione a pasta onde você clonou o repositório.
5. Espere o Gradle sincronizar todas as dependências do projeto.
6. Conecte seu dispositivo físico ou inicie um emulador e clique no botão Run (Play) no topo do Android Studio.

## 🛠️ Gerando o APK para testes físicos:
Se quiser gerar o arquivo de instalação direto para o seu celular:

1. Vá no menu superior em Build > Build Bundle(s) / APK(s) > Build APK(s).
2. Aguarde a conclusão e clique em Locate na notificação para encontrar o arquivo app-debug.apk.