# Testar Presidência do Terror 0.1.0

Minecraft Java 1.21.1, Java 21, Fabric Loader 0.16.14 ou compatível e Fabric API 0.116.7+1.21.1. Mod obrigatório no cliente e servidor. Fazer os primeiros testes num mundo separado: os foguetes **destroem blocos**.

## Itens e comandos

As armas e munições estão na aba Combate do criativo; os dois ovos ficam em Ovos de invocação. Com comandos habilitados:

```mcfunction
/give @s presidencia_terror:revolver
/give @s presidencia_terror:municao_revolver 64
/give @s presidencia_terror:bazuca
/give @s presidencia_terror:foguete_bazuca 16
/give @s presidencia_terror:lula_spawn_egg
/give @s presidencia_terror:bolsonaro_spawn_egg
```

Botão direito dispara. R solicita recarga da arma na mão principal. Apertar disparo com a arma vazia também inicia recarga, quando há munição. Armas craftadas e fornecidas sem componentes começam vazias. No criativo, recarga gratuita; depois de carregar, os tiros não consomem cartuchos.

O tambor mostra a carga real em amarelo, inclusive na pose aberta de recarga. Durante a animação, conserva a munição antiga; a nova quantidade entra ao completar a ação, sem fingir seis cartuchos numa recarga parcial. Cartuchos visuais não são entidades soltas. A bazuca mostra o foguete durante o voo; ainda não há mãos inserindo munição nos itens.

## Valores implementados para o primeiro teste

- Personagens: 80 pontos de vida; primeiro golpe válido de um ser vivo transforma sem curar ou duplicar a entidade. Dois ovos bastam para acessar as quatro formas.
- Após perder o alvo por distância superior a 80 blocos, morte ou falta de visão por 10 s, encerram a perseguição e permanecem montados. Não há provocação automática de todos os jogadores. Novos agressores válidos podem assumir o alvo.
- Ataque próximo montado: 6 pontos, intervalo de 1,5 s. Ambos possuem ataque próximo; Bolsonaro também usa foguetes.
- Revólver: seis cartuchos, 6 pontos por acerto, 0,4 s entre tiros, alcance de 64 blocos; paredes bloqueiam o tiro. Recarga: 2,8 s.
- Bazuca: um foguete, recarga de 2,4 s. Projétil com velocidade de 1,2 blocos/tick e duração máxima de 100 ticks. Explosão de força 2, sem fogo, dano limitado a 12 pontos antes das reduções, com queda por distância/cobertura. Sem aplicar um segundo dano direto além da explosão.
- Bolsonaro: primeiro lançamento após 15 s de combate; tiros posteriores separados por pelo menos 15 s e condicionados à linha de visão. Preparação visual e aviso nos dois segundos finais. A arma é um prop separado do modelo original, com sacar, recuo e retorno ao repouso calculados no renderer.
- Efeitos usam eventos de som do Minecraft instalado. Memes não verificados permanecem fora do JAR. Roubo de comida e spawn natural não entram nesta versão.

Os valores são escolhas iniciais de implementação após a autorização para executar o planejamento; ainda dependem de avaliação de dificuldade e sensação de jogo. Proteções de áreas, regras de servidor e armadura podem alterar o resultado de explosões e danos.

## Receitas

Consulte `visual-assets/previews/craft-revolver.png`, `craft-bazuca.png`, `craft-municao_revolver.png`, `craft-foguete_bazuca.png` e `crafts-v1.png`. São geradas junto com os JSONs de receita; saídas: uma arma, seis cartuchos ou dois foguetes. As receitas podem ser deslocadas na grade quando houver espaço.

## Recursos e validação

O exporter `visual-assets/tools/export_v1.py` grava explicitamente os recursos no mod e registra revisão/hashes em `assets-manifest.json`. A build não altera o repositório de assets. O renderer lê o export de hierarquia e keyframes e mantém as malhas de 32 lados das armas, texturas, transformações e convenção legada de eixos. A variante experimental `.bbmodel` montada com bazuca não é sobrescrita nem exigida pelo runtime.

Sete GameTests passaram: transformação e persistência sem cura, recarga parcial, cancelamento, gasto de um cartucho/cooldown, parede bloqueando revólver, foguete colidindo e destruindo vidro, intervalo de 300 ticks antes do primeiro foguete. O teste de recarga parcial verifica o tempo agendado e simula o instante de conclusão para avaliar o consumo. Um teste anterior com conexão simulada passou mas travou o encerramento de chunks do harness; foi substituído pelo teste isolado sem conexão artificial, e o servidor passou a encerrar normalmente.

Foi aberto um cliente de desenvolvimento e um mundo plano separado para conferir revólver, HUD de munição e a transformação de Bolsonaro para T-Rex. Após corrigir a orientação da arma em primeira pessoa, a revisão revelou a necessidade de indicadores também na face traseira do tambor. Foram adicionadas seis posições traseiras e confirmados no cliente final: um ponto amarelo durante recarga de 1/6, carga completa ao concluir, arma apontada para frente e Lula montado no jumento. O jogo de teste ficou pausado para revisão do usuário. Isso não valida multiplayer de dois clientes nem todas as poses de mãos, animações e efeitos. Revisar especialmente o encaixe da bazuca durante corrida e tiro e o balanceamento das explosões. Build e testes não substituem essa revisão visual.

APIs conferidas na documentação oficial: [renderização dinâmica de itens](https://maven.fabricmc.net/docs/fabric-api-0.116.7+1.21.1/net/fabricmc/fabric/api/client/rendering/v1/BuiltinItemRendererRegistry.html) e [projéteis explosivos](https://maven.fabricmc.net/docs/yarn-1.21.1+build.3/net/minecraft/entity/projectile/ExplosiveProjectileEntity.html).
