# Presidência do Terror

Para retomar o trabalho em outro chat, leia [AGENTS.md](AGENTS.md) e [docs/CONTEXTO.md](docs/CONTEXTO.md). O contexto registra decisões, estado real e pendências, com referências ao repositório de assets.

Versão jogável **0.2.0**, para Minecraft Java 1.21.1, Fabric e Java 21. Nome escolhido pelo usuário em 25/11/2026.

Novo na 0.1.2: Lula bebe cerveja e balança o tronco em um evento ocasional, nas versões a pé e montada. Som vanilla de beber, 4 s de gole + 5 s de atuação cômica. Não começa durante combate; o evento não altera a vida ou a velocidade. Na 0.2.0 a garrafa também é item craftável; consulte docs/V020.md.

Base da correção anterior: 500 de vida compartilhada por conjunto, bazuca presa à mão, animações contextuais e descanso, rastro/recoil do revólver e 23 efeitos sonoros integrados. Leia [CORRECAO_0.1.1.md](docs/CORRECAO_0.1.1.md) antes do teste manual, incluindo como ativar o pacote local de memes. A caminhada foi preservada e o cliente não foi aberto nesta revisão.

Inclui revólver, bazuca, munições craftáveis e dois ovos. Ambos os personagens começam a pé e, ao sofrer dano de um agressor vivo, passam à forma montada e perseguem esse agressor. Lula ataca de perto; Bolsonaro usa a bazuca com 15 segundos entre disparos e aviso sonoro. Sem spawn natural. As explosões destroem blocos.

Use botão direito para disparar e **R** para recarregar. Armas são criadas vazias; munição deve estar no inventário. Revólver: seis cartuchos, 2,8 s de recarga. Bazuca: um foguete, 2,4 s. Posições carregadas do tambor ficam amarelas; uma carga parcial continua parcial durante a recarga e só recebe os cartuchos disponíveis ao concluir. Trocar de slot, largar a arma, morrer ou desconectar cancela a recarga sem consumir munição. No criativo, a recarga é gratuita e o tiro não gasta a carga.

Instale Fabric Loader e Fabric API compatíveis com 1.21.1, e copie `build/libs/presidencia-do-terror-0.2.0.jar` para `mods` (cliente e servidor), substituindo o JAR anterior. Não usar o JAR `-sources`. Não exige GeckoLib: o renderer próprio preserva cubos e malhas arredondadas dos modelos.

Leia [TESTAR_V1.md](docs/TESTAR_V1.md) para instalação, comandos, balanceamento, receitas e limites de verificação. As imagens das quatro receitas ficam em `../visual-assets/previews/craft-*.png` e na prancha `crafts-v1.png`.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
.\gradlew.bat build
.\gradlew.bat runGametest
.\gradlew.bat runClient
```

Sete GameTests passaram em servidor dedicado de teste. O cliente foi aberto e houve conferência de renderização do revólver e da transformação de Bolsonaro. A avaliação prolongada de balanceamento, multiplayer com dois clientes, enquadramento de todas as animações e sensação de recoil continua para a revisão do usuário. Não foram empacotados os memes com direitos pendentes.


## Atualização 0.2.0

Garrafa corrigida, vozes contextuais no pacote local opcional e feedback de jogo integrado. Consulte [V020.md](docs/V020.md). Os recortes de telefone são provisórios e dependem de audição; teste no Minecraft a cargo do usuário.
