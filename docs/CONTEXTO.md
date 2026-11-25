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

Ainda **não há projeto Gradle, código Fabric, receitas registradas, combate funcional ou JAR**. A etapa atual é modelagem, animação, seleção de áudio e discussão das mecânicas. Assets existentes não significam funcionalidades já disponíveis no jogo.

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

Na etapa das armas foram gerados **rascunhos locais ainda sem commit**, no checkout de assets: `bazuca`, `revolver`, `municao_revolver`, `foguete_bazuca` e `bolsonaro_trex_bazuca`, além de scripts, referência e prévias. O conjunto montado inclui clips de sacar, mirar, disparar, recarregar e guardar. A bazuca usa malhas cilíndricas de 32 lados em formato Generic do Blockbench. Esse formato exige definir a futura renderização no Fabric; não é um export GeckoLib diretamente integrável.

A inspeção desses novos assets no Blockbench foi interrompida por Esc. Não foram declarados aprovados, comitados ou publicados. Um clone do GitHub pode não conter esses rascunhos: conferir o outro checkout antes de continuar. As versões anteriores dos personagens foram preservadas.

Pendências da etapa: revisar modelos e animações das armas no Blockbench, verificar encaixe nas mãos e passagem pela cabeça/montaria, produzir a imagem de receitas, analisar/selecionar os áudios e registrar as propostas de mecânica. Só então concluir os commits pertinentes dessa etapa. Não incluir rascunhos incompletos num commit de documentação.

## Como manter este documento

Adicionar decisões e pedidos novos à seção correspondente durante o trabalho, atualizando estado e próximos passos ao encerrar ou interromper. Separar sempre **solicitado**, **proposto**, **implementado** e **verificado**. Quando uma decisão afetar assets, atualizar também o contexto do outro repositório. Não depender do histórico do chat para explicar o que precisa ser feito.
