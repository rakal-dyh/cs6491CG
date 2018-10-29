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

pts PC = new pts();//point on circle aournd the beginning of first elbow
int lastChoosePC;//the point selected on PC last time

multiPointsMotion2D MPM;
int numOfPeriods;

float elbow_twist;
float ppath_twist;

void setup() {
  myFace = loadImage("data/pic.jpg");  // load image from file pic.jpg in folder data *** replace that file with your pic of your own face
  textureMode(NORMAL);
  //size(900, 900, P3D); // P3D means that we will do 3D graphics
  size(600, 600, P3D); // P3D means that we will do 3D graphics
  P.declare(); Q.declare(); R.declare(); // P is a polyloop in 3D: declared in pts
  //P.resetOnCircle(6,100); Q.copyFrom(P); // use this to get started if no model exists on file: move points, save to file, comment this line
  P.loadPts("data/ptsInitial");  Q.loadPts("data/pts2"); // loads saved models from file (comment out if they do not exist yet)

  PC.declare();
  lastChoosePC=0;

  MPM=new multiPointsMotion2D();
  numOfPeriods=16;

  noSmooth();
  frameRate(30);
  }

void draw() {
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

  //-----------------------------------------
  //----project code implementation started--
  //-----------------------------------------

  //---
  //create the PCC and correspond elbows
  //para1: need not be changed, P is the pts which can be modified bt user
  //para2: how many cross section circles for each elbow
  //para3: In each elbow corss section circle, how many vertices used to describe this circle
  //para3: the radius of elbow corss section circle
  elbowControl elbow=new elbowControl(P, 64, 4, 32);//create elbow curve object

  //below command will draw origin PCC elbow
  //elbow.curvebow.draw();

  //this commend will draw control polygon with extended points calculate from PCC
  elbow.extendPolygon.drawClosedCurve(1);

  //-----------
  //draw braids

  //create braids instance
  //para1: elbow.curvebow, which create from elbowControl
  //para2: MPM, this need not be changed. This instace includes all math functions to calculate braids motion in 2D plain
  //para3: braids type, can be 0,1,2,3,4
  //       0:two tubes rotate with same circle center
  //       1:three tubes,each time two of three will change position and rest one keep, see multiPointsMotion2D method1
  //       2:four tubes move like skoubidou, the math function is piecewise function
  //       3:only onw tube move on a 2D circle, which is the reduced version of method0
  //       4:four tubes move like skoubidou, trigonometric function
  //para4: how many cycles for braids tubes move in each elbow
  CurveBraidFrames cbf = new CurveBraidFrames(elbow.curvebow, MPM, 4, numOfPeriods);
  //cbf.draw("cylinder");

  //call draw function to draw braids
  cbf.draw("PCC");
  //cbf.draw("closedCurve");

  //-------------
  //choose points on first cross section circle
  PC=elbow.curvebow.startCircle;
  PC.pv=lastChoosePC;
  PC.SETppToIDofVertexWithClosestScreenProjectionTo(Mouse());
  fill(black); PC.showPicked(8);


  //-----------------------------------------
  //----project code implementation ended----
  //-----------------------------------------

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
