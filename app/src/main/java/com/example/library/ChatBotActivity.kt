package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

private const val REQUISITOS_DO_PROJETO = """
REQUISITOS FUNCIONAIS (RESUMIDOS)

LOGIN E REGISTRO
- Tela de login com matrícula, senha, botão Entrar e link Registrar-se.
- Verificar credenciais; se incorretas, exibir erro adequado.
- Campos obrigatórios; se vazios, avisar.
- Logomarca da Unifor no fundo.
- Tela de Registro com nome, email, matrícula, senha e confirmar senha.
- Verificar se usuário já está cadastrado; se sim, mostrar aviso.
- Após registrar, redirecionar para login.
- Seta no topo retorna ao login.

MENU INICIAL (USUÁRIO)
- Ícone de menu (3 traços) e ícone de pesquisa no topo.
- Grid de livros recomendados com capa e navegação.
- Indicador de posição do grid e botão para atualizar.
- Ícone para abrir chatbot.
- Segundo grid com eventos (nome, título, descrição, data, hora, local) vindos do CRUD do ADM.
- Botão "Acessar calendário".

MENU LATERAL
- Opções: Aluguel de cabines, Acessibilidade, Menu inicial, Sair.
- Sair retorna ao login.

CHATBOT
- Tela com barra de pesquisa, botão limpar texto e seta para voltar ao menu inicial.
- Usuário pode escrever dúvidas; botão de limpar apaga o texto.

PESQUISA
- Tela de pesquisa com barra superior + seta para voltar.
- Usuário pode digitar livremente.
- Ao buscar, abre a tela do livro pesquisado.

TELA DO LIVRO PESQUISADO
- Mostra pesquisa atual, botão de limpar texto e mensagem “Resultado para…”.
- Exibe livro encontrado; clicando leva à tela do livro.
- Depois, seção “Baseado em…” com recomendação relacionada.

TELA DO LIVRO (DETALHES)
- Seta para voltar.
- Exibe título, avaliação (0 a 5), estrelas, total de avaliações, capa, data de lançamento, classificação indicativa e páginas.
- Grid “Mais sobre” com descrição, exemplares disponíveis e botão “Alugar livro”.
- Grid “Avaliações de usuários” com nome, ícone, estrelas, comentário e data.
- Usuário que já leu pode acessar tela de avaliar livro.

ALUGUEL DE LIVRO
- Tela semelhante à do livro, adicionando grid “Alugar livros”.
- Exibe nome do usuário, ícone, quantidade de livros lidos e mini calendário para selecionar dias.
- Botão “Alugar <nome do livro>”.
- Após clicar: mostrar “Reserva concluída” ou “Não foi possível realizar a reserva”.

AVALIAÇÃO DE LIVRO
- Seta para voltar.
- Grid “Faça sua avaliação”.
- Usuário seleciona estrelas de 0 a 5.
- Campo texto “Descreva sobre”.
- Botão “Incluir avaliação” salva e retorna ao livro.

ACESSIBILIDADE
- Tela permite ajustar tamanho da fonte.
- Permite ajustar contraste usando régua com ícones indicativos.

CALENDÁRIO DE EVENTOS (USUÁRIO)
- Ao clicar em “Acessar calendário”: mostrar visualização mensal com lista de eventos do mês.
- Clicar em dia com evento → visualização diária com nome, imagem, tipo, horário, local e convidados.
- Clicar no evento → tela detalhada do evento com nome, imagem, tipo, local, data, hora e convidados.

LOGIN ADMINISTRADOR
- Sistema identifica usuário admin e carrega interface apropriada.

EVENTOS (ADMINISTRADOR)
- Botão “Adicionar evento” na visualização mensal.
- Formulário com: nome, imagem, tipo (clube do livro, lançamento, roda de conversa), local, data e hora, convidados (nome e descrição).
- Lista suspensa para tipo.
- Checkbox para definir se há convidados.
- Botão “Adicionar convidado” e “Excluir convidado”.
- Botão “Criar evento” salva no banco.
- Na tela de detalhes: botões “Editar” e “Excluir”.
- Edição possui os mesmos campos do formulário de criação.
- Botão “Salvar” grava alterações.
- Excluir mostra pop-up de confirmação; ao confirmar, remove o evento e retorna ao calendário diário.
- Exibir pop-up de sucesso após excluir.

LIVROS (ADMINISTRADOR)
- Botão “Adicionar livro” na tela de pesquisa.
- Formulário de criação com: título, autor, capa (imagem), idiomas e campo de exemplares.
- Botão “Adicionar exemplar” abre pop-up com: registro, ISBN, editora, edição, suporte, local, situação e sinopse.
- Listas suspensas: edição (numérica), suporte (impresso/digital), situação (emprestado/disponível/cativo).
- Pop-up possui botões “Cancelar” e “Adicionar”.
- Exemplares adicionados de forma temporária no formulário.
- Botão “Excluir exemplar”.
- Botão “Registrar livro” salva no banco.
- Tela de detalhes do livro tem botão “Editar”.
- Formulário de edição é igual ao de criação.
- Botão “Salvar” atualiza o livro.
- Excluir livro abre pop-up de confirmação; ao confirmar, remove e retorna à tela de pesquisa.

FIM DOS REQUISITOS.
"""

class ChatBotActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var model: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Modelo Gemini (usando a chave do BuildConfig)
        model = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        val btnVoltar   = findViewById<ImageButton>(R.id.btnVoltar)
        val btnEnviar   = findViewById<ImageButton>(R.id.btnEnviar)
        val txtPergunta = findViewById<EditText>(R.id.txtPergunta)
        rvMessages      = findViewById(R.id.rvMessages)

        adapter = MessagesAdapter(mutableListOf())
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        btnVoltar.setOnClickListener {
            // Se quiser voltar pro MenuInicialActivity
            startActivity(Intent(this, MenuInicialActivity::class.java))
            finish()
        }

        btnEnviar.setOnClickListener {
            val pergunta = txtPergunta.text.toString().trim()

            if (pergunta.isEmpty()) {
                Toast.makeText(this, "Digite uma pergunta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mensagem do usuário
            adapter.addMessage(ChatMessage(pergunta, true))
            rvMessages.scrollToPosition(adapter.itemCount - 1)
            txtPergunta.setText("")

            enviarPerguntaGemini(pergunta)
        }
    }

    private fun enviarPerguntaGemini(pergunta: String) {
        lifecycleScope.launch {
            try {

                val systemPrompt = """
                Você é o assistente oficial do aplicativo mobile Sistema de Biblioteca.
                Você só pode responder sobre funcionalidades, telas, regras, fluxos e requisitos do Sistema de Biblioteca.

                Se o usuário perguntar qualquer coisa fora do projeto, responda:
                "Desculpe, eu só posso responder perguntas relacionadas ao SistemaBiblioteca."

                Se não souber algo dentro do projeto, explique apenas para que aquela funcionalidade serve.

                Nunca responda temas externos, nunca seja ofensivo, não invente requisitos.
                
                REQUISITOS DO PROJETO:
                $REQUISITOS_DO_PROJETO
            """.trimIndent()

                // Prompt final combinado
                val promptFinal = "$systemPrompt\n\nPergunta do usuário: $pergunta"

                // >>> CHAMADA CORRETA PARA O GEMINI <<<
                val response = model.generateContent(promptFinal)

                // >>> FORMA CORRETA DE PEGAR O TEXTO <<<
                val texto = response.text ?: "Sem resposta."

                adapter.addMessage(ChatMessage(texto, false))
                rvMessages.scrollToPosition(adapter.itemCount - 1)

            } catch (e: Exception) {
                adapter.addMessage(
                    ChatMessage("Erro ao gerar resposta: ${e.message}", false)
                )
            }
        }
    }
}
