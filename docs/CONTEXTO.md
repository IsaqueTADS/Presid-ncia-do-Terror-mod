# Contexto de Presidência do Terror — mod

Atualizado em 25/11/2026. Ponto de retomada para novos chats e após compactação. Este documento registra o estado conhecido; conferir os arquivos e o Git antes de continuar.

## Identidade e organização

**Presidência do Terror** é um mod de terror cômico para Minecraft Java **1.21.1, Fabric e Java 21**. Lula e Bolsonaro, caricaturados e com faixa presidencial, perseguem o jogador e usam falas de memes em contextos de jogo. As situações são ficção humorística.

- Este repositório guarda código futuro, recursos integrados e decisões de gameplay.
- O repositório irmão guarda fontes Blockbench, texturas, referências, áudio, ferramentas e verificações: [visual-assets no GitHub](https://github.com/IsaqueTADS/Presid-ncia-do-Terror-visual-assets).
- Leia [o contexto audiovisual local](../../visual-assets/docs/CONTEXTO.md), ou [a versão no GitHub](https://github.com/IsaqueTADS/Presid-ncia-do-Terror-visual-assets/blob/main/docs/CONTEXTO.md). Caminhos de checkout irmão podem não existir num clone isolado.
- Não existe repositório Git na pasta que agrupa `mod` e `visual-assets`. Ambos têm Git independente. Não criar contexto na raiz agrupadora.
- Mudanças concluídas devem ser comitadas com Conventional Commits em inglês e enviadas diretamente aos remotos já configurados, conforme autorização permanente do usuário. Não fazer commits vazios.

## Estado real de implementação

**Atualização vigente: 0.1.1, aguardando teste manual do usuário.** Leia [CORRECAO_0.1.1.md](CORRECAO_0.1.1.md). O usuário pediu manter a caminhada atual e avaliar pessoalmente. Não foi aberto cliente nesta revisão. Total compartilhado de 500 de vida por personagem/montaria; saves antigos com 80 são migrados preservando dano recebido. Pose da bazuca agora vinculada à mão e ao corpo, muzzle para frente; rastro de partículas do revólver até o primeiro impacto e recoil gradual.

Todos os clips dos seis rigs de personagens/armas têm rotas no runtime (`animation-routing-0.1.1.json`). Montarias sem alvo podem deitar após 10 s paradas no chão e sem jogador a 12 blocos; entrar em até 10 blocos inicia acordar/levantar e depois perseguição. Deslocamento, gravidade, colisões e navegação prévios foram preservados; apenas transformação/descanso/gestos suspendem passeio. Exemplares antigos do mundo de demonstração com `NoAI:1b` continuam sem IA por intenção do comando: testar novos ovos ou remover a marca, não modificar a caminhada por causa desses exemplares.

23 efeitos CC0 baixados estão integrados no JAR, com manifesto `runtime-audio-manifest.json`. Memes com licença pendente foram preparados em `run/resourcepacks/presidencia-memes-local.zip`, pacote opcional local que precisa ser ativado no perfil; não estão no JAR público ou no Git. Os testes de servidor cobrem vida/migração, sono/levantada e vínculo mão/bazuca além da suíte anterior; percepção sonora, movimento e aparência aguardam as considerações do usuário. As descrições 0.1.0 abaixo são históricas.

**Implementação atual: 0.1.0 jogável.** Após o planejamento [V1.md](V1.md), o usuário autorizou executar a programação e pediu previews das quatro receitas e munição visível por posição do tambor. Leia [TESTAR_V1.md](TESTAR_V1.md): armas funcionais, dois ovos sem spawn natural, transformação após golpe e perseguição. Lula ataca de perto no jumento; roubo de comida fica para depois. Bolsonaro no T-Rex usa bazuca a cada 15 s, com preparação visual. Foguetes destroem blocos. Adotamos os valores iniciais propostos; depois de perder o alvo, permanecem montados e param de perseguir. Essas escolhas de implementação continuam ajustáveis durante testes.

Há projeto Gradle/Fabric, código cliente/servidor, quatro receitas, recursos exportados e JAR em `build/libs/presidencia-do-terror-0.1.0.jar`. Sete GameTests passaram em servidor de teste que encerrou normalmente. O cliente abriu e houve conferência do revólver e da transformação de Bolsonaro. Multiplayer com dois clientes, todas as poses e sensação de recoil ainda exigem revisão; não declarar verificação completa. Fontes e hashes da integração ficam em `docs/assets-manifest.json`. O renderer próprio mantém cubos/malhas e dispensa GeckoLib nesta versão.

As posições do tambor ficam amarelas conforme os cartuchos efetivamente carregados. Durante recarga parcial, a carga antiga permanece visível; somente cartuchos disponíveis entram ao término. Botão direito dispara, R recarrega; trocar/largar arma ou morrer/desconectar cancela sem consumo. Previews: `visual-assets/previews/craft-*.png`, `crafts-v1.png` e `revolver-municao-0-1-6.png`. Efeitos usam eventos do jogo; memes pendentes não foram distribuídos. As seções abaixo conservam o histórico de pedidos e propostas; para o comportamento vigente, priorizar esta seção e TESTAR_V1.md.

As referências de estrutura são os projetos Chicken Stalker e Michael Jackson do workspace. Reutilizar padrões técnicos quando fizer sentido, sem importar automaticamente suas mecânicas. Ao implementar: gameplay e inventário no servidor, audiovisual no cliente, compatibilidade com servidor dedicado e testes pertinentes. Integrar exports revisados com manifesto de revisão Git de origem e hashes; a build não deve regenerar o repositório de assets.

## Pedidos confirmados

- Lula e Bolsonaro a pé com **mínimo de três blocos** de altura (48 unidades; 16 unidades = 1 bloco). Podem ser maiores, especialmente montados. Ambos usam faixa presidencial.
- Lula tem versão montada num jumento próprio, inspirado no burro do Minecraft. Bolsonaro tem versão montada num T-Rex articulado; velociraptor foi alternativa inicial, sem modelo criado.
- Priorizar modelos e animações no Blockbench. Blender só se houver necessidade, avisando o usuário.
- Montarias devem caminhar, correr, mexer mandíbula, rugir/zurrar, observar, reagir, deitar, descansar e levantar com movimentos suaves. Gatilhos automáticos ainda não existem.
- Bolsonaro montado deve carregar uma bazuca nas costas, sacá-la e atirar em alguns momentos do jogo. O usuário quer preservar sua estilização atual. Quando e contra quem ele usa a arma ainda precisa ser definido.
- Bazuca também deve existir como item separado, craftável, com tubo arredondado conforme a referência enviada.
- Um revólver craftável, munição, tiro e recarga também foram solicitados. O usuário mencionou pistola na descrição, mas o pedido principal foi revólver; o rascunho segue revólver.
- Crafts simples com materiais normais do Minecraft, como ferro. Produzir uma imagem com as receitas propostas. Nenhuma receita ou quantidade foi aprovada ou implementada.
- Usar memes e efeitos com contexto, pesquisando licença e permitindo audição antes da integração.

## Ideias de gameplay ainda em discussão

Exemplo do usuário: ao comer carne, Lula poderia tirar carne do inventário e perseguir o jogador gritando “feijão puro”. A ideia está registrada, mas ainda não define o instante da retirada, quantidade, recuperação ou comportamento com inventário vazio.

Aproximação observando de longe, provocação contextual, perseguição com chance de fuga, transformação anunciada e pausa entre eventos são propostas de fluxo. Ainda falta decidir:

- Spawn, frequência, distância, progressão e se começam observando ou atacando.
- Condições da transformação, duração, reversão e descanso.
- Velocidades, dano, alcance, cooldowns e como despistar os personagens.
- Identidade por jogador ou compartilhada no mundo, alvo no multiplayer e persistência.
- Como lidar com morte, desconexão, mudança de dimensão, inventário e recuperação de comida.
- Montarias como parte da entidade ou entidades separadas; possibilidade de dano e interação do jogador.
- Telefone como prop ou item funcional; quais eventos ativam Bolsonaro.
- Armas: receita, capacidade, consumo de munição, controles, tempos de recarga, projéteis, efeitos e eventual dano a blocos. Não há valores fixados.

## Assets disponíveis e limites

O repositório de assets possui Lula e Bolsonaro a pé, Lula com jumento e Bolsonaro com T-Rex. Os cavaleiros foram reposicionados nas selas; rostos e montarias receberam revisões. A revisão mais recente corrigiu suavidade das cabeças/pescoços, abertura de mandíbula e descanso. Os detalhes, clips e relatórios estão no contexto de assets.

Há 52 arquivos de áudio catalogados localmente: 28 de fontes que declaram CC0 e 24 referências com direitos pendentes. Os pendentes não são redistribuídos pelo Git. Houve decodificação técnica, mas não aprovação por audição nem integração. Consulte `visual-assets/audio/README.md`, `download-manifest.json` e `OUVIR.html`.

## Onde o trabalho anterior parou

Na etapa inicial das armas foram gerados rascunhos de `bazuca`, `revolver`, `municao_revolver`, `foguete_bazuca` e `bolsonaro_trex_bazuca`, além de scripts, referência e prévias. Os quatro itens separados passam a ser versionados na revisão de recarga descrita abaixo; a variante montada continua local. O conjunto montado inclui clips de sacar, mirar, disparar, recarregar e guardar. A bazuca usa malhas cilíndricas de 32 lados em formato Generic do Blockbench. Esse formato exige definir a futura renderização no Fabric; não é um export GeckoLib diretamente integrável.

A inspeção inicial desses novos assets no Blockbench foi interrompida por Esc. Na revisão seguinte, os quatro itens separados passaram a ser versionados com correção da queda do tambor na recarga, menor inclinação da bazuca, ajuste da munição ao tambor e remoção de aletas sobrepostas do foguete. Relatório geométrico: `visual-assets/docs/weapon-verification.json`, com 169 instantes da recarga; descrição: `visual-assets/docs/ARMAS.md`. Após o usuário restaurar a janela, as fontes foram recarregadas/abertas no Blockbench: poses de recarga e retorno inspecionados, foguete e cartucho ampliados para conferência. Isso não constitui aprovação final do usuário ou teste no jogo. A variante montada permanece rascunho local sem commit/publicação. As versões anteriores dos personagens foram preservadas.

Pendências da etapa: receber avaliação do usuário sobre os itens revisados; revisar separadamente a variante montada, encaixe nas mãos e passagem pela cabeça/montaria; produzir a imagem de receitas, analisar/selecionar os áudios e registrar as propostas de mecânica. As munições são modelos estáticos e a recarga da bazuca é um gesto sem inserção de foguete ou mãos. Não há armas funcionais no jogo. Não incluir a variante montada incompleta num commit de documentação.

## Como manter este documento

Adicionar decisões e pedidos novos à seção correspondente durante o trabalho, atualizando estado e próximos passos ao encerrar ou interromper. Separar sempre **solicitado**, **proposto**, **implementado** e **verificado**. Quando uma decisão afetar assets, atualizar também o contexto do outro repositório. Não depender do histórico do chat para explicar o que precisa ser feito.


## Observações do usuário durante o teste manual

Lista pendente em [FEEDBACK_TESTE_0.1.1.md](FEEDBACK_TESTE_0.1.1.md): remover mensagem de armas, barra amarela apenas durante recarga; tecla N configurável para dia real do F3 no canto superior esquerdo; feedback de dano e vida dos dois bosses; Lula dropar 320 carnes bovinas cozidas; modelar cerveja separada e preparar craft/preview com trigo, frasco de água e intermediário ficcional de álcool. Nenhuma dessas mudanças foi implementada nesta anotação. Bolsonaro transformando corretamente foi confirmado; testes continuam.


## Cerveja modelada primeiro e drop do Bolsonaro

Novo pedido: Bolsonaro derrotado deve dropar uma bazuca + um stack de 64 foguetes; registrado, não implementado. Prioridade cumprida de assets: cerveja separada em `visual-assets/models/cerveja/cerveja.bbmodel` (caminho a partir da pasta do projeto), PNG, OBJ/MTL e prévia nativa `visual-assets/previews/cerveja-blockbench.png`. Garrafa âmbar de 32 segmentos, tampa dourada e rótulo de trigo; UV corrigido e leitura conferida no Blockbench. Modelo ainda sem integração/craft/consumo; imagem de craft permanece para etapa seguinte. A 0.1.1 e os outros modelos foram preservados.
