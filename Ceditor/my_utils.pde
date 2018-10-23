//Given two point A,B, and the direction Va and Vb at those two points, calculate the distance
//that satisfy AA'=d, BB'=d, A'B'=2d, where A' and B' are two points that A'=A+d*Va, B'=B+d*Vb


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
    d=(-b+sqrt(b*b-4*a*c))/(2*a);
    pt AA=P(A,V(d,va));
    pt BB=P(B,V(d,vb));
    vc=U(V(AA,BB));
    C=P(AA,0.5,BB);
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
    if (test<0.0001){
      this.isLine=true;
    }
    else{
      this.isLine=false;
    }
    this.r=0;
    this.O=P();
    if (!isLine){
      float theta=angle(V(AA,A),V(AA,C))/(2.0);
      this.r=d*tan(theta);
      this.O=P(A,r,vna);
    }
  }
}
