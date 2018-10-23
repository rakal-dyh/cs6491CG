import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.nio.*; 
import processing.core.PMatrix3D; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Ceditor extends PApplet {

//  ******************* Basecode for P2 ***********************
Boolean
  animating=true,
  PickedFocus=false,
  center=true,
  track=false,
  showViewer=false,
  showBalls=false,
  showControl=true,
  showCurve=true,
  showPath=true,
  showKeys=true,
  showSkater=false,
  scene1=false,
  solidBalls=false,
  showCorrectedKeys=true,
  showQuads=true,
  showVecs=true,
  showTube=false;
float
  t=0,
  s=0;
int
  f=0, maxf=2*30, level=4, method=5;
String SDA = "angle";
float defectAngle=0;
pts P = new pts(); // polyloop in 3D
pts Q = new pts(); // second polyloop in 3D
pts R = new pts(); // inbetweening polyloop L(P,t,Q);



public void setup() {
  myFace = loadImage("data/pic.jpg");  // load image from file pic.jpg in folder data *** replace that file with your pic of your own face
  textureMode(NORMAL);
  //size(900, 900, P3D); // P3D means that we will do 3D graphics
   // P3D means that we will do 3D graphics
  P.declare(); Q.declare(); R.declare(); // P is a polyloop in 3D: declared in pts
  //P.resetOnCircle(6,100); Q.copyFrom(P); // use this to get started if no model exists on file: move points, save to file, comment this line
  P.loadPts("data/pts");  Q.loadPts("data/pts2"); // loads saved models from file (comment out if they do not exist yet)
  
  frameRate(30);
  }

public void draw() {
  background(255);
  hint(ENABLE_DEPTH_TEST);
  pushMatrix();   // to ensure that we can restore the standard view before writing on the canvas
  setView();  // see pick tab
  showFloor(); // draws dance floor as yellow mat
  doPick(); // sets Of and axes for 3D GUI (see pick Tab)
  P.SETppToIDofVertexWithClosestScreenProjectionTo(Mouse()); // for picking (does not set P.pv)

  R.copyFrom(P);
  for(int i=0; i<level; i++)
    {
    Q.copyFrom(R);
    if(method==5) {Q.subdivideDemoInto(R);}
    //if(method==4) {Q.subdivideQuinticInto(R);}
    //if(method==3) {Q.subdivideCubicInto(R); }
    //if(method==2) {Q.subdivideJarekInto(R); }
    //if(method==1) {Q.subdivideFourPointInto(R);}
    //if(method==0) {Q.subdivideQuadraticInto(R); }
    }
  //R.displaySkater();

  //fill(blue); if(showCurve) Q.drawClosedCurve(3);
  if(showControl) {fill(grey); P.drawClosedCurve(3);}  // draw control polygon
  System.out.println(P);
  fill(yellow,100); P.showPicked();


  //if(animating)
  //  {
  //  f++; // advance frame counter
  //  if (f>maxf) // if end of step
  //    {
  //    P.next();     // advance dv in P to next vertex
 ////     animating=true;
  //    f=0;
  //    }
  //  }
  //t=(1.-cos(PI*f/maxf))/2; //t=(float)f/maxf;

  //if(track) F=_LookAtPt.move(X(t)); // lookAt point tracks point X(t) filtering for smooth camera motion (press'.' to activate)

  popMatrix(); // done with 3D drawing. Restore front view for writing text on canvas
  hint(DISABLE_DEPTH_TEST); // no z-buffer test to ensure that help text is visible
    if(method==4) scribeHeader("Quintic UBS",2);
    if(method==3) scribeHeader("Cubic UBS",2);
    if(method==2) scribeHeader("Jarek J-spline",2);
    if(method==1) scribeHeader("Four Points",2);
    if(method==0) scribeHeader("Quadratic UBS",2);

  // used for demos to show red circle when mouse/key is pressed and what key (disk may be hidden by the 3D model)
  if(mousePressed) {stroke(cyan); strokeWeight(3); noFill(); ellipse(mouseX,mouseY,20,20); strokeWeight(1);}
  if(keyPressed) {stroke(red); fill(white); ellipse(mouseX+14,mouseY+20,26,26); fill(red); text(key,mouseX-5+14,mouseY+4+20); strokeWeight(1); }
  if(scribeText) {fill(black); displayHeader();} // dispalys header on canvas, including my face
  if(scribeText && !filming) displayFooter(); // shows menu at bottom, only if not filming
  if(filming && (animating || change)) saveFrame("FRAMES/F"+nf(frameCounter++,4)+".tif");  // save next frame to make a movie
  change=false; // to avoid capturing frames when nothing happens (change is set uppn action)
  change=true;
  }
public void keyPressed() 
  {
//  if(key=='`') picking=true;
  if(key=='?') scribeText=!scribeText;
  if(key=='!') snapPicture();
  if(key=='~') filming=!filming;
  if(key==']') showBalls=!showBalls;
  if(key=='f') {P.setPicekdLabel(key);}
  if(key=='s') {P.setPicekdLabel(key);}
  if(key=='b') {P.setPicekdLabel(key);}
  if(key=='c') {P.setPicekdLabel(key);}
  if(key=='F') {P.addPt(Of,'f');}
  if(key=='S') {P.addPt(Of,'s');}
  if(key=='B') {P.addPt(Of,'b');}
  if(key=='C') {P.addPt(Of,'c');}
  if(key=='m') {method=(method+1)%5;}
  if(key=='[') {showControl=!showControl;}
  if(key==']') {showQuads=!showQuads;}
  if(key=='{') {showCurve=!showCurve;}
  if(key=='\\') {showKeys=!showKeys;}
  if(key=='}') {showPath=!showPath;}
  if(key=='|') {showCorrectedKeys=!showCorrectedKeys;}
  if(key=='=') {showTube=!showTube;}

  if(key=='3') {P.resetOnCircle(3,300); Q.copyFrom(P);}
  if(key=='4') {P.resetOnCircle(4,400); Q.copyFrom(P);}
  if(key=='5') {P.resetOnCircle(5,500); Q.copyFrom(P);}
  if(key=='^') track=!track;
  if(key=='q') Q.copyFrom(P);
  if(key=='p') P.copyFrom(Q);
  if(key==',') {level=max(level-1,0); f=0;}
  if(key=='.') {level++;f=0;}

  if(key=='e') {R.copyFrom(P); P.copyFrom(Q); Q.copyFrom(R);}
  if(key=='d') {P.set_pv_to_pp(); P.deletePicked();}
  if(key=='i') P.insertClosestProjection(Of); // Inserts new vertex in P that is the closeset projection of O
  if(key=='W') {P.savePts("data/pts"); Q.savePts("data/pts2");}  // save vertices to pts2
  if(key=='L') {P.loadPts("data/pts"); Q.loadPts("data/pts2");}   // loads saved model
  if(key=='w') P.savePts("data/pts");   // save vertices to pts
  if(key=='l') P.loadPts("data/pts");
  if(key=='a') {animating=!animating; P.setFifo();}// toggle animation
  if(key=='^') showVecs=!showVecs;
  if(key=='#') exit();
  if(key=='=') {}
  change=true;   // to save a frame for the movie when user pressed a key
  }

public void mouseWheel(MouseEvent event)
  {
  dz -= event.getAmount();
  change=true;
  }

public void mousePressed()
  {
  //if (!keyPressed) picking=true;
  if (!keyPressed) {P.set_pv_to_pp(); println("picked vertex "+P.pp);}
  if(keyPressed && key=='a') {P.addPt(Of);}
//  if(keyPressed && (key=='f' || key=='s' || key=='b' || key=='c')) {P.addPt(Of,key);}

 // if (!keyPressed) P.setPicked();
  change=true;
  }

public void mouseMoved()
  {
  //if (!keyPressed)
  if (keyPressed && key==' ') {rx-=PI*(mouseY-pmouseY)/height; ry+=PI*(mouseX-pmouseX)/width;};
  if (keyPressed && key=='`') dz+=(float)(mouseY-pmouseY); // approach view (same as wheel)
  change=true;
  }

public void mouseDragged()
  {
  if (!keyPressed) P.setPickedTo(Of);
//  if (!keyPressed) {Of.add(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); }
  if (keyPressed && key==CODED && keyCode==SHIFT) {Of.add(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));};
  if (keyPressed && key=='x') P.movePicked(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
  if (keyPressed && key=='z') P.movePicked(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
  if (keyPressed && key=='X') P.moveAll(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
  if (keyPressed && key=='Z') P.moveAll(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
  if (keyPressed && key=='t')  // move focus point on plane
    {
    if(center) F.sub(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
    else F.add(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
    }
  if (keyPressed && key=='T')  // move focus point vertically
    {
    if(center) F.sub(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
    else F.add(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));
    }
  change=true;
  }

// **** Header, footer, help text on canvas
public void displayHeader()  // Displays title and authors face on screen
    {
    scribeHeader(title,0); scribeHeaderRight(name);
    fill(white); image(myFace, width-myFace.width/2,25,myFace.width/2,myFace.height/2);
    }
public void displayFooter()  // Displays help text at the bottom
    {
    scribeFooter(guide,1);
    scribeFooter(menu,0);
    }

String title ="3D curve editor", name ="Jarek Rossignac",
       menu="?:help, !:picture, ~:(start/stop)capture, space:rotate, `/wheel:closer, t/T:target, a:anim, #:quit",
       guide="click&drag:pick&slide on floor, xz/XZ:move/ALL, e:exchange, q/p:copy, l/L:load, w/W:write, m:subdivide method"; // user's guide
//Given two point A,B, and the direction Va and Vb at those two points, calculate the distance
//that satisfy AA'=d, BB'=d, A'B'=2d, where A' and B' are two points that A'=A+d*Va, B'=B+d*Vb

// class findCircleCenter{
//   pt O;
//   boolean isLine;
//   float r;
//
//   findCircleCenter(vec vna,vec vnc,pt A,pt C,pt AA,float d){
//     float xav=vna.x;
//     float yav=vna.y;
//     float zav=vna.z;
//     float xa=A.x;
//     float ya=A.y;
//     float za=A.z;
//
//     float xcv=vnc.x;
//     float ycv=vnc.y;
//     float zcv=vnc.z;
//     float xc=C.x;
//     float yc=C.y;
//     float zx=C.z;
//     float test=abs(xcv)-abs(xav)+abs(ycv)-abs(yav)+abs(zcv)-abs(zav);
//     if (test<0.0001){
//       this.isLine=true;
//     }
//     else{
//       this.isLine=false;
//     }
//     this.r=0;
//     this.O=P();
//     if (!isLine){
//       float theta=angle(V(AA,A),V(AA,C))/(2.0);
//       this.r=d*tan(theta);
//       this.O=P(A,r,vna);
//     }
//   }
// }


class InterpolationWithTwoDMethod{
  pt A;
  pt B;
  vec va;
  vec vb;
  float d;
  pt C;
  vec vc;
  pt O1;
  pt O2;
  boolean isLine1;
  boolean isLine2;

  InterpolationWithTwoDMethod(pt A,pt B,vec va,vec vb){
    this.A=A;
    this.B=B;
    this.va=U(va);
    this.vb=U(vb);
    this.vb.x=-this.vb.x;
    this.vb.y=-this.vb.y;
    this.vb.z=-this.vb.z;
    calculate();
  }
  public void calculate(){
    float a;
    float b;
    float c;
    //a=(va.x-vb.x)^2+(va.y-vb.y)^2+(va.z-vb.z)^2-4;
    //b=2*((va.x-vb.x)*(A.x-B.x)+(va.y-vb.y)*(A.y-B.y)+(va.z-vb.z)*(A.z-B.z));
    //c=(A.x-B.x)^2+(A.y-B.y)^2+(A.z-B.z)^2;

    a=(va.x-vb.x)*(va.x-vb.x)+(va.y-vb.y)*(va.y-vb.y)+(va.z-vb.z)*(va.z-vb.z)-4;
    b=2*((va.x-vb.x)*(A.x-B.x)+(va.y-vb.y)*(A.y-B.y)+(va.z-vb.z)*(A.z-B.z));
    c=(A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y)+(A.z-B.z)*(A.z-B.z);


    //d=(-b+(b*b-4*a*c)^0.5)/(2*a);
    d=(-b+sqrt(b*b-4*a*c))/(2*a);
    pt AA=P(A,V(d,va));
    pt BB=P(B,V(d,vb));
    vc=U(V(AA,BB));
    C=P(AA,0.5f,BB);
    vec vna=B(va,vc);//norm to va in va,vc plain
    vec vnb=B(vb,vc);
    vec vnc1=B(vc,va);
    vec vnc2=B(vc,vb);

    vec vac=V(A,C);
    vec vca=V(C,A);
    vec vbc=V(B,C);
    vec vcb=V(C,B);

    //check the angle between vac and vna, to make it <90
    float angle_ac=angle(vac,vna);
    if (angle_ac>PI){
      vna=V(-1,vna);
    }
    //same with vca and vnc1
    float angle_ca=angle(vca,vnc1);
    if (angle_ca>PI){
      vnc1=V(-1,vnc1);
    }
    //check the angle between vbc and vnb, to make it <90
    float angle_bc=angle(vbc,vnb);
    if (angle_bc>PI){
      vnb=V(-1,vnb);
    }
    //same with vcb and vnc2
    float angle_cb=angle(vcb,vnc2);
    if (angle_cb>PI){
      vnc2=V(-1,vnc2);
    }

    findCircleCenter f1=new findCircleCenter(vna,vnc1,A,C,AA,d);
    findCircleCenter f2=new findCircleCenter(vnb,vnc2,B,C,BB,d);
    this.O1=f1.O;
    this.O2=f2.O;
    this.isLine1=f1.isLine;
    this.isLine2=f2.isLine;
    //calculate O1
    // xav=vna.x;
    // yav=vna.y;
    // zav=vna.z;
    // xa=A.x;
    // ya=A.y;
    // za=A.z;
    //
    // xcv=vnc1.x;
    // ycv=vnc1.y;
    // zcv=vnc1.z;
    // xc=C.x;
    // yc=C.y;
    // zx=C.z;
    // float test=abs(xcv)-abs(xav)+abs(ycv)-abs(yav)+abs(zcv)-abs(zav);
    // if (test<0.0001){
    //   isLine=true
    // }
    // else{
    //   isLine=false;
    // }
    // O1=P();
    //
    // if (!isLine){
    //
    // }

    //calculate O2
  }

}

class findCircleCenter{
  pt O;
  boolean isLine;
  float r;

  findCircleCenter(vec vna,vec vnc,pt A,pt C,pt AA,float d){
    float xav=vna.x;
    float yav=vna.y;
    float zav=vna.z;
    float xa=A.x;
    float ya=A.y;
    float za=A.z;

    float xcv=vnc.x;
    float ycv=vnc.y;
    float zcv=vnc.z;
    float xc=C.x;
    float yc=C.y;
    float zx=C.z;
    float test=abs(xcv)-abs(xav)+abs(ycv)-abs(yav)+abs(zcv)-abs(zav);
    if (test<0.0001f){
      this.isLine=true;
    }
    else{
      this.isLine=false;
    }
    this.r=0;
    this.O=P();
    if (!isLine){
      float theta=angle(V(AA,A),V(AA,C))/(2.0f);
      this.r=d*tan(theta);
      this.O=P(A,r,vna);
    }
  }
}

// class findCircleCenter{
//   pt O;
//   boolean isLine;
//   float r;
//
//   findCircleCenter(vec vna,vec vnc,pt A,pt C,pt AA,float d){
//     xav=vna.x;
//     yav=vna.y;
//     zav=vna.z;
//     xa=A.x;
//     ya=A.y;
//     za=A.z;
//
//     xcv=vnc.x;
//     ycv=vnc.y;
//     zcv=vnc.z;
//     xc=C.x;
//     yc=C.y;
//     zx=C.z;
//     float test=abs(xcv)-abs(xav)+abs(ycv)-abs(yav)+abs(zcv)-abs(zav);
//     if (test<0.0001){
//       this.isLine=true;
//     }
//     else{
//       this.isLine=false;
//     }
//     this.r=0;
//     this.O=P();
//     if (!isLine){
//       float theta=angle(V(AA,A),V(AA,C))/(2.0);
//       this.r=d*tan(theta);
//       this.O=P(A,r,vna);
//     }
//   }
// }
// suppport of 3D picking and dragging in user's (i.e., screen) coordinate system



pt PP=P(); // picked point
Boolean  picking=false;

float dz=0;                                 // distance to camera. Manipulated with wheel or when 's' is pressed and mouse moved
float rx=-0.06f*TWO_PI, ry=-0.04f*TWO_PI;     // view angles manipulated when space bar (but not mouse) is pressed and mouse is moved
pt Viewer = P(); // location of the viewpoint
pt F = P(0,0,0);  // focus point:  the camera is looking at it (moved when 'f or 'F' are pressed
pt Of=P(100,100,0), Ofp=P(100,100,0); // current and previous point on the floor under the mouse
boolean viewpoint=false;  // set to show frozen viewpoint and frustum from a different angle

//*********** TOOLS FOR 3D PICK ******************
public pt ToScreen(pt P) {return P(screenX(P.x,P.y,P.z),screenY(P.x,P.y,P.z),0);}  // O+xI+yJ+kZ
public pt ToModel(pt P) {return P(modelX(P.x,P.y,P.z),modelY(P.x,P.y,P.z),modelZ(P.x,P.y,P.z));}  // O+xI+yJ+kZ

vec I=V(1,0,0), J=V(0,1,0), K=V(0,0,1); // screen projetions of global model frame

public void computeProjectedVectors() { 
  pt O = ToScreen(P(0,0,0));
  pt A = ToScreen(P(1,0,0));
  pt B = ToScreen(P(0,1,0));
  pt C = ToScreen(P(0,0,1));
  I=V(O,A);
  J=V(O,B);
  K=V(O,C);
  }

public vec ToIJ(vec V) {
 float x = det2(V,J) / det2(I,J);
 float y = det2(V,I) / det2(J,I);
 return V(x,y,0);
 }
 
public vec ToK(vec V) {
 float z = dot(V,K) / dot(K,K);
 return V(0,0,z);
 }
 
 
public pt pick(int mX, int mY) { // returns point on visible surface at pixel (mX,My)
  PGL pgl = beginPGL();
  FloatBuffer depthBuffer = ByteBuffer.allocateDirect(1 << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
  pgl.readPixels(mX, height - mY - 1, 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT, depthBuffer);
  float depthValue = depthBuffer.get(0);
  depthBuffer.clear();
  endPGL();

  //get 3d matrices
  PGraphics3D p3d = (PGraphics3D)g;
  PMatrix3D proj = p3d.projection.get();
  PMatrix3D modelView = p3d.modelview.get();
  PMatrix3D modelViewProjInv = proj; modelViewProjInv.apply( modelView ); modelViewProjInv.invert();
  
  float[] viewport = {0, 0, p3d.width, p3d.height};
  float[] normalized = new float[4];
  normalized[0] = ((mX - viewport[0]) / viewport[2]) * 2.0f - 1.0f;
  normalized[1] = ((height - mY - viewport[1]) / viewport[3]) * 2.0f - 1.0f;
  normalized[2] = depthValue * 2.0f - 1.0f;
  normalized[3] = 1.0f;
  
  float[] unprojected = new float[4];
  
  modelViewProjInv.mult( normalized, unprojected );
  return P( unprojected[0]/unprojected[3], unprojected[1]/unprojected[3], unprojected[2]/unprojected[3] );
  }

public pt pick(float mX, float mY, float mZ) { 
  PGraphics3D p3d = (PGraphics3D)g;
  PMatrix3D proj = p3d.projection.get();
  PMatrix3D modelView = p3d.modelview.get();
  PMatrix3D modelViewProjInv = proj; modelViewProjInv.apply( modelView ); modelViewProjInv.invert();
  float[] viewport = {0, 0, p3d.width, p3d.height};
  float[] normalized = new float[4];
  normalized[0] = ((mX - viewport[0]) / viewport[2]) * 2.0f - 1.0f;
  normalized[1] = ((height - mY - viewport[1]) / viewport[3]) * 2.0f - 1.0f;
  normalized[2] = mZ * 2.0f - 1.0f;
  normalized[3] = 1.0f;
  float[] unprojected = new float[4];
  modelViewProjInv.mult( normalized, unprojected );
  return P( unprojected[0]/unprojected[3], unprojected[1]/unprojected[3], unprojected[2]/unprojected[3] );
  }
  
  public void setView() 
    {
    float fov = PI/3.0f;
    float cameraZ = (height/2.0f) / tan(fov/2.0f);
    camera(0,0,cameraZ,0,0,0,0,1,0  );       // sets a standard perspective
    perspective(fov, 1.0f, 0.1f, 10000);
    
    translate(0,0,dz); // puts origin of model at screen center and moves forward/away by dz
    lights();  // turns on view-dependent lighting
    rotateX(rx); rotateY(ry); // rotates the model around the new origin (center of screen)
    rotateX(PI/2); // rotates frame around X to make X and Y basis vectors parallel to the floor
    if(center) translate(-F.x,-F.y,-F.z);  // center the focus on F
    
    noStroke(); // if you use stroke, the weight (width) of it will be scaled with you scaleing factor
    //showFrame(50); // show the vectors of the main coordinate system: X-red, Y-green, Z-blue arrows
    }
 
    
  public void doPick()
    {
    Ofp.setTo(Of); // places Of on the floor under the mouse  
    Of = pick( mouseX, mouseY);  
   if(mousePressed&&!keyPressed) // when mouse is pressed (but no key), show red ball at surface point under the mouse (and its shadow)
     {
     PickedFocus=false;  
     fill(magenta); show(Of,3);  fill(grey); show(Ofp,3); 
     }  
    // Locks focus on point Of until Of is reset (mouse pressed, but no key)
    if(PickedFocus) F=P(Of); 
    // show focus point F and its shadow
    fill(magenta); show(F,4); // magenta focus point (stays at center of screen)
    fill(magenta,100); showShadow(F,5); // magenta translucent shadow of focus point (after moving it up with 'F'
    computeProjectedVectors(); // computes screen projections I, J, K of basis vectors (see bottom of pv3D): used for dragging in viewer's frame    

    //P.setIdOfVertexWithClosestScreenProjectionTo2(Mouse()); // ID of vertex of P with closest screen projection to mouse (us in keyPressed 'x'...
    }

public pt viewPoint() {return pick( 0,0, (height/2) / tan(PI/6));}
/*  
in draw, before popMatrix, insert
      
    if(picking) {PP = pick( mouseX, mouseY ); picking=false;} else {fill(yellow); show(PP,3);}

in keyPressed, 

      if(key=='`') picking=true; 

*/
// **************************** DISPLAY PRIMITIVES (spheres, cylinders, cones, arrows) from 3D Pts, Vecs *********
// procedures for showing balls and lines are in tab "pv3D" (for example "show(P,r)")

public void sphere(pt P, float r) {pushMatrix(); translate(P.x,P.y,P.z); sphere(r); popMatrix();}; // render sphere of radius r and center P (same as show(P,r))

// **************************** PRIMITIVE FROM POINT, VECTOR, RADIUS PARAMETER
public void caplet(pt A, float a, pt B, float b) { // cone section surface that is tangent to Sphere(A,a) and to Sphere(B,b)
  vec I = U(A,B);
  float d = d(A,B), s=b/a;
  float x=(a-b)*a/d, y = sqrt(sq(a)-sq(x));
  pt PA = P(A,x,I), PB = P(B,s*x,I); 
  coneSection(PA,PB,y,y*s);
  }  

public void coneSection(pt P, pt Q, float p, float q) { // surface
  vec V = V(P,Q);
  vec I = U(Normal(V));
  vec J = U(N(I,V));
  collar(P,V,I,J,p,q);
  }

public void cylinderSection(pt P, pt Q, float r) { coneSection(P,Q,r,r);}
 


// FANS, CONES, AND ARROWS
public void disk(pt P, vec I, vec J, float r) {
  float da = TWO_PI/36;
  beginShape(TRIANGLE_FAN);
    v(P);
    for(float a=0; a<=TWO_PI+da; a+=da) v(P(P,r*cos(a),I,r*sin(a),J));
  endShape();
  }
  
public void disk(pt P, vec V, float r) {  
  vec I = U(Normal(V));
  vec J = U(N(I,V));
  disk(P,I,J,r);
  }

public void fan(pt P, vec V, vec I, vec J, float r) {
  float da = TWO_PI/36;
  beginShape(TRIANGLE_FAN);
    v(P(P,V));
    for(float a=0; a<=TWO_PI+da; a+=da) v(P(P,r*cos(a),I,r*sin(a),J));
  endShape();
  }
  
public void fan(pt P, vec V, float r) {  
  vec I = U(Normal(V));
  vec J = U(N(I,V));
  fan(P,V,I,J,r);
  }

public void collar(pt P, vec V, float r, float rd) {
  vec I = U(Normal(V));
  vec J = U(N(I,V));
  collar(P,V,I,J,r,rd);
  }
 
public void collar(pt P, vec V, vec I, vec J, float r, float rd) {
  float da = TWO_PI/36;
  beginShape(QUAD_STRIP);
    for(float a=0; a<=TWO_PI+da; a+=da) {v(P(P,r*cos(a),I,r*sin(a),J,0,V)); v(P(P,rd*cos(a),I,rd*sin(a),J,1,V));}
  endShape();
  }

public void cone(pt P, vec V, float r) {fan(P,V,r); disk(P,V,r);}

public void stub(pt P, vec V, float r, float rd) // cone section
  {
  collar(P,V,r,rd); disk(P,V,r); disk(P(P,V),V,rd); 
  }

public void arrow(pt A, pt B, float r) {
  vec V=V(A,B);
  stub(A,V(.8f,V),r*2/3,r/3); 
  cone(P(A,V(.8f,V)),V(.2f,V),r); 
  }  
  
public void arrow(pt P, float s, vec V, float r) {arrow(P,V(s,V),r);}

public void arrow(pt P, vec V, float r) {
  stub(P,V(.8f,V),r*2/3,r/3); 
  cone(P(P,V(.8f,V)),V(.2f,V),r); 
  }  
  

public void block(float w, float d, float h, float x, float y, float z, float a) {
  pushMatrix(); translate(x,y,z); rotateZ(TWO_PI*a); box(w, d, h); popMatrix(); 
  }
  

// **************************** PRIMITIVE IN NOMINAL POSITION (Origin, Z-axis)
public void showFrame(float d) { 
  noStroke(); 
  fill(metal); sphere(d/10);
  fill(blue);  showArrow(d,d/10);
  fill(red); pushMatrix(); rotateY(PI/2); showArrow(d,d/10); popMatrix();
  fill(green); pushMatrix(); rotateX(-PI/2); showArrow(d,d/10); popMatrix();
  }

public void showFan(float d, float r) {
  float da = TWO_PI/36;
  beginShape(TRIANGLE_FAN);
    vertex(0,0,d);
    for(float a=0; a<=TWO_PI+da; a+=da) vertex(r*cos(a),r*sin(a),0);
  endShape();
  }

public void showCollar(float d, float r, float rd) {
  float da = TWO_PI/36;
  beginShape(QUAD_STRIP);
    for(float a=0; a<=TWO_PI+da; a+=da) {vertex(r*cos(a),r*sin(a),0); vertex(rd*cos(a),rd*sin(a),d);}
  endShape();
  }

public void showCone(float d, float r) {showFan(d,r);  showFan(0,r);}

public void showStub(float d, float r, float rd) {
  showCollar(d,r,rd); showFan(0,r);  pushMatrix(); translate(0,0,d); showFan(0,rd); popMatrix();
  }

public void showArrow() {showArrow(1,0.08f);}
 
public void showArrow(float d, float r) {
  float dd=d/5;
  showStub(d-dd,r*2/3,r/3); pushMatrix(); translate(0,0,d-dd); showCone(dd,r); popMatrix();
  }  
  
public void showBlock(float w, float d, float h, float x, float y, float z, float a) {
  pushMatrix(); translate(x,y,h/2); rotateZ(TWO_PI*a); box(w, d, h); popMatrix(); 
  }
  
public void showFloor() 
    {
    fill(yellow); 
    pushMatrix(); 
      translate(0,0,-1.5f); 
      float d=100;
      int n=20;
      pushMatrix();
        translate(0,-d*n/2,0);
          for(int j=0; j<n; j++)
            {
            pushMatrix();
              translate(-d*n/2,0,0);
              for(int i=0; i<n; i++)
                {
                fill(cyan); box(d,d,1);  pushMatrix(); translate(d,d,0);  box(d,d,1); popMatrix();
                fill(pink); pushMatrix(); translate(d,0,0); box(d,d,1); translate(-d,d,0); box(d,d,1); popMatrix();
                translate(2*d,0,0);
                }
            popMatrix();
            translate(0,2*d,0);
            }
      popMatrix(); // draws floor as thin plate
    popMatrix(); // draws floor as thin plate
    }
  

class pts // class for manipulaitng and displaying pointclouds or polyloops in 3D
  {
    int maxnv = 16000;                 //  max number of vertices
    pt[] G = new pt [maxnv];           // geometry table (vertices)
    char[] L = new char [maxnv];             // labels of points
    vec [] LL = new vec[ maxnv];  // displacement vectors
    Boolean loop=true;          // used to indicate closed loop 3D control polygons
    int pv =0,     // picked vertex index,
        iv=0,      //  insertion vertex index
        dv = 0,   // dancer support foot index
        nv = 0,    // number of vertices currently used in P
        pp=1; // index of picked vertex

  public String toString(){
    String out="";
    for (int i=0;i<this.nv;i++){
      out+=G[i].toString();
      out+="; ";
    }
    return out;
  }
  pts() {}
  public pts declare()
    {
    for (int i=0; i<maxnv; i++) G[i]=P();
    for (int i=0; i<maxnv; i++) LL[i]=V();
    return this;
    }     // init all point objects
  public pts empty() {nv=0; pv=0; return this;}                                 // resets P so that we can start adding points
  public pts addPt(pt P, char c) { G[nv].setTo(P); pv=nv; L[nv]=c; nv++;  return this;}          // appends a new point at the end
  public pts addPt(pt P) { G[nv].setTo(P); pv=nv; L[nv]='f'; nv++;  return this;}          // appends a new point at the end
  public pts addPt(float x,float y) { G[nv].x=x; G[nv].y=y; pv=nv; nv++; return this;} // same byt from coordinates
  public pts copyFrom(pts Q) {empty(); nv=Q.nv; for (int v=0; v<nv; v++) G[v]=P(Q.G[v]); return this;} // set THIS as a clone of Q

  public pts resetOnCircle(int k, float r)  // sets THIS to a polyloop with k points on a circle of radius r around origin
    {
    empty(); // resert P
    pt C = P(); // center of circle
    for (int i=0; i<k; i++) addPt(R(P(C,V(0,-r,0)),2.f*PI*i/k,C)); // points on z=0 plane
    pv=0; // picked vertex ID is set to 0
    return this;
    }
  // ********* PICK AND PROJECTIONS *******
  public int SETppToIDofVertexWithClosestScreenProjectionTo(pt M)  // sets pp to the index of the vertex that projects closest to the mouse
    {
    pp=0;
    for (int i=1; i<nv; i++) if (d(M,ToScreen(G[i]))<=d(M,ToScreen(G[pp]))) pp=i;
    return pp;
    }
  public pts showPicked() {show(G[pv],23); return this;}
  public pt closestProjectionOf(pt M)    // Returns 3D point that is the closest to the projection but also CHANGES iv !!!!
    {
    pt C = P(G[0]); float d=d(M,C);
    for (int i=1; i<nv; i++) if (d(M,G[i])<=d) {iv=i; C=P(G[i]); d=d(M,C); }
    for (int i=nv-1, j=0; j<nv; i=j++) {
       pt A = G[i], B = G[j];
       if(projectsBetween(M,A,B) && disToLine(M,A,B)<d) {d=disToLine(M,A,B); iv=i; C=projectionOnLine(M,A,B);}
       }
    return C;
    }

  // ********* MOVE, INSERT, DELETE *******
  public pts insertPt(pt P) { // inserts new vertex after vertex with ID iv
    for(int v=nv-1; v>iv; v--) {G[v+1].setTo(G[v]);  L[v+1]=L[v];}
     iv++;
     G[iv].setTo(P);
     L[iv]='f';
     nv++; // increments vertex count
     return this;
     }
  public pts insertClosestProjection(pt M) {
    pt P = closestProjectionOf(M); // also sets iv
    insertPt(P);
    return this;
    }
  public pts deletePicked()
    {
    for(int i=pv; i<nv; i++)
      {
      G[i].setTo(G[i+1]);
      L[i]=L[i+1];
      }
    pv=max(0,pv-1);
    nv--;
    return this;
    }
  public pts setPt(pt P, int i) { G[i].setTo(P); return this;}

  public pts drawBalls(float r) {for (int v=0; v<nv; v++) show(G[v],r); return this;}
  public pts showPicked(float r) {show(G[pv],r); return this;}
  public pts drawClosedCurve(float r)
    {
    fill(dgreen);
    for (int v=0; v<nv; v++) show(G[v],r*3);
    fill(magenta);
    for (int v=0; v<nv-1; v++) stub(G[v],V(G[v],G[v+1]),r,r);
    stub(G[nv-1],V(G[nv-1],G[0]),r,r);
    pushMatrix(); //translate(0,0,1);
    scale(1,1,0.03f);
    fill(grey);
    for (int v=0; v<nv; v++) show(G[v],r*3);
    for (int v=0; v<nv-1; v++) stub(G[v],V(G[v],G[v+1]),r,r);
    stub(G[nv-1],V(G[nv-1],G[0]),r,r);
    popMatrix();
    return this;
    }
  public pts set_pv_to_pp() {pv=pp; return this;}
  public pts movePicked(vec V) { G[pv].add(V); return this;}      // moves selected point (index p) by amount mouse moved recently
  public pts setPickedTo(pt Q) { G[pv].setTo(Q); return this;}      // moves selected point (index p) by amount mouse moved recently
  public pts moveAll(vec V) {for (int i=0; i<nv; i++) G[i].add(V); return this;};
  public pt Picked() {return G[pv];}
  public pt Pt(int i) {if(0<=i && i<nv) return G[i]; else return G[0];}

  // ********* I/O FILE *******
 public void savePts(String fn)
    {
    String [] inppts = new String [nv+1];
    int s=0;
    inppts[s++]=str(nv);
    for (int i=0; i<nv; i++) {inppts[s++]=str(G[i].x)+","+str(G[i].y)+","+str(G[i].z)+","+L[i];}
    saveStrings(fn,inppts);
    };

  public void loadPts(String fn)
    {
    println("loading: "+fn);
    String [] ss = loadStrings(fn);
    String subpts;
    int s=0;   int comma, comma1, comma2;   float x, y;   int a, b, c;
    nv = PApplet.parseInt(ss[s++]); print("nv="+nv);
    for(int k=0; k<nv; k++)
      {
      int i=k+s;
      //float [] xy = float(split(ss[i],","));
      String [] SS = split(ss[i],",");
      G[k].setTo(PApplet.parseFloat(SS[0]),PApplet.parseFloat(SS[1]),PApplet.parseFloat(SS[2]));
      L[k]=SS[3].charAt(0);
      }
    pv=0;
    };

  // Dancer
  public void setPicekdLabel(char c) {L[pp]=c;}



  public void setFifo()
    {
    _LookAtPt.reset(G[dv],60);
    }


  public void next() {dv=n(dv);}
  public int n(int v) {return (v+1)%nv;}
  public int p(int v) {if(v==0) return nv-1; else return v-1;}

  public pts subdivideDemoInto(pts Q)
    {
    Q.empty();
    for(int i=0; i<nv; i++)
      {
      Q.addPt(P(G[i]));
      Q.addPt(P(G[i],G[n(i)]));
      //...
      }
    return this;
    }

  public void displaySkater()
      {
      if(showCurve) {fill(yellow); for (int j=0; j<nv; j++) caplet(G[j],6,G[n(j)],6); }
      pt[] B = new pt [nv];           // geometry table (vertices)
      for (int j=0; j<nv; j++) B[j]=P(G[j],V(0,0,100));
      if(showPath) {fill(lime); for (int j=0; j<nv; j++) caplet(B[j],6,B[n(j)],6);}
      if(showKeys) {fill(cyan); for (int j=0; j<nv; j+=4) arrow(B[j],G[j],3);}

      if(animating) f=n(f);
      if(showSkater)
        {
        // ....
        }
      else {fill(red); arrow(B[f],G[f],20);} //
      }


} // end of pts class
// points, vectors, frames in 3D
class vec
   {
       float x=0,y=0,z=0;
   // creation
   vec () {};
   vec (float px, float py, float pz) {x = px; y = py; z = pz;};
   vec (float px, float py) {x = px; y = py;};
   public vec set (float px, float py, float pz) {x = px; y = py; z = pz; return this;};
   public vec setTo(vec V) {x = V.x; y = V.y; z = V.z; return this;};
   public vec set (vec V) {x = V.x; y = V.y; z = V.z; return this;};

   // measure
   public float norm() {return(sqrt(sq(x)+sq(y)+sq(z)));};

   //alteration
   public vec add(vec V) {x+=V.x; y+=V.y; z+=V.z; return this;};
   public vec add(float s, vec V) {x+=s*V.x; y+=s*V.y; z+=s*V.z; return this;};
   public vec sub(vec V) {x-=V.x; y-=V.y; z-=V.z; return this;};
   public vec mul(float f) {x*=f; y*=f; z*=f; return this;};
   public vec div(float f) {x/=f; y/=f; z/=f; return this;};
   public vec div(int f) {x/=f; y/=f; z/=f; return this;};
   public vec rev() {x=-x; y=-y; z=-z; return this;};
   public vec normalize() {float n=norm(); if (n>0.000001f) {div(n);}; return this;};
   public vec rotate(float a, vec I, vec J)  // Rotate this by angle a parallel in plane (I,J) Assumes I and J are orthogonal
     {
     float x=d(this,I), y=d(this,J); // dot products
     float c=cos(a), s=sin(a);
     add(x*c-x-y*s,I); add(x*s+y*c-y,J);
     return this;
     }
   } // end class vec

class pt
   {
     float x=0,y=0,z=0;
   pt () {};
   pt (float px, float py) {x = px; y = py;};
   pt (float px, float py, float pz) {x = px; y = py; z = pz; };
   public pt set (float px, float py, float pz) {x = px; y = py; z = pz; return this;};
   public pt set (pt P) {x = P.x; y = P.y; z = P.z; return this;};
   public pt setTo(pt P) {x = P.x; y = P.y; z = P.z; return this;};
   public pt setTo(float px, float py, float pz) {x = px; y = py; z = pz; return this;};
   public pt add(pt P) {x+=P.x; y+=P.y; z+=P.z; return this;};
   public pt add(vec V) {x+=V.x; y+=V.y; z+=V.z; return this;};
   public pt sub(vec V) {x-=V.x; y-=V.y; z-=V.z; return this;};
   public pt add(float s, vec V) {x+=s*V.x; y+=s*V.y; z+=s*V.z; return this;};
   public pt sub(pt P) {x-=P.x; y-=P.y; z-=P.z; return this;};
   public pt mul(float f) {x*=f; y*=f; z*=f; return this;};
   public pt div(float f) {x/=f; y/=f; z/=f; return this;};
   public pt div(int f) {x/=f; y/=f; z/=f; return this;};
   public String toString(){
     return (Float.toString(x)+","+Float.toString(y)+","+Float.toString(z));
   }
   }

// =====  vector functions
public vec V() {return new vec(); };                                                                          // make vector (x,y,z)
public vec V(float x, float y, float z) {return new vec(x,y,z); };                                            // make vector (x,y,z)
public vec V(vec V) {return new vec(V.x,V.y,V.z); };                                                          // make copy of vector V
public vec A(vec A, vec B) {return new vec(A.x+B.x,A.y+B.y,A.z+B.z); };                                       // A+B
public vec A(vec U, float s, vec V) {return V(U.x+s*V.x,U.y+s*V.y,U.z+s*V.z);};                               // U+sV
public vec M(vec U, vec V) {return V(U.x-V.x,U.y-V.y,U.z-V.z);};                                              // U-V
public vec M(vec V) {return V(-V.x,-V.y,-V.z);};                                                              // -V
public vec V(vec A, vec B) {return new vec((A.x+B.x)/2.0f,(A.y+B.y)/2.0f,(A.z+B.z)/2.0f); }                      // (A+B)/2
public vec V(vec A, float s, vec B) {return new vec(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y),A.z+s*(B.z-A.z)); };      // (1-s)A+sB
public vec V(vec A, vec B, vec C) {return new vec((A.x+B.x+C.x)/3.0f,(A.y+B.y+C.y)/3.0f,(A.z+B.z+C.z)/3.0f); };  // (A+B+C)/3
public vec V(vec A, vec B, vec C, vec D) {return V(V(A,B),V(C,D)); };                                         // (A+B+C+D)/4
public vec V(float s, vec A) {return new vec(s*A.x,s*A.y,s*A.z); };                                           // sA
public vec S(vec U, float s, vec V) {return V(U.x+s*V.x,U.y+s*V.y,U.z+s*V.z);};                  // U+sV
public vec V(float a, vec A, float b, vec B) {return A(V(a,A),V(b,B));}                                       // aA+bB
public vec V(float a, vec A, float b, vec B, float c, vec C) {return A(V(a,A,b,B),V(c,C));}                   // aA+bB+cC
public vec V(pt P, pt Q) {return new vec(Q.x-P.x,Q.y-P.y,Q.z-P.z);};                                          // PQ
public vec U(vec V) {float n = V.norm(); if (n<0.0000001f) return V(0,0,0); else return V(1.f/n,V);};           // V/||V||
public vec U(pt P, pt Q) {return U(V(P,Q));};                                                                 // PQ/||PQ||
public vec U(float x, float y, float z) {return U(V(x,y,z)); };                                               // make vector (x,y,z)
public vec N(vec U, vec V) {return V( U.y*V.z-U.z*V.y, U.z*V.x-U.x*V.z, U.x*V.y-U.y*V.x); };                  // UxV cross product (normal to both)
public vec cross(vec U, vec V) {return V( U.y*V.z-U.z*V.y, U.z*V.x-U.x*V.z, U.x*V.y-U.y*V.x); };                  // UxV cross product (normal to both)
public vec N(pt A, pt B, pt C) {return N(V(A,B),V(A,C)); };                                                   // normal to triangle (A,B,C), not normalized (proportional to area)
public vec B(vec U, vec V) {return U(N(N(U,V),U)); }                                                          // normal to U in plane (U,V)
public vec Normal(vec V) {                                                                                    // vector orthogonal to V
  if(abs(V.z)<=min(abs(V.x),abs(V.y))) return V(-V.y,V.x,0);
  if(abs(V.x)<=min(abs(V.z),abs(V.y))) return V(0,-V.z,V.y);
  return V(V.z,0,-V.x);
  }
public float mixed(vec U, vec V, vec W) {return dot(U,cross(V,W));}

// ===== point functions
public pt P() {return new pt(); };                                                                          // point (x,y,z)
public pt P(float x, float y, float z) {return new pt(x,y,z); };                                            // point (x,y,z)
public pt P(float x, float y) {return new pt(x,y); };                                                       // make point (x,y)
public pt P(pt A) {return new pt(A.x,A.y,A.z); };                                                           // copy of point P
public pt P(pt A, float s, pt B) {return new pt(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y),A.z+s*(B.z-A.z)); };        // A+sAB
public pt L(pt A, float s, pt B) {return new pt(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y),A.z+s*(B.z-A.z)); };        // A+sAB
public pt P(pt A, pt B) {return P((A.x+B.x)/2.0f,(A.y+B.y)/2.0f,(A.z+B.z)/2.0f); }                             // (A+B)/2
public pt P(pt A, pt B, pt C) {return new pt((A.x+B.x+C.x)/3.0f,(A.y+B.y+C.y)/3.0f,(A.z+B.z+C.z)/3.0f); };     // (A+B+C)/3
public pt P(pt A, pt B, pt C, pt D) {return P(P(A,B),P(C,D)); };                                            // (A+B+C+D)/4
public pt P(float s, pt A) {return new pt(s*A.x,s*A.y,s*A.z); };                                            // sA
public pt A(pt A, pt B) {return new pt(A.x+B.x,A.y+B.y,A.z+B.z); };                                         // A+B
public pt P(float a, pt A, float b, pt B) {return A(P(a,A),P(b,B));}                                        // aA+bB
public pt P(float a, pt A, float b, pt B, float c, pt C) {return A(P(a,A),P(b,B,c,C));}                     // aA+bB+cC
public pt P(float a, pt A, float b, pt B, float c, pt C, float d, pt D){return A(P(a,A,b,B),P(c,C,d,D));}   // aA+bB+cC+dD
public pt P(pt P, vec V) {return new pt(P.x + V.x, P.y + V.y, P.z + V.z); }                                 // P+V
public pt P(pt P, float s, vec V) {return new pt(P.x+s*V.x,P.y+s*V.y,P.z+s*V.z);}                           // P+sV
public pt P(pt O, float x, vec I, float y, vec J) {return P(O.x+x*I.x+y*J.x,O.y+x*I.y+y*J.y,O.z+x*I.z+y*J.z);}  // O+xI+yJ
public pt P(pt O, float x, vec I, float y, vec J, float z, vec K) {return P(O.x+x*I.x+y*J.x+z*K.x,O.y+x*I.y+y*J.y+z*K.y,O.z+x*I.z+y*J.z+z*K.z);}  // O+xI+yJ+kZ
public void makePts(pt[] C) {for(int i=0; i<C.length; i++) C[i]=P();}
public pt Bezier(pt A, pt B, pt C, float t) {return L(L(A,t,B),t,L(B,t,C));}

// ===== mouse
public pt Mouse() {return P(mouseX,mouseY,0);};                                          // current mouse location
public pt Pmouse() {return P(pmouseX,pmouseY,0);};
public vec MouseDrag() {return V(mouseX-pmouseX,mouseY-pmouseY,0);};                     // vector representing recent mouse displacement
public pt ScreenCenter() {return P(width/2,height/2);}                                                        //  point in center of  canvas

// ===== measures
public float d(vec U, vec V) {return U.x*V.x+U.y*V.y+U.z*V.z; };                                            //U*V dot product
public float dot(vec U, vec V) {return U.x*V.x+U.y*V.y+U.z*V.z; };                                          //U*V dot product
public float det2(vec U, vec V) {return -U.y*V.x+U.x*V.y; };                                                // U:V det product of (x,y) components
public float det3(vec U, vec V) {return sqrt(d(U,U)*d(V,V) - sq(d(U,V))); };                                // U:V det product (norm of UxV)
public float m(vec U, vec V, vec W) {return d(U,N(V,W)); };                                                 // U*(VxW)  mixed product, determinant
public float m(pt E, pt A, pt B, pt C) {return m(V(E,A),V(E,B),V(E,C));}                                    // det (EA EB EC) is >0 when E sees (A,B,C) clockwise
public float n2(vec V) {return sq(V.x)+sq(V.y)+sq(V.z);};                                                   // V*V    norm squared
public float n(vec V) {return sqrt(n2(V));};                                                                // ||V||  norm
public float norm(vec V) {return sqrt(n2(V));};                                                                // ||V||  norm
public float d(pt P, pt Q) {return sqrt(sq(Q.x-P.x)+sq(Q.y-P.y)+sq(Q.z-P.z)); };                            // ||AB|| distance
public float area(pt A, pt B, pt C) {return n(N(A,B,C))/2; };                                               // (positive) area of triangle in 3D
public float volume(pt A, pt B, pt C, pt D) {return m(V(A,B),V(A,C),V(A,D))/6; };                           // (signed) volume of tetrahedron
public boolean parallel (vec U, vec V) {return n(N(U,V))<n(U)*n(V)*0.00001f; }                               // true if U and V are almost parallel
public float angle(vec U, vec V) {return acos(d(U,V)/n(V)/n(U)); };                                         // angle(U,V) positive  (in 0,PI)
public boolean cw(vec U, vec V, vec W) {return m(U,V,W)>0; };                                               // U*(VxW)>0  U,V,W are clockwise in 3D
public boolean cw(pt A, pt B, pt C, pt D) {return volume(A,B,C,D)>0; };                                     // tet is oriented so that A sees B, C, D clockwise
public boolean projectsBetween(pt P, pt A, pt B) {return dot(V(A,P),V(A,B))>0 && dot(V(B,P),V(B,A))>0 ; };
public float disToLine(pt P, pt A, pt B) {return det3(U(A,B),V(A,P)); };
public pt projectionOnLine(pt P, pt A, pt B) {return P(A,dot(V(A,B),V(A,P))/dot(V(A,B),V(A,B)),V(A,B));}

// ===== rotations
public vec R(vec V) {return V(-V.y,V.x,V.z);} // rotated 90 degrees in XY plane
public pt R(pt P, float a, vec I, vec J, pt G) {float x=d(V(G,P),I), y=d(V(G,P),J); float c=cos(a), s=sin(a); return P(P,x*c-x-y*s,I,x*s+y*c-y,J); }; // Rotated P by a around G in plane (I,J)
public vec R(vec V, float a, vec I, vec J) {float x=d(V,I), y=d(V,J); float c=cos(a), s=sin(a); return A(V,V(x*c-x-y*s,I,x*s+y*c-y,J)); }; // Rotated V by a parallel to plane (I,J)
public pt R(pt Q, float a) {float dx=Q.x, dy=Q.y, c=cos(a), s=sin(a); return P(c*dx+s*dy,-s*dx+c*dy,Q.z); };  // Q rotated by angle a around the origin
public pt R(pt Q, float a, pt C) {float dx=Q.x-C.x, dy=Q.y-C.y, c=cos(a), s=sin(a); return P(C.x+c*dx-s*dy, C.y+s*dx+c*dy, Q.z); };  // Q rotated by angle a around point P
public pt R(pt Q, pt C, pt P, pt R)  // returns rotated version of Q by angle(CP,CR) parallel to plane (C,P,R)
   {
   vec I0=U(C,P), I1=U(C,R), V=V(C,Q);
   float c=d(I0,I1), s=sqrt(1.f-sq(c));
                                       if(abs(s)<0.00001f) return Q; // singular cAW
   vec J0=V(1.f/s,I1,-c/s,I0);
   vec J1=V(-s,I0,c,J0);
   float x=d(V,I0), y=d(V,J0);
   return P(Q,x,M(I1,I0),y,M(J1,J0));
   }

// ===== rending functions
public void normal(vec V) {normal(V.x,V.y,V.z);};                                     // changes current normal vector for subsequent smooth shading
public void vertex(pt P) {vertex(P.x,P.y,P.z);};                                      // vertex for shading or drawing
public void v(pt P) {vertex(P.x,P.y,P.z);};                                           // vertex for shading or drawing (between BeginShape() ; and endShape();)
public void vTextured(pt P, float u, float v) {vertex(P.x,P.y,P.z,u,v);};                          // vertex with texture coordinates
public void show(pt P, pt Q) {line(Q.x,Q.y,Q.z,P.x,P.y,P.z); };                       // render edge (P,Q)
public void show(pt P, vec V) {line(P.x,P.y,P.z,P.x+V.x,P.y+V.y,P.z+V.z); };          // render edge from P to P+V
public void show(pt P, float d , vec V) {line(P.x,P.y,P.z,P.x+d*V.x,P.y+d*V.y,P.z+d*V.z); }; // render edge from P to P+dV
public void show(pt A, pt B, pt C) {beginShape(); vertex(A);vertex(B); vertex(C); endShape(CLOSE);};                      // render Triangle(A,B,C)
public void show(pt A, pt B, pt C, pt D) {beginShape(); vertex(A); vertex(B); vertex(C); vertex(D); endShape(CLOSE);};    // render Quad(A,B,C,D)
public void show(pt P, float s, vec I, vec J, vec K) {noStroke(); fill(yellow); show(P,5); stroke(red); show(P,s,I); stroke(green); show(P,s,J); stroke(blue); show(P,s,K); }; // render coordinate system
public void show(pt P, String s) {text(s, P.x, P.y, P.z); }; // prints string s in 3D at P
public void show(pt P, String s, vec D) {text(s, P.x+D.x, P.y+D.y, P.z+D.z);  }; // prints string s in 3D at P+D (offset vector)
public void show(pt P, float r) {pushMatrix(); translate(P.x,P.y,P.z); sphere(r); popMatrix();};                          // render sphere of radius r and center P
public void showShadow(pt P, float r) {pushMatrix(); translate(P.x,P.y,0); scale(1,1,0.01f); sphere(r); popMatrix();}      // render shadow on the floot of sphere of radius r and center P

//===== SUBDIVISION
public pt B(pt A, pt B, pt C, float s) {return( P(P(B,s/4.f,A),0.5f,P(B,s/4.f,C))); };                          // returns a tucked B towards its neighbors
public pt F(pt A, pt B, pt C, pt D, float s) {return( P(P(A,1.f+(1.f-s)/8.f,B) ,0.5f, P(D,1.f+(1.f-s)/8.f,C))); };    // returns a bulged mid-edge point
// ************************************ IMAGES & VIDEO 
int pictureCounter=0, frameCounter=0;
Boolean filming=false, change=false;
PImage myFace; // picture of author's face, should be: data/pic.jpg in sketch folder
public void snapPicture() {saveFrame("PICTURES/P"+nf(pictureCounter++,3)+".jpg"); }

// ******************************************COLORS 
int black=0xff000000, white=0xffFFFFFF, // set more colors using Menu >  Tools > Color Selector
   red=0xffFF0000, green=0xff00FF01, blue=0xff0300FF, yellow=0xffFEFF00, cyan=0xff00FDFF, magenta=0xffFF00FB,
   grey=0xff818181, orange=0xffFFA600, brown=0xffB46005, metal=0xffB5CCDE, 
   lime=0xffA4FA83, pink=0xffFCC4FA, dgreen=0xff057103,
   lightWood=0xffF5DEA6, darkWood=0xffD8BE7A;
public void pen(int c, float w) {stroke(c); strokeWeight(w);}

// ******************************** TEXT , TITLE, and USER's GUIDE
Boolean scribeText=true; // toggle for displaying of help text
public void scribe(String S, float x, float y) {fill(0); text(S,x,y); noFill();} // writes on screen at (x,y) with current fill color
public void scribeHeader(String S, int i) {fill(0); text(S,10,20+i*20); noFill();} // writes black at line i
public void scribeHeaderRight(String S) {fill(0); text(S,width-7.5f*S.length(),20); noFill();} // writes black on screen top, right-aligned
public void scribeFooter(String S, int i) {fill(0); text(S,10,height-10-i*20); noFill();} // writes black on screen at line i from bottom
public void scribeAtMouse(String S) {fill(0); text(S,mouseX,mouseY); noFill();} // writes on screen near mouse
public void scribeMouseCoordinates() {fill(black); text("("+mouseX+","+mouseY+")",mouseX+7,mouseY+25); noFill();}



FIFO _LookAtPt = new FIFO();

class FIFO // class for filtering camera motion
  {   
  int n=0, maxn=200;
  pt [] C = new pt[maxn];
  
  FIFO() 
    {
    n=1;
    C[0]=P();
    }

  public void reset(pt A, int k) 
    {
    n=k;
    for(int i=0; i<n; i++) C[i]=P(A);
    }

  public pt move(pt B) 
    {
    C[0]=P(B,C[0]);
    for(int i=1; i<n; i++) C[i]=P(C[i-1],C[i]);
    return C[n-1];
    }
  }
  public void settings() {  size(600, 600, P3D);  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Ceditor" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
