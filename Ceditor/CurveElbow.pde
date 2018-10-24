import java.lang.Float;

class CurveElbow{
  Elbow[] elbows;
  float[] diffK;

  CurveElbow(Elbow[] elbows){
    this.elbows=elbows;
    calculateDiffK();
    twistForDiffK();
  }

  void calculateDiffK(){
    int n=elbows.length;
    diffK=new float[n];
    diffK[0]=0.0;
    for(int i=1;i<n;i++){
      diffK[i]=angle(elbows[i-1].KS,elbows[i].KS);
      if (i==1){
        System.out.println(elbows[i-1].KS.x);
        System.out.println(elbows[i-1].KS.y);
        System.out.println(elbows[i-1].KS.z);
        System.out.println(elbows[i].KS.x);
        System.out.println(elbows[i].KS.y);
        System.out.println(elbows[i].KS.z);
        System.out.println(diffK[i]);
        System.out.println('-');
      }

      //System.out.println(diffK[i]);
      if(diffK[i]!=diffK[i]) diffK[i]=0;

    }
  }

  void twistForDiffK(){
    int n=elbows.length;
    for(int i=0;i<n;i++){
      //System.out.println(diffK[i]);
      elbows[i].twist_all(diffK[i]);
    }
  }



  void draw(){
    for(int i=0;i<elbows.length;i++){
      drawElbow(elbows[i]);
      if (i==0) drawTorusAroundStartCircle(elbows[i]);
    }
  }
}
