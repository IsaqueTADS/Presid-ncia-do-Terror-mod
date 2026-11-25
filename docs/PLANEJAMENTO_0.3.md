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

## Funcionalidades aceitas pelo usuário — lista oficial

- Fluxo: observando → provocado/transformando → combate → descanso; morte do jogador encerra agressão e retoma observação após respawn.
- Uma dupla compartilhada por mundo/servidor, com UUIDs persistentes. Priorizar agressor durante combate; fora dele manter jogador acompanhado e só trocar se sair/morrer/mudar de dimensão, reduzindo alternância entre jogadores próximos.
- Teleporte como recuperação: verificar a cada 30 s, apenas se distante ou sem entidade carregada; destino inicial 12–20 blocos do alvo, em chão seguro, com espaço para a forma montada. Evitar dentro de blocos, lava e queda. Faixa inicial de destino: 12–20 blocos; distância que dispara a recuperação ainda a definir.
- Sono próximo: após um tempo parado e sem provocação, permitir deitar perto; acordar se atacado ou se o jogador se afastar, em vez de acordar continuamente só porque está perto. Suspender teleporte no meio de deitar/levantar quando não for urgente.
- Memes com intervalo aleatório inicial 45–90 s; sem repetir imediatamente, impedir Lula e Bolsonaro de falar por cima e dar prioridade a transformação/morte. Volume/categoria ajustável. Direção aceita pelo usuário; parâmetros iniciais ajustáveis no balanceamento.
- Configuração de servidor: spawn automático, teleporte, destruição de blocos e frequência das falas, permitindo adaptar partidas de teste.
- Vitória persistente: ao derrotar os dois, registrar conclusão e impedir respawn automático sem reset/comando. Conquista proposta “Fim do mandato”. Usuário definiu derrotar ambos como objetivo; recompensa/conquista e retorno não foram definidos.
- Começar com 2–4 cervejas como drop do Lula e nome customizado Picanha na carne vanilla para compatibilidade com fome/receitas. Faixa inicial aceita; distribuição aleatória/exata ainda a definir.

## Decisões antes de programar

- Dupla compartilhada aceita; fechar escolha inicial do jogador acompanhado e transição entre dimensões.
- Seguir também entre Overworld/Nether/End? Como tratar jogadores em dimensões diferentes?
- Vitória persistente aceita, sem respawn automático após derrota; fechar adoção em mundos já existentes.
- Após morte do jogador, encerrar agressão e voltar a observar (aceito).
- Drop inicial 2–4 cervejas aceito; selecionar trecho exato para morte do Lula.
- Confirmar regra de distância/relógio do teleporte e os gatilhos de sono perto do jogador.

## Cuidados técnicos para implementação futura

Lógica de spawn/recuperação no servidor, com estado persistente e UUIDs das entidades. Não depender somente do tick da entidade para teleporte: chunks descarregadas não executam esse tick. Recuperar/transferir com segurança sem criar cópias, manter vida/forma/agressão e persistir reload do mundo; não forçar grandes áreas de chunks carregadas indefinidamente. Respeitar mudanças de dimensão e desconexão. Cobrir multiplayer, descarregamento, respawn, morte definitiva e reinício em testes pertinentes. Não regenerar assets nem alterar caminhada aprovada sem necessidade. Áudios com licença pendente continuam restritos ao pacote local opcional.


## Combate 0.3 — novos pedidos confirmados

- Revólver: aumentar dano base de 6 para **12 pontos** (6 corações).
- Última bala: **+50%** sobre o dano calculado para o tiro, incluindo eventual modificador de distância que vier a ser definido. Exemplo sem outros bônus: 12 → 18 pontos. O exemplo do usuário de “três metros” não fixa alcance nem bônus; não implementar números de distância sem fechar regra.
- Headshot do revólver: **17 pontos**. Não somar 17 ao dano corporal.
- Exceção expressa: **headshot com a última bala no Lula = exatamente 22 pontos**. Esse resultado substitui a regra genérica de +50%, sem multiplicar novamente ou somar 22. Vale para o personagem Lula; distinguir sua cabeça da cabeça do jumento na forma montada.
- Tooltip deve explicar dano, headshot, última bala e exceção do Lula, incluindo modificadores futuros. Dados exibidos e cálculo devem vir dos mesmos parâmetros.
- Indicadores flutuantes de dano em números, com fonte do Minecraft, tanto para revólver quanto bazuca. Formato/duração/cor ainda propostos abaixo.
- Nenhum desses ajustes foi implementado. Dano da 0.2.2 continua 6 no revólver e teto 12 na explosão.

## Complemento proposto do combate — aguarda avaliação

1. Ordem: identificar parte atingida → escolher base corporal/HS → aplicar distância (se aprovada) → +50% da última bala → resolver exceção Lula/última/HS para 22 → aplicar dano. Para cumprir exatamente 22 no Lula, a exceção deve fixar a perda efetiva solicitada, sem ser reduzida por modificadores comuns; respeitar vida restante, morte e rejeição de dano por invulnerabilidade. Confirmar essa prioridade antes de programar, sem desativar invulnerabilidade indiscriminadamente.
2. Outros alvos: último HS seria 17 × 1,5 = **25,5**, se o usuário aceitar aplicar o bônus também ao HS fora da exceção. Este valor é derivado/proposto, não um novo número confirmado pelo usuário. A exceção faz o último HS do Lula dar menos que o HS genérico bonificado; manter isso explícito.
3. Definir última bala: sugestão é último cartucho atualmente carregado, identificado antes de consumir a munição. Recarga de uma única bala ativaria o bônus sempre; alternativa é exigir o sexto disparo de um tambor completo. Usuário ainda não escolheu entre as duas mecânicas.
4. Headshot pela interseção real do tiro com região da cabeça, usando volumes compatíveis com o modelo animado/escala/formas; não confundir cabeça do cavaleiro com montaria. Sem HS baseado apenas na altura genérica da entidade combinada.
5. Número flutuante mostra a perda real de vida confirmada pelo servidor, após reduções, com até uma casa decimal. Vida restante limita o número no golpe fatal. Não mostrar dano nominal se o golpe foi recusado. Branco/normal, dourado/última bala, vermelho/HS; texto opcional HS. Subir e desaparecer em aproximadamente 1 s, agrupar impactos simultâneos por vítima, limite de indicadores para evitar poluição visual. Cor/estilo/tempo são sugestões.
6. Bazuca: preservar por ora teto de explosão 12, força 2 e destruição de blocos. Indicador por vítima realmente atingida, variável conforme distância/exposição. Sugerir impacto direto com feedback visual próprio, sem somar outra explosão/dano nem classificar toda a área como headshot. Dano novo e eventual bônus por acerto preciso da bazuca ainda precisam de valores aprovados.
7. Não aplicar automaticamente última bala à bazuca: sua capacidade é um foguete, então todo tiro ganharia o bônus. Alternativa futura: foguete de precisão/impacto direto com regra própria. Tooltip da bazuca deve informar “até 12 de dano de explosão; varia com distância e cobertura” enquanto esse balanceamento for mantido.
8. Feedback de dano é cliente; acerto, parte atingida, bônus, morte e resultado numérico são decididos no servidor. Evitar que números de explosão sejam emitidos duas vezes pelo foguete/impacto.

Decisões pendentes do combate: definição de última bala, último HS nos outros alvos (25,5?), regra de distância, prioridade de dano efetivo 22 no Lula e regras novas específicas da bazuca. Não confundir as propostas desta seção com as funcionalidades anteriores já aceitas.


Implementação autorizada e decisões finais: bônus após tambor completo, acompanhamento entre dimensões e HS final 25,5 nos outros alvos confirmados pelo usuário. Estado implementado e verificação em [V030.md](V030.md); planejamento acima preservado como histórico.
