class InterpolationWithTwoDMethod{
  //Given two point A,B, and the direction Va and Vb at those two points, calculate the distance
  //that satisfy AA'=d, BB'=d, A'B'=2d, where A' and B' are two points that A'=A+d*Va, B'=B+d*Vb
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
  pt AA;
  pt BB;

  InterpolationWithTwoDMethod(pt A,pt B,vec va,vec vb){
    this.A=A;
    this.B=B;
    this.va=U(va);
    this.vb=U(vb);
    this.vb.x=-this.vb.x;
    this.vb.y=-this.vb.y;
    this.vb.z=-this.vb.z;

    //System.out.println(A);


    // B.x+=0.000001;
    // B.y+=0.000001;
    // B.z+=0.000001;
    // this.vb.x=this.vb.x;
    // this.vb.y=this.vb.y;
    // this.vb.z=this.vb.z;
    // if (va.x-vb.x==0 && va.y-vb.y==0 && va.z-vb.z==0){
    //   AA=P();BB=P();O1=P();O2=P();isLine1=true;isLine2=true;C=A;vc=va;d=0;
    // }
    // else{
    //   calculate();
    // }
    calculate();

  }
  void calculate(){
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

    if (a==0){d=0;}
    else{
      d=(-b-sqrt(b*b-4*a*c))/(2*a);
    }
    //if (d==0){d=0.01;}
    if (a==0){
      System.out.println("000!!!");
      if (b==0){
        d=0;
      }
      else{
        d=-c/b;
      }
    }
    //
    // }
    // System.out.println(d);
    // System.out.println(a);
    // char qq='-';
    // System.out.println(qq);
    this.AA=P(A,V(d,va));
    this.BB=P(B,V(d,vb));
    //System.out.println(AA);
    //System.out.println(BB);
    vc=U(V(AA,BB));
    //System.out.println(vc);
    vec vc2=V(-1,vc);
    C=P(AA,0.5,BB);
    //System.out.println(C);

    vec vna=B(va,vc);//norm to va in va,vc plain
    vec vnb=B(vb,vc2);
    vec vnc1=B(vc,va);
    vec vnc2=B(vc2,vb);

    vec vac=V(A,C);
    vec vca=V(C,A);
    vec vbc=V(B,C);
    vec vcb=V(C,B);

    //check the angle between vac and vna, to make it <90
    float angle_ac=angle(vac,vna);
    //if (angle_ac!=angle_ac){angle_ac=0;}
    if (angle_ac>PI){
      vna=V(-1,vna);
    }
    //same with vca and vnc1
    float angle_ca=angle(vca,vnc1);
    //if (angle_ca!=angle_ca){angle_ca=0;}
    if (angle_ca>PI){
      vnc1=V(-1,vnc1);
    }
    //check the angle between vbc and vnb, to make it <90
    float angle_bc=angle(vbc,vnb);
    //if (angle_bc!=angle_bc){angle_bc=0;}
    if (angle_bc>PI){
      vnb=V(-1,vnb);
    }
    //same with vcb and vnc2
    float angle_cb=angle(vcb,vnc2);
    //if (angle_cb!=angle_cb){angle_cb=0;}
    if (angle_cb>PI){
      vnc2=V(-1,vnc2);
    }

    findCircleCenter f1=new findCircleCenter(vna,vnc1,A,C,AA,d);
    findCircleCenter f2=new findCircleCenter(vnb,vnc2,B,C,BB,d);
    this.O1=f1.O;
    this.O2=f2.O;
    //if (O1.x!=O1.x) O1=P(A);
    //if (O2.x!=O2.x) O2=P(B);
    this.isLine1=f1.isLine;
    this.isLine2=f2.isLine;
    //System.out.println(O1);
  }
}

class findCircleCenter{
  pt O;
  boolean isLine;
  float r;

  findCircleCenter(vec vna,vec vnc,pt A,pt C,pt AA,float d){
    // System.out.println(angle(vna,vnc));
    // System.out.println(vna.x);
    // System.out.println(vna.y);
    // System.out.println(vna.z);
    // System.out.println(vnc.x);
    // System.out.println(vnc.y);
    // System.out.println(vnc.z);
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
    // if (d==0) {
    //   System.out.println(vna);
    //   System.out.println(vnc);
    //   System.out.println(A);
    //   System.out.println(C);
    //   System.out.println(AA);
    //   }
    //float test=abs(xcv)-abs(xav)+abs(ycv)-abs(yav)+abs(zcv)-abs(zav);
    // if (test<0.00000001){
    //   this.isLine=true;
    // }
    // else{
    //   this.isLine=false;
    // }
    if (vna.x==0 && vna.y==0 && vna.z==0 && vnc.x==0 && vnc.y==0 && vnc.z==0){
      this.isLine=true;
    }
    else{
      this.isLine=false;
    }


    //this.isLine=false;
    this.r=0;
    this.O=P();
    if (!isLine){
      float theta=angle(V(AA,A),V(AA,C))/(2.0);
      //System.out.println(theta);
      if (theta!=theta){theta=0.1;}
      this.r=d*tan(theta);
      //if (r==0){
      //  r=0.0;
      //}
      this.O=P(A,r,vna);
      // if (d==0){
      //   System.out.println(r);
      //   System.out.println(theta);
      //   System.out.println(A);
      //   System.out.println(O);
      // }
    }
    //System.out.println(theta);
    // System.out.println(A);
    // System.out.println(r);
    // System.out.println(O);
    // System.out.println("---");
  }
}

class elbowControl{
  Elbow[] elbows;
  CurveElbow curvebow;

  pt[] inputPoints;
  pt[] extendPoints;
  pts extendPolygon;
  vec[] verticesVec;

  //construction method which will call main function
  elbowControl(pts initialPts){
    mainDo(initialPts,0,0,true,false,false, 36, 36, 20);
  }

  elbowControl(pts initialPts, int num_of_circles, int num_of_circle_vectors, float rc){
    mainDo(initialPts,0,0,true,false,false, num_of_circles, num_of_circle_vectors, rc);
  }

  //first parameter: fixed, the pts given from gui
  //second parameter: decide whether using subdivision for pts from gui,
  //  0 - default, no subdivision
  //third parameter: decide the method to calculate the vec for each vertices
  //  0 - default, using the previous and next pt to calcualte vec, ex: for A,B,C vec(B)=V(A,C)
  //  1 - used for non closed curve, the first point and last point will use different way with method 0, ex: for A,B, vec(A)=V(A,B)
  //forth parameter: true - closed curver
  //fifth parameter: ture - head tail twisted to connected.
  //sixth parameter: ture - plot ppath
  void mainDo(pts initialPts,int subdivisionMethod,int vecMethod,boolean connectTailHead, boolean alignTailHead,boolean drawPPath,
              int num_of_circles, int num_of_circle_vectors, float rc){
    pt[] points=pointsNoOverlapped(initialPts);//from pts to pt[], remove overlapped points

    if (subdivisionMethod==0) this.inputPoints=subdivision_default(points);//subdivision, signal=0 means no subdivision
    int len=this.inputPoints.length;

    if (vecMethod==0) this.verticesVec=createVec_default(this.inputPoints);//create vertives vec, signal=0 means default method
    if (vecMethod==1) this.verticesVec=createVec_FrontEndNoConnection(this.inputPoints);

    this.extendPoints=new pt[2*len];

    this.elbows=calculateElbowsPCC(num_of_circles, num_of_circle_vectors, rc);

    extendPolygon=getExtendPolygon();

    //make curve, twist involed
    this.curvebow=new CurveElbow(this.elbows,connectTailHead,alignTailHead,drawPPath);

  }

  //preprocess,change from pts to pt[], with duplicate point removed
  pt[] pointsNoOverlapped(pts initialPts){
    int initalLength=initialPts.nv;

    //if given points number is not enough to make close elbow (<3), then return default three points
    if (initalLength<=2){
      pt[] returnPt=new pt[3];
      returnPt[0]=P(1,0,0);
      returnPt[1]=P(0,1,0);
      returnPt[2]=P(0,0,1);
      return returnPt;
    }

    //count how many non-overlapped points;
    int NumPointUsed=1;
    for (int i=1;i<initalLength;i++){
      if (d(initialPts.G[i-1],initialPts.G[i])<0.001) continue;
      NumPointUsed++;
    }

    //create point will be used, without overlapping
    pt[] points=new pt[NumPointUsed];
    points[0]=initialPts.G[0];
    int pos=1;
    for (int i=1;i<initalLength;i++){
      if (d(initialPts.G[i-1],initialPts.G[i])<0.001) continue;
      points[pos]=initialPts.G[i];
      pos++;
    }
    return points;
  }

  //modifty input control points using subdivision, default is no change
  pt[] subdivision_default(pt[] inputPoints) {return inputPoints;}

  //create the vertices vector
  vec[] createVec_default(pt[] inputPoints){
    int len=inputPoints.length;
    vec[] returnVec=new vec[len];
    for (int i=0;i<len;i++) returnVec[i]=V(getPrevious(i,inputPoints,len),getNext(i,inputPoints,len));
    return returnVec;
  }

  vec[] createVec_FrontEndNoConnection(pt[] inputPoints){
    int len=inputPoints.length;
    vec[] returnVec=new vec[len];
    for (int i=0;i<len;i++){
      if (i==0){returnVec[i]=V(inputPoints[i],getNext(i,inputPoints,len));continue;}
      if (i==len-1){returnVec[i]=V(getPrevious(i,inputPoints,len),inputPoints[i]);continue;}
      returnVec[i]=V(getPrevious(i,inputPoints,len),getNext(i,inputPoints,len));
    }
    return returnVec;
  }



  Elbow[] calculateElbowsPCC(int num_of_circles, int num_of_circle_vectors, float rc){
    int n=this.inputPoints.length;
    Elbow[] elbows=new Elbow[2*n];
    for(int i=0;i<n;i++){
      pt A=this.inputPoints[i];
      pt B=getNext(i,this.inputPoints,n);
      vec va=this.verticesVec[i];
      vec vb=this.verticesVec[getNextInd(i,n)];
      InterpolationWithTwoDMethod tmpElbow=new InterpolationWithTwoDMethod(A,B,va,vb);

      elbows[2*i]=new Elbow(tmpElbow.A,tmpElbow.C,tmpElbow.O1,false, num_of_circles, num_of_circle_vectors, rc);
      elbows[2*i+1]=new Elbow(tmpElbow.C,tmpElbow.B,tmpElbow.O2,false, num_of_circles, num_of_circle_vectors, rc);
      // System.out.println('-');
      //System.out.println(tmpElbow.O1);
      //System.out.println(tmpElbow.O2);
      // System.out.println('-');
      extendPoints[2*i]=tmpElbow.A;
      extendPoints[2*i+1]=tmpElbow.C;
    }
    return elbows;
  }

  pts getExtendPolygon(){
    pts expts=new pts();

    expts.declare();
    expts.empty();
    for(int i=0;i<extendPoints.length;i++){
      expts.addPt(this.extendPoints[i]);
    }
    return expts;
  }

  void calculateTwoElbow(){}

  pt getPrevious(int pos,pt[] points,int len){
    if(pos==0){
      return points[len-1];
    }
    else{
      return points[pos-1];
    }
  }

  pt getNext(int pos, pt[] points,int len){
    if(pos==len-1){
      return points[0];
    }
    else{
      return points[pos+1];
    }
  }

  int getNextInd(int pos,int len){
    if(pos==len-1){
      return 0;
    }
    else{
      return pos+1;
    }
  }

}
