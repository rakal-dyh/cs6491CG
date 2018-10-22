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
  boolean isLine;

  InterpolationWithTwoDMethod(pt A,pt B,vec va,vec vb){
    this.A=A;
    this.B=B;
    this.va=U(va);
    this.vb=U(vb);
    this.vb.x=-this.vb.x;
    this.vb.y=-this.vb.y;
    this.vb.z=-this.vb.z;

  }
  void calculate(){
    float a;
    float b;
    float c;
    a=(va.x-vb.x)^2+(va.y-vb.y)^2+(va.z-vb.z)^2-4;
    b=2*((va.x-vb.x)*(A.x-B.x)+(va.y-vb.y)*(A.y-B.y)+(va.z-vb.z)*(A.z-B.z));
    c=(A.x-B.x)^2+(A.y-B.y)^2+(A.z-B.z)^2;
    d=(-b+(b*b-4*a*c)^0.5)/(2*a);
    pt AA=P(A,V(d,va));
    pt BB=P(B,V(d,vb));
    vc=U(V(AA,BB));
    C=P(AA,0.5,BB);
    vna=B(va,vc);
    vnb=B(vb,vc);
    vnc=B(cv,va);
    //calculate O1
    xav=vna.x;
    xa=a.x;
    xcv=vnc.x;
    cx=c.x;
    float d=(xa-xc)/(xcv-xav);
    if (d<0){

    }


    //calculate O2

  }
}


// class findCircleCenter(){
//
//   findCircleCenter(){
//
//   }
// }
