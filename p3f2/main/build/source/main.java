import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.nio.*; 
import processing.core.PMatrix3D; 
import processing.pdf.*; 
import java.awt.Toolkit; 
import java.awt.datatransfer.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

//  ******************* 2018 Project 3 basecde ***********************
Boolean
  showFloor=true,
  showBalls=true,
  showPillars=false,
  animating=false,
  showEdges=true,
  showTriangles=true,
  showVoronoi=true,
  showArcs=true,
  showCorner=true,
  showOpposite=true,
  showVoronoiFaces=true,
  live=true,   // updates mesh at each frame

  step1=false,
  step2=false,
  step3=false,
  step4=false,
  step5=false,
  step6=false,
  step7=false,
  step8=false,
  step9=false,
  step10=false,

  PickedFocus=false,
  center=true,

  scribeText=false; // toggle for displaying of help text


float
  da = TWO_PI/32, // facet count for fans, cones, caplets
  t=0,
  s=0,
  rb=50, // radius of the balls
  rt=rb/2, // radius of tubes
  columnHeight = rb*0.7f,
  h_floor=0, h_ceiling=600,  h=h_floor;

int
  f=0,
  maxf=2*30,
  level=4,
  method=5,
  PTris=0,
  QTris=0,
  numberOfBorderEdges=0,
  tetCount=0;


pts P = new pts(); // polyloop in 3D
pts Q = new pts(); // second polyloop in 3D
pts R, S, T;
EdgeSet BP = new EdgeSet();
MESH M = new MESH();

public void setup() {
  myFace = loadImage("data/pic.jpg");  // load image from file pic.jpg in folder data *** replace that file with your pic of your own face
  textureMode(NORMAL);
   // P3D means that we will do 3D graphics
  //size(600, 600, P3D); // P3D means that we will do 3D graphics
  P.declare(); Q.declare(); // P is a polyloop in 3D: declared in pts
  //P.resetOnCircle(6,100); Q.copyFrom(P); // use this to get started if no model exists on file: move points, save to file, comment this line
  P.loadPts("data/pts");
  Q.loadPts("data/pts2"); // loads saved models from file (comment out if they do not exist yet)
  
  //frameRate(30);
  sphereDetail(12);
  R=P; S=Q;
  println(); println("_______ _______ _______ _______");
  }

public void draw() {
  background(255);
  hint(ENABLE_DEPTH_TEST);
  pushMatrix();   // to ensure that we can restore the standard view before writing on the canvas
  setView();  // see pick tab
  if(showFloor) showFloor(h); // draws dance floor as yellow mat
  doPick(); // sets Of and axes for 3D GUI (see pick Tab)
  R.SETppToIDofVertexWithClosestScreenProjectionTo(Mouse()); // for picking (does not set P.pv)

  if(showBalls)
      {
      fill(red); R.drawBalls(rb);
      fill(black,100); R.showPicked(rb+5);
      }
  if(showPillars)
      {
      fill(green); R.drawColumns(rb,columnHeight);
      fill(black,100); R.showPicked(rb+5);
      }

  if(step1)
    {
    pushMatrix();
    translate(0,0,4); fill(cyan); stroke(yellow);
    if(live)
      {
      M.reset();
      M.loadVertices(R.G,R.nv);
      M.triangulate(); // **01 implement it in Mesh
      }
    if(showTriangles) M.showTriangles();
    noStroke();
    popMatrix();
    }

  if(step2)
    {
    fill(yellow);
    if(live) {M.computeO();} // **02 implement it in Mesh
    if(showEdges)
      {
      fill(yellow);//stroke(yellow);
      M.showNonBorderEdges(); // **02 implement it in Mesh
      fill(red);//stroke(red);
      M.showBorderEdges();} // **02 implement it in Mesh
    }

  if(step3)
    {
    M.classifyVertices();  // **03 implement it in Mesh
    showBalls=false;
    fill(green); noStroke();
    M.showVertices(rb+4);
    }

  if(step4)
    {
    for(int i=0; i<10; i++) M.smoothenInterior(); // **04 implement it in Mesh
    M.writeVerticesTo(R);
    }

 // **05 implement corner operators in Mesh
  if(step5)
    {
    live=false;
    fill(magenta);
    if(showCorner) M.showCurrentCorner(20);
    }

  if(step6)
    {
    pushMatrix();
    translate(0,0,6); noFill();
    stroke(blue);
    if(showVoronoi) M.showVoronoiEdges(); // **06 implement it in Mesh
    stroke(red);
    if(showArcs) M.showArcs(); // **06 implement it in Mesh
    noStroke();
    popMatrix();
    }

  if(step7)
    {
    fill(blue); show(R.G[0],1.1f*rb);
    fill(orange); beam(P.G[0],P.G[1],rt);
    fill(grey); beam(R.G[0],R.G[1],1.1f*rt); beam(R.G[1],R.G[2],1.1f*rt); beam(R.G[2],R.G[0],1.1f*rt);
    fill(red); show(CircumCenter(R.G[0],R.G[1],R.G[2]),15);
    fill(magenta,200); show(CircumCenter(R.G[0],R.G[1],R.G[2]),circumRadius(R.G[0],R.G[1],R.G[2]));
    }

  if(step8)
    {
    CIRCLE C1 = Circ(R.G[0],rb), C2 = Circ(R.G[1],rb*1.2f),  C3 = Circ(R.G[2],rb*1.8f);
    CIRCLE C = Apollonius(C1,C2,C3,-1,-1,-1);
    fill(red,150); C1.showAsSphere();
    fill(green,150); C2.showAsSphere();
    fill(blue,150); C3.showAsSphere();
    fill(yellow,200); C.showAsSphere();
    }

  if(step9)
    {
    }

  if(step10)
    {
    }

  popMatrix(); // done with 3D drawing. Restore front view for writing text on canvas
  hint(DISABLE_DEPTH_TEST); // no z-buffer test to ensure that help text is visible

  int line=0;
  scribeHeader(" Project 3 for Rossignac's 2018 Graphics Course CS3451 / CS6491 by First LAST NAME ",line++);
  scribeHeader(P.count()+" vertices, "+M.nt+" triangles ",line++);
  if(live) scribeHeader("LIVE",line++);

  // used for demos to show red circle when mouse/key is pressed and what key (disk may be hidden by the 3D model)
  if(mousePressed) {stroke(cyan); strokeWeight(3); noFill(); ellipse(mouseX,mouseY,20,20); strokeWeight(1);}
  if(keyPressed) {stroke(red); fill(white); ellipse(mouseX+14,mouseY+20,26,26); fill(red); text(key,mouseX-5+14,mouseY+4+20); strokeWeight(1); }
  if(scribeText) {fill(black); displayHeader();} // dispalys header on canvas, including my face
  if(scribeText && !filming) displayFooter(); // shows menu at bottom, only if not filming
  if(filming && (animating || change)) {print("."); saveFrame("../MOVIE FRAMES (PLEASE DO NOT SUBMIT)/F"+nf(frameCounter++,4)+".tif"); change=false;} // save next frame to make a movie
  if(filming && (animating || change)) {print("."); change=false;} // save next frame to make a movie
  //change=false; // to avoid capturing frames when nothing happens (change is set uppn action)
  //change=true;
  }
// CLASS FOR KEEPING THE EDGES OF THE MESH
class EdgeSet // class for storing edges 
  { 
  int maxnbe = 1000;                 //  max number of edges
  int nb=0;                          // current number of edges
  int [] S = new int [maxnbe];       //  ID of ball or vertex where edge starts
  int [] E = new int [maxnbe];       //  ID of ball where edge ends
  EdgeSet() {}
  public void reset() {nb=0;}
  public void addEdge(int i, int j) {if(!isDuplicate(i,j)) {S[nb]=i; E[nb]=j; nb++;}}
  public void showEdges(pts P, float rt) {for(int b=0; b<nb; b++) beam(P.G[S[b]],P.G[E[b]],rt);} // uses vertices of P to draw these edges
  public boolean isDuplicate(int i, int j) // returns true if this edge already exists
    {
    for(int b=0; b<nb; b++) if((E[b]==i && S[b]==j) || (E[b]==j && S[b]==i)) return true;
    return false;
    }
  public int count() {return nb;} // total edge count
  }
//===== CIRCUMCENTER & RADIUS

public float circumRadius (pt A, pt B, pt C) {float a=d(B,C), b=d(C,A), c=d(A,B), s=(a+b+c)/2, d=sqrt(s*(s-a)*(s-b)*(s-c)); return a*b*c/4/d;} 

public pt CircumCenter(pt A, pt B, pt C) // CircumCenter(A,B,C): center of circumscribing circle, where medians meet)
  {
  vec N = U(N(A,B,C));
  vec AB = V(A,B), AC = V(A,C); 
  vec RAB=N(N,AB), RAC=N(N,AC); 
  return P(A,1.f/2/dot(AB,RAC),V(-n2(RAC),RAB,n2(AB),RAC)); 
  }; 
  
// From Rasmus Fonseca: https://rasmusfonseca.github.io/implementations/apollonius.html 
class CIRCLE 
   { 
   pt Center = P();
   float radius=1; 
   // creation    
   CIRCLE () {}; 
   CIRCLE (pt C, float r) {Center=P(C); radius=r;}
   public void setTo(CIRCLE C) {Center=P(C.Center); radius=C.radius;}
   public void showAsSphere() {show(Center,radius);} 
   public void showAsSphere(float dr) {show(Center,radius+dr);} 
   public void showAsColumn(float h) {pillar(Center,h,radius);} 
   }

public CIRCLE Circ(pt C, float r) {return new CIRCLE(C,r);}

public CIRCLE Apollonius(CIRCLE C1, CIRCLE C2, CIRCLE C3, int s1, int s2, int s3) // si are +/- 1 (s1=+1 means C1 is outside of Apollonius circle
  {
  float x1 = C1.Center.x;
  float y1 = C1.Center.y;
  float r1 = C1.radius;
  float x2 = C2.Center.x;
  float y2 = C2.Center.y;
  float r2 = C2.radius;
  float x3 = C3.Center.x;
  float y3 = C3.Center.y;
  float r3 = C3.radius;

 
  float v11 = 2*x2 - 2*x1;
  float v12 = 2*y2 - 2*y1;
  float v13 = x1*x1 - x2*x2 + y1*y1 - y2*y2 - r1*r1 + r2*r2;
  float v14 = 2*s2*r2 - 2*s1*r1;
  
  float v21 = 2*x3 - 2*x2;
  float v22 = 2*y3 - 2*y2;
  float v23 = x2*x2 - x3*x3 + y2*y2 - y3*y3 - r2*r2 + r3*r3;
  float v24 = 2*s3*r3 - 2*s2*r2;
  
  float w12 = v12/v11;
  float w13 = v13/v11;
  float w14 = v14/v11;
  
  float w22 = v22/v21-w12;
  float w23 = v23/v21-w13;
  float w24 = v24/v21-w14;
  
  float P = -w23/w22;
  float Q = w24/w22;
  float M = -w12*P-w13;
  float N = w14 - w12*Q;
  
  float a = N*N + Q*Q - 1;
  float b = 2*M*N - 2*N*x1 + 2*P*Q - 2*Q*y1 + 2*s1*r1;
  float c = x1*x1 + M*M - 2*M*x1 + P*P + y1*y1 - 2*P*y1 - r1*r1;
  
  // Find roots of a quadratic equation
  //double[] quadSols = Polynomial.solve(new double[]{a,b,c}); float rs = (float)quadSols[0];
  float rs = (-b-sqrt(sq(b)-4.f*a*c))/(2*a);
  float xs = M+N*rs;
  float ys = P+Q*rs;
  return Circ(P(xs,ys),rs);
  }
  
// Apollonius graph in CGAL: https://doc.cgal.org/latest/Apollonius_graph_2/index.html   
// TRIANGLE MESH
class MESH {
    // VERTICES
    int nv=0, maxnv = 1000;
    pt[] G = new pt [maxnv];
    // TRIANGLES
    int nt = 0, maxnt = maxnv*2;
    boolean[] isInterior = new boolean[maxnv];
    // CORNERS
    int c=0;    // current corner
    int nc = 0;
    int[] V = new int [3*maxnt];
    int[] O = new int [3*maxnt];
    // current corner that can be edited with keys
  MESH() {for (int i=0; i<maxnv; i++) G[i]=new pt();};
  public void reset() {nv=0; nt=0; nc=0;}                                                  // removes all vertices and triangles
  public void loadVertices(pt[] P, int n) {nv=0; for (int i=0; i<n; i++) addVertex(P[i]);}
  public void writeVerticesTo(pts P) {for (int i=0; i<nv; i++) P.G[i].setTo(G[i]);}
  public void addVertex(pt P) { G[nv++].setTo(P); }                                             // adds a vertex to vertex table G
  public void addTriangle(int i, int j, int k) {V[nc++]=i; V[nc++]=j; V[nc++]=k; nt=nc/3; }     // adds triangle (i,j,k) to V table

  // CORNER OPERATORS
  public int t (int c) {int r=PApplet.parseInt(c/3); return(r);}                   // triangle of corner c
  public int n (int c) {int r=3*PApplet.parseInt(c/3)+(c+1)%3; return(r);}         // next corner
  public int p (int c) {int r=3*PApplet.parseInt(c/3)+(c+2)%3; return(r);}         // previous corner
  public pt g (int c) {return G[V[c]];}                             // shortcut to get the point where the vertex v(c) of corner c is located

  public boolean nb(int c) {return(O[c]!=c);};  // not a border corner
  public boolean bord(int c) {return(O[c]==c);};  // not a border corner

  public pt cg(int c) {return P(0.6f,g(c),0.2f,g(p(c)),0.2f,g(n(c)));}   // computes offset location of point at corner c

  // CORNER ACTIONS CURRENT CORNER c
  public void next() {c=n(c);}
  public void previous() {c=p(c);}
  public void opposite() {c=o(c);}
  public void left() {c=l(c);}
  public void right() {c=r(c);}
  public void swing() {c=s(c);}
  public void unswing() {c=u(c);}
  public void printCorner() {println("c = "+c);}



  // DISPLAY
  public void showCurrentCorner(float r) { if(bord(c)) fill(red); else fill(dgreen); show(cg(c),r); };   // renders corner c as small ball
  public void showEdge(int c) {beam( g(p(c)),g(n(c)),rt ); };  // draws edge of t(c) opposite to corner c
  public void showVertices(float r) // shows all vertices green inside, red outside
    {
    for (int v=0; v<nv; v++)
      {
      if(isInterior[v]) fill(green); else fill(red);
      show(G[v],r);
      }
    }
  public void showInteriorVertices(float r) {for (int v=0; v<nv; v++) if(isInterior[v]) show(G[v],r); }                          // shows all vertices as dots
  public void showTriangles() { for (int c=0; c<nc; c+=3) show(g(c), g(c+1), g(c+2)); }         // draws all triangles (edges, or filled)
  public void showEdges() {for (int i=0; i<nc; i++) showEdge(i); };         // draws all edges of mesh twice

  public void triangulate()      // performs Delaunay triangulation using a quartic algorithm
   {
   c=0;                   // to reset current corner
   // **01 implement it
   for (int i=0;i<nv;i++)
    {
    for (int j=i;j<nv;j++)
      {
      if (j==i) continue;
      for (int k=j;k<nv;k++)
        {
        if ((k==i)||(k==j)) continue;
        boolean good=true;
        for (int m=0;m<nv;m++)
          {
            if ((m==i)||(m==j)||(m==k)) continue;
            float a1,a2,a3,a4,a5,a6,a7,a8,a9;
            a1=G[i].x-G[m].x; a2=G[i].y-G[m].y; a3=a1*a1+a2*a2;
            a4=G[j].x-G[m].x; a5=G[j].y-G[m].y; a6=a4*a4+a5*a5;
            a7=G[k].x-G[m].x; a8=G[k].y-G[m].y; a9=a7*a7+a8*a8;
            if (ccw(G[i],G[j],G[k]))
              {
                float t1,t2,t3;
                t1=a4;t2=a5;t3=a6;
                a4=a7;a5=a8;a6=a9;
                a7=t1;a8=t2;a9=t3;
              }
            float result=determinant(a1,a2,a3,a4,a5,a6,a7,a8,a9);
            if (result<0) good=false;
          }
        if (good)
          {
            if (ccw(G[i],G[j],G[k])) {addTriangle(i,j,k);}
            else {addTriangle(i,k,j);}
          }
        }
      }
    }

   }


  public void computeO() // **02 implement it
    {
    for (int i=0;i<nc;i++) O[i]=i;
    for (int i=0;i<nc;i++)
      {
      int Vin=V[n(i)];
      int Vip=V[p(i)];
      for (int j=i;j<nc;j++)
        {
        if (j==i) continue;
        int Vjn=V[n(j)];
        int Vjp=V[p(j)];
        if ((Vin==Vjp)&&(Vip==Vjn)) {O[i]=j;O[j]=i;break;}
        }
      }

    // **02 implement it
    }

  public void showBorderEdges()  // draws all border edges of mesh
    {
    //int count=0;
    for (int i=0;i<nc;i++)
      {
      if (O[i]==i)
        {
        showEdge(i);
        }

      }
      // System.out.println("=======");
    // **02 implement;
    }

  public void showNonBorderEdges() // draws all non-border edges of mesh
    {
      for (int i=0;i<nc;i++)
        {
        if (O[i]!=i)
          {
          showEdge(i);
          }
        }
    // **02 implement
    }

  public void classifyVertices()
    {
      for (int i=0; i<nc;i++){isInterior[V[i]]=true;}
      for (int i=0; i<nc;i++){
        int cNext=n(i);
        int cPre=p(i);
        if ((O[cNext]==cNext) || (O[cPre]==cPre)){isInterior[V[i]]=false;}
      }
    // **03 implement it
    }

  public void smoothenInterior() { // even interior vertiex locations
    pt[] Gn = new pt[nv];
    // **04 implement it
    for (int v=0;v<nv;v++){
      if (!isInterior[v]) continue;
      //Gn[v]=G[v];
      float x=0;float y=0;float z=0;float w=0;
      for (int c=0;c<nc;c++){
        if (V[c]==v){
          int cNext=n(c);
          int cPre=p(c);
          pt gN=g(cNext);
          pt gP=g(cPre);
          float thisW=d(gN,gP);
          w+=thisW;
          pt pMid=P(gN,0.5f,gP);
          x+=pMid.x*thisW; y+=pMid.y*thisW; z+=pMid.z*thisW;
        }
      }
      Gn[v]=P(x/w,y/w,z/w);
    }
    for (int v=0; v<nv; v++) if(isInterior[v]) G[v].translateTowards(.1f,Gn[v]);
  }


   // **05 implement corner operators in Mesh
  public int v (int c) {return V[c];}                                // vertex of c
  public int o (int c) {
    if (O[c]==c) return O[c];
    else{
      return O[c];
    }
  }                                // opposite corner
  public int l (int c) {return O[n(c)];}                             // left
  public int s (int c) {return n(O[n(c)]);}                             // swing
  public int u (int c) {return p(O[p(c)]);}                             // unswing
  public int r (int c) {return O[p(c)];}                             // right

  public void showVoronoiEdges() // draws Voronoi edges on the boundary of Voroni cells of interior vertices
    {
      for (int v=0;v<nv;v++){

        if (!(isInterior[v])) continue;
        int startC=0;
        for (int c=0;c<nc;c++){
          if (V[c]==v){startC=c;break;}
        }
        int endC=s(startC);
        pt last_center=triCircumcenter(startC);

        while(true){

          pt this_center=triCircumcenter(endC);
          show(last_center,this_center);
          //show(this_center,20);
          last_center=this_center;
          endC=s(endC);
          if (endC==startC) {
            break;
          }
        }
      }
      //System.out.println("---");

    // **06 implement it
    }

  public void showArcs() // draws arcs of quadratic B-spline of Voronoi boundary loops of interior vertices
    {
      for(int v=0;v<nv;v++){
        if (!(isInterior[v])) continue;
        int startC=0;
        for (int c=0;c<nc;c++){
          if (V[c]==v){startC=c;break;}
        }
        int endC=s(startC);
        pt last_center=triCircumcenter(startC);

        while(true){

          pt this_center=triCircumcenter(endC);
          int nextC=s(endC);
          pt next_center=triCircumcenter(nextC);
          show(last_center,this_center);
          pt mid1=P(last_center,0.5f,this_center);
          pt mid2=P(this_center,0.5f,next_center);

          drawParabolaInHat(mid1,this_center,mid2,2);
          //show(this_center,20);
          last_center=this_center;
          endC=s(endC);
          if (endC==startC) {
            break;
          }
        }
      }
    // **06 implement it
    }               // draws arcs in triangles



  public pt triCenter(int c) {return P(g(c),g(n(c)),g(p(c))); }  // returns center of mass of triangle of corner c
  public pt triCircumcenter(int c) {return CircumCenter(g(c),g(n(c)),g(p(c))); }  // returns circumcenter of triangle of corner c


  } // end of MESH
// suppport of 3D picking and dragging in user's (i.e., screen) coordinate system


pt PP=P(); // picked point
Boolean  picking=false;

float dz=0;                                 // distance to camera. Manipulated with wheel or when 's' is pressed and mouse moved
float rx=-0.06f*TWO_PI, ry=-0.04f*TWO_PI;     // view angles manipulated when space bar (but not mouse) is pressed and mouse is moved
pt Viewer = P(); // location of the viewpoint
pt F = P(1000,1000,h_ceiling/2);  // focus point:  the camera is looking at it (moved when 'f or 'F' are pressed
pt Of=P(1000,1000,0), Ofp=P(1000,1000,0); // current and previous point on the floor under the mouse
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
 
public void pillar(pt P, float z, float r) { pt Q = P(P,V(0,0,z)); coneSection(P,Q,r,r); disk(Q,V(0,0,1),r);}
 


// FANS, CONES, AND ARROWS
public void disk(pt P, vec I, vec J, float r) {
  //float da = TWO_PI/36;
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
  //float da = TWO_PI/36;
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
  //float da = TWO_PI/36;
  beginShape(QUAD_STRIP);
    for(float a=0; a<=TWO_PI+da; a+=da) {v(P(P,r*cos(a),I,r*sin(a),J,0,V)); v(P(P,rd*cos(a),I,rd*sin(a),J,1,V));}
  endShape();
  }

public void cone(pt P, vec V, float r) {fan(P,V,r); disk(P,V,r);}

public void beam(pt P, pt Q, float r) // cone section
  {
  stub(P,V(P,Q),r,r);
  }

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
  //float da = TWO_PI/36;
  beginShape(TRIANGLE_FAN);
    vertex(0,0,d);
    for(float a=0; a<=TWO_PI+da; a+=da) vertex(r*cos(a),r*sin(a),0);
  endShape();
  }

public void showCollar(float d, float r, float rd) {
  //float da = TWO_PI/36;
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
  
public void showFloor(float h) 
    {
    fill(yellow); 
    pushMatrix(); 
      translate(0,0,h-1.5f); 
      float d=100;
      int n=20;
      pushMatrix();
        translate(0,-d*n/4,0);
          for(int j=0; j<n; j++)
            {
            pushMatrix();
              translate(-d*n/4,0,0);
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

  pts() {}
  public String toString(){
    if (nv==0){
      return "no points";
    }
    else{
      String out="";
      for (int i=0;i<nv;i++){out+=G[i].toString();}
      out+="|";
      return out;
    }
  }
  public int count() {return nv;}
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

  public pts perturb(float e)
    { for (int i=0; i<nv; i++)
      {G[i].x+=random(-e,e); G[i].y+=random(-e,e);}
    return this;
    }

  public void makeRandom(int n, float w, float d)
    {
    empty();
    int k=0;
    float o=1500;
    while (k<n)
      {
      pt P = P(random(o-w,o+w),random(o-w,o+w));
      addPt(P); k++;
      }
    }

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

  //pts showPicked() {show(G[pv]); return this;}
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
  public pts setZ(float z) {for (int v=0; v<nv; v++)  G[v].z=z; return this;}

  public pts drawBalls(float r) {for (int v=0; v<nv; v++) show(G[v],r); return this;}
  public pts drawColumns(float r, float h) {for (int v=0; v<nv; v++) pillar(G[v],h,r); return this;}
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

  public void setPicekdLabel(char c) {L[pp]=c;}



  public void setFifo()
    {
    _LookAtPt.reset(G[dv],60);
    }


  public void next() {dv=n(dv);}
  public int n(int v) {return (v+1)%nv;}
  public int p(int v) {if(v==0) return nv-1; else return v-1;}


  public int drawDelaunayEdges()
     {
     int triCount=0;
     pt X = P();
     float r=1;  // radius of circumcircle
     for (int i=0; i<nv-2; i++) for (int j=i+1; j<nv-1; j++) for (int k=j+1; k<nv; k++)
        {
        X=CircumCenter(G[i],G[j],G[k]);  r=d(X,G[i]);
        boolean found=false;
        for (int m=0; m<nv; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(d(X,G[m])<=r)) found=true;
        //if (!found) {fill(blue); show(G[i],G[j],G[k]); /* fill(yellow,100); show(X,r); */}
        if (!found) {triCount++; beam(G[i],G[j],rt); beam(G[j],G[k],rt); beam(G[k],G[i],rt);}
        }; // end triple loop
     return triCount;
     }

  public int makeDelaunayEdges(EdgeSet B)
     {
     B.reset();
     int triCount=0;
     pt X = P();
     float r=1;  // radius of circumcircle
     for (int i=0; i<nv-2; i++) for (int j=i+1; j<nv-1; j++) for (int k=j+1; k<nv; k++)
        {
        X=CircumCenter(G[i],G[j],G[k]);  r=d(X,G[i]);
        boolean found=false;
        for (int m=0; m<nv; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(d(X,G[m])<=r)) found=true;
        if (!found) {triCount++; B.addEdge(i,j); B.addEdge(j,k); B.addEdge(k,i);}
        }; // end triple loop
     return triCount;
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
   public String toString()
     {
     return (Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+"; ");
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
   public pt add(float s, pt P)   {x += s*P.x; y += s*P.y; return this;};
   public pt add(vec V) {x+=V.x; y+=V.y; z+=V.z; return this;};
   public pt sub(vec V) {x-=V.x; y-=V.y; z-=V.z; return this;};
   public pt add(float s, vec V) {x+=s*V.x; y+=s*V.y; z+=s*V.z; return this;};
   public pt sub(pt P) {x-=P.x; y-=P.y; z-=P.z; return this;};
   public pt mul(float f) {x*=f; y*=f; z*=f; return this;};
   public pt div(float f) {x/=f; y/=f; z/=f; return this;};
   public pt div(int f) {x/=f; y/=f; z/=f; return this;};
   public pt translateTowards(float s, pt P) {x+=s*(P.x-x);  y+=s*(P.y-y); z+=s*(P.z-z);  return this;};  // transalte by ratio s towards P
   public String toString(){
     return (Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+"; ");
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
public void vert(pt P) {vertex(P.x,P.y,P.z);};                                           // vertex for shading or drawing (between BeginShape() ; and endShape();)
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

//===== PARABOLA
public void drawParabolaInHat(pt A, pt B, pt C, int rec) {
   if (rec==0) { show(A,B); show(B,C); } //if (rec==0) { beam(A,B,rt); beam(B,C,rt); }
   else {
     float w = (d(A,B)+d(B,C))/2;
     float l = d(A,C)/2;
     float t = l/(w+l);
     pt L = L(A,t,B);
     pt R = L(C,t,B);
     pt M = P(L,R);
     drawParabolaInHat(A,L, M,rec-1); drawParabolaInHat(M,R, C,rec-1);
     };
   };

public float determinant(float a1,float a2,float a3,float a4,float a5,float a6,float a7,float a8,float a9){
  float v1=a1*a5*a9;
  float v2=a2*a6*a7;
  float v3=a3*a4*a8;
  float v4=a3*a5*a7;
  float v5=a2*a4*a9;
  float v6=a1*a6*a8;
  return v1+v2+v3-v4-v5-v6;
}
// **** LIBRARIES
    // to save screen shots as PDFs, does not always work: accuracy problems, stops drawing or messes up some curves !!!


// ************************************ IMAGES & VIDEO 
int pictureCounter=0, frameCounter=0;
Boolean filming=false, change=false;
String FileName = "MyPictures";
PImage myFace; // picture of author's face, should be: data/pic.jpg in sketch folder
public void snapPicture() {saveFrame("../PICTURES/"+FileName+"/P"+nf(pictureCounter++,3)+".jpg"); }

// ******************************************COLORS 
int black=0xff000000, white=0xffFFFFFF, // set more colors using Menu >  Tools > Color Selector
   red=0xffFF0000, green=0xff00FF01, blue=0xff0300FF, yellow=0xffFEFF00, cyan=0xff00FDFF, magenta=0xffFF00FB,
   grey=0xff818181, orange=0xffFFA600, brown=0xffB46005, metal=0xffB5CCDE, 
   lime=0xffA4FA83, pink=0xffFCC4FA, dgreen=0xff057103,
   lightWood=0xffF5DEA6, darkWood=0xffD8BE7A;
public void pen(int c, float w) {stroke(c); strokeWeight(w);}

// ******************************** TEXT , TITLE, and USER's GUIDE
public void scribe(String S, float x, float y) {fill(0); text(S,x,y); noFill();} // writes on screen at (x,y) with current fill color
public void scribeHeader(String S, int i) {fill(0); text(S,10,20+i*20); noFill();} // writes black at line i
public void scribeHeaderRight(String S) {fill(0); text(S,width-7.5f*S.length(),20); noFill();} // writes black on screen top, right-aligned
public void scribeFooter(String S, int i) {fill(0); text(S,10,height-10-i*20); noFill();} // writes black on screen at line i from bottom
public void scribeAtMouse(String S) {fill(0); text(S,mouseX,mouseY); noFill();} // writes on screen near mouse
public void scribeMouseCoordinates() {fill(black); text("("+mouseX+","+mouseY+")",mouseX+7,mouseY+25); noFill();}

// *****  CLIPBOARD ITS CONTENT MAY BE USED TO SET FILENAME FOR SAVING/READING POINTS
public static String getClipboard() {   // returns content of clipboard (if it contains text) or null 
       Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
       try {if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
               String text = (String)t.getTransferData(DataFlavor.stringFlavor);
               return text; }} 
       catch (UnsupportedFlavorException e) { } catch (IOException e) { }
       return null;
       }
       
//Not used in this sketch, but convenient for saving 
public static void setClipboard(String str) { // This method writes a string to the system clipboard. 
       StringSelection ss = new StringSelection(str);
       Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
       }


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
public float triArea(pt A, pt B, pt C) {return 0.5f*det3(V(A,B),V(A,C)); }
public float triThickness(pt A, pt B, pt C) {float a = abs(disToLine(A,B,C)), b = abs(disToLine(B,C,A)), c = abs(disToLine(C,A,B)); return min (a,b,c); }
public boolean ccw(pt A, pt B, pt C) {return dot(V(A,B),cross(V(A,C),V(0,0,1)))>0 ;} // CLOCKWISE
public void keyPressed() 
    { 
    if(key=='~') filming=!filming;
    if(key=='!') snapPicture();
    if(key=='@') ; // make a .TIF picture of the canvas, better quality, but large file
    if(key=='#') exit();
    if(key=='$') ;
    if(key=='%') P.makeRandom(30,1500,10);
    if(key=='^') showCorner=!showCorner;
    if(key=='&') ; 
    if(key=='*') ;    
    if(key=='(') ;
    if(key==')') ;  
    if(key=='_') showFloor=!showFloor;
    if(key=='+') ;

    if(key=='`') ;  // hold to zoom with mouse
    if(key=='1') {step1=!step1; if(step1) {M.reset(); M.loadVertices(R.G,R.nv); M.triangulate();}}
    if(key=='2') {step2=!step2; if(step2) M.computeO();}
    if(key=='3') step3=!step3;
    if(key=='4') step4=!step4;
    if(key=='5') step5=!step5;
    if(key=='6') step6=!step6;
    if(key=='7') step7=!step7; 
    if(key=='8') step8=!step8;
    if(key=='9') step9=!step9;
    if(key=='0') step10=!step10;
    if(key=='-') ;
    if(key=='=') S.copyFrom(R);

    if(key=='a') {animating=!animating;}
    if(key=='b') {for(int i=0; i<10; i++) M.smoothenInterior(); M.writeVerticesTo(R);}
    if(key=='c') ; 
    if(key=='d') {R.set_pv_to_pp(); R.deletePicked();}  
    if(key=='e') ;
    if(key=='f') ; // hold to move focus with mouse pressed
    if(key=='g') P.loadPts("data/pts"); 
    if(key=='h') ; // hold do change column height with mouse
    if(key=='i') ; // insert additional vertex
    if(key=='j') ;
    if(key=='k') ; 
    if(key=='l') M.left();
    if(key=='m') {M.reset(); M.loadVertices(R.G,R.nv); M.triangulate(); M.computeO();}
    if(key=='n') M.next();
    if(key=='o') M.opposite();  
    if(key=='p') M.previous();
    if(key=='q') ; 
    if(key=='r') M.right(); 
    if(key=='s') M.swing();
    if(key=='t') ; 
    if(key=='u') M.unswing();
    if(key=='v') ; 
    if(key=='w') P.savePts("data/pts");   // save vertices to pts 
    if(key=='x') ; // hold to move selected vertex with mouse
    if(key=='y') ;
    if(key=='z') ; 

    if(key=='A') showArcs=!showArcs;
    if(key=='B') showBalls=!showBalls ;  
    if(key=='C') {FileName=getClipboard(); println("PicturesFileName="+FileName); pictureCounter=0;} // uses clipboard content to set file name for images 
    if(key=='D') ;  
    if(key=='E') showEdges=!showEdges;
    if(key=='F') ; // press to adjust height of focus with mouse pressed
    if(key=='G') {P.loadPts("data/"+FileName+".pts"); R=P; S=Q;} // jarek
    if(key=='H') ; 
    if(key=='I') showVoronoiFaces=!showVoronoiFaces; 
    if(key=='J') ;
    if(key=='K') ; 
    if(key=='L') live = !live;
    if(key=='M') ; 
    if(key=='N') ;
    if(key=='O') showOpposite=!showOpposite;
    if(key=='P') showPillars=!showPillars;
    if(key=='Q') ;
    if(key=='R') {R=P; S=Q;}; 
    if(key=='S') {S=P; R=Q;};
    if(key=='T') showTriangles=!showTriangles; 
    if(key=='U') ;
    if(key=='V') showVoronoi=!showVoronoi; 
    if(key=='W') P.savePts("data/"+FileName+".pts"); 
    if(key=='X') ;  // hold to move all vertices with mouse
    if(key=='Y') ;
    if(key=='Z') ;  

    if(key=='{') ;
    if(key=='}') ;
    if(key=='|') ; 
    if(key=='[') ;
    if(key==']') ; 
    if(key=='\\') ;
    if(key==':') R.perturb(100); 
    if(key=='"') ;    
    if(key==';') R.perturb(1); 
    if(key=='\'') ;    
    if(key=='<') M.left();
    if(key=='>') M.right();
    if(key=='?') scribeText=!scribeText; // toggle display of help text and authors picture 
    if(key==',') ; 
    if(key=='.') ; // change radius of columns
    if(key=='/') M.printCorner(); 
  
    if(key==' ') // SPACE : hold to rotate view with mouse
    
    if (key == CODED) 
       {
       String pressed = "Pressed coded key ";
       if (keyCode == UP) {pressed="UP";   }
       if (keyCode == DOWN) {pressed="DOWN";   };
       if (keyCode == LEFT) {pressed="LEFT";   };
       if (keyCode == RIGHT) {pressed="RIGHT";   };
       if (keyCode == ALT) {pressed="ALT";   };
       if (keyCode == CONTROL) {pressed="CONTROL";   };
       if (keyCode == SHIFT) {pressed="SHIFT";   };
       println("Pressed coded key = "+pressed); 
       } 
    println("key pressed = "+key);
    
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
  if(!keyPressed) {R.set_pv_to_pp(); println("picked vertex "+R.pp);}
  if(keyPressed && key=='i') {R.addPt(Of);}
  change=true;
  }
  
public void mouseMoved() 
  {
  //if (!keyPressed) 
  if (keyPressed && key==' ') {rx-=PI*(mouseY-pmouseY)/height; ry+=PI*(mouseX-pmouseX)/width;};
  if (keyPressed && key=='`') dz+=(float)(mouseY-pmouseY); // approach view (same as wheel)
  if (keyPressed) change=true;
  }
  
public void mouseDragged() 
  {
  if (!keyPressed) R.setPickedTo(Of); 
//  if (!keyPressed) {Of.add(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); }
  if (keyPressed && key==CODED && keyCode==SHIFT) {Of.add(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));};
  if (keyPressed && key=='x') R.movePicked(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
  if (keyPressed && key=='z') R.movePicked(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
  if (keyPressed && key=='X') R.moveAll(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
  if (keyPressed && key=='Z') R.moveAll(ToK(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
  if (keyPressed && key=='.') {rb+=20.f*(mouseX-pmouseX)/width; }
  if (keyPressed && key=='h') {columnHeight-=100.f*(mouseY-pmouseY)/width; }
  if (keyPressed && key=='f')  // move focus point on plane
    {
    if(center) F.sub(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
    else F.add(ToIJ(V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
    }
  if (keyPressed && key=='F')  // move focus point vertically
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

String title ="Lattice Maker", name ="TEAM NAMES",
       menu="?:help, t/T:move view, space:rotate view, `/wheel:zoom, !:picture, ~:(start/stop) filming,  #:quit",
       guide="click&drag:pick&slide, _:flip ceiling/floor, x/X:move picked/all, m/M:perturb, X:slide All, |:snap heights, l/L:load, w/W:write"; // user's guide
/*

VERTICES
click      to select closest
mouse-drag to move
d          to delete closest
i & click  to inserts at mouse
e/E        to perturn a little/lot
=          to copy real vertex set R into saved vertex set S
S          to   switch to editing the saved set
R          to switch to editing the real set
g/G        to get (load) vertices from data.pts / from named file
w/W        to write (save) vertices to data.pts / to named file
;/:        to perturb vertices a little/a lot (if nt live, use m afterwards to recompute mesh)
C      to use content of clipboard as folder name for saving pictures


BALLS * PILLARS
B         to show/hide balls
P         to show/hide pillars
E         to show/hide the edges of the triangulation 
. & drag  to change radius
h & drag  to change pillar height

MESH
l         to toggle live mode where the mesh is recmputed at each frame or only when m or 1 & 2 are pressed
m         to recompute mesh in non-live mode
T         to show/hide triangles
V         to show/hide Voronoi edges
^         to show/hide current corner of mesh
b         to smoothen interior vertices
O         to show/hide black arcs between opposite corners
I         to show/hide filling Voronoi faces of interior vertices

PICTURES
_      to show/hide floor
~      to start/stop filming
!      to save picture
C      to use content of clipboard as folder name for saving pictures

MY PHASES
1        to show/hide results of 1
2        to show/hide results of 2
3        to show/hide results of 3
...

*/
  public void settings() {  size(900, 900, P3D);  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
