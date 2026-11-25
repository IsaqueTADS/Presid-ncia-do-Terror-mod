# Observações do teste manual — próximas mudanças

Registro em 25/11/2026 durante teste da 0.1.1. **Lista de pedidos, ainda não implementados.** Continuar recebendo observações do usuário antes de executar uma nova revisão; preservar o jogo em teste.

1. Remover a mensagem permanente da arma na action bar, tanto revólver quanto bazuca: nome, quantidade e “R: recarregar” não devem aparecer. Durante recarga, mostrar apenas uma barrinha amarela que preenche conforme o progresso. Preservar cartuchos visuais no tambor.
2. Tecla configurável **N** alterna mostrar/ocultar contador de dias. B foi cogitado e descartado. Mostrar o dia real usado pelo F3, sem limite artificial e sem somar um ao valor; usar aritmética long, sem limitar em 10 ou truncar dias altos. Referência inspecionada: Michael Jackson/mod/src/main/java/dev/michaelhorror/client/DayHud.java: canto superior esquerdo, x=8, y=8, texto branco com sombra. Copiar aparência/posição, não o contador ThrillerVisuals.day nem o limite de progressão daquele mod. Respeitar HUD oculto/F1.
3. Melhorar feedback de dano em Lula e Bolsonaro: impacto legível, reação visual e partículas. O usuário descreveu “sangue”; isso é uma preferência visual a definir, não efeito sanguíneo vanilla existente. Avaliar flash vermelho e partículas de dano antes de escolher estilo/intensidade de sangue. Renderer próprio atualmente não aplica automaticamente todo feedback de LivingEntityRenderer.
4. Mostrar vida diminuindo. Proposta do usuário: barra superior no estilo Ender Dragon, dividida em duas metades, Lula de um lado e Bolsonaro do outro. Definir visibilidade por distância/combate, comportamento com apenas um boss e múltiplos exemplares. O usuário pode enviar mods de referência; não depender deles para manter o pedido registrado.
5. Ao derrotar Lula, dropar **cinco stacks completos de carne bovina cozida**: interpretar como 5 × 64 = **320 unidades de minecraft:cooked_beef**. Dropar no servidor uma vez por morte, sem duplicar na transformação. Confirmar apenas se o usuário corrigir a quantidade.
6. Modelar **cerveja separadamente no Blockbench**, com fonte editável e textura no repositório de assets; definir recipiente/aparência com o usuário. Criar item e imagem do craft na bancada em previews, coerente com a receita efetivamente implementada.
7. Proposta de craft de jogo: frasco de água + cana/açúcar produz um item intermediário de “álcool”; combinar com trigo para a cerveja. Proporções, frascos retornados, efeitos de consumo e desbloqueio ainda não definidos. É abstração de gameplay, não reprodução de fabricação real.

8. Ao derrotar Bolsonaro, dropar **uma bazuca e um stack de foguetes da bazuca (64 unidades)**, no servidor e uma única vez por morte. Pedido registrado; ainda não implementado. A quantidade de munição carregada dentro da bazuca não foi definida.

## Prioridade e progresso da cerveja

O usuário pediu modelar a cerveja primeiro. Criada fonte separada `visual-assets/models/cerveja/cerveja.bbmodel`: garrafa âmbar, tampa dourada e rótulo de trigo, conferida no Blockbench, com PNG e prévia nativa. Craft/preview de receita, efeitos, integração e drops continuam pendentes; não tratar modelo criado como item já funcional. Não alterar o jogo que o usuário está testando.

## Pesquisa breve para a cerveja

Cerveja costuma usar água, malte (normalmente cevada), lúpulo e levedura; a fermentação produz o álcool. Não é necessário adicionar álcool extraído de cana na fabricação normal. Trigo pode representar o cereal no Minecraft; a etapa intermediária sugerida permanece uma escolha ficcional de craft. Referência: https://www.wsetglobal.com/news-insights/insight-opinion/what-is-beer-a-beginner-s-guide/

## Retorno positivo

O usuário confirmou que Bolsonaro está se transformando corretamente. A avaliação das demais animações continua; não considerar a 0.1.1 aprovada integralmente.


Atualização: itens desta lista implementados na 0.2.0; detalhes, limites e verificação em [V020.md](V020.md). Avaliação manual ainda pendente.
