# Presidência do Terror

Para retomar o trabalho em outro chat, leia [AGENTS.md](AGENTS.md) e [docs/CONTEXTO.md](docs/CONTEXTO.md). O contexto registra decisões, estado real e pendências, com referências ao repositório de assets.

Primeira versão jogável **0.1.0**, para Minecraft Java 1.21.1, Fabric e Java 21. Nome escolhido pelo usuário em 25/11/2026.

Inclui revólver, bazuca, munições craftáveis e dois ovos. Ambos os personagens começam a pé e, ao sofrer dano de um agressor vivo, passam à forma montada e perseguem esse agressor. Lula ataca de perto; Bolsonaro usa a bazuca com 15 segundos entre disparos e aviso sonoro. Sem spawn natural. As explosões destroem blocos.

Use botão direito para disparar e **R** para recarregar. Armas são criadas vazias; munição deve estar no inventário. Revólver: seis cartuchos, 2,8 s de recarga. Bazuca: um foguete, 2,4 s. Posições carregadas do tambor ficam amarelas; uma carga parcial continua parcial durante a recarga e só recebe os cartuchos disponíveis ao concluir. Trocar de slot, largar a arma, morrer ou desconectar cancela a recarga sem consumir munição. No criativo, a recarga é gratuita e o tiro não gasta a carga.

Instale Fabric Loader e Fabric API compatíveis com 1.21.1, e copie `build/libs/presidencia-do-terror-0.1.0.jar` para `mods` (cliente e servidor). Não usar o JAR `-sources`. Não exige GeckoLib: o renderer próprio preserva cubos e malhas arredondadas dos modelos.

Leia [TESTAR_V1.md](docs/TESTAR_V1.md) para instalação, comandos, balanceamento, receitas e limites de verificação. As imagens das quatro receitas ficam em `../visual-assets/previews/craft-*.png` e na prancha `crafts-v1.png`.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
.\gradlew.bat build
.\gradlew.bat runGametest
.\gradlew.bat runClient
```

Sete GameTests passaram em servidor dedicado de teste. O cliente foi aberto e houve conferência de renderização do revólver e da transformação de Bolsonaro. A avaliação prolongada de balanceamento, multiplayer com dois clientes, enquadramento de todas as animações e sensação de recoil continua para a revisão do usuário. Não foram empacotados os memes com direitos pendentes.
