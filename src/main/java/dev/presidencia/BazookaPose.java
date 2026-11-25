package dev.presidencia;
import org.joml.*;
/** Shared authoring coordinates: barrel always points to model -Z when aimed. */
public final class BazookaPose {
 public static final Vector3f GRIP=new Vector3f(0,-6,-4);
 private static float ease(float f){f=java.lang.Math.clamp(f,0,1);return f*f*(3-2*f);}
 public static float armAngle(float progress){return progress<=.3f?-150*ease(progress/.3f):-150-90*ease((progress-.3f)/.7f);}
 public static Matrix4f arm(float progress){return new Matrix4f().translate(-11.5f,65,0).rotateX((float)java.lang.Math.toRadians(armAngle(progress))).translate(11.5f,-65,0);}
 public static Matrix4f weapon(float progress){
  // Before the hand reaches the grip, leave the prop in the back holster.
  float grasp=java.lang.Math.max(.3f,progress),turn=ease((progress-.3f)/.7f);
  Vector3f hand=arm(grasp).transformPosition(new Vector3f(-11.5f,51,0));
  return new Matrix4f().translation(hand).rotateX((float)java.lang.Math.toRadians(90*(1-turn))).scale(.6f).translate(new Vector3f(GRIP).negate());
 }
 private BazookaPose(){}
}
