# Mod Presidência do Terror

- Alvo definido: Minecraft Java 1.21.1, Fabric e Java 21.
- Ao iniciar ou retomar um chat, leia [docs/CONTEXTO.md](docs/CONTEXTO.md). Ele contém o conceito, o estado real, os pedidos e as pendências. Para trabalho audiovisual, leia também [o contexto de assets](../visual-assets/docs/CONTEXTO.md) quando o checkout irmão estiver disponível.
- Histórico 0.1.2: atuação de beber/balançar do Lula adicionada; fontes e prévia em `../visual-assets/docs/LULA_CERVEJA.md`. O restante da lista de feedback segue pendente. Base da correção 0.1.1: leia [docs/CORRECAO_0.1.1.md](docs/CORRECAO_0.1.1.md). O usuário fará o teste manual e pediu preservar a caminhada; não abrir o cliente nem alterar navegação/velocidade para tentar antecipar sua avaliação. Vida compartilhada de 500, animações contextuais/sono, efeitos sonoros, pose mão/bazuca e rastro/recoil estão implementados; não confundir build/testes de servidor com aprovação audiovisual. [docs/TESTAR_V1.md](docs/TESTAR_V1.md) e [docs/V1.md](docs/V1.md) conservam a base anterior.
- Quando começar a implementação, mantenha gameplay no servidor e cliente audiovisual separado. Integre somente exports revisados, com manifesto de revisão e hashes.
- Git independente de ../visual-assets. Conventional Commits em inglês; não faça push sem autorização.
- Sempre comite as mudanças concluídas deste repositório. A mesma regra vale no Git separado de assets; não crie commits vazios.
- O usuário autorizou push direto dos commits de mudanças solicitadas para os remotos GitHub configurados, sem nova confirmação a cada atualização.

## Manutenção do contexto

- Este repositório e `../visual-assets` têm históricos independentes. Não criar repositório nem arquivo de contexto na pasta que agrupa os dois. O contexto deve permanecer versionado dentro de cada repositório.
- A cada nova decisão ou pedido relevante do usuário, atualize `docs/CONTEXTO.md` durante o trabalho. Separe pedido confirmado, proposta, implementação e verificação; mantenha próximos passos e pontos em aberto atuais, sem depender do histórico do chat.
- Quando uma decisão afetar código e assets, atualize também `../visual-assets/docs/CONTEXTO.md` e as instruções locais pertinentes. Se o outro checkout não estiver disponível, registre a sincronização pendente; não declare ambos atualizados.
- Ao interromper uma etapa, registre o que existe localmente, o que foi comitado/publicado e o que falta verificar. Confira Git e arquivos ao retomar; o resumo não substitui o estado real.
- Preserve rascunhos e edições fora do escopo. Faça stage explícito apenas dos arquivos da tarefa concluída; não publique assets inacabados junto com uma atualização de documentação.

- Versão vigente 0.2.0: ler docs/V020.md e a seção final de docs/CONTEXTO.md; feedback anterior integrado, recorte de voz de morte aprovado pelo usuário. Teste manual a cargo do usuário.

- Versão 0.2.1: identidade BXE Enterprise implementada após autorização. Ler docs/BXE_0.2.1.md; planejamento anterior é histórico.

- Versão atual 0.2.2: rótulos revisados aprovados pelo usuário; veja docs/CONTEXTO.md.
