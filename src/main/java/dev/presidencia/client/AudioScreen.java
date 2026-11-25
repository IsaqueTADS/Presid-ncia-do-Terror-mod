package dev.presidencia.client;
import net.minecraft.client.gui.screen.Screen;import net.minecraft.client.gui.DrawContext;import net.minecraft.client.gui.widget.*;import net.minecraft.text.Text;
public final class AudioScreen extends Screen {
 public AudioScreen(){super(Text.literal("Áudio — Presidência do Terror"));}
 @Override protected void init(){int x=width/2-130,y=Math.max(32,height/2-100);String[] labels={"Geral do mod","Falas e memes","Montarias e personagens","Armas"};for(int i=0;i<4;i++){final int index=i;var c=AudioSettings.get();double v=i==0?c.general:i==1?c.voices:i==2?c.mounts:c.weapons;addDrawableChild(new SliderWidget(x,y+i*26,260,20,Text.empty(),v){protected void updateMessage(){setMessage(Text.literal(labels[index]+": "+Math.round(value*100)+"%"));}protected void applyValue(){var c=AudioSettings.get();if(index==0)c.general=(float)value;else if(index==1)c.voices=(float)value;else if(index==2)c.mounts=(float)value;else c.weapons=(float)value;} {updateMessage();}});}
 for(int i=0;i<3;i++){final int channel=i;String label=i==0?"Testar fala":i==1?"Testar montaria":"Testar arma";addDrawableChild(ButtonWidget.builder(Text.literal(label),b->ClientAudio.test(channel)).dimensions(x+i*88,y+108,84,20).build());}
 addDrawableChild(ButtonWidget.builder(Text.literal("Restaurar"),b->{AudioSettings.reset();clearAndInit();}).dimensions(x,y+136,125,20).build());addDrawableChild(ButtonWidget.builder(Text.literal("Concluir"),b->close()).dimensions(x+135,y+136,125,20).build());}
 @Override public void render(DrawContext d,int mouseX,int mouseY,float delta){renderBackground(d,mouseX,mouseY,delta);super.render(d,mouseX,mouseY,delta);d.drawCenteredTextWithShadow(textRenderer,title,width/2,12,0xffffff);d.drawCenteredTextWithShadow(textRenderer,"Respeita o volume mestre do Minecraft",width/2,height-20,0xbfc8c0);}
 @Override public void close(){AudioSettings.save();ClientAudio.stopTests();client.setScreen(null);}
 @Override public boolean shouldPause(){return false;}
}
