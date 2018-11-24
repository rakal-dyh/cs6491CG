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
  void reset() {nv=0; nt=0; nc=0;}                                                  // removes all vertices and triangles
  void loadVertices(pt[] P, int n) {nv=0; for (int i=0; i<n; i++) addVertex(P[i]);}
  void writeVerticesTo(pts P) {for (int i=0; i<nv; i++) P.G[i].setTo(G[i]);}
  void addVertex(pt P) { G[nv++].setTo(P); }                                             // adds a vertex to vertex table G
  void addTriangle(int i, int j, int k) {V[nc++]=i; V[nc++]=j; V[nc++]=k; nt=nc/3; }     // adds triangle (i,j,k) to V table

  // CORNER OPERATORS
  int t (int c) {int r=int(c/3); return(r);}                   // triangle of corner c
  int n (int c) {int r=3*int(c/3)+(c+1)%3; return(r);}         // next corner
  int p (int c) {int r=3*int(c/3)+(c+2)%3; return(r);}         // previous corner
  pt g (int c) {return G[V[c]];}                             // shortcut to get the point where the vertex v(c) of corner c is located

  boolean nb(int c) {return(O[c]!=c);};  // not a border corner
  boolean bord(int c) {return(O[c]==c);};  // not a border corner

  pt cg(int c) {return P(0.6,g(c),0.2,g(p(c)),0.2,g(n(c)));}   // computes offset location of point at corner c

  // CORNER ACTIONS CURRENT CORNER c
  void next() {c=n(c);}
  void previous() {c=p(c);}
  void opposite() {c=o(c);}
  void left() {c=l(c);}
  void right() {c=r(c);}
  void swing() {c=s(c);}
  void unswing() {c=u(c);}
  void printCorner() {println("c = "+c);}



  // DISPLAY
  void showCurrentCorner(float r) { if(bord(c)) fill(red); else fill(dgreen); show(cg(c),r); };   // renders corner c as small ball
  void showEdge(int c) {beam( g(p(c)),g(n(c)),rt ); };  // draws edge of t(c) opposite to corner c
  void showVertices(float r) // shows all vertices green inside, red outside
    {
    for (int v=0; v<nv; v++)
      {
      if(isInterior[v]) fill(green); else fill(red);
      show(G[v],r);
      }
    }
  void showInteriorVertices(float r) {for (int v=0; v<nv; v++) if(isInterior[v]) show(G[v],r); }                          // shows all vertices as dots
  void showTriangles() { for (int c=0; c<nc; c+=3) show(g(c), g(c+1), g(c+2)); }         // draws all triangles (edges, or filled)
  void showEdges() {for (int i=0; i<nc; i++) showEdge(i); };         // draws all edges of mesh twice

  void triangulate()      // performs Delaunay triangulation using a quartic algorithm
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


  void computeO() // **02 implement it
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

  void showBorderEdges()  // draws all border edges of mesh
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

  void showNonBorderEdges() // draws all non-border edges of mesh
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

  void classifyVertices()
    {
      for (int i=0; i<nc;i++){isInterior[V[i]]=true;}
      for (int i=0; i<nc;i++){
        int cNext=n(i);
        int cPre=p(i);
        if ((O[cNext]==cNext) || (O[cPre]==cPre)){isInterior[V[i]]=false;}
      }
    // **03 implement it
    }

  void smoothenInterior() { // even interior vertiex locations
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
          pt pMid=P(gN,0.5,gP);
          x+=pMid.x*thisW; y+=pMid.y*thisW; z+=pMid.z*thisW;
        }
      }
      Gn[v]=P(x/w,y/w,z/w);
    }
    for (int v=0; v<nv; v++) if(isInterior[v]) G[v].translateTowards(.1,Gn[v]);
  }


   // **05 implement corner operators in Mesh
  int v (int c) {return V[c];}                                // vertex of c
  int o (int c) {
    if (O[c]==c) return O[c];
    else{
      return O[c];
    }
  }                                // opposite corner
  int l (int c) {return O[n(c)];}                             // left
  int s (int c) {return n(O[n(c)]);}                             // swing
  int u (int c) {return p(O[p(c)]);}                             // unswing
  int r (int c) {return O[p(c)];}                             // right

  void showVoronoiEdges() // draws Voronoi edges on the boundary of Voroni cells of interior vertices
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

  void showArcs() // draws arcs of quadratic B-spline of Voronoi boundary loops of interior vertices
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
          pt mid1=P(last_center,0.5,this_center);
          pt mid2=P(this_center,0.5,next_center);

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



  pt triCenter(int c) {return P(g(c),g(n(c)),g(p(c))); }  // returns center of mass of triangle of corner c
  pt triCircumcenter(int c) {return CircumCenter(g(c),g(n(c)),g(p(c))); }  // returns circumcenter of triangle of corner c


  } // end of MESH
