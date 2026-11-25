# Presidência do Terror

Versão **0.3.1** — Minecraft Java 1.21.1, Fabric e Java 21.

Uma dupla de Lula e Bolsonaro acompanha o jogador pelo mundo e entre dimensões, observa e fala memes. Atacá-los provoca transformação: Lula no jumento e Bolsonaro no T-Rex com bazuca. Cada conjunto tem 500 de vida. Os personagens podem descansar perto do jogador e recuperam a proximidade por teleporte, com intervalo de 30 s. A dupla é compartilhada pelo servidor, persistente e não reaparece depois de derrotada; ovos continuam disponíveis para testes manuais.

Há uma aba criativa própria com armas, munições, cerveja, álcool e ovos. Revólver: corpo 12, headshot 17; sexto disparo de tambor completo aumenta 50%, exceto último HS no Lula, que causa 22. Bazuca: explosão até 12, com destruição de blocos configurável. Números flutuantes mostram a vida realmente retirada. Botão direito dispara/bebe, R recarrega, N alterna o dia do mundo. Teclas configuráveis. Armas começam vazias; receitas e previews estão no repositório irmão visual-assets.

Lula deixa Picanha (320 carnes cozidas renomeadas) e 128 cervejas; Bolsonaro deixa bazuca e 64 foguetes. Derrotar ambos da dupla automática concede Fim do mandato. Administradores podem usar /presidencia reset para reiniciar. Configuração do servidor em config/presidencia-terror.json.

Instale Fabric Loader e Fabric API para 1.21.1 e substitua o JAR anterior por build/libs/presidencia-do-terror-0.3.1.jar em mods, tanto no cliente quanto no servidor. Não usar o JAR -sources. Memes só tocam com o pacote local opcional presidencia-memes-local.zip ativo em cada cliente; efeitos licenciados estão no JAR. As falas pendentes de licença não são distribuídas publicamente.

Leia [docs/V030.md](docs/V030.md) para regras, configuração e testes manuais. Build e 21 GameTests de servidor passaram; não houve teste novo no cliente Minecraft nem sessão prolongada de multiplayer/chunks reais descarregadas. Modelos e animações aprovados foram preservados.

Para retomar o trabalho, leia [AGENTS.md](AGENTS.md) e [docs/CONTEXTO.md](docs/CONTEXTO.md). Mod e visual-assets têm históricos Git independentes. Use o Gradle Wrapper com JDK 21: gradlew.bat build e gradlew.bat runGametest. Build não regenera assets.


0.3.1: início automático no dia 1, armadura de terra craftável, cerveja com náusea, álcool no livro de receitas e falas mais frequentes. Consulte [V031.md](docs/V031.md). Copie o pacote local presidencia-memes-local.zip para resourcepacks em cada cliente: será ativado automaticamente na inicialização.
