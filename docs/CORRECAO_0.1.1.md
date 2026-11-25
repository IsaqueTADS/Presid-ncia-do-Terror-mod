# Correção 0.1.1 — teste manual

Pedido do usuário: preservar a lógica atual de caminhada e entregar o JAR para avaliação manual. **Não foi aberto o cliente Minecraft nesta revisão.** Build, testes de servidor e prévia geométrica não constituem aprovação audiovisual.

## Alterações

- **500 pontos de vida por conjunto personagem/montaria**, com uma única barra compartilhada. Transformar não cura nem cria outra reserva de vida. Ao carregar entidades antigas com máximo de 80, o máximo passa a 500 preservando os pontos de dano já recebidos: 79/80 vira 499/500.
- Bazuca do Bolsonaro presa ao ponto do cabo na mão: alcançar, pegar, sacar, mirar e guardar usam a mesma transformação, com muzzle para a frente do personagem. O prop herda a transformação do corpo, inclusive no descanso. Não foram sobrescritas as fontes aprovadas ou o rascunho experimental montado.
- Revólver: rastro curto de partículas douradas até o primeiro impacto, interrompido por paredes/entidades; partículas e som no impacto. Continua sendo tiro instantâneo, não projétil balístico com queda. Recoil da câmera distribuído em ticks, além do recuo e retorno da arma. Quantidade de cartuchos no tambor permanece sincronizada.
- Efeitos baixados com licença declarada CC0 integrados: passos humanos, cascos candidatos, passos graves do T-Rex, zurro, rugidos, dano, transformação, movimento, impacto e recarga. São 23 exports mono Vorbis a 22.050 Hz; proveniência, processamento e hashes em `runtime-audio-manifest.json`.
- Memes de vozes com direitos ainda não verificados ficam num **pacote de recursos separado para teste local**, sem entrar no JAR público/Git. O arquivo pronto está em `run/resourcepacks/presidencia-memes-local.zip`. Para ouvi-los no perfil de desenvolvimento, ative esse pacote em Opções → Pacotes de recursos. Em outro perfil, copie o ZIP para a pasta `resourcepacks` dele e ative. Contém falas de Lula/Bolsonaro, inclusive edição musical e palavrão; não há autenticação da voz. Sem o pacote, os efeitos e as montarias continuam funcionando; só esses eventos de fala ficam silenciosos.

## Animações ligadas ao contexto

| Contexto | Clips |
| --- | --- |
| A pé, deslocamento real | `walk` ou `run`, conforme velocidade; `idle` ao parar |
| A pé, pausas de observação/gestos | `look_around`, `startle`, `steal_food` (Lula) ou `protect_phone` (Bolsonaro) |
| Primeiro golpe | `transform_cue`, seguido de `mount_reaction`; vida preservada |
| Montado, deslocamento real | `mounted_walk` ou `mounted_run`; `mount_idle` parado |
| Montado, pausas | `idle`, `look_around`, `mount_look_around`, `mouth_chatter`, `bray`/`roar`, `mount_reaction` |
| Combate | Reação ao dano e alternância de vocalização/movimento de boca, com intervalo de 12 s |
| T-Rex descansando | `lie_down` → `settle_sleep` → `rest_sleep` → `wake_up` → `stand_up` |
| Jumento descansando | `lie_down` → `rest_sleep` → `stand_up` |
| Armas | `idle`, `fire`, `reload`; bazuca do Bolsonaro usa pose calculada separadamente |

Os gestos de comida/telefone são atuação, sem roubo de inventário ou item de telefone funcional. Os clips de locomover dependem de deslocamento; não são forçados com a entidade parada. Vocalizações podem se sobrepor aos canais de marcha para não congelar as pernas. O mapa de cobertura dos clips exportados fica em `animation-routing-0.1.1.json`.

## Descanso e proximidade

Na segunda forma, depois de **10 segundos efetivamente parado**, sem alvo e com o chão sob a entidade, ela pode iniciar o descanso se não houver jogador a até 12 blocos. Não é um temporizador que força dormir durante a perseguição. Caminhada espontânea pode adiar a condição.

Um jogador entrando em **até 10 blocos** faz a montaria acordar e levantar antes de perseguir. Essa é a interpretação adotada do pedido de distância. Um novo golpe/alvo também interrompe o sono com a transição de levantar. A forma continua montada. O T-Rex leva cerca de quatro segundos entre acordar e terminar de levantar; o jumento usa os 2,5 s do clip de levantar.

Navegação, velocidade de caminhada, gravidade e colisão existentes foram preservadas. A única suspensão de navegação acrescentada é durante transformação e descanso/levantada; a meta de passeio também aguarda os gestos parados terminarem.

**Mundo de teste anterior:** exemplares invocados manualmente com `NoAI:1b` permanecem com a IA desativada. Eles não servem para avaliar caminhada. Prefira novos ovos ou remova essa marca no exemplar de teste com `/data merge entity <seletor> {NoAI:0b}`. A correção não ignora comandos intencionais de administrador.

## O que testar manualmente

1. Instalar somente o JAR 0.1.1, substituindo o antigo, em Minecraft 1.21.1 + Fabric API.
2. Invocar com os ovos, conferir 500 de vida, provocar a transformação e avaliar mão, sentido da bazuca, repouso e tiro.
3. Recarregar/disparar revólver, conferir rastro até a parede, recoil e som de impacto.
4. Deixar a forma montada sem alvo e longe dos jogadores. Aguardar parada/sono; aproximar-se a 10 blocos e conferir a sequência de levantar/perseguir.
5. Ouvir passos, transformação, vocalizações e recarga. Ativar o pacote local se quiser avaliar também os memes.

Não houve novo teste visual no cliente, audição final, teste de multiplayer com dois clientes ou alteração dos modelos-base. Esses pontos dependem das considerações do usuário.
