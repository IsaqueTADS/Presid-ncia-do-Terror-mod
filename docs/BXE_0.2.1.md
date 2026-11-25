# Identidade BXE Enterprise — 0.2.1

Execução autorizada pelo usuário. Nome adotado: BXE Enterprise, conforme a abreviação principal de Baixa Energia.

Pilha com rosto de galinha, crista vermelha, corpo verde e carga amarela; selo BXE sobre grafite. Fontes editáveis em visual-assets/branding/bxe-enterprise/badge.svg, símbolo 16×20 e selo 64×32 PNG (caminhos a partir da pasta do projeto). Pixel art criada diretamente para texturas.

Dois selos laterais no tubo da bazuca e no cabo do revólver, presos ao osso weapon; rótulo da cerveja com símbolo, BXE e Enterprise. Inclui as garrafas das duas variantes do Lula. Textos pequenos são detalhe; símbolo e BXE são a identificação principal. O álcool ainda compartilha a aparência da garrafa conforme 0.2.0.

Geometria-base, clips, receitas e gameplay preservados. Itens estáticos conferidos no Blockbench, corrigindo a orientação do selo da bazuca. Nenhuma nova execução do cliente Minecraft. Previews em visual-assets/previews/*-bxe-0.2.1.png e bxe-enterprise-0.2.1.png. Exports OBJ/MTL antigos são históricos e não mostram decals; fontes vigentes são bbmodel e export de runtime.

Ordem de ferramentas de assets: apply_bxe_branding.py, export_v1.py, prepare_v020.py. O primeiro é reaplicável sem duplicar decals. Se regenerar fontes com geradores antigos, reaplicar a marca. Build não regenera assets. Memes continuam no pacote local e o corte bolsonaro_death foi aprovado pelo usuário.

Verificação: cinco fontes comparadas à revisão anterior, preservando elementos-base e animações; UVs e referências de faces válidos. Build Fabric 1.21.1 para empacotar a mudança visual. Leitura final no Minecraft depende do teste manual do usuário.
