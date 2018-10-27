class multiPointsMotion2D{

  float[] cood;
  float radius;
  int num;

  vec beginVec;
  pt O=;

  multiPointsMotion2D(){
    this.beginVec=V(P(0,0,0),P(0,1,0));
    this.O=P(0,0,0);
  }

  void set(float time,int methodId){
    while (time>1) t--;
    if (methodId==0){method0(time;)}

  }

  //2 circles rotation
  void method0(float t){
    //2 circles method
    //

    this.num=2;
    this.radius=0.5;
    this.cood=new float[2*this.num];

    pt A=P(0.5,0.0);
    pt B=P(-0.5,0.0);
    theta=t*TWO_PI;

    A=MyRotate2D(A,theta);
    B=MyRotate2D(B,theta);

    this.cood[0]=returnD(A);
    this.cood[1]=returnAngle(A);
    this.cood[2]=returnD(B);
    this.cood[3]=returnAngle(B);
  }

  //3 circles braids
  //abc,bac,bca,cba,cab,acb,abc
  //p:   1,   2,   3,   4,   5,   6
  //a:  up,down,   0,  up,down,   0
  //b:down,   0,  up,down,   0,  up
  //c:   0,  up,down,   0,  up,down
  void method1(float t){
    this.num=3;
    this.radius=0.3;
    this.cood=new float[2*this.num];
    pt A0=P(-0.6,0.0,0.0);
    pt B0=P(0,0,0);
    pt C0=P(0.6,0,0);
    pt O1=P(-0.3,0,0);
    pt O2=P(0.3,0,0);
    pt A;pt B;pt C;
    if (t*6<1){
      //abc
      A=A0;
      B=B0;
      C=C0;
      theta=(t*6)-0)*PI;
      theta=-theta;
      A=MyRotate2D(A,theta,O1);
      B=MyRotate2D(B,theta,O1);
    }
    if (t*6<2 && t*6>=1){
      //bac
      A=B0;
      B=A0;
      C=C0;
      theta=(t*6)-1)*PI;
      A=MyRotate2D(A,theta,O2);
      C=MyRotate2D(C,theta,O2);
    }
    if (t*6<3 && t*6>=2){
      //bca
      A=C0;
      B=A0;
      C=B0;
      theta=(t*6)-2)*PI;
      theta=-theta;
      B=MyRotate2D(B,theta,O1);
      C=MyRotate2D(C,theta,O1);
    }
    if (t*6<4 && t*6>=3){
      //cba
      A=C0;
      B=B0;
      C=A0;
      theta=(t*6)-3)*PI;
      B=MyRotate2D(B,theta,O2);
      A=MyRotate2D(A,theta,O2);
    }
    if (t*6<5 && t*6>=4){
      //cab
      A=B0;
      B=C0;
      C=A0;
      theta=(t*6)-4)*PI;
      theta=-theta;
      A=MyRotate2D(A,theta,O1);
      C=MyRotate2D(C,theta,O1);
    }
    if (t*6>=5){
      //acb
      A=A0;
      B=C0;
      C=B0;
      theta=(t*6)-5)*PI;
      B=MyRotate2D(B,theta,O2);
      C=MyRotate2D(C,theta,O2);
    }
    this.cood[0]=returnD(A);
    this.cood[1]=returnAngle(A);
    this.cood[2]=returnD(B);
    this.cood[3]=returnAngle(B);
    this.cood[4]=returnD(C);
    this.cood[5]=returnAngle(C);

  }

  //skoubidou
  //       |               |               | r2            | r2
  //    r1 | b1         r1 |    b1         |    b1      b1 |
  // ------|------   ------|------   ------|------   ------|------
  //    b2 | r2      b2    | r2      b2    |               | b2
  //       |               |            r1 |            r1 |

  //       |               |            r1 |            r1 |
  //    b1 | r2      b1    | r2      b1    |               | b1
  // ------|------   ------|------   ------|------   ------|------
  //    r1 | b2         r1 |    b2         |    b2      b2 |
  //       |               |               | r2            | r2

// 3       |
// 2    r1 | b1
//   ------|------
// 1    b2 | r2
// 0       |
//   0   1   2   3
  void method2(float t){
    this.num=4;
    this.radius=0.2;
    this.cood= new float[8];
    pt[][] potentialPt= new pt[4][4];
    for (int i=0;i<4;i++){
      for (int j=0;j<4;j++){
        potentialPt[i][j]=P(-0.6+0.4*i,-0.6+0.4*j,0);
      }
    }
    pt r1; pt r2; pt b1; pt b2;
    if (t*8<1){
      r1=P(potentialPt[1][2]);
      r2=P(potentialPt[2][1]);
      b1=P(potentialPt[2][2]);
      b2=P(potentialPt[1][1]);
      frame=t*8;
      b1e=potentialPt[3][2]);
      b2e=potetnialPt[0][1]);
      b1=P(b1,frame,b1e);
      b2=P(b2,frame,b2e);
    }
    if (t*8<2 && t*8>=1){
      r1=P(potentialPt[1][2]);
      r2=P(potentialPt[2][1]);
      b1=P(potentialPt[3][2]);
      b2=P(potentialPt[0][1]);
      frame=t*8-1;
      r1e=P(potentialPt[1][0]);
      r2e=P(potetnialPt[2][3]);
      r1=P(r1,frame,r1e);
      r2=P(r2,frame,r2e);
    }
    if (t*8<3 && t*8>=2){
      r1=P(potentialPt[1][0]);
      r2=P(potentialPt[2][3]);
      b1=P(potentialPt[3][2]);
      b2=P(potentialPt[0][1]);
      frame=t*8-2;
      b1e=P(potentialPt[1][2]);
      b2e=P(potetnialPt[2][1]);
      b1=P(b1,frame,b1e);
      b2=P(b2,frame,b2e);
    }
    if (t*8<4 && t*8>=3){
      r1=P(potentialPt[1][0]);
      r2=P(potentialPt[2][3]);
      b1=P(potentialPt[1][2]);
      b2=P(potentialPt[2][1]);
      frame=t*8-3;
      r1e=P(potentialPt[1][1]);
      r2e=P(potetnialPt[2][2]);
      r1=P(r1,frame,r1e);
      r2=P(r2,frame,r2e);
    }
    if (t*8<5 && t*8>=4){
      r1=P(potentialPt[1][1]);
      r2=P(potentialPt[2][2]);
      b1=P(potentialPt[1][2]);
      b2=P(potentialPt[2][1]);
      frame=t*8-4;
      b1e=P(potentialPt[0][2]);
      b2e=P(potetnialPt[3][1]);
      b1=P(b1,frame,b1e);
      b2=P(b2,frame,b2e);
    }
    if (t*8<6 && t*8>=5){
      r1=P(potentialPt[1][1]);
      r2=P(potentialPt[2][2]);
      b1=P(potentialPt[0][2]);
      b2=P(potentialPt[3][1]);
      frame=t*8-5;
      r1e=P(potentialPt[1][3]);
      r2e=P(potetnialPt[2][0]);
      r1=P(r1,frame,r1e);
      r2=P(r2,frame,r2e);
    }
    if (t*8<7 && t*8>=6){
      r1=P(potentialPt[1][3]);
      r2=P(potentialPt[2][0]);
      b1=P(potentialPt[0][2]);
      b2=P(potentialPt[3][1]);
      frame=t*8-6;
      b1e=P(potentialPt[2][2]);
      b2e=P(potetnialPt[1][1]);
      b1=P(b1,frame,b1e);
      b2=P(b2,frame,b2e);
    }
    if (t*8>=7){
      r1=P(potentialPt[1][3]);
      r2=P(potentialPt[2][0]);
      b1=P(potentialPt[2][2]);
      b2=P(potentialPt[1][1]);
      frame=t*8-7;
      r1e=P(potentialPt[1][2]);
      r2e=P(potetnialPt[2][1]);
      r1=P(r1,frame,r1e);
      r2=P(r2,frame,r2e);
    }
    this.cood[0]=returnD(r1);
    this.cood[1]=returnAngle(r1);
    this.cood[2]=returnD(b1);
    this.cood[3]=returnAngle(b1);
    this.cood[4]=returnD(r2);
    this.cood[5]=returnAngle(r2);
    this.cood[6]=returnD(b2);
    this.cood[7]=returnAngle(b2);
  }


  //utilities;

  //2d plain rotation
  pt MyRotate2D(pt A, float theta){
    float xx=cos(theta)*A.x-sin(theta)*A.y;
    float yy=sin(theta)*A.x+cos(theta)*A.y;
    A.set(xx,yy,0);
    return A:
  }

  //2d palin rotation along center point O
  pt MyRotate2D(pt A, float theta, pt O){
    A=A.sub(O);
    A=MyRotate2D(A,theta);
    A=A.add(O);
  }

  float returnAngle(pt A){
    float angleOA=angle(this.beginVec,V(this.O,A));
    if (A.x<0){
      angleOA=-angleOA;
    }
    return angleOA;
  }
  float returnD(pt A){
    return d(this.O,A);
  }



}