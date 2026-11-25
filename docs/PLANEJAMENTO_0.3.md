# Planejamento 0.3 — perseguição e personalidade

Estado: pedidos e propostas registrados, sem implementação. Versão vigente permanece 0.2.2. Usuário aprovou em teste manual o funcionamento geral, jogabilidade, personagens/animações e armas; isso não substitui testes futuros de multiplayer/persistência.

## Dano vigente conferido no código

- Revólver: 6 pontos (3 corações) de dano base por acerto, via playerAttack. Hitscan de até 64 blocos, bloqueado por paredes. Fonte: src/main/java/dev/presidencia/Gun.java. Sem dano especial adicional por headshot implementado.
- Bazuca: dano de explosão variável com teto de 12 pontos (6 corações) por entidade antes das reduções aplicáveis. Potência 2, destrói blocos; alcance/exposição à explosão afetam o cálculo. Não soma dano direto ao impacto. Fonte: src/main/java/dev/presidencia/Rocket.java. Foguete do jogador e de Bolsonaro usam a mesma implementação.
- Valores não são garantia de perda exata de vida: armadura, efeitos e outras regras de dano podem reduzir o resultado. Pedido atual não altera balanceamento.

## Pedidos confirmados para 0.3

1. Aba própria Presidência do Terror no inventário criativo, com ícone e todos os itens/ovos do mod, conforme pedido anterior. Ícone exato ainda a escolher.
2. Lula e Bolsonaro aparecem automaticamente no início do jogo e acompanham/observam o jogador, inicialmente sem agressão. Spawn pelos ovos continua existente na versão atual; regras de coexistência/duplicatas serão definidas antes de implementar.
3. Enquanto acompanham, soltam ocasionalmente os memes disponíveis de cada personagem. Intervalos ainda não definidos.
4. Ao serem atacados, transformam-se nas montarias e passam a agredir/perseguir, preservando a ideia atual.
5. Bolsonaro: na transformação falar “ninguém vai pegar meu telefone”; na morte, “pegaram meu telefone”. O corte de morte de 3,00 s até o fim já foi aprovado pelo usuário. Transferir a fala inicial para o contexto de transformação na nova lógica, evitando repetição por cada hit.
6. Lula: na morte tocar meme engraçado relacionado a picanha/cervejinha; seleção/corte exato ainda pendente. Adicionar cerveja ao drop, quantidade a definir. Preservar as 320 carnes cozidas já pedidas, com nome exibido “Picanha”; decidir se ItemStack de cooked_beef com nome customizado atende (proposta técnica) ou item próprio.
7. Seguir mesmo se o jogador se afastar muito. Reaproximar por teleporte com intervalo de 30 s, incluindo quando a chunk do personagem tiver descarregado. Distância que dispara a recuperação e contagem exata do intervalo ainda a definir.
8. Acompanhar o jogador após morte/respawn mesmo sem estarem agressivos. Estado agressivo após morte do alvo precisa de definição explícita; sugestão abaixo é voltar a observar.
9. Multiplayer: acompanhar jogador principal ou o mais próximo; usuário apresentou alternativas. Política final de alvo/dupla ainda não fechada. Não implementar simultaneamente regras contraditórias nem duplicar bosses por login.
10. Usar animações de observação e inatividade durante mineração/paradas do jogador. Montarias podem deitar/descansar também com o jogador perto, sem exigir que ele se afaste. Compatibilizar o sono com proximidade e com agressão: hoje o despertar depende de distância curta, o que pode impedir dormir perto.

## Propostas para avaliar — não aprovadas

- Fluxo: observando → provocado/transformando → combate → descanso; morte do jogador encerra agressão e retoma observação após respawn.
- Uma dupla compartilhada por mundo/servidor, com UUIDs persistentes. Priorizar agressor durante combate; fora dele manter jogador acompanhado e só trocar se sair/morrer/mudar de dimensão, reduzindo alternância entre jogadores próximos.
- Teleporte como recuperação: verificar a cada 30 s, apenas se distante ou sem entidade carregada; destino sugerido 12–20 blocos do alvo, em chão seguro, com espaço para a forma montada. Evitar dentro de blocos, lava e queda. Distâncias são propostas.
- Sono próximo: após um tempo parado e sem provocação, permitir deitar perto; acordar se atacado ou se o jogador se afastar, em vez de acordar continuamente só porque está perto. Suspender teleporte no meio de deitar/levantar quando não for urgente.
- Memes com intervalo aleatório sugerido 45–90 s; sem repetir imediatamente, impedir Lula e Bolsonaro de falar por cima e dar prioridade a transformação/morte. Volume/categoria ajustável. Estes números ainda não são regras.
- Configuração de servidor: spawn automático, teleporte, destruição de blocos e frequência das falas, permitindo adaptar partidas de teste.
- Vitória persistente: ao derrotar os dois, registrar conclusão e impedir respawn automático sem reset/comando. Possível conquista “Fim do mandato”. Usuário definiu derrotar ambos como objetivo; recompensa/conquista e retorno não foram definidos.
- Começar com 2–4 cervejas como drop do Lula e nome customizado Picanha na carne vanilla para compatibilidade com fome/receitas. Quantidade é sugestão.

## Decisões antes de programar

- Dupla compartilhada ou uma dupla por jogador? Quem é o “principal” e quando trocar de alvo?
- Seguir também entre Overworld/Nether/End? Como tratar jogadores em dimensões diferentes?
- Aparecem uma vez por mundo, por partida ou voltam após serem derrotados? Como tratar mundos já existentes?
- Ao jogador morrer, encerrar agressão ou manter combate depois do respawn?
- Quantas cervejas e qual trecho exato para a morte do Lula?
- Confirmar regra de distância/relógio do teleporte e os gatilhos de sono perto do jogador.

## Cuidados técnicos para implementação futura

Lógica de spawn/recuperação no servidor, com estado persistente e UUIDs das entidades. Não depender somente do tick da entidade para teleporte: chunks descarregadas não executam esse tick. Recuperar/transferir com segurança sem criar cópias, manter vida/forma/agressão e persistir reload do mundo; não forçar grandes áreas de chunks carregadas indefinidamente. Respeitar mudanças de dimensão e desconexão. Cobrir multiplayer, descarregamento, respawn, morte definitiva e reinício em testes pertinentes. Não regenerar assets nem alterar caminhada aprovada sem necessidade. Áudios com licença pendente continuam restritos ao pacote local opcional.
